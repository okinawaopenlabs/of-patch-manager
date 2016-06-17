/*
 *   Copyright 2015 Okinawa Open Laboratory, General Incorporated Association
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

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