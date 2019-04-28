package object;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Peer implements Serializable {
    private static final long serialVersionUID = 314156L;

    private InetAddress ip;
    private int port;
    private long key;

    public Peer(InetAddress ip, int port, long key) {
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

    public long getKey() { return this.key; }
    public void setKey(long key) { this.key = key; }
}