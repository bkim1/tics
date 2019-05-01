package thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import node.Node;
import object.Message;
import object.Peer;
import object.ReqType;

import static utils.Constants.ENTRY_ADDRESS;
import static utils.Constants.ENTRY_PORT;


public class InitializeThread implements Runnable {
    private Node node;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    
    public InitializeThread(Node node) {
        this.node = node;

        try {
            this.socket = new Socket(ENTRY_ADDRESS, ENTRY_PORT);
            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Attempting to enter network");
        Peer[] initTable = this.getInitialFingerTable();

        this.node.updateFingerTable(initTable);
        this.node.printFingerTable();

        // Call Rocky's Utility function

        // Use newly updated fingerTable to send JOIN msg to affected range
    }

    private Peer[] getInitialFingerTable() {
        Peer[] initFingerTable = null;
        Message setupMsg = new Message(ReqType.SETUP, this.node.getPeerObject());

        try {
            // Send SETUP request message to entry point
            ObjectOutputStream objOutputStream = new ObjectOutputStream(this.outputStream);
            objOutputStream.flush();
            objOutputStream.writeObject(setupMsg);
            objOutputStream.flush();

            // Get SETUP_RESP message from entry point
            int numRetry = 0;
            Message setupResp = null;
            ObjectInputStream objInputStream = new ObjectInputStream(this.inputStream);

            while (numRetry < 3) {
                setupResp = (Message) objInputStream.readObject();

                if (setupResp.getReqType() == ReqType.SETUP_RESP) { break; }
                
                // Else resend message after waiting 100ms
                Thread.sleep(100);
                objOutputStream.flush();
                objOutputStream.writeObject(setupMsg);
                objOutputStream.flush();
                numRetry++;
            }

            if (numRetry == 3) {
                System.out.println("Couldn't establish connection with entry point" +
                                   "... Please try again later");
                System.exit(0);
            }
            initFingerTable = setupResp.getUpdatedPeers();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }

        return initFingerTable;
    }
}