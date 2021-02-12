package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadSocketServer extends Thread implements Runnable {

    private Socket socket = null;
    private final ModelServer modelServer;
    ExecutorService exec = Executors.newFixedThreadPool(3);
    boolean control = true;
    ServerSocket s = null;

    public ThreadSocketServer(ModelServer modelServer) {
        this.modelServer = modelServer;
    }

    @Override
    public void run() {
        try {
            s = new ServerSocket(8189);
            // establish server socket
            while(true) {
                try{
                    socket = s.accept();
                    exec.execute(new TaskServer(socket, modelServer));
                }catch (Exception e){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void terminate() throws IOException {
        this.exec.shutdown();
        s.close();
        control = false;
    }

    //IMPLEMENTARE IL CLOSE X
}
