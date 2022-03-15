package chatApp.server;

public interface AuthService {

    String getNickname(String login, String password);

    boolean changeNickname(String currentNickname, String newNickname);

    boolean register(String login, String password, String nickname);
}