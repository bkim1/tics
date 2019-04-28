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
		
		switch(msg.getReqType()) {
		case JOIN:
			join();
			break;
		case LOOKUP:
			Peer current = node.getPeerObject();
			InetAddress currentIP = current.getIP();
			System.out.println("Node " + currentIP + " now performing look up...");
			Peer next = lookUp();
			if(next == null) { System.out.println("File not found."); }
			else if(current.equals(next)) { System.out.println("File has been found."); }
			else { 
				System.out.println("The look up is now occuring at node " + next.getIP());
			}
			break;
		case UPLOAD:
			
			break;
		}
	}
	
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
	}
	
	public void join() {
		
	}
	
	public Peer lookUp() {
		PeerData data = this.node.getPeerData(msg.getKey());
		Peer receiver = msg.getPeer();
		
		if(data != null) {
			try {
				Socket socket = new Socket(receiver.getIP(), receiver.getPort());
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
			return node.getPeerObject();
		}
		return Utilities.lookUp(msg, this.node.getFingerTable());
	}
	
	

}
