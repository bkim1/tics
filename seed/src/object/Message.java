package object;

import java.io.Serializable;

import object.Peer;
import static utils.Utilities.objToString;

public class Message implements Serializable {
    private static final long serialVersionUID = 3258205L;

    private ReqType msgType;
    private long key;
    private byte[] data;
    private Peer peer;
    private Peer[] updatedPeers;
    private boolean isFound = false;

    public Message(ReqType msgType, Peer peer, long key, byte[] data) {
        this.msgType = msgType;
        this.peer = peer;
        this.key = key;
        this.data = data;
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

    public ReqType getReqType() { return this.msgType; }
    public void setReqType(ReqType rt) { this.msgType = rt; }

    public long getKey() { return this.key; }
    public void setKey(long key) { this.key = key; }

    public byte[] getData() { return this.data; }
    public void setData(byte[] data) { this.data = data; }

    public Peer getPeer() { return this.peer; }
    public void setPeer(Peer peer) { this.peer = peer; }
    
    public void setFound() { this.isFound = true; }

    public Peer[] getUpdatedPeers() { return this.updatedPeers; }
    public void setUpdatedPeers(Peer[] updatedPeers) { this.updatedPeers = updatedPeers; }

    public String toString() { return objToString(this); }
}