package utils;

import static utils.Constants.RING_SIZE;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import object.*;

public class Utilities {

	public static Peer lookUp(Message msg, List<Peer> fingerTable) {
		Peer finger;
		for(int i = 0; i < fingerTable.size(); i++) {
			finger = fingerTable.get(i);
			long fingerHash = peerHash(finger.getIP(), finger.getPort());
			if(msg.getKey() == fingerHash) {
				return finger;
			}
			//if we reach a Node whose hash is larger, we have to return the previous Node
			else if(msg.getKey() < fingerHash) {
				return fingerTable.get(i-1);
			}
		}
		return null;
	}
	
	public static long peerHash(InetAddress ip, int port) {
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