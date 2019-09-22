package com.cs739.kvstore.datastore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLDataStore implements DataStore {

	String mDbName;
	Connection mDatabaseConnection;

	public SQLDataStore(int port) {
		mDbName = "kvstore_" + Integer.toString(port) + ".db";
		establishConnection();
		try {
			createDataStoreIfNotExists();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public PutValueResponse putValue(String key, String value, PutValueRequest type, int updateSequenceNumber) {
		// First, need to check old value
		String queryForPresenceOfKey = "SELECT * FROM kvstore_schema where key=\"" + key + "\"";
		try {
			ResultSet res = executeQuery(queryForPresenceOfKey);
			int size = 0;
			String oldValue = null;
			int seqToReturn = 0;
			Integer stored_seqno = 0;
			while(res.next()) {
				size++;
				oldValue = res.getString("value");
				stored_seqno = res.getInt("sequence_number");
			}
			assert(size == 0 || size == 1);
			if(size == 0 ) {
				// need to insert value for key
				int seqno = 0;
				boolean isDirty = false;
				if (type == PutValueRequest.APPLY_FOLLOWER_PERSIST) {
					seqno = updateSequenceNumber;
					isDirty = false;
				} else if (type == PutValueRequest.APPLY_FOLLOWER_UPDATE) {
					seqno = stored_seqno;
					isDirty = true;
				} else if (type == PutValueRequest.APPLY_PRIMARY_UPDATE) {
					seqno = stored_seqno + 1;
					isDirty = false;
				}
				System.out.println(stored_seqno + ":" + seqno);
				if(seqno >= stored_seqno) {
					seqToReturn = seqno;
					String insertQuery = new StringBuilder("INSERT INTO kvstore_schema values(\"")
							.append(key)
							.append("\",\"")
							.append(value)
							.append("\",")
							.append(seqno)
							.append(",\"")
							.append(isDirty)
							.append("\")").toString();
					System.out.println(insertQuery);
					executeUpdate(insertQuery);
				}
			} else {
				// need to update value for key
				int seqno = stored_seqno;
				boolean isDirty = false;
				if(type == PutValueRequest.APPLY_FOLLOWER_UPDATE) {
					seqno = stored_seqno;
					isDirty = true;
				} else if (type == PutValueRequest.APPLY_FOLLOWER_PERSIST) {
					seqno = updateSequenceNumber;
					isDirty = false;
				} else if (type == PutValueRequest.APPLY_PRIMARY_UPDATE) {
					seqno = stored_seqno + 1;
					isDirty = false;
				}
				if(seqno >= stored_seqno) {
					seqToReturn = seqno;
					String updateQuery = new StringBuilder("UPDATE kvstore_schema set value=\"")
			             	.append(value)
			             	.append("\",sequence_number=")
			             	.append(seqno)
			             	.append(",dirty=\"")
			             	.append(isDirty)
			             	.append("\" where key=\"")
			                .append(key)
			                .append("\"").toString();
					System.out.println(updateQuery);
					executeUpdate(updateQuery);
				}
			}
			return new PutValueResponse(oldValue, seqToReturn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getValue(String key) {
		String queryForPresenceOfKey = "SELECT * FROM kvstore_schema where key=\"" + key + "\"";
		try {
			ResultSet res = executeQuery(queryForPresenceOfKey);
			int size = 0;
			String value = null;
			while(res.next()) {
				size++;
				value = res.getString("value");
			}
			assert(size == 0 || size == 1);
			if(size == 0) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void establishConnection() {
		String jdbc_url = "jdbc:sqlite:" + mDbName;
		try {
			mDatabaseConnection = DriverManager.getConnection(jdbc_url);
			System.out.println(
					"Connection to SQLite has been established for client "
							+ mDbName);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private int createDataStoreIfNotExists() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS kvstore_schema(key char[128], value char[2048], sequence_number int, dirty boolean, PRIMARY KEY (key))";
		return (executeUpdate(query));
	}
	
	private int executeUpdate(String query) throws SQLException {
		Statement st = null;
		try {
			st = mDatabaseConnection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			return st.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private ResultSet executeQuery(String query) throws SQLException {
		Statement st = null;
		try {
			st = mDatabaseConnection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			return st.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}