package org.lyl.simplehttpserver.dhcp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Utils {
	private Utils() {
		
	}
	
	public static InetAddress parse(String ip) {
		byte[] bytes = ipStringToByteArray(ip);
		InetAddress ret;
		try {
			ret = InetAddress.getByAddress(bytes);
		} catch (UnknownHostException e) {
			throw new InvalidParameterException("Unknown host!");
		}
		return ret;
	}
	
	public static byte[] ipStringToByteArray(String ip) {
		String[] strs = ip.split("\\.");
		if(strs.length != 4) 
			throw new InvalidParameterException("Not a IP address!");
		int[] ints = new int[4];
		for(int i = 0; i < 4 ; i++) {
			ints[i] = Integer.parseInt(strs[i]);
			if(ints[i] < 0 || ints[i] > 255) 
				throw new InvalidParameterException("Invalid IP address!");
		}
		byte[] bytes = new byte[]{(byte)ints[0], (byte)ints[1], (byte)ints[2], (byte)ints[3]};
		return bytes;
	}
	
	public static int byteArrayToInteger(byte[] array) {
		if(array.length != 4) {
			throw new InvalidParameterException("Byte array must have 4 elements.");
		}
		return ((array[0] & 0xFF) << 24) |
			   ((array[1] & 0xFF) << 16) |
			   ((array[2] & 0xFF) << 8)	 |
			   ((array[3] & 0xFF));
	}
	
	public static String integerToIPString(int value) {
		int[] byteValue = { (int) ((value & 0xFF000000) >>> 24),
                			(int) ((value & 0X00FF0000) >>> 16),
                			(int) ((value & 0x0000FF00) >>>  8),
                			(int) ((value & 0x000000FF)) };
		return String.format("%d.%d.%d.%d", byteValue[0], byteValue[1], 
											byteValue[2], byteValue[3]);
	}
	
	public static String[] listIPRange(String start, String end) {
		 int startI = byteArrayToInteger(ipStringToByteArray(start));
		 int endI = byteArrayToInteger(ipStringToByteArray(end));
		 List<String> ret = new ArrayList<String>();
		 for(int i = startI; i <= endI; i++) {
			 ret.add(integerToIPString(i));
		 }
		 String[] type = new String[]{};
		 return ret.toArray(type);
	}
}
