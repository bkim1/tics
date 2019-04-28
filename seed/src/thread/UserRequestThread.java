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
import java.util.Scanner;



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
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter 1 for file upload, 2 for file retrieval");
        Int answer = scan.nextInt();
        if (answer == 1){
            upload();
        }
        else{
            initializeLookupRequest();
        }
		switch(msg.getReqType()) {
		case LOOKUP:
			initializeLookupRequest();
            break;
        case UPLOAD:
            upload();
            break;
		}
	}

    public void upload(){ //using lookup method, forward along appropriately
        System.out.println("");
    }

    public void initializeLookupRequest(){

    }

}

//upload
//initiializeLookupRequest
//later: change default download directory
//later: leaveNetwork
