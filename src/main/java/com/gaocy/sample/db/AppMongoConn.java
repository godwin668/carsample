package com.gaocy.sample.db;

import com.gaocy.sample.util.ConfUtil;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.DisposableBean;

public class AppMongoConn implements DisposableBean{
	private static String connectionString;
	private static String dbName;
	private static MongoClient mongo;

	static {
		connectionString = ConfUtil.getString("db.mongodb.connection.string");
		dbName = connectionString.replaceFirst("mongodb://[^/]+/([^\\?]+).*", "$1");
		MongoClientURI clientURI = new MongoClientURI(connectionString);
		mongo = new MongoClient(clientURI);
		System.err.println("##[MONGO INFO] mongoOptions: " + mongo.getMongoClientOptions().toString());
	}

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