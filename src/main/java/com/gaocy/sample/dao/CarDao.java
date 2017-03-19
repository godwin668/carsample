package com.gaocy.sample.dao;

import com.gaocy.sample.vo.InfoVo;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public interface CarDao {

	public enum QueryOrder {
		
		ASC("asc"), DESC("desc");

		private String field;
		private String direction;
		
		private QueryOrder(String direction) {
			this.direction = direction;
		}

		public static QueryOrder getQueryOrder(String field, String direction) {
			if (StringUtils.isBlank(field)) {
				return null;
			}
			if ("desc".equalsIgnoreCase(direction)) {
				return QueryOrder.DESC.setField(field);
			} else {
				return QueryOrder.ASC.setField(field);
			}
		}

		public String getField() {
			return field;
		}

		public QueryOrder setField(String field) {
			this.field = field;
			return this;
		}

		public String getDirection() {
			return direction;
		}

		public void setDirection(String direction) {
			this.direction = direction;
		}

	}

	/**
	 * 添加文档
	 * 
	 * @param app
	 *            应用id
	 * @param uid
	 *            用户id
	 * @param rid
	 *            文档id（用于主键，文档存储路径）
	 * @param uuid
	 *            文档唯一标示id
	 * @param name
	 *            名称
	 * @param size
	 *            大小
	 * @param ext
	 *            扩展名
	 * @param status
	 *            文档状态，-1：已删除；0：私有文档；1：公开文档
	 * @param labelId
	 *            文档所属标签id
	 * @param metas
	 *            文件其它元数据
	 * @throws Exception
	 */
	void add(String app, String uid, String rid, String uuid, String md5, String name, int size, String ext, int status, String labelId, Map<String, Object> metas, String url) throws Exception;

	/**
	 * Delete a document.
	 * 
	 * @param uuid
	 * @param isDeleteRecord
	 *            whether delete the record from collection
	 * @return
	 * @throws Exception
	 */
	boolean delete(String uuid, boolean isDeleteRecord) throws Exception;

	/**
	 * update field
	 * 
	 * @param id
	 * @param name
	 * @param value if NULL, unset the field
	 * @return
	 * @throws Exception
	 */
	boolean updateFieldById(String id, String name, Object value) throws Exception;
	
	/**
	 * update field
	 * 
	 * @param uuid
	 * @param name
	 * @param value if NULL, unset the field
	 * @return
	 * @throws Exception
	 */
	boolean updateFieldByUuid(String uuid, String name, Object value) throws Exception;

	/**
	 * get Doc by rid.
	 * 
	 * @param rid
	 * @param includeDeleted whether include deleted doc
	 * @return
	 */
	InfoVo get(String rid, boolean includeDeleted) throws Exception;
	
	/**
	 * List Application documents
	 * 
	 * @param app
	 * @param offset
	 * @param limit
	 * @param labelId
	 * @param searchString
	 * @param queryOrder
	 * @param status
	 *            -1：包括已删除文档；0：包括私有文档；1：只列出公开文档
	 * @return
	 * @throws Exception
	 */
	List<InfoVo> list(String app, int offset, int limit, String labelId, String searchString, QueryOrder queryOrder, int status) throws Exception;
}