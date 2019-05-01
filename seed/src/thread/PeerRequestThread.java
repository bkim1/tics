package thread;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

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
		
		case JOIN:
			join();
			break;
		
		//file lookup	
		case LOOKUP:
			Peer current = node.getPeerObject();
			InetAddress currentIP = current.getIP();
			System.out.println("Node " + currentIP + " now performing look up...");
			next = lookUp();
			if(next == null) { System.out.println("File not found."); }
			else if(current.equals(next)) { System.out.println("File has been found."); }
			else { System.out.println("The look up is now occuring at node " + next.getIP()); }
			break;
			
		case SETUP:
			next = Utilities.lookUp(msg, node.getFingerTable());
			break;
			
		case UPLOAD:
			upload();
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
	
	public void upload() {
		
	}

	public void join() {
		//long key = msg.getKey();
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
	
	/*
	public void sendFile(Peer peer, Long key) {
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
