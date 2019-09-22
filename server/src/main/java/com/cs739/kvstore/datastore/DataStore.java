package com.cs739.kvstore.datastore;

public interface DataStore {
	/**
	 * Put a value into the datastore
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public PutValueResponse putValue(String key, String value, boolean isDirty, boolean forceUpdate, Integer updateSequenceNumber);

	/**
	 * Get a value from datastore
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key);
}