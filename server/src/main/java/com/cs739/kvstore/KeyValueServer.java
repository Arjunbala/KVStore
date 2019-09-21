package com.cs739.kvstore;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

import com.cs739.kvstore.datastore.DataStore;
import com.cs739.kvstore.datastore.DataStoreFactory;
import com.google.gson.*;

public class KeyValueServer {

	static DataStore mDataStore = null;

	public static void main(String[] args) throws IOException {
		mDataStore = DataStoreFactory.getDataStore(Integer.parseInt(args[0]));
		try (ServerSocket listener = new ServerSocket(Integer.parseInt(args[0]),
				0, InetAddress.getByName("127.0.0.1"))) {
			System.out.println("The key value server is running...");
			ExecutorService pool = Executors.newFixedThreadPool(20);
			ConcurrentHashMap m = new ConcurrentHashMap();
			m.put("100", "Danish");
			m.put("200", "Arjun");
			m.put("300", "Danny");
			while (true) {
				pool.execute(new ClientRequestHandler(listener.accept(), m));
			}
		}
	}

	private static class ClientRequestHandler implements Runnable {
		private Socket socket;
		private ConcurrentHashMap m;

		ClientRequestHandler(Socket socket, ConcurrentHashMap m) {
			this.socket = socket;
			this.m = m;
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
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (in.hasNextLine()) {
				String request = in.nextLine();
				System.out.println("Client Request: " + request);
				JsonObject jsonObject = new JsonParser().parse(request)
						.getAsJsonObject();
				String operation = jsonObject.get("operation").getAsString();
				if (operation.equals("GET")) {
					String key = jsonObject.get("key").getAsString();
					out.println(mDataStore.getValue(key));
					//out.println(m.get(key));
					// Comment out for now to test DB
				} else if (operation.equals("PUT")) {
					String key = jsonObject.get("key").getAsString();
					String value = jsonObject.get("value").getAsString();
					String oldValue = mDataStore.putValue(key, value, false, true, -1);
					if(oldValue == null) {
						oldValue = "";
					}
					// Comment out for now to test DB
					/*if (m.containsKey(key)) {
						oldValue = (String) m.get(key);
					}
					m.put(key, value);*/
					out.println(oldValue);
				}
			}
			in.close();
			out.close();
		}
	}
}
