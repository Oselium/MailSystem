package Client;

import Server.Email;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Networking {
    private Socket socket;
    private final int PORT = 8189;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final MailBox mailBox;

    public Networking(MailBox mailBox){
        this.mailBox = mailBox;
        socket = null;
        out = null;
        in = null;
        try {
            socket = new Socket("127.0.0.1", PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            //System.out.println("Connection error, check the server's status");
        }
    }

    private void close_streams() {
        try {
            if (socket != null)
                socket.close();
            if (out != null)
                out.close();
            if (in != null)
                in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String delete_mail(Email email) {
        String message = "";
        try {
            if(this.out != null) {
                out.writeObject("DELETE");
                out.writeObject(mailBox.getUsername());
                out.writeObject(email);
                message = (String) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close_streams();
        }
        return message;
    }

    public void send_new_mail(Email email){
        try{
            out.writeObject("CREATE");
            out.writeObject(mailBox.getUsername());
            out.writeObject(email);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            close_streams();
        }
    }

    public Boolean control_username(String username){
        Boolean trovato = false;
       try {
           out.writeObject("CONTROL_USERNAME");
           out.writeObject(username);
           out.writeObject(mailBox.getUsername());
           out.flush();
           trovato = (Boolean) in.readObject();
       }   catch (IOException | ClassNotFoundException e){
           e.printStackTrace();
       }
       finally {
           close_streams();
       }
       return trovato;
    }

    public Boolean user_login(String username, Errors errors){
        Boolean trovato = false;
        try {
            if(this.out != null){
                out.writeObject("USER_LOGIN");
                out.writeObject(username);
                out.writeObject(mailBox.getUsername());
                out.flush();
                trovato = (Boolean) in.readObject();
            }else{
                errors.connection();
            }
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        finally {
            close_streams();
        }
        return trovato;
    }

    public void user_logout(String username){
        try {
            if(this.out != null) {
                out.writeObject("USER_LOGOUT");
                out.writeObject(username);
                out.flush();
            }else{
                System.out.println("Client closed without logging out");
            }
        }   catch (IOException e){
            e.printStackTrace();
        }
        finally {
            close_streams();
        }
    }

    public void load_mail_received(int last_id) {
        try {
                out.writeObject("LOAD_MAIL_RECEIVED");
                out.writeObject(mailBox.getUsername());
                out.writeObject(last_id);
                out.flush();
                //System.out.println("Received mail incoming!");
                Integer controllo = (Integer) in.readObject();

                //1) OTTENIMENTO DELLA LISTA DI EMAIL RICEVUTE E INVIATE
                ArrayList<Email> to_add = new ArrayList<>();
                while (controllo-- != 0) {
                    Object obj = in.readObject();
                    if (obj instanceof Email) {
                        Email e = (Email) obj;
                        to_add.add(e);
                    }
                }
                mailBox.add_mails_received(to_add);
            }

        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        finally {
            close_streams();
        }
    }

    public void load_mail_sended(int last_id) throws IOException{
        try {
                out.writeObject("LOAD_MAIL_SENDED");
                out.writeObject(mailBox.getUsername());
                out.writeObject(last_id);
                out.flush();
                //System.out.println("Sent mail incoming!");
                Integer controllo = (Integer) in.readObject();

                //1) OTTENIMENTO DELLA LISTA DI EMAIL RICEVUTE E INVIATE
                ArrayList<Email> to_add = new ArrayList<>();
                while (controllo-- != 0) {
                    Object obj = in.readObject();
                    if (obj instanceof Email) {
                        Email e = (Email) obj;
                        to_add.add(e);
                    }
                }
                mailBox.add_mails_sended(to_add);
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        finally {
            close_streams();
        }
    }

    public String request_mail_id(){
        int id;
        try{
            out.writeObject("GET_ID");
            out.writeObject(mailBox.getUsername());
            id = (int) in.readObject();
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
            id = -1;
        }finally {
            close_streams();
        }
        String r =""+ id;
        return r;
    }
}
