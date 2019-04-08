package thread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
		case SEND:
			sendFile(msg.getPeer(), msg.getKey());
			break;
		case LOOKUP:
			lookUp(msg.getPeer(), msg.getKey());
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
	
	public Peer lookUp() {
		return Utilities.lookUp(msg, nc.getFingerTables());
	}

}
