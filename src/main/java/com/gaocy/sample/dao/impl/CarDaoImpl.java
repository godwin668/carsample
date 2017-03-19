package com.gaocy.sample.dao.impl;

import com.gaocy.sample.dao.CarDao;
import com.gaocy.sample.vo.CarVo;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CarDaoImpl extends BaseDaoImpl implements CarDao, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		if (null != db) {
			DBCollection coll;
			coll = db.getCollection(COLL_DOC);
			coll.createIndex(BasicDBObjectBuilder.start().add(_ID, 1).get());
			coll.createIndex(BasicDBObjectBuilder.start().add(UUID, 1).get());
			coll.createIndex(BasicDBObjectBuilder.start().add(CTIME, 1).get());
			coll.createIndex(BasicDBObjectBuilder.start().add(STATUS, 1).get());
			coll.createIndex(BasicDBObjectBuilder.start().add(STATUS_CONVERT, 1).get());
		}
	}


	@Override
	public void add(String app, String uid, String rid, String uuid, String md5, String name, int size, String ext, int status, String labelId, Map<String, Object> metas, String url) throws Exception {

	}

	@Override
	public boolean delete(String uuid, boolean isDeleteRecord) throws Exception {
		return false;
	}

	@Override
	public boolean updateFieldById(String id, String name, Object value) throws Exception {
		return false;
	}

	@Override
	public boolean updateFieldByUuid(String uuid, String name, Object value) throws Exception {
		return false;
	}

	@Override
	public CarVo get(String rid, boolean includeDeleted) throws Exception {
		return null;
	}

	@Override
	public List<CarVo> list(String app, int offset, int limit, String labelId, String searchString, QueryOrder queryOrder, int status) throws Exception {
		return null;
	}
}