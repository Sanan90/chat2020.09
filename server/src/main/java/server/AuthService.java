package server;

import java.sql.SQLException;

public interface AuthService {
    String getNickNameByLoginAndPassword(String login, String password) throws SQLException;

    boolean registration(String login, String password, String nickName) throws SQLException;
}
