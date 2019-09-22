package com.cs739.kvstore.datastore;

import java.util.List;

public interface DataStore {
	/**
	 * Put a value into the datastore
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public PutValueResponse putValue(String key, String value, PutValueRequest type, int updatedSeqNumber, List<Integer> servers);

	/**
	 * Get a value from datastore
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key);
	
	public Integer getSequenceNumber(String key);
}