import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class Server {

    public static void main(String[] args) throws UnknownHostException, IOException {
        int serverPort;
        BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));
        
        if (args.length > 0) {
            serverPort = Integer.parseInt(args[0]);
        }
        else {
            // Get connection info
            System.out.println("Enter Port #: ");
            serverPort = Integer.parseInt(fromKeyboard.readLine());
        }
        ServerSocket serverSocket = new ServerSocket(serverPort);

        // Startup the UserRequestThread to handle user input
    }
}
