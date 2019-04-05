package thread;

import object.*;
import node.NodeController;

public class ReceiveFileThread implements Runnable {
    private Message msg;
    private NodeController nc;

    public ReceiveFileThread(Message msg, NodeController nc) {
        this.msg = msg;
        this.nc = nc;
    }
    
    public void run() {
        // NodeController should have a download location to get
        // NodeController should also be noting which files that you are trying to get
        // Know what the file name is through NC?
        // String downloadLoc = nc.getDownloadLoc();
        String downloadLoc = "Users/bkim/Documents"; // temp loc
        String filename = "test.txt"; // temp name

        // Use Socket (not ServerSocket) to open connection using 
        // Peer's info from msg

    }
}