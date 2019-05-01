package utils;

import static utils.Constants.RING_SIZE;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import object.*;

public class Utilities {

	public static Peer lookUp(Message msg, Peer[] fingerTable, long selfKey) {
		Peer finger = null;
		int length = fingerTable.length;
		
		for(int i = 0; i < length; i++) {
			finger = fingerTable[i];
			long nodeKey = finger.getKey();
			long targetKey = msg.getKey();
			/*
			if(finger.equals(msg.getPeer())) {
				return null;
			}*/
			
			if(nodeKey == targetKey || 
			   (i == 0 && selfKey < nodeKey && nodeKey < targetKey)) {
				msg.setFound();
				break;
			}
			
			//if we reach a node whose successor's key is larger than the file hash, we return the successor
			else if(msg.getKey() > finger.getKey() && i+1 < length && fingerTable[i+1].getKey() > msg.getKey()) {
				break;
			}	
		}
		try {
			Peer next = finger;
			InetAddress address = next.getIP();
			int port = next.getPort();
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
		return finger;
	}
	
	public static long generatePeerId(InetAddress ip, int port) {
		long key = -1;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(ip.toString().getBytes(Charset.forName("UTF-8")));
			digest.update(Integer.valueOf(port).byteValue());
			byte[] bytes = Arrays.copyOfRange(digest.digest(), 0, RING_SIZE);
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			key = buffer.getLong();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return key;
	}

}
