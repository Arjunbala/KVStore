package com.cs739.kvstore.datastore;

public class DataStoreFactory {

	static DataStore sInstance = null;

	public static DataStore getDataStore(int port) {
		if (sInstance == null) {
			sInstance = new SQLDataStore(port);
		}
		return sInstance;
	}
}