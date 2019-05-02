package node;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import object.*;
import node.Node;

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

    public void addLookup(long key, FileInfo info) {
        String strKey = Long.toString(key);
        this.currentLookup.put(strKey, info);
    }

    public FileInfo getLookupFileInfo(long key) {
        return this.currentLookup.getOrDefault(Long.toString(key), null);
    }

    public String getDownloadLoc() {
        return this.userProps.getProperty("downloadFileLoc");
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
    
    public PeerData getPeerFiles(long key) {
        return this.node.getPeerData(key);
    }

    public Peer getSuccessor() {
        return this.node.getSuccessor();
    }

    public String getFilename(long key) {
        String strKey = Long.toString(key);
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

    public long getPeerId() { return this.node.getPeerId(); }
}