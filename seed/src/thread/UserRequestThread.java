package thread;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import node.NodeController;
import object.Message;
import object.*;
import utils.Utilities;



public class UserRequestThread implements Runnable {
    private Message msg;
    private NodeController nc;
    private Peer peer;

    public UserRequestThread(Message msg, NodeController nc) {
        this.msg = msg;
        this.nc = nc;
        this.peer = msg.getPeer();
    }
    
    public void run() {
        switch(msg.getReqType()) {
            case LOOKUP:
                lookUp();
                break;
            }
    }

    public void upload(){ //using lookup method, forward along appropriately
        System.out.println("Would");
    }

}

//upload
//leaveNetwork
//initiializeLookupRequest