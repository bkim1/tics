import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import node.*;
import object.Message;
import object.ReqType;
import thread.ReceiveFileThread;
import thread.StabilizeThread;


public class Server {
    private int port;
    private ServerSocket serverSocket;
    private Node node;
    private NodeController nc;

    public Server(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
        this.node = new Node(InetAddress.getLocalHost(), this.port);
        this.nc = new NodeController(node);
    }

    public void run() throws UnknownHostException, IOException {
        // Startup the UserRequestThread to handle user input
        Message msg;
        try {
            while (true) {
                Socket peerSocket = this.serverSocket.accept();

                msg = this.getMessage(peerSocket);
                this.processMessage(msg, peerSocket);
            }
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private Message getMessage(Socket peerSocket) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(peerSocket.getInputStream());
        return (Message) in.readObject();
    }

    private void processMessage(Message msg, Socket peerSocket) throws IOException {
        Thread t;
        switch(msg.getReqType()) {
            case JOIN:
                // PeerRequestThread prThread = new PeerRequestThread(msg, this.nc);
                // t = new Thread(prThread);
                break;
            case LOOKUP:
                // PeerRequestThread prThread = new PeerRequestThread(msg, this.nc);
                // t = new Thread(prThread);
                break;
            case SEND:
                ReceiveFileThread rfThread = new ReceiveFileThread(msg, this.nc, peerSocket);
                t = new Thread(rfThread);
                t.start();
                break;
            case STABILIZE:
                StabilizeThread sThread = new StabilizeThread(msg, this.nc);
                t = new Thread(sThread);
                t.start();
                break;
            case UPLOAD:
                // PeerRequestThread prThread = new PeerRequestThread(msg, this.nc);
                // t = new Thread(prThread);
                break;
            default:
                System.out.println("Unknown ReqType... Closing socket.");
                peerSocket.close();
                return;
        }
        // t.start();
    }

    public static void main(String[] args) throws IOException {
        BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));
        int serverPort;

        if (args.length > 0) {
            serverPort = Integer.parseInt(args[0]);
        }
        else {
            System.out.println("Enter Port #: ");
            serverPort = Integer.parseInt(fromKeyboard.readLine());
        }
        new Server(serverPort).run();
    }

}
