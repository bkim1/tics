package thread;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import node.Node;
import object.Message;
import object.*;
import utils.Utilities;

public class PeerRequestThread implements Runnable {
    private Message msg;
    private Node node;

    public PeerRequestThread(Message msg, Node node) {
        this.msg = msg;
        this.node = node;
    }

	@Override
	public void run() {
		
		
		Peer next;
		switch(msg.getReqType()) {
		
		//sent by joining node to nodes corresponding to its finger table entries
		//also sent by fingers back to joining node to establish its finger table
		case JOIN:
			join();
			break;
		
		//sent by nodes looking for a file	
		case LOOKUP:
			Peer current = node.getPeerObject();
			InetAddress currentIP = current.getIP();
			System.out.println("Node " + currentIP + " now performing look up...");
			next = lookUp();
			if(next == null) { System.out.println("File not found."); }
			else if(current.equals(next)) { System.out.println("File has been found."); }
			else { System.out.println("The look up is now occuring at node " + next.getIP()); }
			break;
		
		//sent by joining node to nodes whose finger tables may be affected by new node
		//in order to update
		case SETUP:
			setUp();
			break;
		
		//sent by nodes looking to upload a file	
		case UPLOAD:
			upload();
			break;

		//sent by newly joined predecessor looking for files
		case FILE_REC:
			fileRequest();
			break;

		//sent by successor with file to store
		case FILE_RESP:
			fileResponse();
			break;

		//sent by newly joined predecessor after rcving a file
		case FILE_ACK:
			fileAck();
			break;
		}
	}
	
	public void tcpSend(Message msg, InetAddress address, int port) {
		try {
			Socket socket = new Socket(address, port);
			OutputStream os = socket.getOutputStream(); 
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.flush();
			oos.writeObject(msg);   //send object to server
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* SETUP MESSAGE
	 * Peer = joiner
	 * key = hash of affected node
	 */
	public void setUp() {
		//if current node is the affected node, go through finger table and replace out-of-date entries
		if(msg.getFound()) {
			Peer[] fingerTable = node.getFingerTable();
			int nodeHash = node.getPeerId();
			Peer join = msg.getPeer();
			int key = msg.getKey();
			for(int i = 0; i < fingerTable.length; i++) {
				//if the new node's hash is a successor of node n + 2^i and is smaller than the 
				//current corresponding figure, the old finger is replaced with the new node
				if(key >= nodeHash + Math.pow(2, i) && key < fingerTable[i].getKey()) {
					node.updateFingerTable(join, i);
					System.out.println("Finger table entry " + i + " updated with new node " + join.getIP() + ".");
				}
			}
		}
		//otherwise continue lookup
		else {
			Peer peer = Utilities.lookUp(msg, this.node.getFingerTable(), this.node.getPeerId());
			System.out.println("The look up is now occurding at node " + peer.getIP() + ".");
		}
	}
	
	/* UPLOAD MESSAGE
	 * Peer = origin
	 * data = file
	 * key = file hash
	 */
	public void upload() {
		//if the current node is the target node, add data to files
		if(msg.getFound()) {
			PeerData peerData = new PeerData(msg.getKey(), msg.getData());
			node.addPeerFile(peerData);
			System.out.println("Data stored successfully.");
		}
		//if current node is not the target, perform lookUp
		else {
			Peer next = Utilities.lookUp(msg, this.node.getFingerTable(), this.node.getPeerId());
		}
	}
	
	/* JOIN MESSAGE
	 * Peer = origin
	 * fingerIndex = entry index in origin's finger table
	 * finger = hash of node that is a finger in origin's finger table
	 * 			(if not yet found, the finger is origin)
	 * key = hash of origin node + 2^fingerIndex mod 2^60
	 */
	public void join() {
		//int key = msg.getKey();
		Peer finger = msg.getFinger();
		//if the finger does not equal the current node, then the finger is an entry of the
		//current node's finger table
		if(!finger.equals(node.getPeerObject())) {
			int index = msg.getFingerIndex();
			node.updateFingerTable(finger, msg.getFingerIndex());
			System.out.println("Finger table entry at index " + index + " updated to " + finger.getIP() + ".");
		}
		//if getFound() is true, then the current node is the target node
		else if(msg.getFound()) {
			Peer target = node.getPeerObject();
			Peer receiver = msg.getPeer();
			msg.setFinger(target);
			tcpSend(msg, receiver.getIP(), receiver.getPort());
			System.out.println("Sent message to node " + finger.getIP() + " with its " + msg.getFingerIndex() + " entry.");
		}
		//the current node is not the successor, perform lookUp
		else {
			Peer next = Utilities.lookUp(msg, this.node.getFingerTable(), this.node.getPeerId());
			System.out.println("The lookup is now occurring at node " + next.getIP() + ".");
		}
	}
	
	/* LOOKUP MESSAGE
	 * Peer = origin
	 * key = file hash
	 */
	public Peer lookUp() {
		//if msg.getFound() is true, then the current node is the file successor
		if(msg.getFound()) {
			PeerData data = this.node.getPeerData(msg.getKey());
			Peer receiver = msg.getPeer();
	
			//file exists
			if(data != null) {
				Peer sender = node.getPeerObject();
				Message msg = new Message(ReqType.SEND, sender, data.getKey(), data.getData());	
				tcpSend(msg, receiver.getIP(), receiver.getPort());
				
				return node.getPeerObject();
			}
			//file cannot be found
			else {
				return null;
			}
		}
		//not the correct node, continue lookup
		return Utilities.lookUp(msg, this.node.getFingerTable(), this.node.getPeerId());
	}

	public void fileRequest() {
		Peer newNode = this.msg.getPeer();
		Peer myPeer = this.node.getPeerObject();
		Map<String, PeerData> files = this.node.getPeerFiles();
		for ( String key : files.keySet() ) {
			int lkey = Integer.parseInt(key);
			if (lkey < newNode.getKey()) {
				Message resp = new Message(ReqType.FILE_RESP, myPeer, lkey, files.get(key).getData());
				tcpSend(resp, newNode.getIP(), newNode.getPort());
			}
		}
	}

	public void fileResponse() {
		Peer sender = this.msg.getPeer();
		PeerData file = new PeerData(this.msg.getKey(), this.msg.getData());
		this.node.addPeerFile(file);
		Message ack = new Message(ReqType.FILE_ACK, this.node.getPeerObject(), file.getKey());
		tcpSend(ack, sender.getIP(), sender.getPort());
	}

	public void fileAck() {
		this.node.removePeerFile(this.msg.getKey());
	}
	
	/*
	public void sendFile(Peer peer, int key) {
		PeerData data = this.node.getPeerData(key);
		if(data != null) {
			try {
				Socket socket = new Socket(peer.getIP(), peer.getPort());
				Peer sender = node.getPeerObject();
				Message msg = new Message(ReqType.SEND, sender, data.getKey(), data.getData());
				OutputStream os = socket.getOutputStream(); 
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.flush();
				oos.writeObject(msg);   //send object to server
				oos.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			lookUp();	
		}
	}*/
	
	

}
