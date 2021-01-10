package com.syc.portknock;

import java.util.List;

public class AddressStore {

	private final List<String> data;

	public List<String> getData() {
		return data;
	}

	public AddressStore(List<String> data) {
		this.data = data;
	}

}
