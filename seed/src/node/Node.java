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
    
    private static Object sharedLock = new Object();

    public Node(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        this.fingerTable = new Peer[RING_SIZE];
        this.peerFiles = new HashMap<>();
        this.myFiles = new HashMap<>();
    }

    public String getKey() {
        synchronized(sharedLock) {
            return this.password;
        }
    }
    private void generateKey() {}

    public long getPeerId() {
        synchronized(sharedLock) {
            return this.peerId;
        }
    }

    public void updateAddress(InetAddress ip, int port) {
        synchronized(sharedLock) {
            this.ip = ip;
            this.port = port;
            this.peerId = generatePeerId(this.ip, this.port);
        }
    }

    public InetAddress getIP() {
        synchronized(sharedLock) {
            return this.ip;
        }
    }
    public void setIP(InetAddress ip) {
        synchronized(sharedLock) {
            this.ip = ip;
            this.peerId = generatePeerId(this.ip, this.port);
        }
    }

    public int getPort() {
        synchronized(sharedLock) {
            return this.port;
        }
    }
    public void setPort(int port) {
        synchronized(sharedLock) {
            this.port = port;
            this.peerId = generatePeerId(this.ip, this.port);
        }
    }

    public Peer getPeerObject() {
        synchronized(sharedLock) {
            return new Peer(this.ip, this.port, this.peerId);
        }
    }

    public Peer getSuccessor() {
        synchronized(sharedLock) {
            return this.fingerTable[0];
        }
    }
    public void setSuccessor(Peer p) {
        synchronized(sharedLock) {
            this.fingerTable[0] = p;
        }
    }

    public Peer getPredecessor() {
        synchronized(sharedLock) {
            return this.predecessor;
        }
    }
    public void setPredecessor(Peer p) {
        synchronized(sharedLock) {
            this.predecessor = p; 
        }
    }

    public Peer[] getFingerTable() {
        synchronized(sharedLock) {
            return this.fingerTable;
        }
    }
    public void updateFingerTable(Peer[] fingerTable) {
        synchronized(sharedLock){
            this.fingerTable = fingerTable; 
        }
    }
    public void updateFingerTable(Peer peer, int index) {
        synchronized(sharedLock) {
            this.fingerTable[index] = peer;
        }
    }

    public Map<String, PeerData> getPeerFiles() {
        synchronized(sharedLock) {
            return this.peerFiles;
        }
    }
    public PeerData getPeerData(long key) {
        synchronized(sharedLock) {
            return this.peerFiles.get(Long.toString(key));
        }
    }
    public void addPeerFile(PeerData data) {
        String strKey = Long.toString(data.getKey());
        synchronized(sharedLock) {
            this.peerFiles.put(strKey, data);
        }
    }

    public Map<String, FileInfo> getMyFiles() {
        synchronized(sharedLock) {
            return this.myFiles;
        }
    }
    public void addFile(FileInfo file) {
        synchronized(sharedLock) {
            this.myFiles.put(file.getFilename(), file);
        }
    }
}