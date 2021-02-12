package Server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    ThreadSocketServer threadSocketServer;
    ModelServer modelServer;

    boolean closed = false;
    @FXML
    private ListView server_log_listview;
    @FXML
    private Button disconnect_user_button;
    @FXML
    private ListView connected_users_listview;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (this.modelServer != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        modelServer = new ModelServer();

        socketConnector();
        server_log_listview.setEditable(false);
        connected_users_listview.setEditable(false);
        server_log_listview.setItems(modelServer.getLogs());
        connected_users_listview.setItems(modelServer.getUsers_logged());

        //METODO PER LE RIGHE DELLA LISTVIEW
        server_log_listview.setCellFactory(lv -> new ListCell<String>() {
            @Override
            public void updateItem(String elem, boolean empty) {
                super.updateItem(elem, empty);
                if (empty) {
                    Platform.runLater(() -> setText(null));
                } else {
                    Platform.runLater(() -> setText(elem));
                }
            }
        });

        connected_users_listview.setCellFactory(lv -> new ListCell<String>() {
            @Override
            public void updateItem(String elem, boolean empty) {
                super.updateItem(elem, empty);
                if (empty) {
                    Platform.runLater(() -> setText(null));
                } else {
                    Platform.runLater(() -> setText(elem));
                }
            }
        });
    }

    public void handleCloseServer() throws IOException {
        if(closed){

            threadSocketServer = new ThreadSocketServer(modelServer);
            threadSocketServer.start();
            disconnect_user_button.setText("SHUTDOWN SERVER");
            closed = false;
        }
        else {
            threadSocketServer.terminate();
            disconnect_user_button.setText("TURN ON SERVER");
            closed = true;
        }
    }

    public void socketConnector(){
        threadSocketServer = new ThreadSocketServer(modelServer);
        threadSocketServer.start();
    }
}
