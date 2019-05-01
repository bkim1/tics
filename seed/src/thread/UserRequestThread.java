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
        System.out.println("Enter 1 for file upload, 2 for file retrieval, 3 for changing default directory, and 4 for Leaving Network");
        int UsrIn;
        UsrIn = input.nextInt();
        int userChoice = UsrIn;
        switch (userChoice) {
            case 1: ;
            break;
            case 2: ;
            break;
            case 3: ;
            break;
            case 4: ;
            break;

        }

		switch(msg.getReqType()) {
		case LOOKUP:
			//initializeLookupRequest();
            break;
        case UPLOAD:
            upload();
            break;
		}
	}

    public void upload(){ //using lookup method, forward along appropriately
        System.out.println("");
    }

    public void initializeLookupRequest(Long key){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the name of the file you wish to retrieve");
        String fileName = scan.nextLine();
        //check if it is in myFiles

    }//lookup with getmyfiles

}

//upload
//initiializeLookupRequest
//later: change default download directory
//later: leaveNetwork
