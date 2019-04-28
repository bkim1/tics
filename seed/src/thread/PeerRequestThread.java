package thread;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import node.NodeController;
import object.Message;
import object.*;
import utils.Utilities;

public class PeerRequestThread implements Runnable {
    private Message msg;
    private NodeController nc;

    public PeerRequestThread(Message msg, NodeController nc) {
        this.msg = msg;
        this.nc = nc;
    }

	@Override
	public void run() {
		
		switch(msg.getReqType()) {
		case JOIN:
			join();
			break;
		case LOOKUP:
			Peer current = nc.getPeerObject();
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
		PeerData data;
		if((data = nc.getPeerFiles(key)) != null) {
			try {
				Socket socket = new Socket(peer.getIP(), peer.getPort());
				Peer sender = nc.getPeerObject();
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
		PeerData data;
		Peer receiver = msg.getPeer();
		
		if((data = nc.getPeerFiles(msg.getKey())) != null) {
			try {
				Socket socket = new Socket(receiver.getIP(), receiver.getPort());
				Peer sender = nc.getPeerObject();
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
			return nc.getPeerObject();
		}
		return Utilities.lookUp(msg, nc.getFingerTables());
	}
	
	

}
