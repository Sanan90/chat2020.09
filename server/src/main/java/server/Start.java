package server;

import java.sql.*;

public class Start {

//    private static Connection connection;
//    private static Statement stat;
//    private static PreparedStatement psInsert;
//
//    public static void connect() throws ClassNotFoundException, SQLException {
//        Class.forName("org.sqlite.JDBC");
//        connection = DriverManager.getConnection("jdbc:sqlite:clientList.db");
//        stat = connection.createStatement();
//    }

    public static void main(String[] args) {

//        try {
//            connect();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        try {
//            stat.executeUpdate("INSERT INTO clientList(login, password, nickname) VALUES ('bob22', 80, 'Bob');");
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
        new Server();



    }

    private void createTableEx() {
        String s = "CREATE TABLE students (\n" +
                "    id    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    name  TEXT,\n" +
                "    score INTEGER\n" +
                ");\n";
    }

}
