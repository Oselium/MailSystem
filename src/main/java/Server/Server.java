package Server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Server extends Application {

    public static void main(String[] args) {
        System.out.println("Server launched");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader ServerLoader = new FXMLLoader(getClass().getResource("/progetto_xml_server.fxml"));
        Parent root1 = ServerLoader.load();
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root1));
        primaryStage.show();

        //quando chiudi l'email fa tornare al login e poi da lì chiudi definitivamente se ti serve

    }

}







