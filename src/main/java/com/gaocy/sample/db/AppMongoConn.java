package com.gaocy.sample.db;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.DisposableBean;

public class AppMongoConn implements DisposableBean{
	private static String connectionString;
	private static String dbName;
	private static MongoClient mongo;

	public static DB getDB() {
		return mongo.getDB(dbName);
	}

	@Override
	public void destroy() throws Exception {
		if (mongo != null) {
			mongo.close();
		}
	}

	public static MongoClient getMongo() {
		return mongo;
	}

	public static void setMongo(MongoClient mongo) {
		AppMongoConn.mongo = mongo;
	}

	public static String getConnectionString() {
		return connectionString;
	}

	public static void setConnectionString(String connectionString) {
		AppMongoConn.connectionString = connectionString;
	}

	public static String getDbName() {
		return dbName;
	}

	public static void setDbName(String dbName) {
		AppMongoConn.dbName = dbName;
	}
}