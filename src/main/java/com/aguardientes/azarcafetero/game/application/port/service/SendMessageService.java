package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.in.SendMessageUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.MessagePublisher;
import com.aguardientes.azarcafetero.game.domain.TableMessage;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SendMessageService implements SendMessageUseCase {
    private final MessagePublisher messagePublisher;

    public SendMessageService(MessagePublisher messagePublisher) {
        this.messagePublisher = Objects.requireNonNull(messagePublisher, "MessagePublisher cannot be null");
    }

    @Override
    public void sendMessage(TableMessage message) {
        Objects.requireNonNull(message, "Message cannot be null");
        messagePublisher.publishToTable(message);
    }
}
