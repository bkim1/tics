package thread;

import object.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import node.*;

public class ReceiveFileThread implements Runnable {
    private Message msg;
    private NodeController nc;
    private String downloadLoc;
    private FileInfo info;

    public ReceiveFileThread(Message msg, NodeController nc) {
        this.msg = msg;
        this.nc = nc;
        this.downloadLoc = this.nc.getDownloadLoc();
    }
    
    public void run() {
        // Use Socket (not ServerSocket) to open connection using 
        // Peer's info from msg
        try {
            FileInfo info = this.getFile();
            if (this.verify(info)) {

            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private FileInfo getFile() throws IOException {
        String absFileName = this.downloadLoc + "output-" + this.nc.getFilename(this.msg.getKey());
        File outputFile = new File(absFileName);
        outputFile.createNewFile();
        FileOutputStream writer = new FileOutputStream(outputFile);
        
        byte[] data = this.msg.getData();
        writer.write(data);

        System.out.println("Downloaded the file!");
        writer.flush();
        writer.close();
        
        // int numRetry = 0;
        this.info = this.nc.getLookupFileInfo(this.msg.getKey());
        // while (info == null && numRetry > 3) {
        //     Message retryMsg = new Message(ReqType.SEND_FAIL, this.nc.getPeerObject());
        // }
        return this.info;
    }

    private boolean verify(FileInfo info) {
        byte[] salt = info.getSalt();


        return false;
    }
}