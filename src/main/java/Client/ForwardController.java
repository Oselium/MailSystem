package Client;

import Server.Email;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;
import java.util.GregorianCalendar;

public class ForwardController implements Serializable {
    Errors errors;
    //Stage primaryStage;
    ListView prova_lista;
    MailBox mailBox;

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


    public void initializeModel(MailBox mailBox, ListView lista) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.errors = new Errors();
        this.mailBox = mailBox;
        prova_lista = lista;
        this.mail_textarea.setEditable(false);
        this.from_textfield.setEditable(false);
        this.subject_textfield.setEditable(false);
        this.from_textfield.setText(mailBox.getUsername());
        this.subject_textfield.setText(mailBox.getCurrentEmail().getSubject());
        this.mail_textarea.setText(mailBox.getCurrentEmail().getMessage());
    }

    @FXML
    public void handleForwardSend(ActionEvent event) throws IOException {
        try {
            if (!CorrectSintaxFrom(from_textfield.getText()) || !CorrectSyntaxTo(to_textfield.getText())) {
                //Errors errors = new Errors();
                errors.addressIncorrect();
            } else {
                Networking net = new Networking(mailBox);
                String id = net.request_mail_id();
                String from = from_textfield.getText();
                String to = to_textfield.getText();
                ObservableList<String> list = FXCollections.observableArrayList();

                String[] prova = to.split("\\s*,\\s*");
                list.addAll(prova);

                String Old_TO_tmp = "";
                for (int i = 0; i < mailBox.getCurrentEmail().getAddressee().size(); i++) {
                    Old_TO_tmp = Old_TO_tmp + "<" + mailBox.getCurrentEmail().getAddressee().get(i) + "> ";
                }

                String subject = subject_textfield.getText();
                String message = "--- Fwd message --- \nFrom: " + from_textfield.getText() + "\nTo: " + Old_TO_tmp + "\nDate: " + mailBox.getCurrentEmail().getDate()
                        + "\nSubject: " + mailBox.getCurrentEmail().getSubject() + "\n" + mail_textarea.getText();
                GregorianCalendar date = new GregorianCalendar();
                Email e = new Email(id, from, list, subject, message, date.getTime().toString());

                net = new Networking(mailBox);
                net.send_new_mail(e);
                net = new Networking(mailBox);

                net.load_mail_sended(mailBox.get_last_id_snd());
                Stage stage = (Stage) send_button.getScene().getWindow();
                stage.close();
            }
        }catch (Exception e){
            Stage s = (Stage) this.send_button.getScene().getWindow();
            //s.close();
            errors.connection();
        }


    }

    public Boolean CorrectSyntaxTo(String to) throws IOException {

        Networking net;
        Boolean correct = true;
        if (to_textfield.getText().isEmpty()) {
            correct = false;

        } else {

            ObservableList<String> list = FXCollections.observableArrayList();

            String[] prova = to.split("\\s*,\\s*");

            //INSERIRE CONTROLLO VIRGOLE
            boolean control = true;

            for (int i = 0; i < prova.length && control; i++) {
                net = new Networking(mailBox);
                control = net.control_username(prova[i]);
                //CONTROLLO CHE STRING CI SIA NELL'USERNAME JSON
                if (!control || !mailSyntaxCheck(prova[i].trim())) {
                    correct = false;
                } else {
                    list.add(prova[i].trim());
                }

            }
        }

        return correct;
    }

    @FXML
    public Boolean CorrectSintaxFrom(String from) throws IOException {

        boolean correct = true;
        Networking net;

        //CONTROLLO EMAIL VALIDA
        if (!mailSyntaxCheck(from_textfield.getText())) {
            errors.addressIncorrect();
            correct = false;
        } else {
            net = new Networking(mailBox);
            from = mailBox.getUsername();
            if (!net.control_username(from)) {
                //Errors errors = new Errors();
                errors.addressDoesntExist();
                correct = false;
            }
        }

        return correct;
    }

    private boolean mailSyntaxCheck(String email) {
        return email.matches("^[a-z\\.0-9]+@+[a-zA-Z0-9]+(\\.+[a-zA-Z]{2,})$");
    }
}