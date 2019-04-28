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
    private Properties applicationProps;
    private Map<String, FileInfo> currentLookup;
    
    public NodeController(Node node) throws IOException {
        this.node = node;
        this.currentLookup = new HashMap<>();

        // Setup and load default properties
        Properties defaultProps = new Properties();
        FileInputStream in = new FileInputStream(".defaultProps");
        defaultProps.load(in);
        in.close();

        // Setup and load application properties
        applicationProps = new Properties(defaultProps);
        in = new FileInputStream(".applicationProps");
        applicationProps.load(in);
        in.close();
    }

    public synchronized void addLookup(long key, FileInfo info) {
        String strKey = Long.toString(key);
        this.currentLookup.put(strKey, info);
    }

    public synchronized FileInfo getFileInfo(long key) {
        return this.currentLookup.get(Long.toString(key));
    }

    public synchronized String getDownloadLoc() {
        return this.applicationProps.getProperty("downloadLoc");
    }

    public synchronized Peer getPeerObject() {
        // ip, port, key
        return new Peer(this.node.getIP(), this.node.getPort(), this.node.getPeerId());
    }
    
    public synchronized Peer[] getFingerTables() {
        return this.node.getFingerTable();
    }
    
    public synchronized PeerData getPeerFiles(long key) {
        return this.node.getPeerData(key);
    }

    public synchronized Peer getSuccessor() {
        return this.node.getFingerTable()[0];
    }

    public synchronized String getFilename(long key) {
        String strKey = Long.toString(key);
        FileInfo info = this.currentLookup.get(strKey);

        return info.getFilename();
    }
}