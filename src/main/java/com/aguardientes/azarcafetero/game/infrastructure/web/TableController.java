package com.aguardientes.azarcafetero.game.infrastructure.web;

import com.aguardientes.azarcafetero.game.application.port.in.CreateTableUseCase;
import com.aguardientes.azarcafetero.game.domain.Table;
import com.aguardientes.azarcafetero.game.domain.TableSession;
import com.aguardientes.azarcafetero.game.infrastructure.messaging.TableSessionManager;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.CreateTableRequest;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    @Autowired
    private TableSessionManager sessionManager;

    @Autowired
    private CreateTableUseCase createTableUseCase;

    @PostMapping
    public ResponseEntity<TableDTO> createTable(@RequestBody CreateTableRequest request) {
        if (request.getTableName() == null || request.getTableName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table name cannot be blank");
        }
        if (request.getRequiredBet() == null || request.getRequiredBet() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required bet must be positive");
        }
        
        int maxPlayers = request.getMaxPlayers() != null ? request.getMaxPlayers() : 6;
        if (maxPlayers <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max players must be positive");
        }

        Table table = createTableUseCase.createTable(request.getTableName(), request.getRequiredBet(), maxPlayers);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TableDTO(table.getId(), table.getName(), 0, System.currentTimeMillis(), table.getRequiredBet()));
    }

    @GetMapping
    public List<TableDTO> getAllTables() {
        return sessionManager.getAllSessions()
                .values()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{tableId}")
    public TableDTO getTable(@PathVariable String tableId) {
        return sessionManager.findById(tableId)
                .map(this::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private TableDTO toDTO(TableSession table) {
        return new TableDTO(
                table.getTableId(),
                table.getTable().getName(),
                table.getPlayerCount(),
                table.getCreatedAt(),
                table.getTable().getRequiredBet()
        );
    }
}
