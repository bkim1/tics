package object;

import java.io.File;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import object.DataShard;
import static utils.Constants.RING_SIZE;;

public class FileInfo {
    private String filename;
    private List<DataShard> shardHashes;
    private byte[] salt, key;

    public FileInfo(String filename, List<DataShard> shardHashes) {
        this.filename = filename;
        this.shardHashes = shardHashes;
        this.salt = new byte[128];
        
        this.generateSalt();
        this.updateKey();
    }

    public String getFilename() { return this.filename; }
    public void setFilename(String filename) {
        this.filename = filename;
        this.updateKey();
    }

    public List<DataShard> getShards() { return this.shardHashes; }
    public void setShards(File f) {  }

    public byte[] getSalt() { return this.salt; }
    private void generateSalt() {
        SecureRandom random = new SecureRandom();
        random.nextBytes(this.salt);
    }

    public byte[] getKey() { return this.key; }
    private void updateKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(this.filename.getBytes(Charset.forName("UTF-8")));
            digest.update(this.salt);
            this.key = Arrays.copyOfRange(digest.digest(), 0, RING_SIZE);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}