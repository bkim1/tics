package object;

import java.io.File;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import object.DataShard;
import utils.Utilities;

import static utils.Constants.RING_SIZE;
import static utils.Constants.SALT_SIZE;
import static utils.Utilities.objToString;

public class FileInfo implements Serializable {
    private static final long serialVersionUID = 25802301L;

    private String filename;
    private List<DataShard> shardHashes;
    private byte[] salt;
    private int key;
    private boolean isReceiving;
    private int currentReceived;

    public FileInfo(String filename, List<DataShard> shardHashes) {
        this.filename = this.stripFilePath(filename);
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
        this.salt = new byte[SALT_SIZE];
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
    private String stripFilePath(String filename) {
        int lastIndexOf = filename.lastIndexOf("/");
        if (lastIndexOf == -1) {
            return filename; // empty extension
        }
        return filename.substring(lastIndexOf + 1);
    }

    private void addFile(String fileLoc, String filename) {
        String strippedFilename = this.stripFilePath(filename);
        DataShard shard = new DataShard(fileLoc, strippedFilename, this.salt);
        this.shardHashes.add(shard);
    }

    public List<DataShard> getShards() { return this.shardHashes; }
    public void setShards(File f) {  }

    public byte[] getSalt() { return this.salt; }
    private void generateSalt() {
        SecureRandom random = new SecureRandom();
        random.nextBytes(this.salt);
    }

    public int getKey() { return this.key; }
    private void updateKey() {
        this.key = Utilities.generateFileKey(this.filename, this.salt);
    }

    public boolean isReceiving() { return this.isReceiving; }
    public void setReceiving(boolean b) { this.isReceiving = b; }

    public int getNumReceived() { return this.currentReceived; }
    public void resetReceived() { this.currentReceived = 0; }
    public void incrememntReceived() { this.currentReceived++; }

    public String toString() { return objToString(this); }
}