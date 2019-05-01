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

import static utils.Constants.RING_SIZE;
import static utils.Utilities.objToString;

public class DataShard implements Serializable {
    private static final long serialVersionUID = 674850213L;

    private long key;
    private String filename;
    private String hashedData;

    public DataShard(String fileLoc, String filename, byte[] salt) {
        this.filename = filename;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(this.filename.getBytes(Charset.forName("UTF-8")));
            digest.update(salt);

            byte[] buf = Arrays.copyOfRange(digest.digest(), 0, RING_SIZE);
            ByteBuffer buffer = ByteBuffer.wrap(buf);
            this.key = buffer.getLong();
            digest.reset();

            int count;
            buf = new byte[8192];
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

    public long getKey() { return this.key; }

    public String getFilename() { return this.filename; }

    public String getHashedData() { return this.hashedData; }

    public String toString() { return objToString(this); }
}