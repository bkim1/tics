package thread;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.net.Socket;
import java.util.List;
import node.*;
import object.*;
import utils.Utilities;
import java.util.Scanner;



public class UserRequestThread implements Runnable {
    private Message msg;
    private NodeController nc;
    private Peer peer;
    private Node node;

    public UserRequestThread(Message msg, NodeController nc, Node n) {
        this.msg = msg;
        this.nc = nc;
        this.peer = msg.getPeer();
        this.node = n;
    }
    
	public void run() {
        System.out.println("Enter 1 for file upload, 2 for file retrieval, 3 for changing default directory, and 4 for Leaving Network");
        Scanner userScan = new Scanner(System.in);
        int UsrIn = userScan.nextInt();
        switch (UsrIn) {
            case 1: upload();
            break;
            // case 2: initializeLookupRequest();
            // break;
            case 3: changeDefaultDownloadDirectory();
            break;
            case 4: leaveNetwork();
            break;
            default: System.out.println("Please enter a valid input, an int between 1 and 4");
            break;
        }
	}

    public void upload(){ //using lookup method, forward along appropriately
        System.out.println("Enter the full local address of the file you wish to upload");
        Scanner uploadScan = new Scanner(System.in);
        String newFileLocation = uploadScan.nextLine();
        int index = newFileLocation.lastIndexOf("/");
        if (index == newFileLocation.length()-1) {
            index = newFileLocation.lastIndexOf("/", index);
        }
        String newFileName = newFileLocation.substring(index + 1);
        //generate a key for the file using the newFileName
        FileInfo newFileInfo = new FileInfo(newFileLocation, newFileName);
        Peer peer = this.nc.getPeerObject();
        Message msg = new Message(ReqType.UPLOAD, peer, newFileInfo.getKey());
        Utilities.lookUp(msg, this.node.getFingerTable());
    }

    // public void initializeLookupRequest(){
    //     System.out.println("Enter the name of the file you wish to retrieve");
    //     String requestedFileName;
    //     requestedFileName = input.nextLine();
    //     String userChoice = requestedFileName;
    //     //check if it is in myFiles

    // }
    //lookup with getmyfiles

    public void changeDefaultDownloadDirectory(){

    }

    public void leaveNetwork(){

    }

}

//upload
//initiializeLookupRequest
//later: change default download directory
//later: leaveNetwork
