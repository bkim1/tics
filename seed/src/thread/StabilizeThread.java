package thread;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import object.*;
import node.Node;
import utils.Utilities;

public class StabilizeThread implements Runnable {
    private Message msg;
    private Node node;

    public StabilizeThread(Node n) {
        this.node = n;
    }
    
    public void run() {
        System.out.println("Stabilize Thread starting...");
        stabilzeInit();
    }

    public void executeMsg(Message msg) {
        switch (msg.getReqType()) {
            case STABILIZE_PRED_REQ:
                handleStabilzeReq(msg);
                break;
            case STABILIZE_PRED_RESP:
                handleStabilizeResp(msg);
                break;
            case STABILIZE_PRED_SET:
                handleStabilzePredSet(msg);
                break;
            case STABILIZE_SUCC_SET:
                handleStabilzeSuccSet(msg);
                break;
            case SUCCESSOR_REQ:
                handleSuccReq(msg);
                break;
            case SUCCESSOR_RESP:
                handleSuccResp(msg);
                break;
            default:
                break;
        }
    }

    public void stabilzeInit() {
        Peer successor = this.node.getSuccessor();
        if (successor == null) { return; }
        Message msg = new Message(ReqType.STABILIZE_PRED_REQ, successor);    // request successor's listed predecessor
        try {
            InetAddress address = successor.getIP();
            int port = successor.getPort();
            Socket socket = new Socket(address, port);
            OutputStream os = socket.getOutputStream(); 
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.flush();
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    private void handleStabilzeReq(Message msg) {
        Peer sender = msg.getPeer();
        if (sender == null) { return; }
        Peer pred = this.node.getPredecessor();
        Peer myPeer = this.node.getPeerObject();
        if (pred == null) {
            this.node.setPredecessor(sender);
            pred = sender;
        }
        else if (myPeer.getKey() < pred.getKey() && myPeer.getKey() < sender.getKey() &&
            pred.getKey() - myPeer.getKey() > sender.getKey() - myPeer.getKey()) {
                this.node.setPredecessor(sender);
        }
        else if (sender.getKey() > pred.getKey()) {  // sender is closer to node than the listed predecessor
            this.node.setPredecessor(sender);
        }
        Message resp = new Message(ReqType.STABILIZE_PRED_RESP, pred);
        try {
            InetAddress address = sender.getIP();
            int port = sender.getPort();
            Socket socket = new Socket(address, port);
            OutputStream os = socket.getOutputStream(); 
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.flush();
            oos.writeObject(resp);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    private void handleStabilizeResp(Message resp) {
        Peer successorPred = resp.getPeer();
        Peer myPeer = this.node.getPeerObject();
        if (successorPred.getKey() > myPeer.getKey()) {     // successor's listed predecessor is btw this node and successor
            this.node.setSuccessor(successorPred);      // set successor's new predecessor as node's new successor
            Message notify = new Message(ReqType.STABILIZE_PRED_SET, myPeer);  // notify succcessor's predecessor to update its predecessor to this node
            try {
                InetAddress address = successorPred.getIP();
                int port = successorPred.getPort();
                Socket socket = new Socket(address, port);
                OutputStream os = socket.getOutputStream(); 
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.flush();
                oos.writeObject(notify);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        else if (successorPred.getKey() < myPeer.getKey()) {
            if (this.node.getPredecessor() == null) {
                this.node.setPredecessor(successorPred);
            }
            else if (this.node.getPredecessor().getKey() < successorPred.getKey()) {
                this.node.setPredecessor(successorPred);
            }
            Message notify = new Message(ReqType.STABILIZE_SUCC_SET, myPeer);  // notify succcessor's predecessor to update its predecessor to this node
            try {
                InetAddress address = successorPred.getIP();
                int port = successorPred.getPort();
                Socket socket = new Socket(address, port);
                OutputStream os = socket.getOutputStream(); 
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.flush();
                oos.writeObject(notify);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    private void handleStabilzePredSet(Message msg) {
        Peer newPred = msg.getPeer();
        Peer pred = this.node.getPredecessor();
        if (pred == null) {
            this.node.setPredecessor(newPred);
        }
        else if (newPred.getKey() > pred.getKey()) {
            this.node.setPredecessor(newPred);
        }
        return;
    }

    private void handleStabilzeSuccSet(Message msg) {
        Peer newSuc = msg.getPeer();
        Peer suc = this.node.getSuccessor();
        if (suc == null) {
            this.node.setSuccessor(newSuc);
        }
        else if (newSuc.getKey() < suc.getKey()) {
            this.node.setSuccessor(newSuc);
        }
        return;
    }

    public void fixFingerInit() {
        Peer[] fingerTable = this.node.getFingerTable();
        if (fingerTable == null) {
            return;
        }
        else if (fingerTable[0] == null) {
            return;
        }
        Random rand = new Random();
        int i = rand.nextInt(fingerTable.length-1); // pick any but the last b/c checking their successor
        Peer finger = fingerTable[i];
        Message req = new Message(ReqType.SUCCESSOR_REQ, this.node.getPeerObject());
        //req.setFingerIndex(i);
        try {
            InetAddress address = finger.getIP();
            int port = finger.getPort();
            Socket socket = new Socket(address, port);
            OutputStream os = socket.getOutputStream(); 
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.flush();
            oos.writeObject(req);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    private void handleSuccReq(Message msg) {
        Peer sender = msg.getPeer();
        Peer successor = this.node.getSuccessor();
        Message resp = new Message(ReqType.SUCCESSOR_RESP, successor);
        //resp.setFinger(this.node.getPeerObject());
        //resp.setFingerIndex(msg.getFingerIndex());
        try {
            InetAddress address = sender.getIP();
            int port = sender.getPort();
            Socket socket = new Socket(address, port);
            OutputStream os = socket.getOutputStream(); 
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.flush();
            oos.writeObject(resp);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    private void handleSuccResp(Message msg) {
        Peer fingerSuccessor = msg.getPeer();
        Utilities.adjustFingerTable(this.node, fingerSuccessor);
        return;
    }
}


