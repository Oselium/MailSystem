package Server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.Serializable;

/*- Il mail server ha un’interfaccia grafica sulla quale viene visualizzato il log
delle azioni effettuate dai mail clients e degli eventi che occorrono durante
l’interazione tra i client e il server.
o Per esempio: apertura/chiusura di una connessione tra mail client e
server, invio di messaggi da parte di un client, ricezione di messaggi
da parte di un client, errori nella consegna di messaggi.*/
public class ModelServer implements Serializable {
    ObservableList<String> logs;
    ObservableList<String> users_logged;

    public ModelServer(){
        logs = FXCollections.observableArrayList();
        users_logged = FXCollections.observableArrayList();
    }

    public ObservableList<String> getLogs() {
        return logs;
    }

    public void setLogs(ObservableList<String> logs) {
        this.logs = logs;
    }

    public ObservableList<String> getUsers_logged() { return users_logged;  }

    public void setUsers_logged(ObservableList<String> users_logged) { this.users_logged = users_logged; }

    public void addUserLogged(String user){
        users_logged.add(user);
    }
    public void addLog(String log){
        logs.add(log);
    }

}

