package server;

import java.io.EOFException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService{


    private static Connection connection;
    private static Statement stat;
    private static PreparedStatement psInsert;


    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:clientList.db");
        stat = connection.createStatement();
    }

    public static void disconnect() {
        try {
            stat.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void fillTable(String login, String password, String nickname) throws SQLException {
        psInsert.setString(1, login);
        psInsert.setString(2, password);
        psInsert.setString(3, nickname);
        psInsert.executeUpdate();
    }

    @Override
    public String getNickNameByLoginAndPassword(String login, String password) throws SQLException {

        try {
            connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        ResultSet rs = stat.executeQuery("SELECT login, password, nickName FROM clientList;");

        while (rs.next() == true) {
            String nick = rs.getString("nickName");
            if (rs.getString("login").equals(login) && rs.getString("password").equals(password)) {
                disconnect();
                System.out.println("R1");
                return nick;
            }
        }

//        for (UserData user : users) {
//            if (user.login.equals(login) && user.password.equals(password)) {
//                return user.nickname;
//            }
//        }
        System.out.println("R2");
        stat.close();
        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickName) throws SQLException {
        try {
            connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ResultSet rs = stat.executeQuery("SELECT login, nickName FROM clientList;");


        while (rs.next() == true) {
                if (rs.getString("login").equals(login) || rs.getString("nickName").equals(nickName)) {
                    disconnect();

                    rs.close();
                    return false;
                }
        }
        disconnect();
        rs.close();
        return true;
//        for (UserData user : users) {
//            if (user.login.equals(login) || user.nickname.equals(nickName)) {
//                return false;
//            }
//        }
//        users.add(new UserData(login, password, nickName));
//        return true;
    }
}






