package object;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static utils.Utilities.objToString;

public class Peer implements Serializable {
    private static final long serialVersionUID = 314156L;

    private InetAddress ip;
    private int port, setupPort;
    private long key;

    public Peer(InetAddress ip, int port, long key) {
        this.ip = ip;
        this.port = port;
        this.key = key;
    }
    
    public Peer(InetAddress ip, int port, int setupPort, long key) {
        this.ip = ip;
        this.port = port;
        this.setupPort = setupPort;
        this.key = key;
    }

    public InetAddress getIP() { return this.ip; }
    public void setIP(InetAddress ip) { this.ip = ip; }
    public void setIP(String address) throws UnknownHostException{
        this.ip = InetAddress.getByName(address);
    }

    public int getPort() { return this.port; }
    public void setPort(int port) { this.port = port; }

    public int getSetupPort() { return this.setupPort; }
    public void setSetupPort(int setupPort) { this.setupPort = setupPort; }

    public long getKey() { return this.key; }
    public void setKey(long key) { this.key = key; }

    public boolean equals(Peer peer) {
        return this.ip.equals(peer.getIP()) &&
               this.port == peer.getPort() &&
               this.key == peer.getKey();
    }

    public String toString() { return objToString(this); }
}