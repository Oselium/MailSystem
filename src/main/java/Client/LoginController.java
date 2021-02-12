package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;

public class LoginController {

    @FXML
    TextField username_textfield;
    @FXML
    Button login_button;

    Errors errors;
    Boolean loaded = false;
    MailBox mb;
    Networking net;

    public LoginController(){
        this.errors = new Errors();
    }

    private boolean userSyntaxCheck(String user) throws IOException {

        if(!user.matches("^[a-z\\.0-9]+@+[a-zA-Z0-9]+(\\.+[a-zA-Z]{2,})$") || username_textfield.getText().isEmpty()) {
            errors.addressIncorrect();
            this.username_textfield.setText("");
            return false;
        }else{
            return true;
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) throws IOException {
        Stage s = new Stage();
        try {
            if(userSyntaxCheck(username_textfield.getText())){
                mb = new MailBox(username_textfield.getText());
                net = new Networking(mb);
                loaded = net.user_login(username_textfield.getText(), errors);
                if (loaded) {

                    Stage stage = (Stage) login_button.getScene().getWindow();
                    stage.close();
                    FXMLLoader mailLoader = new FXMLLoader(getClass().getResource("/progetto_xml_received.fxml"));
                    Parent root1 = mailLoader.load();
                    System.out.println("Sending messages to the ServerSocket as " + mb.getUsername());
                    ReceivedController mbc = mailLoader.getController();
                    mbc.initializeModel(mb, s, errors);

                    s.setTitle("MailBox");
                    s.setScene(new Scene(root1));
                    s.show();
                } else {

                    errors.addressDoesntExist();
                    this.username_textfield.setText("");
                }
            }
        } catch (IOException e){
            s = (Stage) this.login_button.getScene().getWindow();
            s.close();
            errors.connection();
        }
    }
}
