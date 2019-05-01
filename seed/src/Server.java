import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import node.*;
import object.Message;
import object.ReqType;
import thread.*;


public class Server {
    private int port;
    private ServerSocket serverSocket;
    private Node node;
    private NodeController nc;
    private StabilizeThread stabilizeThread;

    public Server(int port) throws IOException, InterruptedException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
        this.node = new Node(InetAddress.getLocalHost(), this.port);
        this.nc = new NodeController(node);

        InitializeThread iThread = new InitializeThread(this.node);
        Thread t = new Thread(iThread);
        t.start();
        t.join();

        // UserRequestThread uThread = new UserRequestThread();
        // t = new Thread(uThread);
        // t.start();

        // this.stabilizeThread = new StabilizeThread(this.node, this.nc);
        // t = new Thread(sThread);
    }

    public void run() throws IOException {
        try {
            while (true) {
                Socket peerSocket = this.serverSocket.accept();

                Message msg = this.getMessage(peerSocket);
                this.forwardMessage(msg, peerSocket);
            }
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private Message getMessage(Socket peerSocket) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(peerSocket.getInputStream());
        Message msg = (Message) in.readObject();
        in.close();
        return msg;
    }

    private void forwardMessage(Message msg, Socket peerSocket) throws IOException {
        Thread t;
        
        switch(msg.getReqType()) {
            case JOIN: case LOOKUP: case UPLOAD:
                PeerRequestThread prThread = new PeerRequestThread(msg, this.node);
                t = new Thread(prThread);
                peerSocket.close();
                break;
            case SEND:
                ReceiveFileThread rfThread = new ReceiveFileThread(msg, this.nc, peerSocket);
                t = new Thread(rfThread);
                break;
            case STABILIZE: case STABILIZE_PRED_RESP: case STABILIZE_PRED_REQ:
                // this.stabilizeThread.request(msg);
                t = new Thread();
                peerSocket.close();
                break;
            case SETUP:
                SetupRequestThread sThread = new SetupRequestThread(this.node, peerSocket);
                t = new Thread(sThread);
                break;
            default:
                System.out.println("Unknown ReqType... Closing socket.");
                peerSocket.close();
                return;
        }
        t.start();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int serverPort;
        
        if (args.length > 0) {
            serverPort = Integer.parseInt(args[0]);
        }
        else {
            BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter Port #: ");
            serverPort = Integer.parseInt(fromKeyboard.readLine());
            fromKeyboard.close();
        }
        new Server(serverPort).run();
    }

}
