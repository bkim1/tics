package thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import node.Node;
import node.NodeController;
import object.Message;

/*
 * ForwardRequestThread handles forwarding all incoming requests
 * by spawning up the relevant threads or making the correct
 * call to an already running thread (i.e. for stabilization).
 * Abstracted out so that the main server is not blocked
 * by a large file download.
 */ 
public class ForwardRequestThread implements Runnable {
    private Node node;
    private NodeController nc;
    private Socket socket;
    private InputStream inputStream;

    public ForwardRequestThread(Node node, NodeController nc, Socket socket) {
        this.node = node;
        this.nc = nc;
        this.socket = socket;

        try {
            this.inputStream = this.socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            Message msg = this.getMessage();
            this.forwardMessage(msg);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private Message getMessage() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(this.inputStream);
        Message msg = (Message) in.readObject();
        in.close();
        return msg;
    }

    private void forwardMessage(Message msg) throws IOException {
        Thread t;
        
        switch(msg.getReqType()) {
            case JOIN: case LOOKUP: case UPLOAD:
                PeerRequestThread prThread = new PeerRequestThread(msg, this.node);
                t = new Thread(prThread);
                this.socket.close();
                break;
            case SEND:
                ReceiveFileThread rfThread = new ReceiveFileThread(msg, this.nc, this.socket);
                t = new Thread(rfThread);
                break;
            case STABILIZE: case STABILIZE_PRED_RESP: case STABILIZE_PRED_REQ:
                // this.stabilizeThread.request(msg);
                t = new Thread();
                this.socket.close();
                break;
            case SETUP:
                SetupRequestThread sThread = new SetupRequestThread(this.node, this.socket);
                t = new Thread(sThread);
                break;
            default:
                System.out.println("Unknown ReqType... Closing socket.");
                this.socket.close();
                return;
        }
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}