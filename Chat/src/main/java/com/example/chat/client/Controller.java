package com.example.chat.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextArea textArea;

    @FXML
    TextField msgField, loginField;

    @FXML
    HBox msgPanel, authPanel;

    @FXML
    PasswordField passField;

    @FXML
    ListView<String> clientsList;

    private boolean authentificated;
    private String nickname;
    private File messageHistoryFile;
    private final int RECOVERABLE_MESSAGE_QUANTITY = 100;

    public void setAuthenticated(boolean authentificated) {
        this.authentificated = authentificated;
        authPanel.setVisible(!authentificated);
        authPanel.setManaged(!authentificated);
        msgPanel.setVisible(authentificated);
        msgPanel.setManaged(authentificated);
        clientsList.setVisible(authentificated);
        clientsList.setManaged(authentificated);
        if (!authentificated) {
            nickname = "";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthenticated(false);
        clientsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String nickname = clientsList.getSelectionModel().getSelectedItem();
                msgField.setText("/w " + nickname + " ");
                msgField.requestFocus();
                msgField.selectEnd();
            }
        });
        linkCallbacks();
    }

    public void sendAuth() {
        Network.sendAuth(loginField.getText(), passField.getText());
        loginField.clear();
        passField.clear();
    }

    public void sendMsg() {
        if (Network.sendMsg(msgField.getText())) {
            msgField.clear();
            msgField.requestFocus();
        }
    }

    public void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
            alert.showAndWait();
        });
    }


    public void linkCallbacks() {
        Network.setCallOnException(args -> showAlert(args[0].toString()));

        Network.setCallOnCloseConnection(args -> setAuthenticated(false));

        Network.setCallOnAuthenticated(args -> {
            setAuthenticated(true);
            nickname = args[0].toString();
            //////////вынести в отдельный класс///////////////
            int lineNumber = 0;
            if (setMessageHistoryFile().exists()) {
                try (FileReader fileReader = new FileReader(messageHistoryFile)) {
                    LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
                    while (lineNumberReader.readLine() != null) {
                        lineNumber++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(messageHistoryFile))) {
                    for (int i = 0; i < lineNumber; i++) {
                        String line = bufferedReader.readLine();
                        if (i >= lineNumber - RECOVERABLE_MESSAGE_QUANTITY) {
                            textArea.appendText(line + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    messageHistoryFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ///////////////
        });


        Network.setCallOnMsgReceived(args -> {
            String msg = args[0].toString();
            if (msg.startsWith("/")) {
                if (msg.startsWith("/clients ")) {
                    String[] clients = msg.substring(9).split("\\s");
                    Platform.runLater(() -> {
                        clientsList.getItems().clear();
                        for (String client : clients) {
                            clientsList.getItems().add(client);
                        }
                    });
                }
                if (msg.startsWith("/changenick ")) {
                    nickname = msg.split("\\s")[1];
                }
            } else {
                textArea.setWrapText(true);
                textArea.appendText(msg + "\n");
                try (FileWriter fw = new FileWriter(messageHistoryFile, true)) {
                    fw.append(msg + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private File setMessageHistoryFile() {
        messageHistoryFile = new File( nickname + ".cmh");
        return messageHistoryFile;
    }
}
