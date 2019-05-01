package thread;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import node.Node;
import object.Message;
import object.ReqType;


public class SetupRequestThread implements Runnable {
    private Node node;
    private Socket socket;
    private OutputStream outputStream;
    

    public SetupRequestThread(Node node, Socket socket) {
        this.node = node;
        this.socket = socket;

        try {
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
            
            System.out.println("Sent finger table... Shutting down.");
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}