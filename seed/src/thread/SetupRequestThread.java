package thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import node.Node;
import object.Message;
import object.Peer;
import object.ReqType;
import utils.Utilities;


public class SetupRequestThread implements Runnable {
    private Node node;
    private Peer peer;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    

    public SetupRequestThread(Node node, Message msg) {
        this.node = node;
        this.peer = msg.getPeer();
        System.out.println(this.peer);
        System.out.println(this.node);
        try {
            this.socket = new Socket(this.peer.getIP(), this.peer.getPort());
            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // Send current finger table to connected peer
        System.out.println("Got SETUP request... Attempting to send finger table");
        Message msg = new Message(
            ReqType.SETUP_RESP,
            this.node.getPeerObject(),
            this.node.getFingerTable()
        );

        try {
            // Send message with current finger table
            ObjectOutputStream objOutputStream = new ObjectOutputStream(this.outputStream);
            objOutputStream.flush();
            objOutputStream.writeObject(msg);
            objOutputStream.flush();

            // Adjust finger table of current node if necessary
            Utilities.adjustFingerTable(this.node, this.peer);
            
            System.out.println("Sent finger table... Shutting down thread.");
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}