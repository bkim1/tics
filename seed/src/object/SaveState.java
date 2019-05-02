package object;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Map;

import node.Node;

public class SaveState implements Serializable {
    private static final long serialVersionUID = 5728591L;

    private int peerId;
    private InetAddress ip;
    private int port;
    private Peer predecessor;
    private Peer[] fingerTable;
    private Map<String, PeerData> peerFiles;
    private Map<String, FileInfo> myFiles;

    public SaveState(Node node) {
        this.peerId = node.getPeerId();
        this.ip = node.getIP();
        this.port = node.getPort();
        this.predecessor = node.getPredecessor();
        this.fingerTable = node.getFingerTable();
        this.peerFiles = node.getPeerFiles();   
        this.myFiles = node.getMyFiles();
    }

    public int getPeerId() { return this.peerId; }

    public InetAddress getIP() { return this.ip; }

    public int getPort() { return this.port; }
    
    public void restoreAddress(Node node) {
        node.updateAddress(this.ip, this.port);
    }

    public Peer getPredecessor() { return this.predecessor; }
    public void restorePredecessor(Node node) {
        node.setPredecessor(this.predecessor);
    }

    public Peer[] getFingerTable() { return this.fingerTable; }
    public void restoreFingerTable(Node node) {
        node.updateFingerTable(this.fingerTable);
    }

    public Map<String, PeerData> getPeerFiles() { return this.peerFiles; }
    public void restorePeerFiles(Node node) {
        node.setPeerFiles(this.peerFiles);
    }

    public Map<String, FileInfo> getMyFiles() { return this.myFiles; }
    public void restoreMyFiles(Node node) {
        node.setMyFiles(this.myFiles);
    }

    public void restorePreviousState(Node node) {
        node.updateAddress(this.ip, this.port);
        node.setPredecessor(this.predecessor);
        node.updateFingerTable(this.fingerTable);
        node.setPeerFiles(this.peerFiles);
        node.setMyFiles(this.myFiles);
    }
}