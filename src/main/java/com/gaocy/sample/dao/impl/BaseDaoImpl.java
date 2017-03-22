package com.gaocy.sample.dao.impl;

import com.gaocy.sample.dao.BaseDao;
import com.gaocy.sample.db.AppMongoConn;
import com.mongodb.DB;
import org.springframework.stereotype.Repository;

@Repository
public class BaseDaoImpl implements BaseDao {

	protected static DB db;

	static {
		// db = AppMongoConn.getDB();
	}
}