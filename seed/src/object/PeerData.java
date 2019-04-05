package object;


public class PeerData {
    private byte[] key, data;

    public PeerData(byte[] key, byte[] data) {
        this.key = key;
        this.data = data;
    }

    public byte[] getKey() { return this.key; }
    public void setKey(byte[] key) { this.key = key; }

    public byte[] getData() { return this.data; }
    public void setData(byte[] data) { this.data = data; }
    // public void setData(InputStream in) {} ?
}