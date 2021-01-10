package com.syc.portknock;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
	private static final Logger logger = Logger.getLogger(Server.class.getName());
	private static volatile Map<InetAddress, AddressStore> packetCountsSent = new HashMap<>();

	public static void main(String[] args) {
		for (String thePort : args) {
			service(Integer.parseInt(thePort));
		}
	}

	private static void service(int port) {
		logger.log(Level.INFO, "Starting UDP server in port: " + port);
		new Thread(() -> {
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket(port);
			} catch (SocketException e) {
				logger.log(Level.SEVERE,"Can't open port"+ port);
			}
			byte[] receive = new byte[UDP.MAX_DATAGRAM_SIZE];

			DatagramPacket dpReceive;
			List<String> updatedData = new LinkedList<>();
			while (true) {
				dpReceive = new DatagramPacket(receive, receive.length);
				try {
					assert ds != null;
					ds.receive(dpReceive);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String data = new String(dpReceive.getData(), 0, dpReceive.getLength()).trim();
				InetAddress clientAddress = dpReceive.getAddress();
				int clientPort = dpReceive.getPort();
				if (packetCountsSent.entrySet().size() != 0) {
					updatedData = packetCountsSent.get(clientAddress).getData();
				}
				updatedData.add(data);
				packetCountsSent.put(clientAddress, new AddressStore(clientPort, updatedData));
				logger.log(Level.INFO,"Packet added to server, current size of packets received:"
						+ packetCountsSent.get(clientAddress).getData().size());
				// exit when the request is "bye"
				if (data.equalsIgnoreCase("bye")) {
					logger.log(Level.INFO, "UDP Client sent bye.....EXITING");
					process(ds);
				}
				// Clear the buffer after every message.
				receive = new byte[UDP.MAX_DATAGRAM_SIZE];
			}
//			process(ds);
		}).start();
	}

	private static void process(DatagramSocket datagramSocket) {
		for (InetAddress address : packetCountsSent.keySet()) {
			int minPort = 1025;
			int maxPort = 65536;
			int port = (int) (Math.random() * (maxPort - minPort + 1) + minPort);
			byte[] sendResp = String.valueOf(port).getBytes();
			DatagramPacket resp = new DatagramPacket(sendResp, sendResp.length, address,
					packetCountsSent.get(address).getPort());
			int datagramSocketPort = datagramSocket.getPort();
			logger.log(Level.INFO, "the value of dataGramPort " + datagramSocketPort);
			try {
				datagramSocket.send(resp);
//				datagramSocket.close();
			} catch (IOException e) {
				logger.log(Level.INFO, "Error Occurred while sending response: " + e.getMessage());
			}
			Socket client = null;
			ServerSocket server;
			try {
				server = new ServerSocket(port);
				client = server.accept();
				logger.log(Level.INFO,"Client accepted");
			} catch (IOException e) {
				logger.log(Level.INFO,"Accept failed");
				System.exit(-1);
			}
			Map.Entry<InetAddress, AddressStore> existingEntry = packetCountsSent.entrySet().iterator().next();
			logger.log(Level.INFO,"starting TCP server thread at port: "+ port);

			//reset packetCountsSent
			packetCountsSent = new HashMap<>();
			(new TCPServerThread(client, existingEntry.getValue().getData())).start();
		}
	}
}
