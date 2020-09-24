package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.*;

public class RegController {

    private static Connection connection;
    private static Statement stat;
    private static PreparedStatement psInsert;

    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:clientList.db");
        stat = connection.createStatement();
    }


    private Controller controller;
    @FXML
    private TextArea regTextArea;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField nickField;

    public void tryToReg(ActionEvent actionEvent) {

        controller.tryToReg(loginField.getText().trim(),
                passwordField.getText().trim(),
                nickField.getText().trim());
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }


    public static void prepareAllStatement() throws SQLException {
        psInsert = connection.prepareStatement("INSERT INTO clientList(login, password, nickname) VALUES (?, ?, ?);");
    }

    private static void fillTable(String login, String password, String nickname) throws SQLException {
        psInsert.setString(1, login);
        psInsert.setString(2, password);
        psInsert.setString(3, nickname);
            psInsert.executeUpdate();
    }

    public void addMsgToTextArea (String msg) {
        if (msg.startsWith(" =) ")) {
            regTextArea.appendText("Поздравляем!!! " + msg + "\n" +
                    "Ваш логин для входа " + loginField.getText() + "\n" +
                    "Ваш пароль для входа " + passwordField.getText() + "\n");



            try {
                connect();
                prepareAllStatement();
                fillTable(loginField.getText(), passwordField.getText(), nickField.getText() );
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }



        }   else {   regTextArea.appendText(msg + "\n");

        }
    }

    public void regOk(ActionEvent actionEvent) {
    }
}
