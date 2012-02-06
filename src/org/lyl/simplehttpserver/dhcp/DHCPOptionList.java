package org.lyl.simplehttpserver.dhcp;

import java.net.InetAddress;
import java.util.ArrayList;

import org.dhcp4java.DHCPOption;

public class DHCPOptionList {
	private ArrayList<DHCPOption> list = new ArrayList<DHCPOption>();
	
	public DHCPOptionList() {
		
	}
	
	public void add(byte code, InetAddress address) {
		list.add(DHCPOption.newOptionAsInetAddress(code, address));
	}
	
	public void add(byte code, int value) {
		list.add(DHCPOption.newOptionAsInt(code, value));
	}
	
	public void add(byte code, byte value) {
		list.add(DHCPOption.newOptionAsByte(code, value));
	}
	
	public void add(byte code, short value) {
		list.add(DHCPOption.newOptionAsShort(code, value));
	}
	
	public void add(byte code, String value) {
		list.add(DHCPOption.newOptionAsString(code, value));
	}
	
	public void add(byte code, short[] values) {
		list.add(DHCPOption.newOptionAsShorts(code, values));
	}
	
	public void add(byte code, InetAddress[] addresses) {
		list.add(DHCPOption.newOptionAsInetAddresses(code, addresses));
	}
	
	public DHCPOption[] toArray() {
		DHCPOption[] a = new DHCPOption[list.size()];
		return list.toArray(a);
	}
}
