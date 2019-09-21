package com.cs739.kvstore;

import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MulticastReceiverThread implements Runnable {
	private MulticastSocket socket;
	private ConcurrentHashMap<String, String> serverCache;

	public MulticastReceiverThread(MulticastSocket socket, ConcurrentHashMap<String, String> serverCache) {
		this.socket = socket;
		this.serverCache = serverCache;
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
			serverCache.put(key, value); 
		}		
	}

}
