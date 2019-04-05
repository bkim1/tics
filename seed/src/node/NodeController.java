package node;
import object.*;

import node.Node;

public class NodeController {
    private Node node;
    
    public NodeController(Node node) {
        this.node = node;
    }

    public Peer getPeerObject() {
        // ip, port, key
        return new Peer(this.node.getIP(), this.node.getPort(), this.node.getPeerId());
    }
}