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

package org.okinawaopenlabs.orientdb.client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.ENABLE_DEVICE_TYPES;
import static org.okinawaopenlabs.constants.OfpmDefinition.STATUS_NOTFOUND;
import static org.okinawaopenlabs.constants.OrientDBDefinition.*;

import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.device.DeviceInfo;
import org.okinawaopenlabs.ofpm.utils.OFPMUtils;
import org.okinawaopenlabs.orientdb.utils.handlers.MapListHandler;

public class DaoImpl implements Dao {

	private static final Logger logger = Logger.getLogger(DaoImpl.class);

	protected ConnectionUtils utils = null;
	protected ODatabaseDocumentTx database = null;
	protected List<ODocument> documents = null;

	// jdbc
	protected ConnectionUtilsJdbc utilsJdbc = null;

	public DaoImpl(ConnectionUtils utils) throws SQLException {
		if (logger.isTraceEnabled()){
			logger.trace(String.format("DaoImpl(utils=%s) - start", utils));
		}
		this.utils = utils;
		init();
		if (logger.isTraceEnabled()){
			logger.trace("DaoImpl() - end");
		}
	}

	// jdbc
	public DaoImpl(ConnectionUtilsJdbc utils) {
		if (logger.isTraceEnabled()){
			logger.trace(String.format("DaoImpl(utils=%s) - start", utils));
		}
		this.utilsJdbc = utils;
		if (logger.isTraceEnabled()){
			logger.trace("DaoImpl() - end");
		}
	}

	// default constructor
	public DaoImpl() {
		if (logger.isTraceEnabled()){
			logger.trace(String.format("DaoImpl() - start"));
		}
		if (logger.isTraceEnabled()){
			logger.trace("DaoImpl() - end");
		}
	}

	// connectionUtil setter
	public void setConnectionUtilsJdbc(ConnectionUtilsJdbc utils) {
		this.utilsJdbc = utils;
	}

	synchronized private void init() throws SQLException {
		if (logger.isTraceEnabled()){
			logger.trace("init() - start");
		}
		database = utils.getDatabase();
		if (logger.isTraceEnabled()){
			logger.trace("init() - end");
		}
	}

	synchronized public void close() throws SQLException {
		if (logger.isTraceEnabled()){
			logger.trace("close() - start");
		}
		if (database != null && !database.isClosed()) {
			utils.close(database);
		}
		if (logger.isTraceEnabled()){
			logger.trace("close() - end");
		}
	}

	/* (non-Javadoc)
	 * @see org.okinawaopenlabs.orientdb.client.Dao#getDeviceNameFromDatapathId(java.lang.String)
	 */
	@Override
	public String getDeviceNameFromDatapathId(Connection conn, String datapathId) throws SQLException {
		final String fname = "getDeviceNameFromDatapathId";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, datapathId=%s) - start", fname, conn, datapathId));
		}
		String ret = null;
		try {
			List<Map<String, Object>> records = utilsJdbc.query(conn, SQL_GET_DEVICENAME_FROM_DATAPATHID,
                    new MapListHandler(), datapathId);
			if (records.size() <= 0) {
                throw new Exception(String.format(NOT_FOUND, datapathId));
			}
			ret = records.get(0).get("name").toString();
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", ret));
			}
		}
	}

	@Override
	public String getPortRidFromDeviceNamePortNumber(Connection conn, String deviceName, Integer portNumber) throws SQLException {
		final String fname = "getPortRidFromDeviceNamePortNumber";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(deviceName=%s, portNumber=%s) - start", fname, deviceName, portNumber));
		}
		String ret = null;
		try {
			List<Map<String, Object>> records = utilsJdbc.query(conn, SQL_GET_PORTRID_FROM_DEVICENAME_PORTNUMBER,
                    new MapListHandler(), deviceName, portNumber);
			if (records.size() <= 0) {
				throw new Exception(String.format(NOT_FOUND, deviceName + "," + portNumber));
			}
			ret = records.get(0).get("rid").toString();
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", ret));
			}
		}
	}

	@Override
	public List<Map<String, Map<String, Object>>> getDevicePortInfoSetFlowFromPortRid(Connection conn, String portRid) throws SQLException {
		final String fname = "getDevicePortInfoSetFlowFromPortRid";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(portRid=%s) - start", fname, portRid));
		}
		List<Map<String, Map<String, Object>>> ret = new ArrayList<Map<String, Map<String, Object>>>();
		try {
			List<Map<String, Object>> records = utilsJdbc.query(conn, SQL_GET_PATCH_INFO_FROM_PATCHWIRING_PORTRID,
                    new MapListHandler(), portRid);
			if (records.size() <= 0) {
				throw new Exception(String.format(NOT_FOUND, portRid + ":wiring info"));
			}
			String inDeviceName = records.get(0).get("inDeviceName").toString();
			String inPortName = records.get(0).get("inPortName").toString();
			String outDeviceName = records.get(0).get("outDeviceName").toString();
			String outPortName = records.get(0).get("outPortName").toString();
			records = utilsJdbc.query(conn, SQL_GET_PATCH_INFO_FROM_PATCHWIRING_DEVICENAME_PORTNAME,
                    new MapListHandler("out", "in", "parent"), inDeviceName, inPortName, outDeviceName, outPortName);
			if (records.size() != 3 && records.size() != 1) {
				throw new Exception(UNEXPECTED_ERROR);
			}

			Iterator<Map<String, Object>> it = records.iterator();
			while (it.hasNext()) {
				Map<String, Object> record = it.next();
				Map<String, Object> inPortInfo = getPortInfoFromPortRid(conn, record.get("in").toString());
				Map<String, Object> outPortInfo = getPortInfoFromPortRid(conn, record.get("out").toString());
				Map<String, Object> parentInfo = getDeviceInfoFromDeviceRid(conn, record.get("parent").toString());

				Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String, Object>>();
				tmp.put("in", inPortInfo);
				tmp.put("out", outPortInfo);
				tmp.put("parent", parentInfo);
				ret.add(tmp);
			}
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public List<Map<String, Object>> getCableLinksFromDeviceName(Connection conn, String deviceName) throws SQLException {
		final String fname = "getCableLinks";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, conn, deviceName));
		}
		List<Map<String, Object>> maps = null;
		MapListHandler rhs = new MapListHandler(
				"inDeviceName",  "inPortName",  "inPortNumber",
				"outDeviceName", "outPortName", "outPortNumber",
				"@rid.asString()", "band", "used");
		try {
			maps = utilsJdbc.query(
					conn,
					SQL_GET_CABLE_LINKS,
					rhs,
					deviceName);
			return maps;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, maps));
			}
		}
	}

	@Override
	public List<Map<String, Object>> getPatchWiringsFromDeviceName(Connection conn, String deviceName) throws SQLException {
		final String fname = "getPatchWirings";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, conn, deviceName));
		}
		List<Map<String, Object>> maps = null;
		MapListHandler rhs = new MapListHandler(
				"inDeviceName",  "inPortName",  "inPortNumber",
				"outDeviceName", "outPortName", "outPortNumber",
				"@rid.asString()", "band", "used");
		try {
			maps = utilsJdbc.query(
					conn,
					SQL_GET_PATCH_WIRINGS_FROM_DEVICENAME,
					rhs,
					deviceName);
			return maps;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, maps));
			}
		}
	}

	@Override
	public List<Map<String, Object>> getPatchWiringsFromDeviceNamePortName(Connection conn, String deviceName, String portName) throws SQLException {
		final String fname = "getPatchWiringsFromDeviceNamePortName";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, devicename=%s, portName=%s) - start", fname, conn, deviceName, portName));
		}
		List<Map<String, Object>> maps = null;
		try {
			MapListHandler rsh = new MapListHandler("in", "out", "parent", "inDeviceName", "inPortName", "outDeviceName", "outPortName", "sequence");
			maps = utilsJdbc.query(conn, SQL_GET_PATCH_WIRINGS_FROM_DEVICENAME_PORTNAME, rsh, deviceName, portName);
			return maps;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, maps));
			}
		}
	}

	@Override
	public List<Map<String, Object>> getPatchWiringsFromParentRid(Connection conn, String parentRid) throws SQLException {
		final String fname = "getPatchWiringsFromParentRid";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, parentRid=%s) - start", fname, conn, parentRid));
		}
		List<Map<String, Object>> ret = null;
		try {
			ret = utilsJdbc.query(
					conn,
					SQL_GET_PATCH_WIRINGS_FROM_PARENTRID,
					new MapListHandler("in", "out", "parent", "inDeviceName", "inPortName", "outDeviceName", "outPortName", "sequence"),
					parentRid);
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", ret));
			}
		}
	}

	@Override
	public boolean isContainsPatchWiringFromDeviceNamePortName(Connection conn, String deviceName, String portName) throws SQLException {
		final String fname = "isContainsPatchWiringFromDeviceNamePortName";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, devicename=%s, portName=%s) - start", fname, conn, deviceName, portName));
		}
		boolean ret = true;
		try {
			MapListHandler rsh = new MapListHandler("in", "out", "parent", "inDeviceName", "inPortName", "outDeviceName", "outPortName");
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_PATCH_WIRINGS_FROM_DEVICENAME_PORTNAME, rsh, deviceName, portName);
			if (maps == null || maps.isEmpty()) {
				ret = false;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public boolean isDeviceNameContainedIntoPatchWiring(Connection conn, String deviceName) throws SQLException {
		final String fname = "isDeviceNameContainedIntoPatchWiring";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, conn, deviceName));
		}
		boolean ret = true;
		try {
			MapListHandler rsh = new MapListHandler("parent");
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_PATCH_WIRINGS_FROM_DEVICENAME, rsh, deviceName);
			if (maps == null || maps.isEmpty()) {
				ret = false;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public boolean isNodeRidContainedIntoPatchWiring(Connection conn, String nodeRid) throws SQLException {
		final String fname = "isNodeRidContainedIntoPatchWiring";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, nodeRid=%s) - start", fname, conn, nodeRid));
		}
		boolean ret = true;
		try {
			MapListHandler rsh = new MapListHandler("parent");
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_PATCH_WIRINGS_FROM_NODERID, rsh, nodeRid);
			if (maps == null || maps.isEmpty()) {
				ret = false;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public boolean isPortRidContainedIntoPatchWiring(Connection conn, String portRid) throws SQLException {
		final String fname = "isPortRidContainedIntoPatchWiring";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, portRid=%s) - start", fname, conn, portRid));
		}
		boolean ret = true;
		try {
			MapListHandler rsh = new MapListHandler("parent");
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_PATCH_WIRINGS_FROM_PORTRID, rsh, portRid, portRid);
			if (maps == null || maps.isEmpty()) {
				ret = false;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public int deletePatchWiring(Connection conn, String deviceName, String portName) throws SQLException {
		final String fname = "deletePatchWiring";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, devicename=%s, portName=%s) - start", fname, conn, deviceName, portName));
		}
		int ret = 0;
		try {
			Object[] params = {deviceName, portName, deviceName, portName};
			ret = utilsJdbc.update(conn, SQL_DELETE_PATCH_WIRING_FROM_DEVICE_NAME_PORT_NAME, params);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
		return ret;
	}

	@Override
	public Map<String, Object> getCableLinkFromInPortRid(Connection conn, String inPortRid) throws SQLException {
		final String fname = "getCableLinkFromInPortRid";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, inPortRid=%s) - start", fname, conn, inPortRid));
		}
		Map<String, Object> ret = null;
		try {
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_CABLE_FROM_IN_PORTRID, new MapListHandler(), inPortRid);
			if (!maps.isEmpty()) {
				ret = maps.get(0);
			}
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
		return ret;
	}

	@Override
	public Map<String, Object> getCableLinkFromOutPortRid(Connection conn, String outPortRid) throws SQLException {
		final String fname = "getCableLinkFromOutPortRid";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, outPortRid=%s) - start", fname, conn, outPortRid));
		}
		Map<String, Object> ret = null;
		try {
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_CABLE_FROM_OUT_PORTRID, new MapListHandler(), outPortRid);
			if (!maps.isEmpty()) {
				ret = maps.get(0);
			}
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
		return ret;
	}

	@Override
	public int createCableLink(Connection conn, String deviceName0, String portName0, String deviceName1, String portName1) throws SQLException {
		final String fname = "createCableLink";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName0=%s, portName0=%s, deviceName1=%s, portName1=%s) - start",
					fname, conn, deviceName0, portName0, deviceName1, portName1));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			String port0Rid = this.getPortRidFromDeviceNamePortName(conn, deviceName0, portName0);
			if (StringUtils.isBlank(port0Rid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}
			String port1Rid = this.getPortRidFromDeviceNamePortName(conn, deviceName1, portName1);
			if (StringUtils.isBlank(port1Rid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			String sql = SQL_INSERT_CABLE;
			sql = sql.replaceFirst("\\?", port0Rid);
			sql = sql.replaceFirst("\\?", port1Rid);
			int result = utilsJdbc.update(conn, sql);
			if (result != 1) {
				ret = DB_RESPONSE_STATUS_EXIST;
				return ret;
			}

			sql = SQL_INSERT_CABLE;
			sql = sql.replaceFirst("\\?", port1Rid);
			sql = sql.replaceFirst("\\?", port0Rid);
			result = utilsJdbc.update(conn, sql);
			if (result != 1) {
				ret = DB_RESPONSE_STATUS_EXIST;
				return ret;
			}

			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public int deleteCableLink(Connection conn, String deviceName0, String portName0, String deviceName1, String portName1) throws SQLException {
		final String fname = "deleteCableLink";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName0=%s, portName0=%s, deviceName1=%s, portName1=%s) - start",
					fname, conn, deviceName0, portName0, deviceName1, portName1));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			boolean contain = false;
			contain = this.isContainsPatchWiringFromDeviceNamePortName(conn, deviceName0, portName0);
			if (contain) {
				ret = DB_RESPONSE_STATUS_USED;
				return ret;
			}

			contain = this.isContainsPatchWiringFromDeviceNamePortName(conn, deviceName1, portName1);
			if (contain) {
				ret = DB_RESPONSE_STATUS_USED;
				return ret;
			}

			String port0Rid = this.getPortRidFromDeviceNamePortName(conn, deviceName0, portName0);
			if (StringUtils.isBlank(port0Rid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}
			String port1Rid = this.getPortRidFromDeviceNamePortName(conn, deviceName1, portName1);
			if (StringUtils.isBlank(port1Rid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			contain = this.isPortRidContainedIntoPatchWiring(conn, port0Rid);
			if (contain) {
				ret = DB_RESPONSE_STATUS_USED;
				return ret;
			}

			contain = this.isPortRidContainedIntoPatchWiring(conn, port1Rid);
			if (contain) {
				ret = DB_RESPONSE_STATUS_USED;
				return ret;
			}

			String sql = SQL_DELETE_CABLE_FROM_ONE_PORTRID;
			sql = sql.replaceFirst("\\?", port0Rid);
			sql = sql.replaceFirst("\\?", port1Rid);
			int result = utilsJdbc.update(conn, sql);
			if (result == 0) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}
			if (result != 1) {
				ret = DB_RESPONSE_STATUS_FAIL;
				return ret;
			}

			sql = SQL_DELETE_CABLE_FROM_ONE_PORTRID;
			sql = sql.replaceFirst("\\?", port1Rid);
			sql = sql.replaceFirst("\\?", port0Rid);
			result = utilsJdbc.update(conn, sql);
			if (result == 0) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}
			if (result != 1) {
				ret = DB_RESPONSE_STATUS_FAIL;
				return ret;
			}

			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public void updateCableLinkUsedFromPortRid(Connection conn, String portRid, long newUsed) throws SQLException {
		final String fname = "updateCableLinkUsedFromPortRid";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, portRid=%s, newUsed=%s) - start", fname, conn, portRid, newUsed));
		}
		try {
			Object[] params = {newUsed, portRid, portRid};
			int result = utilsJdbc.update(conn, SQL_UPDATE_CALBE_LINK_USED_VALUE_FROM_PORT_RID, params);
			if (result != 2) {
				throw new SQLException(String.format(INVALID_NUMBER_OF, "link(represent cable)"));
			}
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s() - end", fname));
		}
	}

	@Override
	public List<Map<String, Object>> getShortestPath(Connection conn, String ridA, String ridZ) throws SQLException {
		final String fname = "getShortestPath";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, ridA=%s, ridZ=%s) - start", fname, conn, ridA, ridZ));
		}
		List<Map<String, Object>> ret = null;
		try {
			String sql = SQL_GET_DIJKSTRA_PATH_FLATTEN;
			sql = sql.replaceFirst("\\?", ridA);
			sql = sql.replaceFirst("\\?", ridZ);
			MapListHandler rsh = new MapListHandler("rid", "class", "name", "number", "deviceName", "type", "datapathId", "ofcIp");
			ret = utilsJdbc.query(conn, sql, rsh);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
		return ret;
	}

	@Override
	public int insertPatchWiring(Connection conn, String ofpRid, String in, String out, String inDeviceName, String inPortName, String outDeviceName, String outPortName, int sequence) throws SQLException {
		final String fname = "insertPatchWiring";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, ofpRid=%s, in=%s, out=%s, inDeviceName=%s, inPortName=%s, outDeviceName=%s, outPortName=%s, sequence=%s) - start",
					fname, conn, ofpRid, in, out, inDeviceName, inPortName, outDeviceName, outPortName, sequence));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			Object[] forwardParams = {in, out, ofpRid, inDeviceName, inPortName, outDeviceName, outPortName, sequence};
			int result = utilsJdbc.update(conn, SQL_INSERT_PATCH_WIRING_2, forwardParams);
			if (result != 1) {
				throw new SQLException(String.format(PATCH_INSERT_FAILD, inDeviceName, inPortName, outDeviceName, outPortName, ofpRid, in, out));
			}
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
		return ret;
	}

	@Override
	public Map<String, Object> getNodeInfoFromDeviceName(Connection conn, String deviceName) throws SQLException {
		final String fname = "getNodeInfoFromDeviceName";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, conn, deviceName));
		}
		Map<String, Object> map = null;
		try {
			List<Map<String, Object>> infoMapList = this.getNodeInfoList(conn);

			for (Map<String, Object> infoMap : infoMapList) {
				if (deviceName.contentEquals((String) infoMap.get("name"))) {
					return infoMap;
				}
			}

			// not found.
			return null;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, map));
			}
		}
	}

	@Override
	public List<Map<String, Object>> getNodeInfoList(Connection conn) throws SQLException {
		final String fname = "getNodeInfoList";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s) - start", fname, conn));
		}
		List<Map<String, Object>> maps = null;
		List<Map<String, Object>> rent_resource_maps = null;
		try {
			maps = utilsJdbc.query(conn, SQL_GET_NODE_INFO_LIST_FM_SYS, new MapListHandler());
			rent_resource_maps = utilsJdbc.query(conn, SQL_GET_NODE_INFO_LIST_FM_RENT, new MapListHandler());

			for(Map<String, Object> rent_res: rent_resource_maps){
				maps.add(rent_res);
			}
			return maps;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, maps));
			}
		}
	}

	@Override
	public Map<String, Object> getNodeInfoFromDeviceRid(Connection conn, String nodeRid) throws SQLException {
		final String fname = "getDeviceInfoFromDeviceRid";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, nodeRid=%s) - start", fname, conn, nodeRid));
		}
		Map<String, Object> map = null;
		try {
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_NODE_INFO_FROM_DEVICE_RID, new MapListHandler(), nodeRid);
			if (!maps.isEmpty()) {
				map = maps.get(0);
			}
			return map;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, map));
			}
		}
	}

	@Override
	public String getNodeRidFromDeviceName(Connection conn, String deviceName) throws SQLException {
		final String fname = "getNodeRidFromDeviceName";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, conn, deviceName));
		}
		String ret = null;
		try {
			List<Map<String, Object>> records = utilsJdbc.query(
					conn,
					SQL_GET_NODE_RID_FROM_DEVICENAME,
					new MapListHandler("rid"),
					deviceName);
			if (records != null && !records.isEmpty() && records.get(0) != null) {
				ret = (String) records.get(0).get("rid");
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public int createNodeInfo(Connection conn, String deviceName, String deviceType, String location, String tenant, String datapathId, String ofcIp) throws SQLException {
		final String fname = "createNodeInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s, deviceType=%s, location=%s, tenant=%s, datapathId=%s, ofcIp=%s) - start", fname, conn, deviceName, deviceType, location, tenant, datapathId, ofcIp));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			Object[] params = {deviceName, location};
			int nRecords = utilsJdbc.update(conn, SQL_INSERT_NODE_INFO, params);
			if (nRecords == 0) {
				return DB_RESPONSE_STATUS_EXIST;
			}

			ret = createResourceInfo(conn, deviceName, deviceType, datapathId, tenant, ofcIp);
			
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	public String getResourceRidFromNodeRid(Connection conn, String nodeRid, String deviceType) throws SQLException {
		final String fname = "getResourceRidFromNodeRid";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, nodeRid=%s, deviceType=%s) - start", fname, conn, nodeRid, deviceType));
		}
		String ret = null;

		try {
			String sql = null;
			if (ArrayUtils.contains(SYSTEM_RESOURCE_TYPES, deviceType)) {
				sql = SQL_GET_STSTEM_RESOURCE_RID_FROM_NODE_RID;
			} else {
				sql = SQL_GET_RENT_RESOURCE_RID_FROM_NODE_RID;
			}
			
			List<Map<String, Object>> records = utilsJdbc.query(
					conn,
					sql,
					new MapListHandler("rid"),
					nodeRid);
			if (records != null && !records.isEmpty() && records.get(0) != null) {
				ret = (String) records.get(0).get("rid");
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}
	
	public int createResourceInfo(Connection conn, String deviceName, String deviceType, String datapathId, String tenant, String ofcIp) throws SQLException {
		final String fname = "createResourceInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s, deviceType=%s, tenant=%s, datapathId=%s, ofcIp=%s) - start", fname, conn, deviceName, deviceType, tenant, datapathId, ofcIp));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			String nodeRid = this.getNodeRidFromDeviceName(conn, deviceName);
			if (StringUtils.isBlank(nodeRid)) {
				return DB_RESPONSE_STATUS_NOT_FOUND;
			}
			
			String sql = null;
			if (ArrayUtils.contains(SYSTEM_RESOURCE_TYPES, deviceType)) {
				sql = SQL_INSERT_SYSTEM_RESOURCE_INFO;
			} else {
				sql = SQL_INSERT_RENT_RESOURCE_INFO;
			}
//			Object[] params = {nodeRid, deviceType, tenant};
//			int nRecords = utilsJdbc.update(conn, sql, params);
			sql = sql.replaceFirst("\\?", nodeRid);
			sql = sql.replaceFirst("\\?", deviceType);
			sql = sql.replaceFirst("\\?", tenant);
			int nRecords = utilsJdbc.update(conn, sql);
			if (nRecords == 0) {
				return DB_RESPONSE_STATUS_EXIST;
			}

			if (ArrayUtils.contains(SYSTEM_RESOURCE_TYPES, deviceType)) {
				String resourceRid = this.getResourceRidFromNodeRid(conn, nodeRid, deviceType);
				ret = this.createOfsInfo(conn, resourceRid, datapathId, ofcIp);
			}
			
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	public int createOfsInfo(Connection conn, String resourceRid, String datapathId, String ofcIp) throws SQLException {
		final String fname = "createOfsInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, resourceRid=%s, datapathId=%s, ofcIp=%s) - start", fname, conn, resourceRid, datapathId, ofcIp));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			if (!StringUtils.isBlank(datapathId) && !StringUtils.isBlank(ofcIp)) {
				String ofcRid = this.getOfcRid(conn, ofcIp);
				if (StringUtils.isBlank(ofcRid)) {
					return DB_RESPONSE_STATUS_NOT_FOUND;
				}
//				Object[] params = {datapathId, resourceRid, ofcRid};
//				int nRecords = utilsJdbc.update(conn, SQL_INSERT_OFS_INFO, params);
				String sql = SQL_INSERT_OFS_INFO;
				sql = sql.replaceFirst("\\?", datapathId);
				sql = sql.replaceFirst("\\?", resourceRid);
				sql = sql.replaceFirst("\\?", ofcRid);
				int nRecords = utilsJdbc.update(conn, sql);

				if (nRecords == 0) {
					return DB_RESPONSE_STATUS_EXIST;
				}					
			}

			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}
	
	@Override
	public String getOfcRid(Connection conn, String ofcIp) throws SQLException {
		final String fname = "getOfcRid";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, ofcIp=%s) - start", fname, conn, ofcIp));
		}

		String ret = null;
		try {
			String[] ofc = ofcIp.split(":", 0);
			String ip = ofc[0];
			if(!this.isNum(ofc[1])) {
				return null;
			}
			int port = Integer.parseInt(ofc[1]);

			List<Map<String, Object>> records = utilsJdbc.query(
					conn,
					SQL_GET_OFC_RID_FROM_IP_AND_PORT,
					new MapListHandler("rid"),
					ip, port);
			if (records != null && !records.isEmpty() && records.get(0) != null) {
				ret = (String) records.get(0).get("rid");
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	public boolean isNum(String number) {
	    try {
	        Integer.parseInt(number);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
	
	@Override
	public int updateNodeInfo(Connection conn, String keyDeviceName, String deviceName, String location,String tenant, String datapathId, String ofcIp) throws SQLException {
		final String fname = "updateNodeInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, keyDeviceName=%s, newDeviceName=%s, datapathId=%s, ofcIp=%s) - start", fname, conn, keyDeviceName, deviceName, datapathId, ofcIp));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			Map<String, Object> current = this.getNodeInfoFromDeviceName(conn, keyDeviceName);
			if (current == null) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			String nodeRid = (String)current.get("rid");
			if (StringUtils.isBlank(deviceName)) {
				deviceName = (String)current.get("name");
			}
			if (StringUtils.isBlank(location)) {
				location = (String)current.get("location");
			}
			if (StringUtils.isBlank(tenant)) {
				tenant = (String)current.get("tenant");
			}
			if (StringUtils.isBlank(datapathId)) {
				datapathId = (String)current.get("datapathId");
			}
			if (StringUtils.isBlank(ofcIp)) {
				ofcIp = (String)current.get("ofcIp");
			}
			String ofcRid = this.getOfcRid(conn, ofcIp);
			if (StringUtils.isBlank(ofcRid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

//			Object[] params = {deviceName, datapathId, location, tenant, ofcRid, nodeRid};
//			int result = utilsJdbc.update(conn, SQL_UPDATE_NODE_INFO_FROM_RID, params);
			String sql = SQL_UPDATE_NODE_INFO_FROM_RID;
			sql = sql.replaceFirst("\\?", deviceName);
			sql = sql.replaceFirst("\\?", datapathId);
			sql = sql.replaceFirst("\\?", location);
			sql = sql.replaceFirst("\\?", tenant);
			sql = sql.replaceFirst("\\?", ofcRid);
			sql = sql.replaceFirst("\\?", nodeRid);
			int result = utilsJdbc.update(conn, sql);
			if (result == 0) {
				ret = DB_RESPONSE_STATUS_EXIST;
				return ret;
			}
//TODO
//			Object[] updDevNamePara = {deviceName, keyDeviceName};
//			utilsJdbc.update(conn, SQL_UPDATE_PORT_DEVICENAME, updDevNamePara);
//			utilsJdbc.update(conn, SQL_UPDATE_PATCH_WIRING_INDEVICENAME,  updDevNamePara);
//			utilsJdbc.update(conn, SQL_UPDATE_PATCH_WIRING_OUTDEVICENAME, updDevNamePara);

			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public int deleteNodeInfo(Connection conn, String deviceName) throws SQLException {
		final String fname = "deleteNodeInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, conn, deviceName));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			String nodeRid = this.getNodeRidFromDeviceName(conn, deviceName);
			if (StringUtils.isBlank(nodeRid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			boolean contain = this.isDeviceNameContainedIntoPatchWiring(conn, deviceName);
			if (contain) {
				ret = DB_RESPONSE_STATUS_FORBIDDEN;
				return ret;
			}

			contain = this.isNodeRidContainedIntoPatchWiring(conn, nodeRid);
			if (contain) {
				ret = DB_RESPONSE_STATUS_FORBIDDEN;
				return ret;
			}

			Object[] params = {deviceName};
			utilsJdbc.update(
					conn,
					SQL_DELETE_PORT_FROM_DEVICENAME,
					params);

			String sql = SQL_DELETE_NODE_FROM_NODERID;
			sql = sql.replaceFirst("\\?", nodeRid);
			int nRecord = utilsJdbc.update(conn, sql);
			if (nRecord != 1) {
				ret = DB_RESPONSE_STATUS_FAIL;
				return ret;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public int createPortInfo(Connection conn, String portName, Integer portNumber, String band, String deviceName) throws SQLException {
		final String fname = "createPortInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, portName=%s, portNumber=%s, deviceName=%s) - start", fname, conn, portName, portNumber, deviceName));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			/* PHASE 1: Insert port info */
			Map<String, Object> devMap = this.getNodeInfoFromDeviceName(conn, deviceName);
			if (devMap == null) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			if (StringUtils.equals((String)devMap.get("type"), NODE_TYPE_SERVER)) {
				portNumber = null;
			}

			Object[] params = {portName, portNumber, band, deviceName};
			int nRecords = utilsJdbc.update(conn, SQL_INSERT_PORT_INFO, params);
			if (nRecords == 0) {
				ret = DB_RESPONSE_STATUS_EXIST;
				return ret;
			}

			/* PHASE 2: Insert Bus-link */
			String devRid  = (String) devMap.get("rid");
			String devType = (String) devMap.get("type");
			String portRid = this.getPortRidFromDeviceNamePortName(conn, deviceName, portName);
			if (StringUtils.isBlank(portRid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			/* Here, decide Used default value of bus of Node. */
			Long used = USED_BLOCKING_VALUE;
			if (StringUtils.equals(devType, NODE_TYPE_SPINE)) {
				used = SPINE_BUS_USED_VALUE;
			} else if (StringUtils.equals(devType, NODE_TYPE_LEAF)) {
				used = 0L;
			}

			String sql = SQL_INSERT_UBUS;
			sql = sql.replaceFirst("\\?", portRid);
			sql = sql.replaceFirst("\\?", devRid);
			sql = sql.replaceFirst("\\?", used.toString());
			nRecords = utilsJdbc.update(conn, sql);
			if (nRecords == 0) {
				ret = DB_RESPONSE_STATUS_EXIST;
				return ret;
			}

			sql = SQL_INSERT_DBUS;
			sql = sql.replaceFirst("\\?", devRid);
			sql = sql.replaceFirst("\\?", portRid);
			sql = sql.replaceFirst("\\?", used.toString());
			nRecords = utilsJdbc.update(conn, sql);
			if (nRecords == 0) {
				ret = DB_RESPONSE_STATUS_EXIST;
				return ret;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public int updatePortInfo(Connection conn, String keyPortName, String keyDeviceName, String portName, Integer portNumber, String band) throws SQLException {
		final String fname = "updatePortInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, keyPortName=%s, keyDeviceName=%s, portName=%s, portNumber=%s) - start", fname, conn, keyPortName, keyDeviceName, portName, portNumber));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			Map<String, Object> current = this.getPortInfoFromPortName(conn, keyDeviceName, keyPortName);
			if (current == null) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				if (logger.isTraceEnabled()){
					logger.trace(String.format("%s(ret=%s) - end", fname, ret));
				}
				return ret;
			}

			String portRid = (String)current.get("rid");
			if (StringUtils.isBlank(portName)) {
				portName = (String)current.get("name");
			}
			if (portNumber == null) {
				portNumber = (Integer)current.get("portNumber");
			}
			if (StringUtils.isBlank(band)) {
				band = (String)current.get("band");
			}

			Object[] params = {portName, portNumber, band, portRid};
			int result = utilsJdbc.update(conn, SQL_UPDATE_PORT_INFO_FROM_RID, params);
			if (result == 0) {
				ret = DB_RESPONSE_STATUS_EXIST;
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("%s(ret=%s) - end", fname, ret));
				}
				return ret;
			}

			Object[] updPortNamePara = {portName, keyPortName, keyDeviceName};
			utilsJdbc.update(conn, SQL_UPDATE_PATCH_WIRING_INPORTNAME, updPortNamePara);
			utilsJdbc.update(conn, SQL_UPDATE_PATCH_WIRING_OUTPORTNAME, updPortNamePara);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
		return ret;
	}

	@Override
	public int deletePortInfo(Connection conn, String portName, String deviceName) throws SQLException {
		final String fname = "deletePortInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, portName=%s, deviceName=%s) - start", fname, conn, portName, deviceName));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			String portRid = this.getPortRidFromDeviceNamePortName(conn, deviceName, portName);
			if (StringUtils.isBlank(portRid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			boolean contain = this.isContainsPatchWiringFromDeviceNamePortName(conn, deviceName, portName);
			if (contain) {
				ret = DB_RESPONSE_STATUS_FORBIDDEN;
				return ret;
			}

			contain = this.isPortRidContainedIntoPatchWiring(conn, portRid);
			if (contain) {
				ret = DB_RESPONSE_STATUS_FORBIDDEN;
				return ret;
			}

			String sql = SQL_DELETE_PORT_FROM_PORTRID;
			sql = sql.replaceFirst("\\?", portRid);
			int nRecord = utilsJdbc.update(conn, sql);
			if (nRecord != 1) {
				ret = DB_RESPONSE_STATUS_FAIL;
				return ret;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public Map<String, Object> getPortInfoFromPortName(Connection conn, String deviceName, String portName) throws SQLException {
		final String fname = "getPortInfoFromPortName";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s, portName=%s) - start", fname, conn, deviceName, portName));
		}
		Map<String, Object> map = null;
		try {
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_PORT_INFO_FROM_PORT_NAME, new MapListHandler(), portName, deviceName);
			if (!maps.isEmpty()) {
				map = maps.get(0);
			}
			return map;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, map));
			}
		}
	}

	@Override
	public Map<String, Object> getPortInfoFromPortRid(Connection conn, String rid) throws SQLException {
		final String fname = "getPortInfoFromPortRid";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(rid=%s) - start", fname, rid));
		}
		Map<String, Object> ret = null;
		try {
			List<Map<String, Object>> records = utilsJdbc.query(
					conn,
					SQL_GET_PORT_INFO_FROM_PORTRID,
                    new MapListHandler(),
                    rid);
			if (records != null && !records.isEmpty()) {
				ret = records.get(0);
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("getPortInfo(ret=%s) - end", ret));
			}
		}
	}

	@Override
	public String getPortRidFromDeviceNamePortName(Connection conn, String deviceName, String portName) throws SQLException {
		final String fname = "getPortRidFromDeviceNamePortName";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s, portName=%s) - start", fname, deviceName, portName));
		}
		String ret = null;
		try {
			List<Map<String, Object>> records = utilsJdbc.query(
					conn,
					SQL_GET_PORT_RID_FROM_DEVICENAME_PORTNAME,
					new MapListHandler("rid"),
					portName, deviceName);
			if (records != null && !records.isEmpty() && records.get(0) != null) {
				ret = (String) records.get(0).get("rid");
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public List<Map<String, Object>> getPortInfoListFromDeviceName(Connection conn, String deviceName) throws SQLException {
		final String fname = "getPortInfoListFromDeviceName";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, deviceName));
		}
		List<Map<String, Object>> ret = null;
		try {
			ret = utilsJdbc.query(
					conn,
					SQL_GET_PORT_INFO_FROM_DEVICENAME,
					new MapListHandler(),
					deviceName);
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public Map<String, Object> getNeighborPortFromPortRid(Connection conn, String portRid) throws SQLException {
		final String fname = "getPortInfoListFromDeviceName";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, portRid=%s) - start", fname, portRid));
		}
		Map<String, Object> ret = null;
		try {
			List<Map<String, Object>> records = utilsJdbc.query(
					conn,
					SQL_GET_NEIGHBOR_PORT_INFO_FROM_PORT_RID,
					new MapListHandler(),
					portRid);
			if (records != null && !records.isEmpty()) {
				ret = records.get(0);
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public Map<String, Object> getDeviceInfoFromDeviceRid(Connection conn, String rid) throws SQLException {
		final String fname = "getDeviceInfoFromDeviceRid";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(rid=%s) - start", fname, rid));
		}
		Map<String, Object> record = null;
		try {
			List<Map<String, Object>> records = utilsJdbc.query(conn, SQL_GET_DEVICE_INFO_FROM_DEVICERID,
                    new MapListHandler(), rid);
			if (records.size() <= 0) {
				String msg = String.format(NOT_FOUND, "node(" + rid + ")");
                throw new SQLException(msg);
			}
			record = records.get(0);
			return record;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, record));
			}
		}
	}

	static synchronized public String getInternalMacFromDeviceNameInPortSrcMacDstMac(ConnectionUtilsJdbc utilsJdbc, Connection conn, String deviceName, String inPort, String srcMac, String dstMac) throws SQLException {
		final String fname = "getInternalMacFromDeviceNameInPortSrcMacDstMac";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s, inPort=%s, srcMac=%s, dstMac=%s) - start", fname, conn, deviceName, inPort, srcMac, dstMac));
		}
		String ret = null;
		try {
			DbAccessFlag.lock();

			List<Map<String, Object>> records = utilsJdbc.query(conn, SQL_GET_INTERNALMAC_FROM_SRCMAC_DSTMAC_INPORT_DEVICENAME,
                    new MapListHandler(), deviceName, inPort, srcMac, dstMac);
			if (records.size() > 0) {
                ret = OFPMUtils.longToMacAddress(Long.parseLong((records.get(0).get("internalMac").toString())));
			} else {
				records = utilsJdbc.query(conn, SQL_GET_MAX_INTERNALMAC, new MapListHandler());
				Long newInternalMac = 1L;
				if (records.size() > 0) {
					newInternalMac = Long.parseLong((records.get(0).get("maxInternalMac").toString())) + 1L;
				}
				Object[] params = {deviceName, inPort, srcMac, dstMac, newInternalMac};
				int rows = utilsJdbc.update(conn, SQL_INSERT_INTERNALMAC, params);
				if (rows == 0) {
					// TODO exist error
				}
				ret = OFPMUtils.longToMacAddress(newInternalMac);
			}
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", ret));
			}
			DbAccessFlag.unlock();
		}
	}

	@Override
	public List<String> getInternalMacListFromDeviceNameInPort(Connection conn, String deviceName, String inPort) throws SQLException {
		final String fname = "getInternalMacListFromDeviceNameInPort";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s, inPort=%s) - start", fname, conn, deviceName, inPort));
		}
		List<String> ret = null;
		try {
			ret = new ArrayList<String>();
			List<Map<String, Object>> records = utilsJdbc.query(conn, SQL_GET_INTERNALMAC_LIST_FROM_DEVICENAME_INPORT, new MapListHandler("internalMac"), deviceName, inPort);
			for (int i = 0; i < records.size(); i++) {
				Map<String, Object> record = records.get(i);
				Long internalMac = Long.parseLong(record.get("internalMac").toString());
				ret.add(OFPMUtils.longToMacAddress(internalMac));
			}
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", ret));
			}
		}
	}

	@Override
	public int deleteInternalMac(Connection conn, String deviceName, int inPort) throws SQLException {
		final String fname = "deleteInternalMac";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, devicename=%s, portName=%d) - start", fname, conn, deviceName, inPort));
		}
		int ret = 0;
		try {
			Object[] params = {deviceName, inPort};
			ret = utilsJdbc.update(conn, SQL_DELETE_INTERNALMAC_FROM_DEVICE_NAME_PORT_NAME, params);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
		return ret;
	}

	@Override
	public List<Map<String, Object>> getInternalMacInfoListFromDeviceNameInPort(Connection conn, String deviceName, Integer portNumber) throws SQLException {
		final String fname = "getInternalMacInfoListFromDeviceNameInPort";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s, portNumber=%s) - start", fname, conn, deviceName, portNumber));
		}
		List<Map<String, Object>> ret = null;
		try {
			ret = utilsJdbc.query(
					conn,
					SQL_GET_INTERNALMAC_INFO_LIST_FROM_DEVICENAME_PORTNUMBER,
					new MapListHandler("srcMac", "dstMac", "internalMac"),
					deviceName,
					portNumber);
			if (ret == null) {
				return new ArrayList<Map<String, Object>>();
			}
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", ret));
			}
		}
	}

	@Override
	public String getPortBandFromDeviceNamePortName(Connection conn, String deviceName, String portName) throws SQLException {
		final String fname = "getPortBandFromDeviceNamePortName";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s, portName=%s) - start", fname, deviceName, portName));
		}
		String ret = null;
		try {
			List<Map<String, Object>> records = utilsJdbc.query(
					conn,
					SQL_GET_PORT_BAND_FROM_DEVICENAME_PORTNAME,
					new MapListHandler("band"),
					portName, deviceName);
			if (records != null && !records.isEmpty() && records.get(0) != null) {
				ret = (String) records.get(0).get("band");
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public List<Map<String, Object>> getOfcInfoList(Connection conn) throws SQLException {
		final String fname = "getOfcInfoList";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s) - start", fname, conn));
		}
		List<Map<String, Object>> maps = null;
//		List<Map<String, Object>> rent_resource_maps = null;
		try {
			maps = utilsJdbc.query(conn, SQL_GET_OFC_INFO_LIST, new MapListHandler());
			return maps;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, maps));
			}
		}	
	}
}
