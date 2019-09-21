package com.cs739.kvstore.datastore;

public class DataStoreFactory {

	static DataStore sInstance = null;

	public static DataStore createDataStore(int port) {
		sInstance = new SQLDataStore(port);
		return sInstance;
	}
	public static DataStore getDataStore() {
		return sInstance;
	}
}