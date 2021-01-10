package com.syc.portknock;

public class UDP {
	static private final int MIN_MTU = 576; // Ethernet I
	static private final int MAX_IP_HEADER_SIZE = 60;
	static private final int UDP_HEADER_SIZE = 8;
	static public int MAX_DATAGRAM_SIZE = MIN_MTU - MAX_IP_HEADER_SIZE - UDP_HEADER_SIZE;
}
