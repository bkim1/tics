package object;

import java.io.Serializable;
import java.util.List;

import object.Peer;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private ReqType msgType;
    private long key;
    private byte[] data;
    private Peer peer;
    private List<Peer> updatedPeers;

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
    
    public Message(ReqType msgType, Peer peer, List<Peer> updatedPeers) {
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

    public List<Peer> getUpdatedPeers() { return this.updatedPeers; }
    public void setUpdatedPeers(List<Peer> updatedPeers) { this.updatedPeers = updatedPeers; }
}