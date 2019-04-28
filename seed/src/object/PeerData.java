package object;

import java.io.Serializable;

public class PeerData implements Serializable {
    private static final long serialVersionUID = 1001100L;

    private long key;
    private byte[] data;

    public PeerData(long key, byte[] data) {
        this.key = key;
        this.data = data;
    }

    public long getKey() { return this.key; }
    public void setKey(long key) { this.key = key; }

    public byte[] getData() { return this.data; }
    public void setData(byte[] data) { this.data = data; }
    // public void setData(InputStream in) {} ?
}