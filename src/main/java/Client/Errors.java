package Client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class Errors {
    Stage s;

    public Errors() {
        this.s = new Stage();
    }


    @FXML
    public void connection() throws IOException {
            BorderPane ConnectionErrorView = new BorderPane();
            FXMLLoader ConnectionErrorLoader = new FXMLLoader(getClass().getResource("/progetto_xml_connection.fxml"));
            ConnectionErrorView.setCenter(ConnectionErrorLoader.load());
            ConnectionErrorController rec = ConnectionErrorLoader.getController();
            try {rec.initializeModel();}catch(NullPointerException e){e.printStackTrace();}
            s.setAlwaysOnTop(true);
            s.setTitle("Connection error!");
            s.setScene(new Scene(ConnectionErrorView));
            s.show();
    }

    @FXML
    public void noMailSelected() throws IOException {
            error("No email selected");
    }

    @FXML
    public void addressIncorrect() throws IOException {
            error("Email address incorrect or blank");
    }

    @FXML
    public void DeleteEmailError() throws IOException {
            error("Error during deleting server's mail");
    }

    @FXML
    public void addressDoesntExist() throws IOException {
            error("Email address doesn't exist");
    }

    public void error(String error) throws IOException {
        BorderPane ErrorView = new BorderPane();
        FXMLLoader ErrorLoader = new FXMLLoader(getClass().getResource("/progetto_xml_error.fxml"));
        ErrorView.setCenter(ErrorLoader.load());
        ErrorController rec = ErrorLoader.getController();

        try {rec.initializeModel(error);}catch(NullPointerException e){e.printStackTrace();}
        s.setTitle("Error!");
        s.setScene(new Scene(ErrorView));
        s.show();
    }
}

