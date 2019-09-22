package com.cs739.kvstore;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import com.cs739.kvstore.datastore.DataStore;
import com.cs739.kvstore.datastore.DataStoreFactory;
import com.cs739.kvstore.datastore.PutValueRequest;
import com.cs739.kvstore.datastore.PutValueResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClientRequestHandlerThread implements Runnable {
	private Socket socket;
	private int externalPort;
	private List<Integer> servers;
	private BlockingQueue<String> blockingQueue;
	private DataStore dataStore;
	public ClientRequestHandlerThread(Socket socket,
			BlockingQueue<String> blockingQueue, List<Integer> servers, int externalPort) {
		this.socket = socket;
		this.dataStore = DataStoreFactory.getDataStore();
		this.blockingQueue = blockingQueue;
		this.servers = servers;
		this.externalPort = externalPort;
	}
	
	public int hashFunc(String key) {
		System.out.println();
		return key.hashCode() % servers.size();
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
			String request = in.nextLine();
			System.out.println("Client Request: " + request);
			JsonObject jsonObject = new JsonParser().parse(request).getAsJsonObject();
			String operation = jsonObject.get("operation").getAsString();
			if (operation.equals("GET")) {
				String key = jsonObject.get("key").getAsString();
				out.println(dataStore.getValue(key));
			} else if (operation.equals("PUT")) {
				// TODO: Put hash logic
				String key = jsonObject.get("key").getAsString();
				String value = jsonObject.get("value").getAsString();
				int primary = hashFunc(key);
				// This is the primary
				if (servers.get(primary) == externalPort) {
					// TODO : Continue here
					PutValueResponse putValueResponse = dataStore.putValue(key, value, PutValueRequest.APPLY_PRIMARY_UPDATE, -1);
					out.println(putValueResponse.getOldValue());
					jsonObject.addProperty("seq", putValueResponse.getSequenceNumber());
					// Broadcast to other servers
					blockingQueue.add(jsonObject.toString());
				} else {
					PutValueResponse putValueResponse = dataStore.putValue(key, value, PutValueRequest.APPLY_FOLLOWER_UPDATE, -1);
					out.println(putValueResponse.getOldValue());
					System.out.println("Primary server is " + servers.get(primary) + " and " + socket.getPort());
					// Some other server is primary
					Socket primarySocket = null;
					try {
						primarySocket = new Socket("127.0.0.1", servers.get(primary));
					} catch (Exception e) {
						e.printStackTrace();
					}
					PrintWriter primaryWriter = null;
					try {
						primaryWriter = new PrintWriter(primarySocket.getOutputStream(), true);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					primaryWriter.println(jsonObject.toString());
					// Forward it
				}
			}
		}
		in.close();
		out.close();	
	}

}
