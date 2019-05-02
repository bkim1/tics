package node;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import object.FileInfo;
import object.Peer;
import object.PeerData;
import static utils.Constants.RING_SIZE;
import utils.Utilities;

public class Node {
    private int peerId;
    private InetAddress ip;
    private int port;
    private Peer[] fingerTable;
    private Map<String, PeerData> peerFiles;
    private Peer predecessor;
    private Map<String, FileInfo> myFiles;
    
    private static Object addressLock = new Object();
    private static Object peerLock = new Object();
    private static Object peerFilesLock = new Object();
    private static Object fingerTableLock = new Object();
    private static Object myFilesLock = new Object();


    public Node(InetAddress ip, int port) {
        this.updateAddress(ip, port);
        this.fingerTable = new Peer[RING_SIZE];
        this.peerFiles = new HashMap<>();
        this.myFiles = new HashMap<>();

        System.out.println("Peer ID: " + this.peerId);
    }

    public int getPeerId() {
        synchronized(addressLock) {
            return this.peerId;
        }
    }

    public void updateAddress(InetAddress ip, int port) {
        synchronized(addressLock) {
            this.ip = ip;
            this.port = port;
            this.peerId = Utilities.generatePeerId(this.ip, this.port);
        }
    }

    public InetAddress getIP() {
        synchronized(addressLock) {
            return this.ip;
        }
    }
    public void setIP(InetAddress ip) {
        synchronized(addressLock) {
            this.ip = ip;
            this.peerId = Utilities.generatePeerId(this.ip, this.port);
        }
    }

    public int getPort() {
        synchronized(addressLock) {
            return this.port;
        }
    }
    public void setPort(int port) {
        synchronized(addressLock) {
            this.port = port;
            this.peerId = Utilities.generatePeerId(this.ip, this.port);
        }
    }

    public Peer getPeerObject() {
        synchronized(peerLock) {
            return new Peer(this.ip, this.port, this.peerId);
        }
    }

    public Peer getSuccessor() {
        synchronized(peerLock) {
            return this.fingerTable[0];
        }
    }
    public void setSuccessor(Peer p) {
        synchronized(fingerTableLock) {
            this.fingerTable[0] = p;
        }
    }

    public Peer getPredecessor() {
        synchronized(peerLock) {
            return this.predecessor;
        }
    }
    public void setPredecessor(Peer p) {
        synchronized(peerLock) {
            this.predecessor = p; 
        }
    }

    public Peer[] getFingerTable() {
        synchronized(fingerTableLock) {
            return this.fingerTable;
        }
    }
    public void updateFingerTable(Peer[] fingerTable) {
        synchronized(fingerTableLock){
            this.fingerTable = fingerTable;

            System.out.println("Finger Table has been updated!");
            System.out.println("Updated version: ");
            this.printFingerTable();
        }
    }
    public void updateFingerTable(Peer[] fingerTable, boolean print) {
        synchronized(fingerTableLock){
            this.fingerTable = fingerTable;

            System.out.println("Finger Table has been updated!");
            System.out.println("Updated version: ");
            if (print) { this.printFingerTable(); }
        }
    }
    public void updateFingerTable(Peer peer, int index) {
        synchronized(fingerTableLock) {
            this.fingerTable[index] = peer;

            System.out.println("Finger Table has been updated!");
            System.out.println("Updated version: ");
            this.printFingerTable();
        }
    }

    public void printFingerTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("i\t| Peer\n");
        sb.append("-------------------------\n");
        if (this.fingerTable == null) {
            sb.append("No peers");
            System.out.println(sb.toString() + "\n");
            return;
        }
        for (int i = 0; i < this.fingerTable.length; i++) {
            String finger;
            if (this.fingerTable[i] == null) {
                finger = "";
            }
            else {
                finger = this.fingerTable[i].toCondensedString();
            }
            sb.append(Integer.toString(i) + ": " + finger + "\n");
            sb.append("-------------------------\n");
        }

        System.out.println(sb.toString() + "\n");
    }

    public Map<String, PeerData> getPeerFiles() {
        synchronized(peerFilesLock) {
            return this.peerFiles;
        }
    }
    public PeerData getPeerData(int key) {
        synchronized(peerFilesLock) {
            return this.peerFiles.get(Integer.toString(key));
        }
    }
    public void addPeerFile(PeerData data) {
        String strKey = Integer.toString(data.getKey());
        synchronized(peerFilesLock) {
            this.peerFiles.put(strKey, data);
        }
    }

    public void removePeerFile(long key) {
        String strKey = Long.toString(key);
        synchronized(peerFilesLock) {
            this.peerFiles.remove(strKey);
        }
    }

    public void setPeerFiles(Map<String, PeerData> peerFiles) { 
        synchronized(peerFilesLock) {
            this.peerFiles = peerFiles;
        }
    }

    public Map<String, FileInfo> getMyFiles() {
        synchronized(myFilesLock) {
            return this.myFiles;
        }
    }
    public void addFile(FileInfo file) {
        synchronized(myFilesLock) {
            this.myFiles.put(file.getFilename(), file);
        }
    }

    public void removeFile(FileInfo file) {
        synchronized(myFilesLock) {
            this.myFiles.remove(file.getFilename());
        }
    }

    public boolean containsFile(String filename) {
        synchronized(myFilesLock) {
            return this.myFiles.containsKey(filename);
        }
    }

    public FileInfo getFileInfo(String filename) {
        synchronized(myFilesLock) {
            return this.myFiles.getOrDefault(filename, null);
        }
    }

    public void setMyFiles(Map<String, FileInfo> myFiles) {
        synchronized(myFilesLock) {
            this.myFiles = myFiles;
        }
    }

    public String toString() { return Utilities.objToString(this); }
}