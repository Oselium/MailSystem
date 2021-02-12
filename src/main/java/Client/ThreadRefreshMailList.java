package Client;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.IOException;

public class ThreadRefreshMailList extends Thread {

    private MailBox mailbox;
    private Errors errors;
    private Label serverstatus;
    private boolean checkOpen;
    private Networking net;

    public ThreadRefreshMailList(MailBox mailbox, Errors errors, Label serverstatus) {
        this.mailbox = mailbox;
        this.errors = errors;
        this.serverstatus = serverstatus;
        checkOpen = false;
    }

    @Override
    public void run() {
        boolean logged_in = true;

        while(true) {
            try {
                //apriamo richiesta al server
                net = new Networking(mailbox);
                if(!logged_in){
                    boolean loaded = net.user_login(mailbox.getUsername(), errors);

                    //il server è tornato online
                    if(loaded){
                        net = new Networking(mailbox);
                        load(net);
                        logged_in = true;
                        checkOpen = false;
                        Platform.runLater(() ->  serverstatus.setText("online"));
                    }else{
                        //System.out.println("Internal error");
                        logged_in = false;
                    }

                }else{
                    Platform.runLater(() -> serverstatus.setText("online"));
                    load(net);
                }
            } catch (Exception e) {
                logged_in = false;
                Platform.runLater(() ->  serverstatus.setText("offline"));
                if(!checkOpen) {
                    error();
                    checkOpen = true;
                }
            }
            try{
                Thread.sleep(2000);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void load(Networking net) throws IOException{
        net.load_mail_sended(mailbox.get_last_id_snd());
        net = new Networking(mailbox);
        net.load_mail_received(mailbox.get_last_id_recv());
    }

    private void error(){
        Platform.runLater(() -> {
            try{
                errors.connection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
