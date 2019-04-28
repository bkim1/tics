package node;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import object.FileInfo;
import object.Peer;
import object.PeerData;
import static utils.Constants.RING_SIZE;
import static utils.Utilities.generatePeerId;;

public class Node {
    private String password;
    private long peerId;
    private InetAddress ip;
    private int port;
    private Peer[] fingerTable;
    private Map<String, PeerData> peerFiles;
    private Peer predecessor;
    private Map<String, FileInfo> myFiles;

    public Node(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        this.fingerTable = new Peer[RING_SIZE];
        this.peerFiles = new HashMap<>();
        this.myFiles = new HashMap<>();
    }

    public String getKey() { return this.password; }
    private void generateKey() {}

    public long getPeerId() { return this.peerId; }

    public synchronized void updateAddress(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        this.peerId = generatePeerId(this.ip, this.port);
    }

    public InetAddress getIP() { return this.ip; }
    public synchronized void setIP(InetAddress ip) {
        this.ip = ip;
        this.peerId = generatePeerId(this.ip, this.port);
    }

    public int getPort() { return this.port; }
    public synchronized void setPort(int port) {
        this.port = port;
        this.peerId = generatePeerId(this.ip, this.port);
    }

    public Peer getPeerObject() { return new Peer(this.ip, this.port, this.peerId); }

    public Peer getSuccessor() { return this.fingerTable[0]; }
    public synchronized void setSuccessor(Peer p) { this.fingerTable[0] = p; }

    public Peer getPredecessor() { return this.predecessor; }
    public synchronized void setPredecessor(Peer p) { this.predecessor = p; }

    public Peer[] getFingerTable() { return this.fingerTable; }
    public synchronized void updateFingerTable(Peer[] fingerTable) { this.fingerTable = fingerTable; }
    public synchronized void updateFingerTable(Peer peer, int index) { this.fingerTable[index] = peer; }

    public Map<String, PeerData> getPeerFiles() { return this.peerFiles; }
    public PeerData getPeerData(long key) {
        return this.peerFiles.get(Long.toString(key));
    }
    public synchronized void addPeerFile(PeerData data) {
        String strKey = Long.toString(data.getKey());
        this.peerFiles.put(strKey, data);
    }

    public Map<String, FileInfo> getMyFiles() { return this.myFiles; }
    public synchronized void addFile(FileInfo file) { this.myFiles.put(file.getFilename(), file); }
}