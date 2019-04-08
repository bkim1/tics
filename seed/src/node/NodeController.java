package node;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

    public void addLookup(long key, FileInfo info) {
        String strKey = Long.toString(key);
        this.currentLookup.put(strKey, info);
    }

    public FileInfo getFileInfo(long key) {
        return this.currentLookup.get(Long.toString(key));
    }

    public String getDownloadLoc() {
        return this.applicationProps.getProperty("downloadLoc");
    }

    public Peer getPeerObject() {
        // ip, port, key
        return new Peer(this.node.getIP(), this.node.getPort(), this.node.getPeerId());
    }
    
    public List<Peer> getFingerTables(){
    	
    		return this.node.getFingerTable();
    }
}