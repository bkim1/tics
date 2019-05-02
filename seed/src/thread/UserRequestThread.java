package thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.Scanner;

import node.*;
import object.*;
import utils.Utilities;

public class UserRequestThread implements Runnable {
    private NodeController nc;
    private BufferedReader input;

    public UserRequestThread(NodeController nc) {
        this.nc = nc;
    }
    
	public void run() {
        this.input = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Enter 1 for file upload, 2 for file retrieval, 3 for changing default directory, and 4 for Leaving Network");
            try {
                int UsrIn = Integer.parseInt(this.input.readLine());
                
                switch (UsrIn) {
                    case 1: upload();
                    break;
                    case 2: initializeLookupRequest();
                    break;
                    case 3: changeDefaultDownloadDirectory();
                    break;
                    // case 4: leaveNetwork();
                    // break;
                    default: System.out.println("Please enter a valid input, an int between 1 and 4");
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid input, an int between 1 and 4");
            }
        }
	}

    public void upload(){ //using lookup method, forward along appropriately
        System.out.println("Enter the full local address of the file you wish to upload");
        try {
            String newFileLocation = this.input.readLine();

            int index = newFileLocation.lastIndexOf("/");
            if (index == newFileLocation.length()-1) {
                index = newFileLocation.lastIndexOf("/", index);
            }

            String newFileName = newFileLocation.substring(index + 1);
            FileInfo newFileInfo = new FileInfo(newFileLocation, newFileName); //register file info in nodecontroller
            Peer peer = this.nc.getPeerObject();
            Message msg = new Message(ReqType.UPLOAD, peer, newFileInfo.getKey());
            
            // Get byte[] of file being requested
        
            File file = new File(newFileLocation);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];

            fileInputStream.read(data);
            msg.setData(data);
            fileInputStream.close();

            Utilities.lookUp(msg, this.nc.getFingerTable(), this.nc.getPeerId());
            this.nc.registerFileInfo(newFileInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void initializeLookupRequest(){
        System.out.println("Enter the name of the file you wish to retrieve");
        try {
            String requestedFileName = this.input.readLine();
            Peer peer = this.nc.getPeerObject();
            FileInfo requestFileInfo = this.nc.getRegisteredFileInfo(requestedFileName); //check if obj is null, bc then lookup can't proceed
            if (requestFileInfo == null) {
                System.out.println("Not a registered file. Please try again");
                return;
            }
            Message msg = new Message(ReqType.LOOKUP, peer, requestFileInfo.getKey());
            this.nc.registerLookup(requestFileInfo.getKey(), requestFileInfo);
            Utilities.lookUp(msg, this.nc.getFingerTable(), this.nc.getPeerId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeDefaultDownloadDirectory(){
        this.nc.setDownloadLoc();
    }

    // public void leaveNetwork(){

    // }
}