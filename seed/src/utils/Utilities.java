package utils;


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


import node.Node;
import object.*;
import static utils.Constants.RING_SIZE;
import static utils.Constants.RING_SIZE_BYTES;


public class Utilities {

	public static Peer lookUp(Message msg, Peer[] fingerTable, int selfKey) {
		Peer finger = null;
		int length = fingerTable.length;
		
		for(int i = 0; i < length; i++) {
			finger = fingerTable[i];
			int nodeKey = finger.getKey();
			int targetKey = msg.getKey();

			System.out.println("Node Key: " + Integer.toString(nodeKey));
			System.out.println("Target Key: " + Integer.toString(targetKey));
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

	public static int generatePeerId(InetAddress ip, int port) {
		int key = -1;
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(ip.toString().getBytes(Charset.forName("UTF-8")));
			digest.update(Integer.valueOf(port).byteValue());
			Byte bits = digest.digest()[RING_SIZE];
			System.out.println(bits);
			key = bits.intValue();
			System.out.println("Int Key: " + key);
			
			if (key < 0) { key = -key; }
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return key;
	}

	public static int generateFileKey(String filename, byte[] salt) {
		int key = -1;
		try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(filename.getBytes(Charset.forName("UTF-8")));
            digest.update(salt);
            Byte bits = digest.digest()[RING_SIZE];

            key = bits.intValue();

            if (key < 0) { key = -key; }
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
		int peerID = sender.getKey();
		for(int i = 0; i < init.length; i++) {
			//each entry is denoted by the hash of the node + 2^index of the finger table
			int targetKey = getFingerTableThreshold(peerID, i);
			Message msg = new Message(ReqType.JOIN, sender, targetKey, null);
			msg.setFingerIndex(i);
			msg.setFinger(sender);
			//perform lookUp on node n + 2^index
			lookUp(msg, init, peerID);
			System.out.println("Searching for " + i + "th entry in finger table...");
		}
	}
	
	public static void adjustFingerTable(Node node, Peer peer) {
		Peer[] fingerTable = node.getFingerTable();
		Peer[] originalTable = Arrays.copyOf(fingerTable, fingerTable.length);

		if (fingerTable == null) {
			fingerTable = new Peer[RING_SIZE];
		}
		int currentKey = node.getPeerId();
		int peerKey = peer.getKey();
		int threshold;

		for (int i = 0; i < fingerTable.length; i++) { 
			// Empty fingerTable --> Assign peer and skip over rest
			if (fingerTable[i] == null) {
				fingerTable[i] = peer;
				continue;
			}

			threshold = getFingerTableThreshold(currentKey, i);
			System.out.println("Threshold: " + threshold);
			Peer finger = fingerTable[i];

			// Standard case for inserting new node
			// When new node's key is less than the finger's key
			if (threshold > finger.getKey() && peerKey >= threshold) {
				shiftTableRight(fingerTable, i);
				fingerTable[i] = findBestFinger(currentKey, threshold, originalTable, peer);
			}
			else if (threshold <= peerKey && finger.getKey() > peerKey) {
				System.out.println("Standard Case hit!");
				// Shift nodes to the right for new entry
				shiftTableRight(fingerTable, i);
				fingerTable[i] = findBestFinger(currentKey, threshold, originalTable, peer);
			}
			// Case for when it loops around the ring
			else if (finger.getKey() < threshold &&
					 (peerKey < finger.getKey() || peerKey >= threshold)) {
				System.out.println("Edge case hit!");
				// Shift nodes to the right for new entry
				shiftTableRight(fingerTable, i);
				fingerTable[i] = findBestFinger(currentKey, threshold, originalTable, peer);	
			}
		}
		node.updateFingerTable(fingerTable);
	}

	public static int getFingerTableThreshold(int currentKey, int index) {
		int sum = currentKey + ((int) Math.pow(2, index + 1) - 1);
		int remainder = sum % (int) Math.pow(2, RING_SIZE);
		return remainder;
	}

	public static void shiftTableRight(Peer[] fingerTable, int index) {
		for (int j = index + 1; j < fingerTable.length; j++) {
			fingerTable[j] = fingerTable[j - 1];
		}
	}

	public static Peer findBestFinger(int currentKey, int threshold, Peer[] fingerTable, Peer peer) {
		Peer bestPeer = peer;
		System.out.println("\nEntered findBestFinger\n");

		for (int i = 0; i < fingerTable.length; i++) {
			Peer p = fingerTable[i];
			System.out.println("Finger: " + p.getKey() + " Peer: " + peer.getKey());
			if (threshold <= p.getKey() && threshold <= peer.getKey() &&
					p.getKey() - threshold < peer.getKey() - threshold) {
				bestPeer = p;
				System.out.println("Find Best Finger: 1st case!");
			}
			else if (threshold > p.getKey() && threshold > peer.getKey() &&
					 threshold - p.getKey() > threshold - peer.getKey()) {
				bestPeer = p;
				System.out.println("Find Best Finger: 2nd case!");
			}
		}

		return bestPeer;
	}

}
