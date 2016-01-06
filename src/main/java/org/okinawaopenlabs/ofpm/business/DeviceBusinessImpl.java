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

package org.okinawaopenlabs.ofpm.business;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import static org.okinawaopenlabs.constants.OrientDBDefinition.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.common.BaseResponse;
import org.okinawaopenlabs.ofpm.json.device.DeviceInfo;
import org.okinawaopenlabs.ofpm.json.device.DeviceInfoCreateJsonIn;
import org.okinawaopenlabs.ofpm.json.device.DeviceInfoListReadJsonOut;
import org.okinawaopenlabs.ofpm.json.device.DeviceInfoReadJsonOut;
import org.okinawaopenlabs.ofpm.json.device.DeviceInfoUpdateJsonIn;
import org.okinawaopenlabs.ofpm.json.device.DeviceManagerGetConnectedPortInfoJsonOut;
import org.okinawaopenlabs.ofpm.json.device.PortInfoCreateJsonIn;
import org.okinawaopenlabs.ofpm.json.device.PortInfoListReadJsonOut;
import org.okinawaopenlabs.ofpm.json.device.PortInfoUpdateJsonIn;
import org.okinawaopenlabs.ofpm.json.device.DeviceManagerGetConnectedPortInfoJsonOut.ResultData;
import org.okinawaopenlabs.ofpm.json.device.DeviceManagerGetConnectedPortInfoJsonOut.ResultData.LinkData;
import org.okinawaopenlabs.ofpm.json.device.OfcInfo;
import org.okinawaopenlabs.ofpm.json.device.OfcInfoCreateJsonIn;
import org.okinawaopenlabs.ofpm.json.device.OfcInfoListReadJsonOut;
import org.okinawaopenlabs.ofpm.json.device.OfcInfoReadJsonOut;
import org.okinawaopenlabs.ofpm.json.device.OfcInfoUpdateJsonIn;
import org.okinawaopenlabs.ofpm.json.device.PortInfo;
import org.okinawaopenlabs.ofpm.utils.Config;
import org.okinawaopenlabs.ofpm.utils.ConfigImpl;
import org.okinawaopenlabs.ofpm.utils.OFPMUtils;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;
import org.okinawaopenlabs.ofpm.validate.device.DeviceInfoCreateJsonInValidate;
import org.okinawaopenlabs.ofpm.validate.device.DeviceInfoUpdateJsonInValidate;
import org.okinawaopenlabs.ofpm.validate.device.OfcInfoCreateJsonInValidate;
import org.okinawaopenlabs.ofpm.validate.device.OfcInfoUpdateJsonInValidate;
import org.okinawaopenlabs.ofpm.validate.device.PortInfoCreateJsonInValidate;
import org.okinawaopenlabs.ofpm.validate.device.PortInfoUpdateJsonInValidate;
import org.okinawaopenlabs.orientdb.client.ConnectionUtilsJdbc;
import org.okinawaopenlabs.orientdb.client.ConnectionUtilsJdbcImpl;
import org.okinawaopenlabs.orientdb.client.Dao;
import org.okinawaopenlabs.orientdb.client.DaoImpl;


public class DeviceBusinessImpl implements DeviceBusiness {
	private static final Logger logger = Logger.getLogger(DeviceBusinessImpl.class);

	Config conf = new ConfigImpl();

	public String createDevice(String newDeviceInfoJson) {
		String fname = "createDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newDeviceInfoJson=%s) - start", fname, newDeviceInfoJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: Convert to DeviceInfoCreateJsonIn from json and validation check. */
		DeviceInfoCreateJsonIn deviceInfo = null;
		try {
			deviceInfo = DeviceInfoCreateJsonIn.fromJson(newDeviceInfoJson);
			DeviceInfoCreateJsonInValidate validator = new DeviceInfoCreateJsonInValidate();
			validator.checkValidation(deviceInfo);
		} catch (JsonSyntaxException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);

			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return res.toString();
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());

			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return res.toString();
		}

		/* PHASE 2: Add node info to ofpdb */
		ConnectionUtilsJdbc utils = null;
		Connection           conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);

			if (!StringUtils.isBlank(deviceInfo.getOfcIp())) {
				String ofcRid = dao.getOfcRid(conn, deviceInfo.getOfcIp());
				if (StringUtils.isBlank(ofcRid)) {
					res.setStatus(STATUS_NOTFOUND);
					res.setMessage(String.format(NOT_FOUND, deviceInfo.getOfcIp()));
					return res.toJson();
				}
			}

			int status = dao.createNodeInfo(
					conn,
					deviceInfo.getDeviceName(),
					deviceInfo.getDeviceType(),
					deviceInfo.getLocation(),
					deviceInfo.getTenant(),
					deviceInfo.getDatapathId(),
					deviceInfo.getOfcIp());

			if (status == DB_RESPONSE_STATUS_EXIST) {
				utils.rollback(conn);
				res.setStatus(STATUS_BAD_REQUEST);
				res.setMessage(String.format(ALREADY_EXIST, deviceInfo.getDeviceName()));
				return res.toJson();
			}

			utils.commit(conn);
			res.setStatus(STATUS_CREATED);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	public String deleteDevice(String deviceName) {
		String fname = "deleteDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s) - start", fname, deviceName));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: check validation */
		try {
			BaseValidate.checkStringBlank(deviceName);
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());

			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 2: Delete node from ofp db */
		ConnectionUtilsJdbc utils = null;
		Connection           conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			int status = dao.deleteNodeInfo(conn, deviceName);

			switch (status) {
				case DB_RESPONSE_STATUS_NOT_FOUND:
					utils.rollback(conn);
					res.setStatus(DB_RESPONSE_STATUS_NOT_FOUND);
					res.setMessage(String.format(NOT_FOUND, deviceName));
					return res.toJson();

				case STATUS_FORBIDDEN:
					utils.rollback(conn);
					res.setStatus(STATUS_FORBIDDEN);
					res.setMessage(String.format(IS_PATCHED, deviceName));
					return res.toJson();

				case DB_RESPONSE_STATUS_FAIL:
					utils.rollback(conn);
					res.setStatus(STATUS_INTERNAL_ERROR);
					res.setMessage(String.format(COULD_NOT_DELETE, deviceName));
					return res.toJson();
			}

			utils.commit(conn);
			res.setStatus(STATUS_SUCCESS);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
    		return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	public String updateDevice(String deviceName, String updateDeviceInfoJson) {

		String fname = "updateDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s, newDeviceInfoJson=%s) - start", fname, deviceName, updateDeviceInfoJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> DeviceInfoUpdateJosnIn and check validation */
		DeviceInfoUpdateJsonIn newDeviceInfo = null;
		try {
			newDeviceInfo = DeviceInfoUpdateJsonIn.fromJson(updateDeviceInfoJson);
			DeviceInfoUpdateJsonInValidate validator = new DeviceInfoUpdateJsonInValidate();
			validator.checkValidation(deviceName, newDeviceInfo);
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		ConnectionUtilsJdbc utils = null;
		Connection           conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);

			int status = dao.updateNodeInfo(
					conn,
					deviceName,
					newDeviceInfo.getDeviceName(),
					newDeviceInfo.getLocation(),
					newDeviceInfo.getTenant(),
					newDeviceInfo.getDatapathId(),
					newDeviceInfo.getOfcIp(),
					newDeviceInfo.getDeviceType());

			switch (status) {
				case DB_RESPONSE_STATUS_NOT_FOUND:
					utils.rollback(conn);
					res.setStatus(STATUS_NOTFOUND);
					res.setMessage(String.format(NOT_FOUND, deviceName));
					return res.toJson();

				case DB_RESPONSE_STATUS_EXIST:
					utils.rollback(conn);
					res.setStatus(STATUS_CONFLICT);
					res.setMessage(String.format(ALREADY_EXIST, newDeviceInfo.getDeviceName()));
					return res.toJson();

				case DB_RESPONSE_STATUS_INVALID_ERR:
					utils.rollback(conn);
					res.setStatus(STATUS_BAD_REQUEST);
					res.setMessage(String.format(INVALID_PARAMETER, "deviceType"));
					return res.toJson();
			}

			utils.commit(conn);
			res.setStatus(STATUS_SUCCESS);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			return res.toJson();
		} finally {
			utils.close(conn);
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	public String readDevice(String deviceName) {
		String fname = "readDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s) - start", fname, deviceName));
		}
		DeviceInfoReadJsonOut res = new DeviceInfoReadJsonOut();

		/* PHASE 1: check validation */
		try {
			BaseValidate.checkStringBlank(deviceName);
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());

			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 2: Read node from ofp db */
		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(true);

			Dao dao = new DaoImpl(utils);
			Map<String, Object> infoMap = dao.getNodeInfoFromDeviceName(conn, deviceName);
			if (infoMap == null || infoMap.isEmpty()) {
				res.setStatus(DB_RESPONSE_STATUS_NOT_FOUND);
				res.setMessage(String.format(NOT_FOUND, deviceName));
				return res.toJson();
			}

			DeviceInfo dev = new DeviceInfo();
			dev.setDeviceName((String) infoMap.get("name"));
			dev.setDeviceType((String) infoMap.get("type"));
			dev.setDatapathId((String) infoMap.get("datapathId"));
			dev.setOfcIp((String) infoMap.get("ofcIp"));
			dev.setLocation((String) infoMap.get("location"));
			dev.setTenant((String) infoMap.get("tenant"));
			if(infoMap.containsKey("ip") && infoMap.containsKey("port")){
				dev.setOfcIp(((String)infoMap.get("ip") + ":" + (String)infoMap.get("port").toString()));
			}

			res.setResult(dev);
			res.setStatus(STATUS_SUCCESS);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
    		return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	public String readDeviceList() {
		String fname = "readDeviceList";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - start", fname));
			logger.debug(String.format("OF-Patch manager version = " + MAJOR_VERSION + "." + MINOR_VERSION + "." + BUILD_VERSION));
		}
		DeviceInfoListReadJsonOut res = new DeviceInfoListReadJsonOut();
		res.setStatus(STATUS_SUCCESS);

		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(true);

			Dao dao = new DaoImpl(utils);
			List<Map<String, Object>> infoMapList = dao.getNodeInfoList(conn);

			List<DeviceInfo> result = new ArrayList<DeviceInfo>();
			for (Map<String, Object> infoMap : infoMapList) {
				DeviceInfo dev = new DeviceInfo();
				dev.setDeviceName((String) infoMap.get("name"));
				dev.setDeviceType((String) infoMap.get("type"));
				dev.setLocation((String) infoMap.get("location"));
				dev.setTenant((String) infoMap.get("tenant"));
				if(infoMap.containsKey("datapathId")){
					dev.setDatapathId((String)infoMap.get("datapathId"));
				}
				if(infoMap.containsKey("ip") && infoMap.containsKey("port")){
					dev.setOfcIp(((String)infoMap.get("ip") + ":" + (String)infoMap.get("port").toString()));
				}
//				StringBuilder str_build = new StringBuilder();
//				str_build.append((String) infoMap.get("ip"));
//				str_build.append((String) infoMap.get("port"));
				result.add(dev);
			}

			res.setResult(result);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
    		return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	public String createPort(String deviceName, String newPortInfoJson) {
		String fname = "createPort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newPortInfoJson=%s) - start", fname, newPortInfoJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> obj and validation check */
		PortInfoCreateJsonIn portInfo = null;
		try {
			portInfo = PortInfoCreateJsonIn.fromJson(newPortInfoJson);
			PortInfoCreateJsonInValidate validator = new PortInfoCreateJsonInValidate();
			validator.checkValidation(deviceName ,portInfo);
		} catch (JsonSyntaxException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 2: */
		ConnectionUtilsJdbc utils = null;
		Connection           conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			int status = dao.createPortInfo(
					conn,
					portInfo.getPortName(),
					portInfo.getPortNumber(),
					portInfo.getBand(),
					deviceName);

			switch (status) {
				case DB_RESPONSE_STATUS_EXIST:
					utils.rollback(conn);
					res.setStatus(STATUS_BAD_REQUEST);
					res.setMessage(String.format(ALREADY_EXIST, portInfo.getPortName()));
		    		return res.toJson();

				case DB_RESPONSE_STATUS_NOT_FOUND:
					utils.rollback(conn);
					res.setStatus(STATUS_NOTFOUND);
					res.setMessage(String.format(NOT_FOUND, deviceName));
		    		return res.toJson();
			}

			utils.commit(conn);
			res.setStatus(STATUS_CREATED);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);
    		OFPMUtils.logErrorStackTrace(logger, e);
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
    		return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	@Override
	public String readPortList(String deviceName) {
		String fname = "readPortList";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s) - start", fname, deviceName));
		}
		PortInfoListReadJsonOut res = new PortInfoListReadJsonOut();
		res.setStatus(STATUS_SUCCESS);

		/* PHASE 1: json -> obj and validation check */
		PortInfoCreateJsonIn portInfo = null;
		try {
			BaseValidate.checkStringBlank(deviceName);
		} catch (JsonSyntaxException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 2: */
		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(true);

			Dao dao = new DaoImpl(utils);
			List<Map<String, Object>> infoMapList = dao.getPortInfoListFromDeviceName(conn, deviceName); 

			List<PortInfo> result = new ArrayList<PortInfo>();
			for (Map<String, Object> infoMap : infoMapList) {
				PortInfo port = new PortInfo();
				port.setPortName((String) infoMap.get("name"));
				port.setPortNumber((Integer) infoMap.get("number"));
				port.setBand((Integer) infoMap.get("band"));
				result.add(port);
			}

			res.setResult(result);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
    		return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	public String deletePort(String deviceName, String portName) {
		String fname = "deletePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s, portName=%s) - start", fname, deviceName, portName));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: check validation */
		try {
			BaseValidate.checkStringBlank(deviceName);
			BaseValidate.checkStringBlank(portName);
		} catch (ValidateException ve) {
			String message = String.format(IS_BLANK, "portName or deviceName");
			logger.error(ve.getClass().getName() + ": " + message);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(message);
		}

		/* PHASE 2: Delete port from ofp db */
		ConnectionUtilsJdbc utils = null;
		Connection           conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			int status = dao.deletePortInfo(
					conn,
					portName,
					deviceName);

			switch (status) {
				case DB_RESPONSE_STATUS_NOT_FOUND:
					utils.rollback(conn);
					res.setStatus(DB_RESPONSE_STATUS_NOT_FOUND);
					res.setMessage(String.format(NOT_FOUND, deviceName + "." + portName));
					return res.toJson();

				case DB_RESPONSE_STATUS_FORBIDDEN:
					utils.rollback(conn);
					res.setStatus(STATUS_FORBIDDEN);
					res.setMessage(String.format(IS_PATCHED, deviceName + "." + portName));
					return res.toJson();

				case DB_RESPONSE_STATUS_FAIL:
					utils.rollback(conn);
					res.setStatus(STATUS_INTERNAL_ERROR);
					res.setMessage(String.format(COULD_NOT_DELETE, deviceName + "." + portName));
					return res.toJson();
			}

			utils.commit(conn);
			res.setStatus(STATUS_SUCCESS);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	public String updatePort(String deviceName, String portName, String updatePortInfoJson) {
		String fname = "updatePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s, portName=%s, updatePortInfoJson=%s) - start", fname, deviceName, portName, updatePortInfoJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> obj and check validation */
		PortInfoUpdateJsonIn portInfo = null;
		try {
			portInfo = PortInfoUpdateJsonIn.fromJson(updatePortInfoJson);
			PortInfoUpdateJsonInValidate validator = new PortInfoUpdateJsonInValidate();
			validator.checkValidation(deviceName, portName, portInfo);
		} catch (JsonSyntaxException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 2: update port info */
		ConnectionUtilsJdbc utils = null;
		Connection           conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			int status = dao.updatePortInfo(
					conn,
					portName,
					deviceName,
					portInfo.getPortName(),
					portInfo.getPortNumber(),
					portInfo.getBand());

			switch (status) {
				case DB_RESPONSE_STATUS_NOT_FOUND:
					utils.rollback(conn);
					res.setStatus(STATUS_NOTFOUND);
					res.setMessage(String.format(NOT_FOUND, portName));
		    		return res.toJson();

				case DB_RESPONSE_STATUS_EXIST:
					utils.rollback(conn);
					res.setStatus(STATUS_CONFLICT);
					res.setMessage(String.format(ALREADY_EXIST, portInfo.getPortName()));
		    		return res.toJson();
			}

			utils.commit(conn);
			res.setStatus(STATUS_SUCCESS);
	    	return res.toJson();
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);
    		OFPMUtils.logErrorStackTrace(logger, e);
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
    		return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	@Override
	public String getConnectedPortInfo(String deviceName) {
		final String fname = "getConnectedPortInfo";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(params=%s) - start ", fname, deviceName));
		}
		DeviceManagerGetConnectedPortInfoJsonOut res = new DeviceManagerGetConnectedPortInfoJsonOut();

		/* PHASE 1: validation check */
		try {
			BaseValidate.checkStringBlank(deviceName);
		} catch (ValidateException e) {
			String message = String.format(IS_BLANK, "portName or deviceName");
			logger.error(e.getClass().getName() + ": " + message);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(message);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 2: connected calcuration */
		ConnectionUtilsJdbc utils = null;
		Connection           conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);

			Map<String, Object> devMap = dao.getNodeInfoFromDeviceName(conn, deviceName);
			if (devMap == null) {
				res.setStatus(STATUS_NOTFOUND);
				res.setMessage(String.format(NOT_FOUND, deviceName));
				return res.toJson();
			}
			List<Map<String, Object>> portMapList = dao.getPortInfoListFromDeviceName(conn, deviceName);
			if (portMapList == null) {
				portMapList = new ArrayList<Map<String, Object>>();
			}

			Map<String, Map<String, Object>> devCache = new HashMap<String, Map<String, Object>>();
			for (Map<String, Object> portMap: portMapList) {
				Map<String, Object> nghbrPortMap = dao.getNeighborPortFromPortRid(conn, (String)portMap.get("rid"));
				if (nghbrPortMap == null) {
					continue;
				}

				/* Get neighbor device info from map. However, if the device info is null, get neighbor device info from ofp db */
				String nghbrDevName = (String) nghbrPortMap.get("node_name");
				Map<String, Object> nghbrDevMap = devCache.get(nghbrDevName);
				if (nghbrDevMap == null) {
					nghbrDevMap = dao.getNodeInfoFromDeviceName(conn, nghbrDevName);
					if (nghbrDevMap == null) {
						res.setStatus(STATUS_NOTFOUND);
						res.setMessage(String.format(NOT_FOUND, deviceName));
						return res.toJson();
					}
					devCache.put(nghbrDevName, nghbrDevMap);
				}

				/* Make LinkData and add to ResultData. finally ResultData add to outPara. */
				ResultData resultData = res.new ResultData();
				this.addLinkDataToResultData(resultData,      devMap,      portMap);
				this.addLinkDataToResultData(resultData, nghbrDevMap, nghbrPortMap);
				res.addResultData(resultData);
			}

			res.setStatus(STATUS_SUCCESS);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
			return res.toJson();
		} finally {
			utils.rollback(conn);
			utils.close(conn);
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("getConnectedPortInfo(ret=%s) - end ", ret));
			}
		}
	}

	private void addLinkDataToResultData(ResultData resultData, Map<String, Object> nodeInfoMap, Map<String, Object> portInfoMap) {
		String  nodeName = (String)  nodeInfoMap.get("name");
		String  nodeType = (String)  nodeInfoMap.get("type");
		String  portName = (String)  portInfoMap.get("name");
		Integer number   = (Integer) portInfoMap.get("number");
		LinkData linkData = resultData.new LinkData();
		linkData.setDeviceName(nodeName);
		linkData.setDeviceType(nodeType);
		linkData.setPortName(  portName);
		linkData.setPortNumber(number);
		if (OFPMUtils.isNodeTypeOfpSwitch(nodeType)) {
			linkData.setOfpFlag(OFP_FLAG_TRUE);
		} else {
			linkData.setOfpFlag(OFP_FLAG_FALSE);
		}
		resultData.addLinkData(linkData);
	}

	@Override
	public String createOfc(String newOfcInfoJson) {
		String fname = "createOfc";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newOfcInfoJson=%s) - start", fname, newOfcInfoJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: Convert to OfcInfoCreateJsonIn from json and validation check. */
		OfcInfoCreateJsonIn ofcInfo = null;
		try {
			ofcInfo = OfcInfoCreateJsonIn.fromJson(newOfcInfoJson);
			OfcInfoCreateJsonInValidate validator = new OfcInfoCreateJsonInValidate();
			validator.checkValidation(ofcInfo);
		} catch (JsonSyntaxException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);

			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return res.toString();
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());

			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return res.toString();
		}

		/* PHASE 2: Add ofc info to ofpdb */
		ConnectionUtilsJdbc utils = null;
		Connection           conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);

//		/* Delete Ofc Not Found Check(from create device) */ 
//			if (!StringUtils.isBlank(ofcInfo.getOfcIp())) {
//				String ofcRid = dao.getOfcRid(conn, deviceInfo.getOfcIp());
//				if (StringUtils.isBlank(ofcRid)) {
//					res.setStatus(STATUS_NOTFOUND);
//					res.setMessage(String.format(NOT_FOUND, deviceInfo.getOfcIp()));
//					return res.toJson();
//				}
//			}

			int status = dao.createOfcInfo(
					conn,
					ofcInfo.getIp(),
					ofcInfo.getPort());

			if (status == DB_RESPONSE_STATUS_EXIST) {
				utils.rollback(conn);
				res.setStatus(STATUS_BAD_REQUEST);
				//TODO replace getIp
				res.setMessage(String.format(ALREADY_EXIST, ofcInfo.getIp()));
				return res.toJson();
			}

			utils.commit(conn);
			res.setStatus(STATUS_CREATED);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
		
	}

	@Override
	public String deleteOfc(String ofcIpPort) {
		String fname = "deleteOfc";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ofcIpPort=%s) - start", fname, ofcIpPort));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: check validation */
		try {
			BaseValidate.checkStringBlank(ofcIpPort);
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());

			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 2: Delete node from ofp db */
		ConnectionUtilsJdbc utils = null;
		Connection           conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			Map<String, Object> infoMap = dao.getOfcInfo(conn, ofcIpPort);
				
			if(infoMap == null){
				res.setStatus(STATUS_NOTFOUND);
				return res.toJson();
			}

//			OfcInfo ofcInfo = new OfcInfo();
//			ofcInfo.setIp((String) infoMap.get("ip"));
//			ofcInfo.setPort((Integer) infoMap.get("port"));
//			res.setResult(ofcInfo);
//			return res.toJson();
						
			int status = dao.deleteOfcInfo(
					conn,
					ofcIpPort);

			switch (status) {
				case DB_RESPONSE_STATUS_NOT_FOUND:
					utils.rollback(conn);
					res.setStatus(DB_RESPONSE_STATUS_NOT_FOUND);
					res.setMessage(String.format(NOT_FOUND, ofcIpPort));
					return res.toJson();

				case STATUS_FORBIDDEN:
					utils.rollback(conn);
					res.setStatus(STATUS_FORBIDDEN);
					res.setMessage(String.format(IS_PATCHED, ofcIpPort));
					return res.toJson();

				case DB_RESPONSE_STATUS_FAIL:
					utils.rollback(conn);
					res.setStatus(STATUS_INTERNAL_ERROR);
					res.setMessage(String.format(COULD_NOT_DELETE, ofcIpPort));
					return res.toJson();
			}

			utils.commit(conn);
			res.setStatus(STATUS_SUCCESS);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
    		return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	
	
	}

	@Override
	public String updateOfc(String ofcIpPort, String updateOfcInfoJson) {
		// TODO Auto-generated method stub
	String fname = "updateOfc";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(OfcPort=%s, updateOfcInfoJson=%s) - start", fname, ofcIpPort, updateOfcInfoJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> OfcInfoUpdateJosnIn and check validation */
		OfcInfoUpdateJsonIn newOfcInfo = null;
//		newOfcInfo = OfcInfoUpdateJsonIn.fromJson(updateOfcInfoJson);
		try {
			newOfcInfo = OfcInfoUpdateJsonIn.fromJson(updateOfcInfoJson);
			OfcInfoUpdateJsonInValidate validator = new OfcInfoUpdateJsonInValidate();
			validator.checkValidation(ofcIpPort, newOfcInfo);
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}	
			return ret;
		}
		
		ConnectionUtilsJdbc utils = null;
		Connection           conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);

			int status = dao.updateOfcInfo(
					conn,
					ofcIpPort,
					newOfcInfo.getIp(),
					newOfcInfo.getPort());

			switch (status) {
				case DB_RESPONSE_STATUS_NOT_FOUND:
					utils.rollback(conn);
					res.setStatus(STATUS_NOTFOUND);
					res.setMessage(String.format(NOT_FOUND));
					return res.toJson();
			
/*
				case DB_RESPONSE_STATUS_EXIST:
					utils.rollback(conn);
					res.setStatus(STATUS_CONFLICT);
					res.setMessage(String.format(ALREADY_EXIST, newOfcInfo.getIp()));
					return res.toJson();
*/
			}

			utils.commit(conn);
			res.setStatus(STATUS_SUCCESS);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			return res.toJson();
		} finally {
			utils.close(conn);
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}


	@Override
	public String readOfcList() {
		String fname = "readOfcList";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - start", fname));
		}
		OfcInfoListReadJsonOut res = new OfcInfoListReadJsonOut();
		res.setStatus(STATUS_SUCCESS);

		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(true);

			Dao dao = new DaoImpl(utils);
			List<Map<String, Object>> infoMapList = dao.getOfcInfoList(conn);

			List<OfcInfo> result = new ArrayList<OfcInfo>();
			for (Map<String, Object> infoMap : infoMapList) {
				OfcInfo ofc = new OfcInfo();
				ofc.setIp((String) infoMap.get("ip"));
				ofc.setPort((Integer) infoMap.get("port"));
				result.add(ofc);
			}

			res.setResult(result);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
    		return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	@Override
	public String readOfc(String ofcIpPort) {
		String fname = "readOfc";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - start", fname));
		}
		OfcInfoReadJsonOut res = new OfcInfoReadJsonOut();
		res.setStatus(STATUS_SUCCESS);

		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(true);

			Dao dao = new DaoImpl(utils);
			Map<String, Object> infoMap = dao.getOfcInfo(conn, ofcIpPort);
				
			if(infoMap == null){
				res.setStatus(STATUS_NOTFOUND);
				return res.toJson();
			}
			
			OfcInfo ofcInfo = new OfcInfo();
			ofcInfo.setIp((String) infoMap.get("ip"));
			ofcInfo.setPort((Integer) infoMap.get("port"));
			res.setResult(ofcInfo);
			return res.toJson();
			
		} catch (SQLException | RuntimeException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
    		return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}
}
