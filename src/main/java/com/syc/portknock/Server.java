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
	private static final Map<ClientAddress, AddressStore> packetCountsSent = new HashMap<>();

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
				logger.log(Level.SEVERE, "Can't re open port, socket already in use" + port);
			}
			byte[] receive = new byte[UDP.MAX_DATAGRAM_SIZE];

			DatagramPacket dpReceive;
			while (true) {
				dpReceive = new DatagramPacket(receive, receive.length);
				try {
					assert ds != null;
					ds.receive(dpReceive);
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Restarting UDP Server socket" + port);
				}
				String data = new String(dpReceive.getData(), 0, dpReceive.getLength()).trim();
				InetAddress clientAddress = dpReceive.getAddress();
				int clientPort = dpReceive.getPort();
				ClientAddress address = new ClientAddress(clientAddress, clientPort);
				List<String> updatedData;
				if (packetCountsSent.containsKey(address)) {
					updatedData = packetCountsSent.get(address).getData();
				} else {
					updatedData = new LinkedList<>();
				}
				updatedData.add(data);
				packetCountsSent.put(new ClientAddress(clientAddress, clientPort), new AddressStore(updatedData));
				logger.log(Level.INFO, "Packet added to server, current size of packets received:"
						+ packetCountsSent.get(address).getData().size());
				// exit when the request is "bye"
				if (data.equalsIgnoreCase("bye")) {
					logger.log(Level.INFO, "UDP Client sent bye.....EXITING");
					process(ds, port, address);
				}
				// Clear the buffer after every message.
				receive = new byte[UDP.MAX_DATAGRAM_SIZE];
			}
		}).start();
	}

	private static int process(DatagramSocket datagramSocket, int initialDSPort, ClientAddress processAddress) {
		int minPort = 1025;
		int maxPort = 65536;
		int port = (int) (Math.random() * (maxPort - minPort + 1) + minPort);
		byte[] sendResp = String.valueOf(port).getBytes();
		DatagramPacket resp = new DatagramPacket(sendResp, sendResp.length, processAddress.getAddress(),
				processAddress.getPort());
		try {
			datagramSocket.send(resp);
		} catch (IOException e) {
			logger.log(Level.INFO, "Error Occurred while sending response: " + e.getMessage());
		}
		Socket client = null;
		ServerSocket server;
		try {
			server = new ServerSocket(port);
			client = server.accept();
			logger.log(Level.INFO, "Client accepted");
		} catch (IOException e) {
			logger.log(Level.INFO, "Accept failed");
			System.exit(-1);
		}
		logger.log(Level.INFO, "starting TCP server thread at port: " + port);
		(new TCPServerThread(client, packetCountsSent.get(processAddress).getData())).start();
		return 0;
	}
}
