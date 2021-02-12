package Client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.Serializable;

public class ErrorController implements Serializable {
    @FXML
    private Label text_label;
    @FXML
    private javafx.scene.control.Button button_close;
    public void initializeModel(String error) {
        if (error == null) {
            throw new IllegalStateException("There's no string.\n Internal error.");
        }
        this.text_label.setText("The action can't be processed \nbecause of the following error: \n• "+ error.toUpperCase());
    }

    public void handleCloseError() {
        Stage stage = (Stage) button_close.getScene().getWindow();
        stage.close();
    }
}
