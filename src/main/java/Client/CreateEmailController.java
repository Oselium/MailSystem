package Client;

import Server.Email;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.GregorianCalendar;

public class CreateEmailController {

    MailBox mailBox;
    Errors errors;

    @FXML
    private Button send_button;
    @FXML
    private TextField to_textfield;
    @FXML
    private TextField from_textfield;
    @FXML
    private TextField subject_textfield;
    @FXML
    private TextArea mail_textarea;


    public void initializeModel(MailBox mailBox) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.errors = new Errors();
        this.mailBox = mailBox;
        this.from_textfield.setEditable(false);
        this.from_textfield.setText(mailBox.getUsername());
    }

    @FXML
    public void handleSendNewEmail(ActionEvent event) throws IOException {
        try {
            Networking net = new Networking(mailBox);
            String id = net.request_mail_id();

            String from = from_textfield.getText();
            String to = to_textfield.getText();
            ObservableList<String> list = FXCollections.observableArrayList();
            String[] prova = to.split("\\s*,\\s*");
            list.addAll(prova);

            Boolean correct = true;
            for (String s: list) {
                net = new Networking(mailBox);
                if(!correctSyntaxTo(s) || !mailSyntaxCheck(s) || !net.control_username(s)) correct = false;
            }
            String subject = subject_textfield.getText();
            String message = mail_textarea.getText();
            GregorianCalendar date = new GregorianCalendar();

            if(CorrectSintaxFrom(from)  && correct ) {
                net = new Networking(mailBox);
                Email e = new Email(id, from, list, subject, message, date.getTime().toString());
                net.send_new_mail(e);
                Stage stage = (Stage) send_button.getScene().getWindow();
                stage.close();
            }else{
                errors.addressIncorrect();
            }
        }catch (Exception e){
            Stage s = (Stage) this.send_button.getScene().getWindow();
            errors.connection();
        }

    }

    public Boolean correctSyntaxTo(String to){
        Networking net;
        boolean correct = true;
        if (to_textfield.getText().isEmpty()) {
            correct = false;
        } else {
                net = new Networking(mailBox);
                if (to.equals("") || !net.control_username(to) || !mailSyntaxCheck(to)) {
                    correct = false;
                }
            }
        return correct;
    }

    @FXML
    public Boolean CorrectSintaxFrom(String from) throws IOException {

        boolean correct = true;
        Networking net;

        //CONTROLLO EMAIL VALIDA
        if (!mailSyntaxCheck(from_textfield.getText()) ) {


            errors.addressIncorrect();
            correct = false;
        }else {
            net= new Networking(mailBox);
            from = mailBox.getUsername();
            if(!net.control_username(from)){

                errors.addressDoesntExist();
                correct = false;
            }
        }

        return correct;
    }

    //DA SPOSTARE IN UNA CLASSE PROPRIA
    private boolean mailSyntaxCheck(String email) {
        return email.matches("^[a-z\\.0-9]+@+[a-zA-Z0-9]+(\\.+[a-zA-Z]{2,})$");
    }
}


