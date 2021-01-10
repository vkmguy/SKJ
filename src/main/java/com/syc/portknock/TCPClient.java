package com.syc.portknock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPClient {
	private static final Logger logger = Logger.getLogger(TCPClient.class.getName());
	private final int tcpPort;
	public TCPClient(int tcpPort) {
		this.tcpPort = tcpPort;
	}

	public void tcpRequest() {
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		// Open TCP connection
		try {
			String server_host_address = InetAddress.getLocalHost().getHostAddress();
			socket = new Socket(server_host_address, tcpPort);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE,"Unknown host");
			System.exit(-1);
		} catch (IOException e) {
			logger.log(Level.SEVERE,"No I/O");
			System.exit(-1);
		}

		// Communicate
		try {
			out.println("Hello from TCP client port:" + tcpPort);
			String line;
			line = in.readLine();
			if (line != null) {
				logger.log(Level.INFO,"Message from TCP Server:"+ line);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE,"Error during communication");
			System.exit(-1);
		}

		// Close TCP connection
		try {
			logger.log(Level.INFO,"Closing TCP client connection...");
			socket.close();
		} catch (IOException e) {
			logger.log(Level.INFO,"Cannot close the socket");
			System.exit(-1);
		}
	}

}
