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
    private StabilizeThread stabilizeThread;

    public ForwardRequestThread(Node node, NodeController nc, Socket socket, StabilizeThread stabilizeThread) {
        this.node = node;
        this.nc = nc;
        this.socket = socket;

        try {
            this.inputStream = this.socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stabilizeThread = stabilizeThread;
    }

    public void run() {
        try {
            Message msg = this.getMessage();
            
            Thread forwardThread = this.forwardMessage(msg);
            // Wait for thread to finish up their action before closing
            // the ForwardRequestThread
            if (forwardThread != null) {
                forwardThread.join();
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private Message getMessage() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(this.inputStream);
        Message msg = (Message) in.readObject();
        in.close();
        return msg;
    }

    private Thread forwardMessage(Message msg) throws IOException {
        Thread t;
        
        System.out.println("ForwardReq: Got " + msg.getReqType() + " request!\n");
        switch(msg.getReqType()) {
            case JOIN:
            case LOOKUP:
            case UPLOAD:
            case FILE_REQ:
            case FILE_RESP:
            case FILE_ACK:
                PeerRequestThread prThread = new PeerRequestThread(msg, this.node);
                t = new Thread(prThread);
                this.socket.close();
                break;
            case SEND:
                ReceiveFileThread rfThread = new ReceiveFileThread(msg, this.nc, this.socket);
                t = new Thread(rfThread);
                break;
            case STABILIZE_PRED_REQ:
            case STABILIZE_PRED_RESP:
            case STABILIZE_PRED_SET:
            case SUCCESSOR_REQ:
            case SUCCESSOR_RESP:
                this.stabilizeThread.executeMsg(msg);
                this.socket.close();
                return null;
            case SETUP:
                SetupRequestThread sThread = new SetupRequestThread(this.node, msg);
                t = new Thread(sThread);
                break;
            default:
                System.out.println("Unknown ReqType... Closing socket.");
                this.socket.close();
                return null;
        }
        t.start();

        return t;
    }
}