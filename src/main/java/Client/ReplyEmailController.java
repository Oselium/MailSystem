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

public class ReplyEmailController {
    Errors errors;
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


    public void initializeModel(MailBox mailBox) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.errors = new Errors();
        this.mailBox = mailBox;
        this.from_textfield.setEditable(false);
        this.to_textfield.setEditable(false);
        this.subject_textfield.setEditable(false);

        subject_textfield.setText(mailBox.getCurrentEmail().getSubject());
        from_textfield.setText(mailBox.getUsername());
        to_textfield.setText(mailBox.getCurrentEmail().getSender());

        mail_textarea.setText(mailBox.getCurrentEmail().getSender()+"\n"+mailBox.getCurrentEmail().getDate()
                +":\n"+mailBox.getCurrentEmail().getMessage()+"\n--------------\n\n");
    }

    @FXML
    public void handleSendNewEmail(ActionEvent event) throws IOException {
        try {
            Networking net = new Networking(mailBox);
            String id = net.request_mail_id();
            String subject = subject_textfield.getText();
            String message = mail_textarea.getText();
            GregorianCalendar date = new GregorianCalendar();
            net = new Networking(mailBox);
            String from = null;

            //CONTROLLO EMAIL VALIDA
            if (!mailSyntaxCheck(from_textfield.getText()) ) {
                //Errors errors = new Errors();
                errors.addressIncorrect();
            }else {from = from_textfield.getText();}
            String to = to_textfield.getText();

            ObservableList<String> list = FXCollections.observableArrayList();

            String[] prova = to.split("\\s*,\\s*");
            list.addAll(prova);

            String Old_TO_tmp = "";
            for (int i = 0; i < mailBox.getCurrentEmail().getAddressee().size(); i++) {
                Old_TO_tmp = Old_TO_tmp + "<" + mailBox.getCurrentEmail().getAddressee().get(i) + "> ";
            }
            Boolean correct = true;
            for (String s: list) {
                net = new Networking(mailBox);
                if(!mailSyntaxCheck(s) ) correct = false;
            }

            //System.out.println(correct);
            if(correct){
            Email e = new Email(id, from, list, subject, message, date.getTime().toString());
            net.send_new_mail(e);

            Stage stage = (Stage) send_button.getScene().getWindow();
            stage.close();}/* else System.out.println(correct);*/

        }catch (Exception e){
            Stage s = (Stage) this.send_button.getScene().getWindow();
            s.close();
            //Errors errors = new Errors();
            errors.connection();
        }
    }

    private boolean mailSyntaxCheck(String email) {
        return email.matches("^[a-z\\.0-9]+@+[a-zA-Z0-9]+(\\.+[a-zA-Z]{2,})$");
    }
}