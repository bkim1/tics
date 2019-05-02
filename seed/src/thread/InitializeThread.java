package thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import node.Node;
import object.Message;
import object.Peer;
import object.ReqType;
import utils.Utilities;

import static utils.Constants.*;
import static utils.Constants.RING_SIZE;


public class InitializeThread implements Runnable {
    private Node node;
    private Peer entryPeer;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ServerSocket servSocket;
    private int servPort;
    
    public InitializeThread(Node node) {
        this.node = node;

        try {
            this.socket = new Socket(ENTRY_ADDRESS, ENTRY_PORT);
            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Random r = new Random();
        this.servPort = r.nextInt((MAX_PORT - MIN_PORT) + 1) + MIN_PORT;
    }

    public void run() {
        System.out.println("Attempting to enter network");
        this.setupServerSocket();
        this.sendSetupMessage();
        Peer[] initTable = this.getInitialFingerTable();
        this.node.updateFingerTable(initTable, false);

        Utilities.adjustFingerTable(this.node, this.entryPeer);

        // Call Rocky's Utility function
        // Use Node's actual server address
        Utilities.generateFingerTable(this.node.getFingerTable(), this.node.getPeerObject());

        try {
            this.socket.close();
            this.servSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Initialize: DONE!");
    }

    private void sendSetupMessage() {
        Peer currentPeer = this.node.getPeerObject();
        currentPeer.setSetupPort(this.servPort);
        Message setupMsg = new Message(ReqType.SETUP, currentPeer);

        try {
            // Send SETUP request message to entry point
            System.out.println("Sending msg to Entry Point!");
            ObjectOutputStream objOutputStream = new ObjectOutputStream(this.outputStream);
            objOutputStream.flush();
            objOutputStream.writeObject(setupMsg);
            objOutputStream.flush();
            
            objOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Sets up the server socket to accept all incoming requests
     * specifically for the initialization process
    */
    private void setupServerSocket() {
        try {
            this.servSocket = new ServerSocket(this.servPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Peer[] getInitialFingerTable() {
        Peer[] initFingerTable = null;
        try {
            System.out.println("Initialize: Awaiting response...");
            Socket entrySocket = this.servSocket.accept();

            // Get SETUP_RESP message from entry point
            int numRetry = 0;
            Message setupResp = null;
            
            InputStream entryInputStream = entrySocket.getInputStream();
            while (entryInputStream.available() == 0) {  }
            ObjectInputStream objInputStream = new ObjectInputStream(entryInputStream);
            
            while (numRetry < 3) {
                setupResp = (Message) objInputStream.readObject();

                if (setupResp.getReqType() == ReqType.SETUP_RESP) { break; }
                
                // // Else resend message after waiting 100ms
                // Thread.sleep(100);
                // objOutputStream.flush();
                // objOutputStream.writeObject(setupMsg);
                // objOutputStream.flush();
                // numRetry++;
            }

            if (numRetry == 3) {
                System.out.println("Couldn't establish connection with entry point" +
                                   "... Please try again later");
                System.exit(0);
            }
            initFingerTable = setupResp.getUpdatedPeers();
            this.entryPeer = setupResp.getPeer();
            objInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return initFingerTable;
    }
}