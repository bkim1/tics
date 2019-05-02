package object;

import java.io.Serializable;

import object.Peer;
import static utils.Utilities.objToString;

public class Message implements Serializable {
    private static final long serialVersionUID = 3258205L;

    private ReqType msgType;
    private int key;
    private byte[] data;
    private Peer peer;
    private Peer finger;
    private int fingerIndex;
    private Peer[] updatedPeers;
    private PeerData[] transferFiles;
    private int[] transferKeys;
    private boolean isFound = false;

    public Message(ReqType msgType, Peer peer, int key, byte[] data) {
        this.msgType = msgType;
        this.peer = peer;
        this.key = key;
        this.data = data;
    }

    public Message(ReqType msgType, Peer peer, PeerData[] files) {
        this.msgType = msgType;
        this.peer = peer;
        this.transferFiles = files;
    }

    public Message(ReqType msgType, Peer peer, int[] keys) {
        this.msgType = msgType;
        this.peer = peer;
        this.transferKeys = keys;
    }

    public Message(ReqType msgType, Peer peer) {
        this.msgType = msgType;
        this.peer = peer;
    }
    
    public Message(ReqType msgType, Peer peer, Peer[] updatedPeers) {
        this.msgType = msgType;
        this.peer = peer;
        this.updatedPeers = updatedPeers;
    }

    public Message(ReqType msgType, Peer peer, int Key) {
        this.msgType = msgType;
        this.peer = peer;
        this.key = Key;
    }

    public Peer getFinger() { return this.finger; }
    public void setFinger(Peer finger) { this.finger = finger; }
    
    public int getFingerIndex() { return this.fingerIndex; }
    public void setFingerIndex(int index) { this.fingerIndex = index; }
    
    public ReqType getReqType() { return this.msgType; }
    public void setReqType(ReqType rt) { this.msgType = rt; }

    public int getKey() { return this.key; }
    public void setKey(int key) { this.key = key; }

    public byte[] getData() { return this.data; }
    public void setData(byte[] data) { this.data = data; }

    public Peer getPeer() { return this.peer; }
    public void setPeer(Peer peer) { this.peer = peer; }

    public void setFound() { this.isFound = true; }
    public boolean getFound() { return this.isFound; }


    public Peer[] getUpdatedPeers() { return this.updatedPeers; }
    public void setUpdatedPeers(Peer[] updatedPeers) { this.updatedPeers = updatedPeers; }

    public PeerData[] getTransferFiles() { return this.transferFiles; }
    public void setTransferFiles(PeerData[] files) { this.transferFiles = files; }

    public int[] getTransferKeys() { return this.transferKeys; }
    public void setTransferKeys(int[] keys) { this.transferKeys = keys; }

    public String toString() { return objToString(this); }
}