package Client;

import Server.Email;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;

public class MailBox implements Serializable {

    private transient String username;
    private transient ObservableList<Email> maillist_sended;
    private transient ObservableList<Email> maillist_received;
    private int last_id_recv;
    private int last_id_snd;


    public ObservableList<Email> getMaillist_sended() {
        return maillist_sended;
    }

    public ObservableList<Email> getMaillist_received() {
        return maillist_received;
    }

    public void setMaillist_sended(ObservableList<Email> maillist_sended) {
        this.maillist_sended = maillist_sended;
    }

    public void setMaillist_received(ObservableList<Email> maillist_received) {
        this.maillist_received = maillist_received;
    }

    public MailBox(String username) {
        last_id_recv = -1;
        last_id_snd = -1;
        this.username=username;
        maillist_sended = FXCollections.observableArrayList();
        maillist_received = FXCollections.observableArrayList();
    }


    private final ObjectProperty<Email> currentEmail = new SimpleObjectProperty<>(null);

    public ObjectProperty<Email> currentEmailProperty() {
        return currentEmail ;
    }



    public final void setCurrentEmail(Email email) {
        currentEmailProperty().set(email);
    }
    public final Email getCurrentEmail() {
        return currentEmailProperty().get();
    }

    public void add_mails_received(ArrayList<Email> mails){
        if(mails.size() > 0) {
            last_id_recv = Integer.parseInt(mails.get(mails.size() - 1).getID());
            Platform.runLater(() -> {maillist_received.addAll(mails);});
        }
    }

    public void add_mails_sended(ArrayList<Email> mails){
        if(mails.size() > 0) {
            last_id_snd = Integer.parseInt(mails.get(mails.size() - 1).getID());
            maillist_sended.addAll(mails);
        }
    }

    public int get_last_id_snd(){
        return last_id_snd;
    }

    public int get_last_id_recv(){
        return last_id_recv;
    }

    public String getUsername() {
        return username;
    }

    //METODO PER ELIMINARE UNA EMAIL DALLE RICEVUTE
    public void deleteReceivedEmail(Email email){
        maillist_received.remove(email);
    }
    public void deleteSendedEmail(Email email){
        maillist_sended.remove(email);
    }

}
