package com.syc.portknock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServerThread extends Thread {
	private static final Logger logger = Logger.getLogger(TCPServerThread.class.getName());
	private final Socket socket;
	private final List<String> data;

	public TCPServerThread(Socket socket, List<String> data) {
		this.socket = socket;
		this.data = data;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("Total packets received from UDP client: " + data.size()+", Closing TCP server connection!!");
			String readClient = in.readLine();
			socket.close();
			logger.log(Level.INFO,"Message from TCP client:" + readClient);
		} catch (IOException e1) {
			logger.log(Level.INFO,"No I/O");
		}
	}

}
