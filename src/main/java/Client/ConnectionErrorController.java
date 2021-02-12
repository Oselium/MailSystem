package Client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.Serializable;

public class ConnectionErrorController implements Serializable {
    @FXML
    private Label text_label;
    @FXML
    private javafx.scene.control.Button button_retry;

    public void initializeModel() {
        this.text_label.setText("The server is offline or \nthere's a connection problem, \nevery action will not work \nuntil the connection is restored");
    }

    public void handleRetry(){
        Stage error = (Stage) button_retry.getScene().getWindow();
        error.close();
    }
}
