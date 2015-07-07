package org.okinawaopenlabs.orientdb.utils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapListHandler extends org.apache.commons.dbutils.handlers.MapListHandler {
	private final String[] labels;

	/**
	 * When instanced by this constructor, process is same as org.apache.commons.dbutils.handlers.MapListHandler.
	 */
	public MapListHandler() {
		super();
		this.labels = null;
	}
	/**
	 * When instanced by this constructor, get map-list by the key that is specified labels.
	 * @param labels
	 */
	public MapListHandler(String... labels) {
		super();
		this.labels = labels;
	}

	/**
	 * Handler method for QueryRunner.
	 */
	@Override
	public List<Map<String, Object>> handle(ResultSet rs) throws SQLException {
		if (this.labels == null) {
			return super.handle(rs);
		}

		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (String label : labels) {
				map.put(label, rs.getObject(label));
			}
			ret.add(map);
		}
		return ret;
	}

}