package object;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import object.DataShard;
import static utils.Constants.RING_SIZE;
import static utils.Constants.SALT_SIZE;

public class FileInfo {
    private String filename;
    private List<DataShard> shardHashes;
    private byte[] salt;
    private long key;
    private boolean isReceiving;
    private int currentReceived;

    public FileInfo(String filename, List<DataShard> shardHashes) {
        this.filename = filename;
        this.shardHashes = shardHashes;
        this.salt = new byte[SALT_SIZE];
        this.isReceiving = false;
        this.currentReceived = 0;
        
        this.generateSalt();
        this.updateKey();
    }

    public FileInfo(String fileLoc, String filename) {
        this.filename = filename;
        this.shardHashes = new ArrayList<>();
        this.isReceiving = false;
        this.currentReceived = 0;

        this.generateSalt();
        this.updateKey();
        this.addFile(fileLoc, this.filename);
    }

    public String getFilename() { return this.filename; }
    public void setFilename(String filename) {
        this.filename = filename;
        this.updateKey();
    }

    private void addFile(String fileLoc, String filename) {
        DataShard shard = new DataShard(fileLoc, filename, this.salt);
        this.shardHashes.add(shard);
    }

    public List<DataShard> getShards() { return this.shardHashes; }
    public void setShards(File f) {  }

    public byte[] getSalt() { return this.salt; }
    private void generateSalt() {
        SecureRandom random = new SecureRandom();
        random.nextBytes(this.salt);
    }

    public long getKey() { return this.key; }
    private void updateKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(this.filename.getBytes(Charset.forName("UTF-8")));
            digest.update(this.salt);
            byte[] buf = Arrays.copyOfRange(digest.digest(), 0, RING_SIZE);

            ByteBuffer buffer = ByteBuffer.wrap(buf);
            this.key = buffer.getLong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public boolean isReceiving() { return this.isReceiving; }
    public void setReceiving(boolean b) { this.isReceiving = b; }

    public int getNumReceived() { return this.currentReceived; }
    public void resetReceived() { this.currentReceived = 0; }
    public void incrememntReceived() { this.currentReceived++; }
}