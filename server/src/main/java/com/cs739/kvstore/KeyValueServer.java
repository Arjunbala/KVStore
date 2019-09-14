package com.cs739.kvstore;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class KeyValueServer {
	public static void main(String[] args) throws IOException {
		try (ServerSocket listener = new ServerSocket(Integer.parseInt(args[0]),
				0, InetAddress.getByName("127.0.0.1"))) {
			System.out.println("The key value server is running...");
			ExecutorService pool = Executors.newFixedThreadPool(20);
			
			while (true) {
                pool.execute(new ClientRequestHandler(listener.accept()));
            }
		}
	}
	
	private static class ClientRequestHandler implements Runnable {
        private Socket socket;

        ClientRequestHandler(Socket socket) {
            this.socket = socket;
        }

		@Override
		public void run() {
			System.out.println("Accepted connection from client");
			Scanner in = null;
			try {
				in = new Scanner(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			PrintWriter out = null;
			try {
				out = new PrintWriter(socket.getOutputStream(),
						true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (in.hasNextLine()) {
				System.out.println("Client Request: " + in.nextLine());
				out.println("Arjun Server");
			}
			in.close();
			out.close();
		}
	}
}
