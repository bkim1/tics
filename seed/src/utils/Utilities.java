package utils;

import static utils.Constants.RING_SIZE;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;

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
			
			//the target node is found if the keys are equal or 
			//if the first entry on the finger table is the closest successor to the target key
			if(nodeKey == targetKey || 
			   (i == 0 && selfKey < nodeKey && nodeKey < targetKey)) {
				msg.setFound();
				break;
			}
			
			//if we reach a node whose successor's key is larger than the file hash,
			//the lookup continues at this new node
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

	public static String objToString(Object obj) {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(obj.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		//determine fields declared in this class only (no fields of superclass)
		Field[] fields = obj.getClass().getDeclaredFields();
		
		//print field names paired with their values
		for (Field field : fields) {
			field.setAccessible(true);
			
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				//requires access to private field:
				result.append(field.get(obj));
			}
			catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine + newLine);
		}
		result.append("}");

		return result.toString();
	}
	
	public void generateFingerTable(Peer[] init, Peer sender) {
		long peerID = sender.getKey();
		for(int i = 0; i < init.length; i++) {
			//each entry is denoted by the hash of the node + 2^index of the finger table
			long targetKey = peerID + (long) Math.pow(2, i);
			Message msg = new Message(ReqType.JOIN, sender, targetKey, null);
			msg.setFingerIndex(i);
			//perform lookUp on node n + 2^index
			lookUp(msg, init, peerID);
			System.out.println("Searching for " + i + "th entry in finger table...");
		}
	}

}
