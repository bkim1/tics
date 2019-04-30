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

import object.*;

public class Utilities {

	public static Peer lookUp(Message msg, Peer[] fingerTable) {
		Peer finger = null;
		int length = fingerTable.length;
		for(int i = 0; i < length; i++) {
			finger = fingerTable[i];
			
			if(finger.equals(msg.getPeer())) {
				return null;
			}
			else if(msg.getKey() == finger.getKey()) {
				return finger;
			}
			//if we reach a node whose successor's key is larger than the file hash, we return the successor
			else if(msg.getKey() > finger.getKey() && i+1 < length && fingerTable[i+1].getKey() > msg.getKey()) {
				try {
					Peer next = fingerTable[i+1];
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
				return fingerTable[i+1];
			}
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

}
