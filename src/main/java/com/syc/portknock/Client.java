package com.syc.portknock;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
	private static final Logger logger = Logger.getLogger(Client.class.getName());
	public static void main(String[] args) throws IOException {
		final String ip = args[0];
		for (int i=1;i<args.length;i++) {
			request(ip, Integer.parseInt(args[i]));
		}
	}

	@SuppressWarnings("resource")
	public static void request(String ip, int port) {

		new Thread(() -> {
			logger.log(Level.INFO, "Creating  thread ID: "+ Thread.currentThread().getId());
		Scanner sc = new Scanner(System.in);
			DatagramSocket ds = null;
			InetAddress theIp = null;
			try {
				ds = new DatagramSocket();
				theIp = InetAddress.getByName(ip);
			} catch (UnknownHostException | SocketException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
			byte[] endBuf;
		while (true) {
			String inp = sc.nextLine();
			endBuf = inp.getBytes();
			DatagramPacket endReq = new DatagramPacket(endBuf, endBuf.length, theIp, port);
			try {
				assert ds != null;
				ds.send(endReq);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
			if (inp.equalsIgnoreCase("bye"))
				break;
		}
		byte[] respBuff = new byte[UDP.MAX_DATAGRAM_SIZE];
		DatagramPacket packet = new DatagramPacket(respBuff, respBuff.length);
		logger.log(Level.INFO, "Waiting for TCP server response...");
			try {
				ds.receive(packet);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
			String tcpPort = new String(packet.getData(), 0, packet.getLength()).trim();
		new TCPClient(Integer.parseInt(tcpPort)).tcpRequest();
		ds.close();
	}).start();
	}
}
