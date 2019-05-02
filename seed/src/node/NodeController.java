package node;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import object.*;
import node.Node;
import java.util.Scanner;

public class NodeController {
    private Node node;
    private Properties userProps;
    private Map<String, FileInfo> currentLookup;
    
    public NodeController(Node node) throws IOException {
        this.node = node;
        this.currentLookup = new HashMap<>();

        // Setup and load default properties
        Properties defaultProps = new Properties();
        FileInputStream in = new FileInputStream(System.getProperty("user.dir") + "/.defaultProps");
        defaultProps.load(in);
        in.close();

        // Setup and load user properties on top of defaults
        userProps = new Properties(defaultProps);
        in = new FileInputStream(System.getProperty("user.dir") + "/.userProps");
        userProps.load(in);
        in.close();
    }

    public Node getNode() { return this.node; }

    public void addLookup(int key, FileInfo info) {
        String strKey = Integer.toString(key);
        this.currentLookup.put(strKey, info);
    }

    public FileInfo getLookupFileInfo(int key) {
        return this.currentLookup.getOrDefault(Integer.toString(key), null);
    }

    public String getDownloadLoc() {
        return this.userProps.getProperty("downloadFileLoc");
    }

    public Object setDownloadLoc() {
        System.out.println("Enter the full address of the folder to which you want to download to");
        Scanner changeDirScan = new Scanner(System.in);
        String requestedDirectory = changeDirScan.nextLine();
        changeDirScan.close();
        return this.userProps.setProperty("downloadFileLoc", requestedDirectory);
    }

    public String getSaveStateLoc() {
        return this.userProps.getProperty("saveStateLoc");
    }

    public Peer getPeerObject() {
        // ip, port, key
        return this.node.getPeerObject();
    }
    
    public Peer[] getFingerTable() {
        return this.node.getFingerTable();
    }
    
    public PeerData getPeerFiles(int key) {
        return this.node.getPeerData(key);
    }

    public Peer getSuccessor() {
        return this.node.getSuccessor();
    }

    public String getFilename(int key) {
        String strKey = Integer.toString(key);
        FileInfo info = this.currentLookup.get(strKey);

        return info.getFilename();
    }

    public FileInfo getRegisteredFileInfo(String filename) {
        return this.node.getFileInfo(filename);
    }

    public boolean registerFileInfo(FileInfo info) {
        this.node.addFile(info);
        return true;
    }

    public int getPeerId() { return this.node.getPeerId(); }
}