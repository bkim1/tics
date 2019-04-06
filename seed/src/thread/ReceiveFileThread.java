package thread;

import object.*;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.time.LocalDateTime;

import node.NodeController;

public class ReceiveFileThread implements Runnable {
    private Message msg;
    private NodeController nc;
    private Peer peer;
    private Socket socket;
    private InputStream inputStream;

    public ReceiveFileThread(Message msg, NodeController nc) throws IOException {
        this.msg = msg;
        this.nc = nc;
        this.peer = msg.getPeer();
        this.socket = new Socket(this.peer.getIP(), this.peer.getPort());
        this.inputStream = this.socket.getInputStream();
    }
    
    public void run() {
        // Use Socket (not ServerSocket) to open connection using 
        // Peer's info from msg
        try {
            String filename = this.getFile();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private String getFile() throws IOException {
        String downloadLoc = this.nc.getDownloadLoc();
        String fileName = downloadLoc + LocalDateTime.now().toString() + "-output.txt";
        File outputFile = new File(fileName);
        outputFile.createNewFile();

        int dataSize;
        byte[] buffer = new byte[4096];
        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(outputFile));

        while ((dataSize = this.inputStream.read(buffer)) > 0) {
            writer.write(buffer, 0, dataSize);
        }

        System.out.println("Received the file!");
        writer.flush();
        writer.close();
        this.socket.close();

        return fileName;
    }

    private void verify() {
        
    }
}