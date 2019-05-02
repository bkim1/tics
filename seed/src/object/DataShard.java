package object;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import utils.Utilities;

import static utils.Constants.RING_SIZE;

public class DataShard implements Serializable {
    private static final long serialVersionUID = 674850213L;

    private int key;
    private String filename;
    private String hashedData;

    public DataShard(String fileLoc, String filename, byte[] salt) {
        this.filename = filename;

        try {
            this.key = Utilities.generateFileKey(filename, salt);
            
            int count;
            byte[] buf = new byte[8192];
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileLoc));
            while((count = bis.read(buf)) > 0) {
                digest.update(buf);
            }
            bis.close();

            this.hashedData = Base64.getEncoder().encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public int getKey() { return this.key; }

    public String getFilename() { return this.filename; }

    public String getHashedData() { return this.hashedData; }

    public String toString() { return Utilities.objToString(this); }
}