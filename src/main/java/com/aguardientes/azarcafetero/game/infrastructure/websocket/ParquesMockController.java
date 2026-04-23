package com.aguardientes.azarcafetero.game.infrastructure.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ParquesMockController {

    private final SimpMessagingTemplate messagingTemplate;
    private final com.aguardientes.azarcafetero.game.application.port.in.JoinTableUseCase joinTableUseCase;
    private final Map<String, Map<String, Object>> games = new ConcurrentHashMap<>();

    private static final String[] COLORS = {"AMARILLO", "AZUL", "ROJO", "VERDE"};

    public ParquesMockController(SimpMessagingTemplate messagingTemplate, com.aguardientes.azarcafetero.game.application.port.in.JoinTableUseCase joinTableUseCase) {
        this.messagingTemplate = messagingTemplate;
        this.joinTableUseCase = joinTableUseCase;
    }

    @MessageMapping("/game/create")
    public void handleCreateGame(Map<String, Object> payload) {
        String gameId = (String) payload.get("gameId");
        if (gameId != null && !games.containsKey(gameId)) {
            games.put(gameId, createInitialGameState(gameId));
        }
    }

    @MessageMapping("/game/{gameId}/join")
    public void handleJoinGame(Map<String, Object> payload, @DestinationVariable String gameId) {
        String playerId = (String) payload.get("playerId");
        String playerName = (String) payload.get("playerName");

        Map<String, Object> gameState = games.computeIfAbsent(gameId, this::createInitialGameState);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> players = (List<Map<String, Object>>) gameState.get("players");
        
        boolean exists = players.stream().anyMatch(p -> p.get("id").equals(playerId));
        
        if (!exists && players.size() < 4) {
            Map<String, Object> newPlayer = new HashMap<>();
            newPlayer.put("id", playerId);
            newPlayer.put("name", playerName != null ? playerName : "Jugador " + (players.size() + 1));
            newPlayer.put("color", COLORS[players.size()]);
            newPlayer.put("jailAttemptsRemaining", 3);
            newPlayer.put("consecutivePairs", 0);
            
            List<Map<String, Object>> pieces = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                Map<String, Object> piece = new HashMap<>();
                piece.put("id", playerId + "-piece-" + i);
                piece.put("absolutePosition", -1); // En la cárcel
                piece.put("relativePosition", -1);
                piece.put("inJail", true);
                piece.put("atHome", false);
                pieces.add(piece);
            }
            newPlayer.put("pieces", pieces);
            
            players.add(newPlayer);
            
            // Si es el primer jugador, asignarlo como current player
            if (players.size() == 1) {
                gameState.put("currentPlayerId", playerId);
            }
            
            // Sincronizar con el lobby (TableSession)
            try {
                com.aguardientes.azarcafetero.game.domain.Player playerObj = new com.aguardientes.azarcafetero.game.domain.Player(
                    playerId, 
                    playerName != null ? playerName : "Jugador", 
                    playerId, 
                    1000.0 // Saldo ficticio para el mock
                );
                joinTableUseCase.joinTable(gameId, playerObj);
            } catch (Exception e) {
                // Silencioso si falla la sincronización con el lobby
            }
        }
        
        messagingTemplate.convertAndSend("/topic/game/" + gameId, (Object) gameState);
    }

    @MessageMapping("/game/{gameId}/roll")
    public void handleRollDice(Map<String, Object> payload, @DestinationVariable String gameId) {
        String playerId = (String) payload.get("playerId");
        Map<String, Object> gameState = games.get(gameId);
        
        if (gameState != null && gameState.get("currentPlayerId").equals(playerId) && !(Boolean)gameState.get("diceRolled")) {
            Random random = new Random();
            int d1 = random.nextInt(6) + 1;
            int d2 = random.nextInt(6) + 1;
            
            gameState.put("die1", d1);
            gameState.put("die2", d2);
            gameState.put("moveValue", d1 + d2);
            gameState.put("diceRolled", true);
            
            // Si sacó par, habilitar salida de cárcel
            if (d1 == d2) {
                gameState.put("jailExitAvailable", true);
            } else {
                gameState.put("jailExitAvailable", false);
            }
            
            messagingTemplate.convertAndSend("/topic/game/" + gameId, (Object) gameState);
        }
    }

    @MessageMapping("/game/{gameId}/move")
    public void handleMovePiece(Map<String, Object> payload, @DestinationVariable String gameId) {
        String playerId = (String) payload.get("playerId");
        String pieceId = (String) payload.get("pieceId");
        Map<String, Object> gameState = games.get(gameId);
        
        if (gameState != null && gameState.get("currentPlayerId").equals(playerId) && (Boolean)gameState.get("diceRolled")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> players = (List<Map<String, Object>>) gameState.get("players");
            
            Map<String, Object> player = players.stream().filter(p -> p.get("id").equals(playerId)).findFirst().orElse(null);
            if (player != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> pieces = (List<Map<String, Object>>) player.get("pieces");
                Map<String, Object> piece = pieces.stream().filter(p -> p.get("id").equals(pieceId)).findFirst().orElse(null);
                
                if (piece != null) {
                    int moveValue = (Integer) gameState.get("moveValue");
                    boolean inJail = (Boolean) piece.get("inJail");
                    
                    if (inJail) {
                        if ((Boolean) gameState.get("jailExitAvailable")) {
                            piece.put("inJail", false);
                            piece.put("absolutePosition", 0);
                            piece.put("relativePosition", 0);
                        }
                    } else {
                        int pos = (Integer) piece.get("relativePosition");
                        if (pos + moveValue <= 68) {
                            piece.put("relativePosition", pos + moveValue);
                            piece.put("absolutePosition", ((Integer)piece.get("absolutePosition") + moveValue) % 68);
                            if ((Integer)piece.get("relativePosition") == 68) {
                                piece.put("atHome", true);
                            }
                        }
                    }
                    
                    // Resetear estado para el siguiente turno o tiro
                    gameState.put("diceRolled", false);
                    gameState.put("jailExitAvailable", false);
                    
                    // Si no sacó par, pasar el turno
                    if ((Integer)gameState.get("die1") != (Integer)gameState.get("die2")) {
                        int currentIndex = 0;
                        for (int i = 0; i < players.size(); i++) {
                            if (players.get(i).get("id").equals(playerId)) {
                                currentIndex = i;
                                break;
                            }
                        }
                        int nextIndex = (currentIndex + 1) % players.size();
                        gameState.put("currentPlayerId", players.get(nextIndex).get("id"));
                    }
                }
            }
            
            messagingTemplate.convertAndSend("/topic/game/" + gameId, (Object) gameState);
        }
    }

    private Map<String, Object> createInitialGameState(String gameId) {
        Map<String, Object> state = new HashMap<>();
        state.put("gameId", gameId);
        state.put("currentPlayerId", "");
        state.put("die1", 1);
        state.put("die2", 1);
        state.put("moveValue", 0);
        state.put("diceRolled", false);
        state.put("jailExitAvailable", false);
        state.put("finished", false);
        state.put("winnerId", null);
        state.put("players", new ArrayList<Map<String, Object>>());
        return state;
    }
}
