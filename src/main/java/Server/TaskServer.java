package Server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.hildan.fxgson.FxGson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TaskServer implements Runnable{
    private final Socket client;
    private final ModelServer model;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock wr;
    private final Lock rd;


    private final ArrayList<String> username_list = new ArrayList<>();
    private JsonArray json = null;
    private JsonParser parser = new JsonParser();
    private Iterator iterator = null;
    private JsonArray received_json = null;
    private JsonArray mail_json = null;
    private ArrayList<Email> email_received = new ArrayList<>();
    private ArrayList<Email> email_sended = new ArrayList<>();

    public TaskServer(Socket client, ModelServer modelServer) {
        this.client = client;
        model = modelServer;
        wr = rwl.writeLock();
        rd = rwl.readLock();
    }

    @Override
    public void run() {
        try (
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream())
        ) {
            String username;
            String command = (String) in.readObject();
            switch (command) {
                case "USER_LOGIN":
                    username = (String) in.readObject();
                    user_login(out, username);
                    out.writeObject("OK");
                    break;
                case "USER_LOGOUT":
                    username = (String) in.readObject();
                    user_logout(out, username);
                    out.writeObject("OK");
                    break;
                case "CONTROL_USERNAME":
                    username = (String) in.readObject();
                    control_username(out, username);
                    out.writeObject("OK");
                    break;
                case "LOAD_MAIL_RECEIVED":
                    username = (String) in.readObject();
                    Integer last_id = (Integer) in.readObject();
                    send_mail_received(out, username, last_id);
                    out.writeObject("OK");
                    break;
                case "LOAD_MAIL_SENDED":
                    username = (String) in.readObject();
                    Integer last_id_sent = (Integer) in.readObject();
                    send_mail_sended(out, username, last_id_sent);
                    out.writeObject("OK");
                    break;
                case "DELETE":
                    username = (String) in.readObject();
                    Email mail = (Email) in.readObject();
                    delete_email(mail);
                    out.writeObject("OK");
                    model.addLog("Mail ID "+mail.getID() + " deleted by " + "<" + username + ">");
                    break;
                case "CREATE":
                    username = (String) in.readObject();
                    Email new_mail = (Email) in.readObject();
                    create_new_email(new_mail);
                    out.writeObject("OK");
                    model.addLog("<" + username + ">" + " sent new mail");
                    break;
                case "GET_ID":
                    username = (String) in.readObject();
                    get_mail_id(out);
                    break;

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void user_login(ObjectOutputStream out, String username) throws IOException {
        try {
            rd.lock();
            json = (JsonArray) parser.parse(new FileReader("src/main/resources/username.json"));
            iterator = json.iterator();
            while (iterator.hasNext()) {
                username_list.add(iterator.next().toString());
            }
            rd.unlock();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //1) CONTROLLO USERNAME
        boolean trovato = false;
        for (String a : username_list) {
            if (a.equals("\"" + username + "\"")) {
                trovato = true;
                break;
            }
        }

        if (!trovato) {

            model.addUserLogged(username + " not found");
        } else {

            GregorianCalendar s = new GregorianCalendar();
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            fmt.setCalendar(s);
            String dateFormatted = fmt.format(s.getTime());
            model.addUserLogged("<" + username + "> logged in at "+dateFormatted);
            out.flush();
        }
        out.writeObject(trovato);
    }

    private void user_logout(ObjectOutputStream out, String username) throws IOException {

            //set the date and time
            GregorianCalendar s = new GregorianCalendar();
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            fmt.setCalendar(s);
            String dateFormatted = fmt.format(s.getTime());

            model.addUserLogged("<" + username + "> logged out at "+dateFormatted);
            out.flush();
    }

    public void control_username(ObjectOutputStream out, String username) throws IOException {
        try {
            rd.lock();
            json = (JsonArray) parser.parse(new FileReader("src/main/resources/username.json"));
            iterator = json.iterator();
            while (iterator.hasNext()) {
                username_list.add(iterator.next().toString());
            }
            rd.unlock();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //1) CONTROLLO USERNAME
        boolean trovato = false;
        for (String a : username_list) {
            if (a.equals("\"" + username + "\"")) {
                trovato = true;
                break;
            }
        }

        if (!trovato) {
            model.addLog(username + " not found");
        } else {
            out.flush();
        }
        out.writeObject(trovato);
    }

    public void send_mail_received(ObjectOutputStream out, String username, int last_id) throws IOException {

        //3) TRASFORMIAMO L'OGGETTO JSON CONTENENTE LE EMAIL IN UN OGGETTO EMAIL
        rd.lock();
        mail_json = (JsonArray) parser.parse(new FileReader("src/main/resources/mail_list.json"));
        Gson prova = FxGson.coreBuilder().create();
        Email[] mail_list = prova.fromJson(mail_json, Email[].class);
        rd.unlock();

        int i = 0;

        boolean newMail=false;
        //4) ESTRAPOLO LE EMAIL RICEVUTE e INVIATE DELL'UTENTE INTERESSATO
        if (mail_list != null) {
            for (Email email : mail_list) {
                if (email.getAddressee().contains(username) &&
                        Integer.parseInt(email.getID()) > last_id) {
                    email_received.add(email);
                    newMail=true;
                    i++;

                }
            }
            if(newMail){
                model.addLog("<" + username + ">" + " received new email.");
                newMail=false;
            }

        }
        out.writeObject(i);
        out.flush();

        //5) PROVIAMO A PASSARE LA LISTA di email ricevute
        for (Email e : email_received) {
            out.writeObject(e);
        }
        out.flush();
    }

    //METODO PER AGGIORNARE LE LISTE DI EMAIL
    public void send_mail_sended(ObjectOutputStream out, String username, int last_id) throws IOException {

        //3) TRASFORMIAMO L'OGGETTO JSON CONTENENTE LE EMAIL IN UN OGGETTO EMAIL
        rd.lock();
        mail_json = (JsonArray) parser.parse(new FileReader("src/main/resources/mail_list.json"));
        Gson prova = FxGson.coreBuilder().create();
        Email[] mail_list = prova.fromJson(mail_json, Email[].class);
        rd.unlock();

        int i = 0;

        //4) ESTRAPOLO LE EMAIL RICEVUTE e INVIATE DELL'UTENTE INTERESSATO
        if (mail_list != null) {
            for (Email email : mail_list) {
                if (email.getSender().contains(username) && Integer.parseInt(email.getID()) > last_id) {
                    email_sended.add(email);
                    i++;
                }
            }

        }
        out.writeObject(i);
        out.flush();

        //5) PROVIAMO A PASSARE LA LISTA di email ricevute
        for (Email e : email_sended) {
            out.writeObject(e);
        }
        out.flush();
    }

    public void delete_email(Email email_to_delete) throws IOException {

        rd.lock();
        received_json = (JsonArray) parser.parse(new FileReader("src/main/resources/mail_list.json"));
        Gson prova = FxGson.coreBuilder().create();
        Email[] mail_list = prova.fromJson(received_json, Email[].class);
        rd.unlock();

        ArrayList<Email> to_write = new ArrayList<>();
        Gson gson = FxGson.coreBuilder().setPrettyPrinting().create();
        FileWriter file = new FileWriter("src/main/resources/mail_list.json");
        //Boolean available = false;

            wr.lock();
            for (Email n : mail_list) {
                if (!n.getID().equals(email_to_delete.getID())) {
                    to_write.add(n);
                }
            }
            gson.toJson(to_write.toArray(), file);
            file.close();
            wr.unlock();
    }

    public  void create_new_email(Email new_email) throws IOException {

        rd.lock();
        mail_json = (JsonArray) parser.parse(new FileReader("src/main/resources/mail_list.json"));
        Gson prova = FxGson.coreBuilder().create();
        Email[] mail_list = prova.fromJson(mail_json, Email[].class);
        rd.unlock();

        ArrayList<Email> to_write = new ArrayList<>();
        Gson gson = FxGson.coreBuilder().setPrettyPrinting().create();
        FileWriter file = new FileWriter("src/main/resources/mail_list.json");

        //cambiato il for con l'addAll
        Collections.addAll(to_write, mail_list);
        wr.lock();
        to_write.add(new_email);
        gson.toJson(to_write.toArray(), file);
        file.close();
        wr.unlock();
    }

    public  void get_mail_id(ObjectOutputStream out) throws IOException {

        rd.lock();
        received_json = (JsonArray) parser.parse(new FileReader("src/main/resources/mail_list.json"));
        Gson prova = FxGson.coreBuilder().create();
        Email[] mail_list = prova.fromJson(received_json, Email[].class);
        rd.unlock();

        int mail_counter = 0;
        if (mail_list.length > 0)
            mail_counter = Integer.parseInt(mail_list[mail_list.length - 1].getID()) + 1;
        out.writeObject(mail_counter);
        out.flush();
    }
}
