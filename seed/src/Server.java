import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import node.*;
import object.Message;
import object.ReqType;
import object.SaveState;
import thread.*;
import utils.SaveRunnable;


public class Server {
    private int port;
    private ServerSocket serverSocket;
    private Node node;
    private NodeController nc;
    private StabilizeThread stabilizeThread;

    public Server(int port) throws IOException, InterruptedException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
        this.node = new Node(InetAddress.getLocalHost(), this.port);
        this.nc = new NodeController(node);

        // Restore previous state if any
        boolean isRestored = this.restoreStateIfNecessary();

        this.initializeThreads(isRestored);
        
        // Add continuous execution of save state
        Runnable saveRunnable = new SaveRunnable(this.node, this.nc);
        ScheduledThreadPoolExecutor schedExec = new ScheduledThreadPoolExecutor(1);
        schedExec.scheduleAtFixedRate(saveRunnable, 5, 5, TimeUnit.MINUTES);
        Runtime.getRuntime().addShutdownHook(new Thread(saveRunnable));
    }

    public void run() throws IOException {
        try {
            while (true) {
                Socket peerSocket = this.serverSocket.accept();

                new Thread(
                    new ForwardRequestThread(this.node, this.nc, peerSocket)
                ).start();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private boolean restoreStateIfNecessary() {
        SaveState prevState = null;
        String saveLoc = this.nc.getSaveLoc();
        File saveFile = new File(saveLoc);
        boolean isRestored = false;

        try {
            saveFile.createNewFile();

            FileInputStream myFileInputStream = new FileInputStream(saveFile);
            ObjectInputStream objInputStream = new ObjectInputStream(myFileInputStream);
            prevState = (SaveState) objInputStream.readObject(); 
            objInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (prevState != null && 
            this.node.getIP().equals(prevState.getIP()) &&
            this.node.getPort() == prevState.getPort()) {
            
            prevState.restorePreviousState(this.node);
            isRestored = true;
        }

        return isRestored;
    }

    private void initializeThreads(boolean isRestored) {
        try {
            if (!isRestored) {
                InitializeThread iThread = new InitializeThread(this.node);
                Thread t = new Thread(iThread);
                t.start();
                t.join();
            }
    
            // UserRequestThread uThread = new UserRequestThread();
            // Thread t1 = new Thread(uThread);
            // t.start();
    
            // this.stabilizeThread = new StabilizeThread(this.node, this.nc);
            // Thread t2 = new Thread(sThread);
            // t2.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int serverPort;
        
        if (args.length > 0) {
            serverPort = Integer.parseInt(args[0]);
        }
        else {
            BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter Port #: ");
            serverPort = Integer.parseInt(fromKeyboard.readLine());
            fromKeyboard.close();
        }
        new Server(serverPort).run();
    }

}
