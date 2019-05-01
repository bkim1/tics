package thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import node.Node;
import object.Message;
import object.Peer;
import object.ReqType;

import static utils.Constants.MIN_PORT;
import static utils.Constants.MAX_PORT;
import static utils.Constants.ENTRY_ADDRESS;
import static utils.Constants.ENTRY_PORT;

public class InitializeThread implements Runnable {
    private Node node;
    private int port;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    

    public InitializeThread(Node node) {
        this.node = node;
        Random random = new Random();
        this.port = random.nextInt((MAX_PORT - MIN_PORT) + 1) + MIN_PORT;

        try {
            this.socket = new Socket(ENTRY_ADDRESS, ENTRY_PORT);
            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Attempting to enter network");
        Peer[] initTable = this.getInitialFingerTable();

    }

    private Peer[] getInitialFingerTable() {
        // USE SETUP REQTYPE
        Message setupMsg = new Message(ReqType.SETUP, this.node.getPeerObject());


        return new Peer[5];
    }
}