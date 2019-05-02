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

    public Server(int port, boolean isFirst) throws IOException, InterruptedException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
        this.node = new Node(InetAddress.getLocalHost(), this.port);
        this.nc = new NodeController(node);

        // Restore previous state if any and initialize threads
        // boolean isRestored = this.restoreStateIfNecessary();
        this.initializeThreads(false, isFirst);
        
        // Add continuous execution of save state
        Runnable saveRunnable = new SaveRunnable(this.node, this.nc);
        ScheduledThreadPoolExecutor saveSchedExec = new ScheduledThreadPoolExecutor(1);
        saveSchedExec.scheduleAtFixedRate(saveRunnable, 5, 5, TimeUnit.MINUTES);
        Runtime.getRuntime().addShutdownHook(new Thread(saveRunnable));

        // Add continuous execution of Stabilization protocol
        Runnable stabilizeRunnable = new StabilizeThread(this.node);
        ScheduledThreadPoolExecutor stabilizeSchedExec = new ScheduledThreadPoolExecutor(1);
        stabilizeSchedExec.scheduleAtFixedRate(stabilizeRunnable, 0, 1, TimeUnit.MINUTES);
    }

    public void run() throws IOException {
        try {
            while (true) {
                Socket socket = this.serverSocket.accept();

                new Thread(
                    new ForwardRequestThread(
                        this.node, this.nc, socket,
                        this.stabilizeThread
                    )
                ).start();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Restores the previous user state if necessary based on if the
     * address (IP/Port) of the user is the same as before. 
     */
    private boolean restoreStateIfNecessary() {
        SaveState prevState = null;
        String saveLoc = this.nc.getSaveStateLoc();
        File saveFile = new File(saveLoc);
        boolean isRestored = false;

        try {
            saveFile.createNewFile();

            FileInputStream fileInputStream = new FileInputStream(saveFile);
            // Empty file
            if (fileInputStream.available() == 0) { 
                fileInputStream.close();
                return false; 
            }
            
            ObjectInputStream objInputStream = new ObjectInputStream(fileInputStream);

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

    private void initializeThreads(boolean isRestored, boolean isFirst) {
        try {
            if (!isRestored && !isFirst) {
                InitializeThread iThread = new InitializeThread(this.node);
                Thread t = new Thread(iThread);
                t.start();
                t.join();
            }
    
            UserRequestThread uThread = new UserRequestThread(this.nc);
            Thread t1 = new Thread(uThread);
            t1.start();
    
            this.stabilizeThread = new StabilizeThread(this.node);
            Thread t2 = new Thread(this.stabilizeThread);
            t2.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int serverPort = 0;
        boolean isFirst = false;
        
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("first") || args[i].equals("f")) {
                    isFirst = true;
                    serverPort = 10000;
                }
                else if (args[i].equals("port") || args[i].equals("p")) {
                    serverPort = Integer.parseInt(args[i + 1]);
                    i++;
                }
            }
        }
        else if (serverPort == 0) {
            BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter Port #: ");
            serverPort = Integer.parseInt(fromKeyboard.readLine());
            // fromKeyboard.close();
        }
        new Server(serverPort, isFirst).run();
    }

}
