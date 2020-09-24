package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public HBox authPanel;
    @FXML
    public TextField logindField;
    @FXML
    public TextField passwordField;
    @FXML
    public HBox mshPanel;
    @FXML
    public ListView<String> clientList;
    private final String IP_ADDRESS = "localhost";
    private final int PORT = 8189;


    private Socket socket;
    DataInputStream in;
    DataOutputStream out;

    private boolean authenticated;
    private String  nickname;
    private final String TITLE = "";

    private Stage stage;
    private Stage regStage;
    private RegController regController;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        mshPanel.setVisible(authenticated);
        mshPanel.setManaged(authenticated);
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);

        if (!authenticated) {
            nickname = "";
        }
        setTitle(nickname);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthenticated(false);
        createRegWindow();
        Platform.runLater(()->{
            stage = (Stage) textField.getScene().getWindow();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    if (socket != null && !socket.isClosed()) {
                        try {
                            out.writeUTF("/end");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
    }

    private void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        while (true) {
                            String str = in.readUTF();

                            if (str.equals("/end")) {
                                out.writeUTF("/end");
                            }

                            if (str.startsWith("/authok")) {
                                nickname = str.split(" ", 2)[1];
                                setAuthenticated(true);
                                textArea.clear();
                                break;
                            }

                            if (str.startsWith("/regok")) {
                                regController.addMsgToTextArea(" =) Регистрация прошла успешно");
                            }
                            if (str.startsWith("/regno")) {
                                regController.addMsgToTextArea(" =( Регистрация не прошла");
                            } else {

                                LocalDateTime time = LocalDateTime.now();
                                DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
                                String afterFormat = time.format(format);

                                textArea.appendText(afterFormat + ": " + str + "\n");
                            }
                        }

                        while (true) {
                            String str = in.readUTF();

                            if (str.startsWith("/")) {
                                if (str.equals("/end")) {
                                    break;
                                }


                                if (str.startsWith("/clientlist ")) {
                                    String[] token = str.split("\\s");
                                    Platform.runLater(()->{
                                        clientList.getItems().clear();
                                        for (int i = 1; i < token.length; i++) {
                                            clientList.getItems().add(token[i]);
                                        }
                                    });
                                }
                            }

                            LocalDateTime time = LocalDateTime.now();
                            DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
                            String afterFormat = time.format(format);

                            textArea.appendText(afterFormat +  ": " + str + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        setAuthenticated(false);
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(ActionEvent actionEvent) {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToLogin(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF(String.format("/auth %s %s",
                    logindField.getText().trim().toLowerCase(),
                    passwordField.getText().trim()));
            passwordField.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTitle(String nick) {
        Platform.runLater(()-> {
            ((Stage) textField.getScene().getWindow()).setTitle(TITLE + nick);
        });
    }
    public void clickClientList(MouseEvent mouseEvent) {
        String receiver = clientList.getSelectionModel().getSelectedItem();
        textField.setText("/w " + receiver + " ");
    }

    private void createRegWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/reg.fxml"));
            Parent root = fxmlLoader.load();
            regStage = new Stage();
            regStage.setTitle("Окно регистрации");
            regStage.setScene(new Scene(root, 300, 200));

            regController = fxmlLoader.getController();
            regController.setController(this);

            regStage.initModality(Modality.APPLICATION_MODAL);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registratioin(ActionEvent actionEvent) {
        regStage.show();
    }

    public void tryToReg(String login, String password, String nickName) {
        String msg = String.format("/reg %s %s %s", login, password, nickName );

        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
