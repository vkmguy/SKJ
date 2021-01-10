package com.syc.portknock;

import java.util.List;

public class AddressStore {
	
	private final int port;
	private final List<String> data;
	public int getPort() {
		return port;
	}
	public List<String> getData() {
		return data;
	}

	public AddressStore(int port, List<String> data) {
		super();
		this.port = port;
		this.data = data;
	}
}
