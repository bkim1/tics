package object;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Peer {
    private InetAddress ip;
    private int port;
    private byte[] key;

    public Peer(InetAddress ip, int port, byte[] key) {
        this.ip = ip;
        this.port = port;
        this.key = key;
    }

    public InetAddress getIP() { return this.ip; }
    public void setIP(InetAddress ip) { this.ip = ip; }
    public void setIP(String address) throws UnknownHostException{
        this.ip = InetAddress.getByName(address);
    }

    public int getPort() { return this.port; }
    public void setPort(int port) { this.port = port; }

    public byte[] getKey() { return this.key; }
    public void setKey(byte[] key) { this.key = key; }
}