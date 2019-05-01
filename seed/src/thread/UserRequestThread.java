package thread;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.net.Socket;
import java.util.List;
import node.NodeController;
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
            case 1: upload();
            break;
            case 2: initializeLookupRequest(key);
            break;
            case 3: changeDefaultDownloadDirectory();
            break;
            case 4: leaveNetwork();
            break;
            
            default: System.out.println("Please enter a valid input, an int between 1 and 4");
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
        System.out.println("Enter the full local address of the file you wish to upload");
        String newFileLocation;
        newFileLocation = input.nextLine();
        String userChoice = newFileLocation;
        int index = newFileLocation.lastIndexOf("/");
        if (index == newFileLocation.length()-1) {
            index = newFileLocation.lastIndexOf("/", index);
        }
        String newFileName = newFileLocation.substring(index + 1);

        String fileContents = getContents(userChoice);
        new FileInfo(fileLoc, filename)

    }

    public void initializeLookupRequest(Long key){
        System.out.println("Enter the name of the file you wish to retrieve");
        String requestedFileName;
        requestedFileName = input.nextLine();
        String userChoice = requestedFileName;
        //check if it is in myFiles

    }
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
