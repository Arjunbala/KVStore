package com.cs739.kvstore;

import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.List;

import com.cs739.kvstore.datastore.DataStore;
import com.cs739.kvstore.datastore.DataStoreFactory;
import com.cs739.kvstore.datastore.PutValueRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MulticastReceiverThread implements Runnable {
	private MulticastSocket socket;
	private DataStore dataStore;
	private List<Integer> servers;

	public MulticastReceiverThread(MulticastSocket socket, List<Integer> servers) {
		this.socket = socket;
		this.dataStore = DataStoreFactory.getDataStore();
		this.servers = servers;
	}

	@Override
	public void run() {
		System.out.println("Started multicast receiver thread...");
		while(true) {
			byte[] buf = new byte[4096];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String received = new String(packet.getData());
			System.out.println("Received Packet: " + received);
			JsonObject jsonObject = new JsonParser().parse(received.trim()).getAsJsonObject();
			String key = jsonObject.get("key").getAsString();
			String value = jsonObject.get("value").getAsString();
			int updateSequenceNumber = jsonObject.get("seq").getAsInt();
			dataStore.putValue(key, value, PutValueRequest.APPLY_FOLLOWER_UPDATE, updateSequenceNumber, servers); 
		}		
	}

}
