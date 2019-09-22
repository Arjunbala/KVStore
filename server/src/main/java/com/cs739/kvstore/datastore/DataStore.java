package com.cs739.kvstore.datastore;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public interface DataStore {
	/**
	 * Put a value into the datastore
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public PutValueResponse putValue(String key, String value, PutValueRequest type, int updatedSeqNumber, List<Integer> servers, 
			CopyOnWriteArrayList<Boolean> serverStatus, BlockingQueue<String> blockingQueue);

	/**
	 * Get a value from datastore
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key);
	
	public Integer getSequenceNumber(String key);
}