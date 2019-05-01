package thread;
import java.util.Random;
import object.*;
import node.NodeController;

public class StabilizeThread implements Runnable {
    private Message msg;
    private NodeController nc;

    public StabilizeThread(NodeController nc) {
        this.nc = nc;
    }
    
    public void run() {
        console.log("Stabilize Thread starting...");
    }

    public void executeMsg(Message msg) {
        switch (msg.getReqType()) {
            case STABILIZE:
                stabilzeInit();
                break;
            case STABILIZE_PRED_RESP:
                handleStabilizeResp(msg);
                break;
            case STABILIZE_PRED_REQ:
                handleStabilzeReq(msg);
            default:
                break;
        }
    }

    private void stabilzeInit() {
        Peer suc = this.nc.getSuccessor();
        Message msg = new Message("STABILIZE_PRED_REQ", suc);    // request successor's listed predecessor
        // Send(msg to suc)
        // Wait for response
    }

    private void handleStabilizeResp(Message resp) {
        //Message resp = new Message("STABILIZE_PRED_RESP", suc); // this should actually come in as this.msg
        Peer sucPred = resp.getPeer();
        if (sucPred.getKey() > this.nc.getKey()) {     // successor's listed predecessor is btw this node and successor
            this.nc.setSuccessor(sucPred);      // set successor's new predecessor as node's new successor
            Message notify = new Message("STABILIZE_PRED_SET", this.nc.getPeerObject());  // notify sucPred to update its predecessor to this node
            // Send(notify to sucPred)
        }
    }

    private void handleStabilzeReq(Message msg) {
        Peer sender = msg.getPeer();
        Peer pred = this.nc.getPredecessor();
        if (!pred || sender.getKey() > pred.getKey()) {  // sender is closer to node than the listed predecessor
            this.nc.setPredecessor(sender);     // set sender as node's new predecessor
        }
        Message resp = new Message("STABILIZE_PRED_RESP", pred);
        // Send(resp to sender)
    }

    private void fixFingers() {
        List<Peer> fingerTable = this.nc.getFingerTable();
        Random rand = new Random();
        int i = rand.nextInt(fingerTable.size()-1) + 1;  // +1 b/c don't want to pick 0 which is successor
        Peer finger = fingerTable.get(i);
        finger.node = findSuccessor(finger.start);
        fingerTable.set(i, finger);
        this.nc.updateFingerTable(fingerTable);
    }
}


