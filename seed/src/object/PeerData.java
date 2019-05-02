package object;

import java.io.Serializable;

import static utils.Utilities.objToString;

public class PeerData implements Serializable {
    private static final long serialVersionUID = 1001100L;

    private int key;
    private byte[] data;

    public PeerData(int key, byte[] data) {
        this.key = key;
        this.data = data;
    }

    public int getKey() { return this.key; }
    public void setKey(int key) { this.key = key; }

    public byte[] getData() { return this.data; }
    public void setData(byte[] data) { this.data = data; }
    
    public String toString() { return objToString(this); }
}