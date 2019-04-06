package node;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import object.FileInfo;
import object.Peer;
import object.PeerData;
import static utils.Constants.RING_SIZE;

public class Node {
    private String password;
    private long peerId;
    private InetAddress ip;
    private int port;
    private List<Peer> fingerTable;
    private Map<String, PeerData> peerFiles;
    private Map<String, FileInfo> myFiles;

    public Node(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        this.fingerTable = new ArrayList<>();
        this.peerFiles = new HashMap<>();
        this.myFiles = new HashMap<>();
    }

    public String getKey() { return this.password; }
    private void generateKey() {}

    public long getPeerId() { return this.peerId; }
    private void updatePeerId() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(this.ip.toString().getBytes(Charset.forName("UTF-8")));
            digest.update(Integer.valueOf(this.port).byteValue());
            byte[] bytes = Arrays.copyOfRange(digest.digest(), 0, RING_SIZE);

            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            this.peerId = buffer.getLong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void updateAddress(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        this.updatePeerId();
    }

    public InetAddress getIP() { return this.ip; }
    public void setIP(InetAddress ip) {
        this.ip = ip;
        this.updatePeerId();
    }

    public int getPort() { return this.port; }
    public void setPort(int port) { this.port = port; }

    public List<Peer> getFingerTable() { return this.fingerTable; }
    public void updateFingerTable(List<Peer> fingerTable) { this.fingerTable = fingerTable; }

    public Map<String, PeerData> getPeerFiles() { return this.peerFiles; }
    public PeerData getPeerData(long key) {
        return this.peerFiles.get(Long.toString(key));
    }
    public void addPeerFile(PeerData data) {
        String strKey = Long.toString(data.getKey());
        this.peerFiles.put(strKey, data);
    }

    public Map<String, FileInfo> getMyFiles() { return this.myFiles; }
    public void addFile(FileInfo file) { this.myFiles.put(file.getFilename(), file); }

}