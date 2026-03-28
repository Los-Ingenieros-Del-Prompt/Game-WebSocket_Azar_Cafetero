package com.aguardientes.azarcafetero.game.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class LobbyPlayerDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("balance")
    private BigDecimal balance;

    public LobbyPlayerDTO() {
    }

    public LobbyPlayerDTO(String id, String username, String displayName, String avatar, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.avatar = avatar;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "LobbyPlayerDTO{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", balance=" + balance +
                '}';
    }
}
