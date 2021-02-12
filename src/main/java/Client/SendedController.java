package Client;

import Server.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.Serializable;

public class SendedController implements Serializable {

    Errors errors;
    Stage stage;
    MailBox mailBox;

    @FXML
    private Label username_label;
    @FXML
    private ListView sended_listview;
    @FXML
    private Label To_sended_label;
    @FXML
    private Label subject_sended_label;
    @FXML
    private Label date_sended_label;
    @FXML
    private Label serverstatus_send_label;
    @FXML
    private TextArea sended_textarea;

    //INIZIALIZZIAMO IL MODEL
    public void initializeModel(MailBox mailBox, Stage stage, Errors errors, String server_status) {
        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.errors = errors;
        this.stage = stage;
        this.mailBox = mailBox;
        this.mailBox.setCurrentEmail(null);
        sended_textarea.setEditable(false);
        this.serverstatus_send_label.setText(server_status);

        //CARICHIAMO LA LISTA DELLE EMAIL RICEVUTE
        sended_listview.setItems(mailBox.getMaillist_sended());
        //CARICHIAMO IL NOME DELL'USER PRESENTE NELLA MAILBOX
        username_label.setText(mailBox.getUsername());

        //METODO PER LE RIGHE DELLA LISTVIEW
        sended_listview.setCellFactory(lv -> new ListCell<Email>() {
            @Override
            public void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(email.getSubject());
                }
            }
        });

        //AGGIORNIAMO LA LISTA OGNI VOLTA CHE SELEZIONIAMO UNA MAIL
        sended_listview.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                mailBox.setCurrentEmail((Email)newSelection));

        mailBox.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
            if (oldEmail != null) {
                To_sended_label.setText(oldEmail.getAddressee().toString());
                subject_sended_label.setText(oldEmail.getSubject());
                date_sended_label.setText(oldEmail.getDate());
                sended_textarea.setText(oldEmail.getMessage());
            }
            if (newEmail == null) {
                To_sended_label.setText("");
                subject_sended_label.setText("");
                date_sended_label.setText("");
                sended_textarea.setText("");
            } else {
                To_sended_label.setText(newEmail.getStringAddressee());
                subject_sended_label.setText(newEmail.getSubject());
                date_sended_label.setText(newEmail.getDate());
                sended_textarea.setText(newEmail.getMessage());
            }
        });
    }

    @FXML public void handleChangeToReceivedListMail(ActionEvent event) throws IOException {
            Networking net = new Networking(mailBox);
            BorderPane ReceivedMailView =  new BorderPane();
            FXMLLoader receivedMailLoader= new FXMLLoader(getClass().getResource("/progetto_xml_received.fxml"));
            ReceivedMailView.setCenter(receivedMailLoader.load());

            Scene newView = new Scene(ReceivedMailView);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(newView);
            window.show();

            ReceivedController rc = receivedMailLoader.getController();
            rc.initializeModel(mailBox, stage, errors);
            try{
                net.load_mail_received(mailBox.get_last_id_recv());
            }catch(Exception e){
                e.printStackTrace();
            }
    }

    //EVENTO CHE ELIMINA L'EMAIL RICHIAMANDO IL MODEL
    @FXML public void handleDeleteSendedMail() throws IOException {
        if(mailBox.getCurrentEmail() == null){
            errors.noMailSelected();
        }else{
            Networking net = new Networking(mailBox);
            if(net.delete_mail(mailBox.getCurrentEmail()).equals("OK")){
                mailBox.deleteSendedEmail(mailBox.getCurrentEmail());
                sended_listview.setItems(mailBox.getMaillist_sended());
            }else{
                errors.DeleteEmailError();
            }
        }
    }


    @FXML public void handleForward() throws IOException {
        Networking net = new Networking(mailBox);
        if(mailBox.getCurrentEmail() == null){
            errors.noMailSelected();
        }else{
            BorderPane forwardView = new BorderPane();
            FXMLLoader ForwardLoader = new FXMLLoader(getClass().getResource("/progetto_xml_forward.fxml"));
            forwardView.setCenter(ForwardLoader.load());
            ForwardController c = ForwardLoader.getController();

            try {c.initializeModel(mailBox, sended_listview);}catch(NullPointerException e){e.printStackTrace();}
            Stage s = new Stage();
            s.setTitle("ForwardEmail2");
            s.setScene(new Scene(forwardView));
            s.show();
            net.load_mail_sended(mailBox.get_last_id_snd());
        }
    }
}
