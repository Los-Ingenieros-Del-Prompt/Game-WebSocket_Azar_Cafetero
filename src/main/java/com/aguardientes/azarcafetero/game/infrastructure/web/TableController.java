package com.aguardientes.azarcafetero.game.infrastructure.web;

import com.aguardientes.azarcafetero.game.domain.TableSession;
import com.aguardientes.azarcafetero.game.infrastructure.messaging.TableSessionManager;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    @Autowired
    private TableSessionManager sessionManager;

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
                table.getCreatedAt()
        );
    }
}
