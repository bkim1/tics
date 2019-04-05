package node;

import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import object.FileInfo;
import object.Peer;
import object.PeerData;
import static utils.Constants.RING_SIZE;

public class Node {
    private byte[] privateKey;
    private long peerId;
    private InetAddress ip;
    private int port;
    private List<Peer> fingerTable;
    private Peer predecessor;
    private Map<byte[], PeerData> peerFiles;
    private Map<String, FileInfo> myFiles;

    public Node() {

    }

    public byte[] getKey() { return this.privateKey; }
    private void generateKey() {}

    public long getPeerId() { return this.peerId; }
    private void updatePeerId() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(this.ip.toString().getBytes(Charset.forName("UTF-8")));
            byte[] bytes = Arrays.copyOfRange(digest.digest(), 0, RING_SIZE);

            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            this.peerId = buffer.getLong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getIP() { return this.ip; }
    public void setIP(InetAddress ip) {
        this.ip = ip;
        this.updatePeerId();
    }

    public int getPort() { return this.port; }
    public void setPort(int p) { this.port = p; }

    public Peer getSuccessor() { return this.fingerTable[0]; }
    public void setSuccessor(Peer p) { this.fingerTable[0] = p; }

    public Peer getPredecessor() { return this.predecessor; }
    public void setPredecessor(Peer p) { this.predecessor = p; }

    public List<Peer> getFingerTable() { return this.fingerTable; }
    public void updateFingerTable(List<Peer> fingerTable) { this.fingerTable = fingerTable; }

    public Map<byte[], PeerData> getPeerFiles() { return this.peerFiles; }
    public void addPeerFile(PeerData data) { this.peerFiles.put(data.getKey(), data); }

    public Map<String, FileInfo> getMyFiles() { return this.myFiles; }
    public void addFile(FileInfo file) { this.myFiles.put(file.getFilename(), file); }

}