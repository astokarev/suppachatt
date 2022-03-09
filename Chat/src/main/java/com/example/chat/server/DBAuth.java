package com.example.chat.server;

public class DBAuth implements AuthService {
    @Override
    public String getNickname(String login, String password) {
        return Database.getUserNickname(login, password);
    }

    @Override
    public boolean changeNickname(String currentNickname, String newNickname) {
        return Database.changeUserNickname(currentNickname, newNickname);
    }
}
