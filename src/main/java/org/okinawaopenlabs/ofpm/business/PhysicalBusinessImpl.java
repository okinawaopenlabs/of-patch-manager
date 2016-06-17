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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;

import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.common.BaseResponse;
import org.okinawaopenlabs.ofpm.json.device.DeviceInfo;
import org.okinawaopenlabs.ofpm.json.device.PortData;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalLink;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopology;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopologyGetJsonOut;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopology.OfpConDeviceInfo;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopology.OfpConPortInfo;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopology.getnetwork;

import org.okinawaopenlabs.ofpm.json.topology.physical.ConnectPhysicalLinksJsonIn;
import org.okinawaopenlabs.ofpm.json.topology.physical.DisconnectPhysicalLinksJsonIn;
import org.okinawaopenlabs.ofpm.json.topology.physical.PhysicalLink;
import org.okinawaopenlabs.ofpm.json.topology.physical.AddNetworkId;
import org.okinawaopenlabs.ofpm.utils.Config;
import org.okinawaopenlabs.ofpm.utils.ConfigImpl;
import org.okinawaopenlabs.ofpm.utils.OFPMUtils;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;
import org.okinawaopenlabs.ofpm.validate.topology.physical.ConnectPhysicalLinksJsonInValidate;
import org.okinawaopenlabs.ofpm.validate.topology.physical.DisconnectPhysicalLinksJsonInValidate;
import org.okinawaopenlabs.ofpm.validate.topology.physical.AddNetworkIdJsonInValidate;
import org.okinawaopenlabs.orientdb.client.ConnectionUtilsJdbc;
import org.okinawaopenlabs.orientdb.client.ConnectionUtilsJdbcImpl;
import org.okinawaopenlabs.orientdb.client.Dao;
import org.okinawaopenlabs.orientdb.client.DaoImpl;

public class PhysicalBusinessImpl implements PhysicalBusiness {
	private static final Logger logger = Logger.getLogger(PhysicalBusinessImpl.class);

	Config conf = new ConfigImpl();

	@Override
	public String getPhysicalTopology() {
		final String fname = "getLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - start", fname));
		}
		LogicalTopologyGetJsonOut res = new LogicalTopologyGetJsonOut();

		ConnectionUtilsJdbc utilsJdbc = null;
		Connection conn = null;
		try {
			utilsJdbc = new ConnectionUtilsJdbcImpl();
			conn = utilsJdbc.getConnection(true);
			Dao dao = new DaoImpl(utilsJdbc);

			List<Map<String, Object>> infoMapList = dao.getNodeInfoList(conn);

			/* Make nodes and links */
			List<OfpConDeviceInfo> nodeList = new ArrayList<OfpConDeviceInfo>();
			for (Map<String, Object> infoMap : infoMapList) {
				OfpConDeviceInfo node = new OfpConDeviceInfo();
				node.setDeviceName((String) infoMap.get("name"));
				node.setDeviceType((String) infoMap.get("type"));
				node.setLocation((String) infoMap.get("location"));
				node.setTenant((String) infoMap.get("tenant"));

				List<OfpConPortInfo> portList = new ArrayList<OfpConPortInfo>();
				List<Map<String, Object>> linkDocList = dao.getCableLinksFromDeviceName(conn, (String) infoMap.get("name"));
				if (linkDocList == null) {
					return null;
				}
				for (Map<String, Object> linkDoc : linkDocList) {
					String outDevName = (String)linkDoc.get("outDeviceName");

					PortData ofpPort = new PortData();
					String  outPortName = (String)linkDoc.get("outPortName");
					Integer outPortNmbr = (Integer)linkDoc.get("outPortNumber");
					ofpPort.setDeviceName(outDevName);
					ofpPort.setPortName(outPortName);
					ofpPort.setPortNumber(outPortNmbr);

					String  inPortName = (String)linkDoc.get("inPortName");
					Integer inPortNmbr = (Integer)linkDoc.get("inPortNumber");
					OfpConPortInfo port = new OfpConPortInfo();
					port.setPortName(inPortName);
					port.setPortNumber(inPortNmbr);
					port.setOfpPortLink(ofpPort);

					portList.add(port);
				}
				node.setPorts(portList);
				
				nodeList.add(node);
			}
			

			List<Map<String, Object>> cableMapList = dao.getCableList(conn);
			Set<LogicalLink> linkSet = new HashSet<LogicalLink>();
			List<LogicalLink>      linkList = new ArrayList<LogicalLink>();
			for (Map<String, Object> cableMap : cableMapList) {

				String inDevName  = (String)cableMap.get("inDeviceName");
				String inPortName = (String)cableMap.get("inPortName");
				PortData inPort = new PortData();
				inPort.setDeviceName(inDevName);
				inPort.setPortName(inPortName);

				String outDevName  = (String)cableMap.get("outDeviceName");
				String outPortName = (String)cableMap.get("outPortName");
				PortData outPort = new PortData();
				outPort.setDeviceName(outDevName);
				outPort.setPortName(outPortName);

				List<PortData> ports = new ArrayList<PortData>();
				ports.add(inPort);
				ports.add(outPort);

				LogicalLink link = new LogicalLink();
				link.setLink(ports);

				linkSet.add(link);
			}
			linkList.addAll(linkSet);

			LogicalTopology topology = new LogicalTopology();
			topology.setNodes(nodeList);
			topology.setLinks(linkList);

			// create response data
			res.setResult(topology);
			res.setStatus(STATUS_SUCCESS);
		} catch (Exception e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		} finally {
			utilsJdbc.close(conn);
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	@Override
	public String connectPhysicalLink(String physicalLinkJson) {
		final String fname = "connectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> obj and validation check */
		ConnectPhysicalLinksJsonIn inParam = null;
		try {
			inParam = ConnectPhysicalLinksJsonIn.fromJson(physicalLinkJson);
			ConnectPhysicalLinksJsonInValidate validator = new ConnectPhysicalLinksJsonInValidate();
			validator.checkValidation(inParam);
		} catch (JsonSyntaxException jse) {
			OFPMUtils.logErrorStackTrace(logger, jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		} catch (ValidateException ve) {
			OFPMUtils.logErrorStackTrace(logger, ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 2: Create cable-link */
		ConnectionUtilsJdbc utils = null;
		Connection          conn  = null;
		Map<String, Object> port0_info,port1_info;
		
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			for (PhysicalLink link : inParam.getLinks()) {
				PortData port0 = link.getLink().get(0);
				PortData port1 = link.getLink().get(1);

				int status = dao.createCableLink(
						conn,
						port0.getDeviceName(),
						port0.getPortName(),
						port1.getDeviceName(),
						port1.getPortName());

				switch (status) {
					case DB_RESPONSE_STATUS_OK:
						continue;

					case DB_RESPONSE_STATUS_NOT_FOUND:
						utils.rollback(conn);
						res.setMessage(String.format(NOT_FOUND, port0.getPortName() + " or " + port1.getPortName()));
						res.setStatus(STATUS_NOTFOUND);
						return res.toJson();

					case DB_RESPONSE_STATUS_EXIST:
						utils.rollback(conn);
						res.setStatus(STATUS_CONFLICT);
						res.setMessage(String.format(ALREADY_EXIST, port0.getPortName() + "<-->" + port1.getPortName()));
						return res.toJson();

					default:
						utils.rollback(conn);
						res.setStatus(STATUS_INTERNAL_ERROR);
						return res.toJson();
				}
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
			System.out.println("added to spine switch");
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	@Override
	public String disconnectPhysicalLink(String physicalLinkJson) {
		final String fname = "disconnectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> obj and validation check */
		DisconnectPhysicalLinksJsonIn disconPhysicalLinks = null;
		try {
			disconPhysicalLinks = DisconnectPhysicalLinksJsonIn.fromJson(physicalLinkJson);
			DisconnectPhysicalLinksJsonInValidate validator = new DisconnectPhysicalLinksJsonInValidate();
			validator.checkValidation(disconPhysicalLinks);
		} catch (JsonSyntaxException jse) {
			OFPMUtils.logErrorStackTrace(logger, jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		} catch (ValidateException ve) {
			OFPMUtils.logErrorStackTrace(logger, ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 2: Delete cable-link */
		ConnectionUtilsJdbc utils = null;
		Connection          conn  = null;
		Map<String, Object> port0_info,port1_info;

		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			for (PhysicalLink link : disconPhysicalLinks.getLinks()) {
				PortData port0 = link.getLink().get(0);
				PortData port1 = link.getLink().get(1);

				int status = dao.deleteCableLink(
						conn,
						port0.getDeviceName(),
						port0.getPortName(),
						port1.getDeviceName(),
						port1.getPortName());

				switch (status) {
					case DB_RESPONSE_STATUS_OK:
						port0_info = dao.getNodeInfoFromDeviceName(
								conn,
								port0.getDeviceName());

						port1_info = dao.getNodeInfoFromDeviceName(
								conn,
								port1.getDeviceName());
						
						String port0_type = port0_info.get("type").toString();						
						String port1_type = port1_info.get("type").toString();
						
						if((port0_type.equals("Aggregate_Switch") && port1_type.equals("Spine")) || 
								(port0_type.equals("Spine") && port1_type.equals("Aggregate_Switch")))
						{
							String rid="";
							if(port0_type.equals("Spine"))
							{
								rid = port0_info.get("rid").toString();
							}
							else
							{
								rid = port1_info.get("rid").toString();								
							}
							//int nwid = dao.returnNetworkid(conn, rid);
							//System.out.println(nwid);
						}
						continue;

					case DB_RESPONSE_STATUS_NOT_FOUND:
						utils.rollback(conn);
						res.setMessage(String.format(NOT_FOUND, port0.getPortName() + "<-->" + port1.getPortName()));
						res.setStatus(STATUS_NOTFOUND);
						return res.toJson();

					case DB_RESPONSE_STATUS_USED:
						utils.rollback(conn);
						res.setStatus(STATUS_FORBIDDEN);
						res.setMessage(String.format(IS_PATCHED, port0.getPortName() + " or " + port1.getPortName()));
						return res.toJson();

					case DB_RESPONSE_STATUS_FAIL:
						utils.rollback(conn);
						res.setStatus(STATUS_FORBIDDEN);
						res.setMessage(String.format(COULD_NOT_DELETE, port0.getPortName() + "<-->" + port1.getPortName()));
						return res.toJson();

					default:
						utils.rollback(conn);
						res.setStatus(STATUS_INTERNAL_ERROR);
						res.setMessage(UNEXPECTED_ERROR);
						return res.toJson();
				}
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
			System.out.println("delete to spine switch");
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}
	
	@Override
	public String addnetworkid(String physicalLinkJson) {
		final String fname = "addnetworkid";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> obj and validation check */

		AddNetworkId inParam = null;

		try {
			inParam = AddNetworkId.fromJson(physicalLinkJson);
			AddNetworkIdJsonInValidate validator = new AddNetworkIdJsonInValidate();
			validator.checkValidation(inParam);
		} catch (JsonSyntaxException jse) {
			OFPMUtils.logErrorStackTrace(logger, jse);			
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		} catch (ValidateException ve) {
			OFPMUtils.logErrorStackTrace(logger, ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 2: Create cable-link */
		ConnectionUtilsJdbc utils = null;
		Connection          conn  = null;
		
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			System.out.println(inParam);
			int status = 201;
			for(int i=inParam.GetStart();i<=inParam.GetEnd();i++)
			{
				status = dao.AddNetworkId(
						conn,
						i,
						inParam.GetType());

				if(status == 1)
				{
					status = DB_RESPONSE_STATUS_OK;
				}
				else
				{
					break;
				}
			}
			System.out.println(status);
			switch (status) {
					case DB_RESPONSE_STATUS_OK:
						utils.commit(conn);
						res.setStatus(STATUS_CREATED);
						return res.toJson();

					case DB_RESPONSE_STATUS_EXIST:
						utils.rollback(conn);
						res.setStatus(STATUS_CONFLICT);
						res.setMessage(String.format(ALREADY_EXIST, "" + "<-->" + ""));
						return res.toJson();

					default:
						utils.rollback(conn);
						res.setStatus(STATUS_INTERNAL_ERROR);
						return res.toJson();
				}
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
	public String delnetworkid(String physicalLinkJson) {
		final String fname = "addnetworkid";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> obj and validation check */

		AddNetworkId inParam = null;

		try {
			inParam = AddNetworkId.fromJson(physicalLinkJson);
			AddNetworkIdJsonInValidate validator = new AddNetworkIdJsonInValidate();
			validator.checkValidation(inParam);
		} catch (JsonSyntaxException jse) {
			OFPMUtils.logErrorStackTrace(logger, jse);			
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		} catch (ValidateException ve) {
			OFPMUtils.logErrorStackTrace(logger, ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 2: Create cable-link */
		ConnectionUtilsJdbc utils = null;
		Connection          conn  = null;
		
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			System.out.println(inParam);
			int status = 201;
			int vlanid;
			for(vlanid=inParam.GetStart();vlanid<=inParam.GetEnd();++vlanid)
			{
				status = dao.delNetworkId(
						conn,
						vlanid,
						inParam.GetType());
				if(status == 1)
				{
					status = DB_RESPONSE_STATUS_OK;
				}
				else
				{
					break;
				}
			}
			switch (status) {
					case DB_RESPONSE_STATUS_OK:
						utils.commit(conn);
						res.setStatus(STATUS_CREATED);
						return res.toJson();

					case DB_RESPONSE_STATUS_NOW_USED:
						utils.rollback(conn);
						res.setStatus(STATUS_NOW_USED);
						res.setMessage(String.format(NOW_USED,vlanid));
						return res.toJson();

					default:
						utils.rollback(conn);
						res.setStatus(STATUS_INTERNAL_ERROR);
						return res.toJson();
			}
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	@Override
	public String getNetworkId() {
		final String fname = "getNetworkId";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - start", fname));
		}
		LogicalTopologyGetJsonOut res = new LogicalTopologyGetJsonOut();

		ConnectionUtilsJdbc utilsJdbc = null;
		Connection conn = null;
		try {
			utilsJdbc = new ConnectionUtilsJdbcImpl();
			conn = utilsJdbc.getConnection(true);
			Dao dao = new DaoImpl(utilsJdbc);

			List<Map<String, Object>> infoMapList = dao.getNetworkId(conn);
			System.out.println(infoMapList);

			/* Make nodes and links */
			List<getnetwork> outer_tag_list = new ArrayList<getnetwork>();
			for (Map<String, Object> infoMap : infoMapList) {
				getnetwork NetworkId = new getnetwork();
				NetworkId.setnetworkId((int) infoMap.get("outer_tag"));
				NetworkId.setnetworkType((String) infoMap.get("name"));
				NetworkId.setUseRoute((String) infoMap.get("spine1")+"<=>"+(String) infoMap.get("spine2"));
				outer_tag_list.add(NetworkId);
			}
			LogicalTopology topology = new LogicalTopology();
			topology.setNetwork(outer_tag_list);
			// create response data
			res.setResult(topology);
			res.setStatus(STATUS_SUCCESS);
		} catch (Exception e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		} finally {
			utilsJdbc.close(conn);
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}
}
