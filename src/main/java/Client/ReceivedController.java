package Client;

import Server.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class ReceivedController {

    Errors errors;
    private Stage stage;
    MailBox mailBox;

    @FXML
    private Label username_label;
    @FXML
    private ListView received_listview;
    @FXML
    private Label sender_label;
    @FXML
    private Label cc_label;
    @FXML
    private Label subject_label;
    @FXML
    private Label date_label;
    @FXML
    private Label serverstatus_label;
    @FXML
    private TextArea email_textarea;

    //INIZIALIZZIAMO IL MODEL
    public void initializeModel(MailBox mailBox, Stage stage, Errors errors) throws IOException {

        if (this.mailBox != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.errors = errors;
        this.stage = stage;
        this.mailBox = mailBox;
        this.mailBox.setCurrentEmail(null);

        email_textarea.setEditable(false);

        //CARICHIAMO LA LISTA DELLE EMAIL RICEVUTE
        received_listview.setItems(mailBox.getMaillist_received());
        //CARICHIAMO IL NOME DELL'USER PRESENTE NELLA MAILBOX
        username_label.setText(mailBox.getUsername());
        serverstatus_label.setText("checking...");

        //METODO PER LE RIGHE DELLA LISTVIEW
        received_listview.setCellFactory(lv -> new ListCell<Email>() {
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
        received_listview.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                mailBox.setCurrentEmail((Email) newSelection));

        //MOSTRA LE INFORMAZIONI DELLA EMAIL SELEZIONATA
        mailBox.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
            if (oldEmail != null) {
                sender_label.setText(oldEmail.getSender());
                subject_label.setText(oldEmail.getSubject());

                String cc = "";
                for (String addressee : oldEmail.getAddressee()) {
                    if (!addressee.equals(mailBox.getUsername()))
                        if (cc.equals("")) cc = cc + addressee;
                        else cc = cc + ", " + addressee;
                }
                cc_label.setText(cc);
                date_label.setText(oldEmail.getDate());
                email_textarea.setText(oldEmail.getMessage());
            }
            if (newEmail == null) {
                sender_label.setText("");
                subject_label.setText("");
                cc_label.setText("");
                date_label.setText("");
                email_textarea.setText("");
            } else {
                sender_label.setText(newEmail.getSender());
                subject_label.setText(newEmail.getSubject());
                String cc = "";
                for (String addressee : newEmail.getAddressee()) {
                    if (!addressee.equals(mailBox.getUsername()))
                        if (cc.equals("")) cc = cc + addressee;
                        else cc = cc + ", " + addressee;
                }
                cc_label.setText(cc);
                date_label.setText(newEmail.getDate());
                email_textarea.setText(newEmail.getMessage());
            }
        });

        stage.setOnCloseRequest(e -> {
            Networking net = new Networking(mailBox);
            net.user_logout(mailBox.getUsername());
            stage.close();
        });

        //OGNI 2 SECONDI RICARICHIAMO LE EMAIL
        ThreadRefreshMailList refresh = new ThreadRefreshMailList(mailBox, errors, serverstatus_label);
        try {
            //refresh.setDaemon(true);
            refresh.start();

        } catch (Exception e) {
            refresh.interrupt();

        }
    }

    //EVENTO CHE ELIMINA L'EMAIL RICHIAMANDO IL MODEL
    @FXML
    public void handleDeleteReceivedMail() throws IOException {
            if (mailBox.getCurrentEmail() == null) {
                errors.noMailSelected();
            } else {
                Networking net = new Networking(mailBox);
                if (net.delete_mail(mailBox.getCurrentEmail()).equals("OK")) {
                    mailBox.deleteReceivedEmail(mailBox.getCurrentEmail());
                    received_listview.setItems(mailBox.getMaillist_received());
                } else {
                    errors.DeleteEmailError();
                }
            }
    }

    @FXML
    public void handleChangeToSendedListMail(ActionEvent event) throws IOException {
            Networking net = new Networking(mailBox);
            BorderPane SendedMailView = new BorderPane();
            FXMLLoader sendedMailLoader = new FXMLLoader(getClass().getResource("/progetto_xml_sended.fxml"));
            SendedMailView.setCenter(sendedMailLoader.load());

            Scene newView = new Scene(SendedMailView);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(newView);
            window.show();

            SendedController sc = sendedMailLoader.getController();
            sc.initializeModel(mailBox, stage, errors, serverstatus_label.getText());
            net.load_mail_sended(mailBox.get_last_id_snd());
    }

    @FXML
    public void handleCreateNewEmail() throws IOException {
            BorderPane root = new BorderPane();
            FXMLLoader mailLoader2 = new FXMLLoader(getClass().getResource("/progetto_xml_crea.fxml"));
            root.setCenter(mailLoader2.load());
            CreateEmailController c = mailLoader2.getController();
            try {
                c.initializeModel(mailBox);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            Stage s = new Stage();
            s.setTitle("CreateEmail");
            s.setScene(new Scene(root));
            s.show();
    }

    @FXML
    public void handleReplyMail() throws IOException {
            if (mailBox.getCurrentEmail() == null) {
                errors.noMailSelected();
            } else {
                BorderPane SendedReplyView = new BorderPane();
                FXMLLoader sendedReplyLoader = new FXMLLoader(getClass().getResource("/progetto_xml_reply.fxml"));
                SendedReplyView.setCenter(sendedReplyLoader.load());
                ReplyEmailController rec = sendedReplyLoader.getController();

                try {
                    rec.initializeModel(mailBox);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                Stage s = new Stage();
                s.setTitle("Reply to " + mailBox.getCurrentEmail().getSubject());
                s.setScene(new Scene(SendedReplyView));
                s.show();
            }
    }

    @FXML
    public void handleReplyAllMail() throws IOException {
            if (mailBox.getCurrentEmail() == null) {
                errors.noMailSelected();
            } else {
                BorderPane SendedReplyView = new BorderPane();
                FXMLLoader sendedReplyLoader = new FXMLLoader(getClass().getResource("/progetto_xml_replyAll.fxml"));
                SendedReplyView.setCenter(sendedReplyLoader.load());
                ReplyAllEmailController rec = sendedReplyLoader.getController();
                try {
                    rec.initializeModel(mailBox);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                Stage s = new Stage();
                s.setTitle("Reply to " + mailBox.getCurrentEmail().getSubject());
                s.setScene(new Scene(SendedReplyView));
                s.show();
            }

    }

    @FXML
    public void handleForward() throws IOException {
            if (mailBox.getCurrentEmail() == null) {
                errors.noMailSelected();
            } else {
                BorderPane forwardView = new BorderPane();
                FXMLLoader ForwardLoader = new FXMLLoader(getClass().getResource("/progetto_xml_forward.fxml"));
                forwardView.setCenter(ForwardLoader.load());
                ForwardController c = ForwardLoader.getController();

                try {
                    c.initializeModel(mailBox, received_listview);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                Stage s = new Stage();
                s.setTitle("ForwardEmail");
                s.setScene(new Scene(forwardView));
                s.show();
            }
    }

}
