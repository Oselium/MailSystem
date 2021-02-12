package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Client extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader mailLoader = new FXMLLoader(getClass().getResource("/progetto_xml_login.fxml"));
        Parent root1 = mailLoader.load();
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root1));
        primaryStage.show();
    }
}
