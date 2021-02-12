package Server;

import com.google.gson.Gson;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hildan.fxgson.FxGson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class Email implements Serializable {
    private StringProperty ID = new SimpleStringProperty();

    private StringProperty sender = new SimpleStringProperty();

    private ListProperty<String> addressee = new SimpleListProperty<>();

    private StringProperty subject = new SimpleStringProperty();

    private StringProperty message = new SimpleStringProperty();

    private StringProperty date = new SimpleStringProperty();


    public Email(String ID, String sender, ObservableList<String> addressee, String subject, String message, String date) {
        this.ID.set(ID);
        this.sender.set(sender);
        this.addressee.set(addressee);
        this.subject.set(subject);
        this.message.set(message);
        this.date.set(date);
    }

    public String getID() {
        return ID.get();
    }

    public String getSender() {
        return sender.get();
    }

    public ObservableList<String> getAddressee() {
        return addressee.get();
    }

    public String getSubject() {
        return subject.get();
    }

    public String getMessage() {
        return message.get();
    }

    public String getDate() { return date.get(); }

    public void setID(String ID) {
        this.ID.set(ID);
    }

    public void setSender(String sender) {
        this.sender.set(sender);
    }

    public void setAddressee(ObservableList<String> addressee) {
        this.addressee.set(addressee);
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getStringAddressee(){
        String s = "";
        Iterator it = addressee.iterator();
        while(it.hasNext()){
            s = s + it.next().toString();
            if(it.hasNext()){s = s+" ,";}
        }

        return s;
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException {
        out.writeObject(getID());
        out.writeObject(getSender());
        writeListProp(out);
        out.writeObject(getSubject());
        out.writeObject(getMessage());
        out.writeObject(getDate());
    }

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        ID = new SimpleStringProperty((String) in.readObject());
        sender= new SimpleStringProperty((String) in.readObject());
        addressee= new SimpleListProperty<>(readListProp(in));
        subject= new SimpleStringProperty((String) in.readObject());
        message= new SimpleStringProperty((String) in.readObject());
        date = new SimpleStringProperty((String) in.readObject());
    }

    private void writeListProp(ObjectOutputStream s) throws IOException {

        s.writeInt(addressee.size());

        for(String elt:addressee.getValue()) s.writeObject(elt);
    }

    // Read a ListProperty from ObjectInputStream (and return it)
    private ListProperty<String> readListProp(ObjectInputStream s) throws IOException, ClassNotFoundException {
        ListProperty<String> lst=new SimpleListProperty<>(FXCollections.observableArrayList());
        int loop=s.readInt();
        for(int i = 0;i<loop;i++) {
            lst.add((String)s.readObject());
        }
        return lst;
    }
}
