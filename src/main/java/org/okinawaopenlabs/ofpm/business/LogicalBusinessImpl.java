package org.okinawaopenlabs.ofpm.business;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import static org.okinawaopenlabs.constants.OrientDBDefinition.*;
import static org.okinawaopenlabs.constants.OfcClientDefinition.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import com.google.gson.JsonSyntaxException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.okinawaopenlabs.ofpm.client.OFCClient;
import org.okinawaopenlabs.ofpm.client.OFCClientImpl;
import org.okinawaopenlabs.ofpm.exception.NoRouteException;
import org.okinawaopenlabs.ofpm.exception.OFCClientException;
import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.common.BaseResponse;
import org.okinawaopenlabs.ofpm.json.device.PortData;
import org.okinawaopenlabs.ofpm.json.ofc.InitFlowIn;
import org.okinawaopenlabs.ofpm.json.ofc.SetFlowIn;
import org.okinawaopenlabs.ofpm.json.ofc.SetFlowToOFC;
import org.okinawaopenlabs.ofpm.json.ofc.SetFlowToOFC.Action;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalLink;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopology;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopology.OfpConDeviceInfo;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopology.OfpConPortInfo;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopologyGetJsonOut;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopologyUpdateJsonIn;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopologyUpdateJsonOut;
import org.okinawaopenlabs.ofpm.utils.Config;
import org.okinawaopenlabs.ofpm.utils.ConfigImpl;
import org.okinawaopenlabs.ofpm.utils.OFPMUtils;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;
import org.okinawaopenlabs.ofpm.validate.ofc.InitFlowInValidate;
import org.okinawaopenlabs.ofpm.validate.topology.logical.LogicalTopologyValidate;
import org.okinawaopenlabs.orientdb.client.ConnectionUtilsJdbc;
import org.okinawaopenlabs.orientdb.client.ConnectionUtilsJdbcImpl;
import org.okinawaopenlabs.orientdb.client.Dao;
import org.okinawaopenlabs.orientdb.client.DaoImpl;

public class LogicalBusinessImpl implements LogicalBusiness {
	private static final Logger logger = Logger.getLogger(LogicalBusinessImpl.class);

	Config conf = new ConfigImpl();
	Dao dao = null;

	public LogicalBusinessImpl() {
		if (logger.isDebugEnabled()) {
			logger.debug("LogicalBusinessImpl");
		}
	}

	/**
	 * Normalize nodes for update/get LogicalTopology.
	 * Remove node that deviceType is not SERVER or SWITCH and remove node that have no ports.
	 * @param conn
	 * @param nodes
	 * @throws SQLException
	 */
	private void normalizeLogicalNode(Connection conn, Collection<OfpConDeviceInfo> nodes) throws SQLException {
		final String fname = "normalizeLogicalNode";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(conn=%s, nodes=%s) - start", fname, conn, nodes));
		}
		Map<String, Boolean> devTypeMap = new HashMap<String, Boolean>();
		List<OfpConDeviceInfo> removalNodeList = new ArrayList<OfpConDeviceInfo>();
		for (OfpConDeviceInfo node : nodes) {
			String devType = node.getDeviceType();
			if (!devType.equals(NODE_TYPE_SERVER) && !devType.equals(NODE_TYPE_SWITCH) && !devType.equals(NODE_TYPE_EX_SWITCH)) {
				removalNodeList.add(node);
				continue;
			}

			List<OfpConPortInfo> ports = node.getPorts();
			List<OfpConPortInfo> removalPortList = new ArrayList<OfpConPortInfo>();
			for (OfpConPortInfo port : ports) {
				String neiDevName = port.getOfpPortLink().getDeviceName();
				/* check outDevice is LEAF, other wise don't append port */
				Boolean isOfpSw = devTypeMap.get(neiDevName);
				if (isOfpSw == null) {
					Map<String, Object> outDevDoc = dao.getNodeInfoFromDeviceName(conn, neiDevName);
					String outDevType = (String)outDevDoc.get("type");
					isOfpSw = OFPMUtils.isNodeTypeOfpSwitch(outDevType);
					devTypeMap.put(neiDevName, isOfpSw);
				}
				if (!isOfpSw) {
					removalPortList.add(port);
				}
			}
			ports.removeAll(removalPortList);

			/* if node don't has port that connect to leaf-switch, node remove from nodeList */
			if (ports.isEmpty()) {
				removalNodeList.add(node);
			}
		}
		nodes.removeAll(removalNodeList);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

	/**
	 * Make node for update/get-LogicalTopology from deviceName, and return it.
	 * @param conn
	 * @param devName
	 * @return node for Logicaltopology.
	 * @throws SQLException
	 */
	private OfpConDeviceInfo getLogicalNode(Connection conn, String devName) throws SQLException {
		final String fname = "getLogicalNode";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(conn=%s, devName=%s) - start", fname, conn, devName));
		}
		Map<String, Object> devDoc = dao.getNodeInfoFromDeviceName(conn, devName);
		if (devDoc == null) {
			return null;
		}
		OfpConDeviceInfo node = new OfpConDeviceInfo();
		node.setDeviceName(devName);
		node.setDeviceType((String)devDoc.get("type"));
		node.setLocation((String)devDoc.get("location"));
		node.setTenant((String)devDoc.get("tenant"));

		List<OfpConPortInfo> portList = new ArrayList<OfpConPortInfo>();
		List<Map<String, Object>> linkDocList = dao.getCableLinksFromDeviceName(conn, devName);
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
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, node));
		}
		return node;
	}

	/**
	 * Normalize links for update/get LogicalTopology.
	 * Remove list that does not contains nodes.
	 * @param nodes
	 * @param links
	 */
	private void normalizeLogicalLink(Collection<OfpConDeviceInfo> nodes, Collection<LogicalLink> links) {
		final String fname = "normalizeLogicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(nodes=%s, links=%s) - start", fname, nodes, links));
		}

		List<LogicalLink> removalLinks = new ArrayList<LogicalLink>();
		for (LogicalLink link : links) {
			List<PortData> ports = link.getLink();
			if (!OFPMUtils.nodesContainsPort(nodes, ports.get(0).getDeviceName(), null)) {
				removalLinks.add(link);
			} else if (!OFPMUtils.nodesContainsPort(nodes, ports.get(1).getDeviceName(), null)) {
				removalLinks.add(link);
			}
		}
		links.removeAll(removalLinks);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

	/**
	 * Make list of link for LogicalTopology from deviceName, and return it.
	 * @param conn
	 * @param devName
	 * @param setPortNumber If this value is false, portNumber is every time 0.
	 * @return list of link for LogicalTopology.
	 * @throws SQLException
	 */
	private Set<LogicalLink> getLogicalLink(Connection conn, String devName, boolean setPortNumber) throws SQLException {
		final String fname = "getLogicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(conn=%s, devName=%s, setPortNumber=%s) - start", fname, conn, devName, setPortNumber));
		}
		Set<LogicalLink> linkSet = new HashSet<LogicalLink>();
		List<Map<String, Object>> patchDocList = dao.getLogicalLinksFromDeviceName(conn, devName);
		if (patchDocList == null) {
			return null;
		}

		/*check src->dst*/
		for (Map<String, Object> patchDoc : patchDocList) {
			String inDevName  = (String)patchDoc.get("inDeviceName");
			String inPortName = (String)patchDoc.get("inPortName");
			PortData inPort = new PortData();
			inPort.setDeviceName(inDevName);
			inPort.setPortName(inPortName);

			String outDevName  = (String)patchDoc.get("outDeviceName");
			String outPortName = (String)patchDoc.get("outPortName");
			PortData outPort = new PortData();
			outPort.setDeviceName(outDevName);
			outPort.setPortName(outPortName);

			List<PortData> ports = new ArrayList<PortData>();
			ports.add(inPort);
			ports.add(outPort);

			LogicalLink link = new LogicalLink();
			link.setLink(ports);

			linkSet.add(link);


			PortData inPort_2 = new PortData();
			inPort_2.setDeviceName(outDevName);
			inPort_2.setPortName(outPortName);
			PortData outPort_2 = new PortData();
			outPort_2.setDeviceName(inDevName);
			outPort_2.setPortName(inPortName);

			List<PortData> ports_2 = new ArrayList<PortData>();
			ports_2.add(inPort_2);
			ports_2.add(outPort_2);

			LogicalLink link_2 = new LogicalLink();
			link_2.setLink(ports_2);

			linkSet.add(link_2);

		}

		if (linkSet.isEmpty()) {
			return null;
		}
		if (!setPortNumber) {
			return linkSet;
		}
		/* Set port number at port data in logical link */
		for (LogicalLink link : linkSet) {
			for (PortData port : link.getLink()) {
				Map<String, Object> portMap = dao.getPortInfoFromPortName(
						conn,
						port.getDeviceName(),
						port.getPortName());
				port.setPortNumber((Integer)portMap.get("number"));
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, linkSet));
		}
		return linkSet;
	}

	public String getLogicalTopology(String deviceNamesCSV) {
		final String fname = "getLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.trace(String.format("%s(deviceNames=%s) - start", fname, deviceNamesCSV));
		}
		LogicalTopologyGetJsonOut res = new LogicalTopologyGetJsonOut();

		/* PHASE 1: Validation */
		List<String> deviceNames = null;
		try {
			BaseValidate.checkStringBlank(deviceNamesCSV);
			deviceNames = Arrays.asList(deviceNamesCSV.split(CSV_SPLIT_REGEX));
			BaseValidate.checkArrayStringBlank(deviceNames);
			BaseValidate.checkArrayOverlapped(deviceNames);
			// TODO: check user tenant(by used-info from DMDB).
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 2: Get logical topology */
		ConnectionUtilsJdbc utilsJdbc = null;
		Connection conn = null;
		try {
			utilsJdbc = new ConnectionUtilsJdbcImpl();
			conn = utilsJdbc.getConnection(true);
			dao = new DaoImpl(utilsJdbc);

			/* Make nodes and links */
			List<OfpConDeviceInfo> nodeList = new ArrayList<OfpConDeviceInfo>();
			List<LogicalLink>      linkList = new ArrayList<LogicalLink>();
			Set<LogicalLink>       linkSet  = new HashSet<LogicalLink>();
			for (String devName : deviceNames) {
				OfpConDeviceInfo node = this.getLogicalNode(conn, devName);
				if (node == null) {
					continue;
				}
				nodeList.add(node);

				Set<LogicalLink> links = this.getLogicalLink(conn, devName, true);
				if (links == null) {
					continue;
				}
				linkSet.addAll(links);
			}
			linkList.addAll(linkSet);
			this.normalizeLogicalNode(conn,     nodeList);

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
			logger.trace(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	public String updateLogicalTopology(String requestedTopologyJson) {
		final String fname = "updateLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.trace(String.format("%s(requestedTopology=%s) - start", fname, requestedTopologyJson));
		}
		LogicalTopologyUpdateJsonOut res = new LogicalTopologyUpdateJsonOut();
		res.setStatus(STATUS_SUCCESS);
		res.setResult(null);

		LogicalTopologyUpdateJsonIn requestedTopology = null;
		try {
			requestedTopology = LogicalTopologyUpdateJsonIn.fromJson(requestedTopologyJson);
		} catch (JsonSyntaxException jse) {
			OFPMUtils.logErrorStackTrace(logger, jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			String ret = res.toString();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		try {
			LogicalTopologyValidate validator = new LogicalTopologyValidate();
			validator.checkValidationRequestIn(requestedTopology);
			// TODO: check user tenant (by used-info in DMDB).
		} catch (ValidateException ve) {
			OFPMUtils.logErrorStackTrace(logger, ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		MultivaluedMap<String, SetFlowToOFC> reducedFlows   = new MultivaluedHashMap<String, SetFlowToOFC>();
		MultivaluedMap<String, SetFlowToOFC> augmentedFlows = new MultivaluedHashMap<String, SetFlowToOFC>();
		ConnectionUtilsJdbc utilsJdbc = null;
		Connection conn = null;
		try {
			/* initialize db connectors */
			utilsJdbc = new ConnectionUtilsJdbcImpl();
			conn = utilsJdbc.getConnection(false);
			dao  = new DaoImpl(utilsJdbc);

			/* compute Inclement/Declement LogicalLink */
			List<LogicalLink> incLinkList = new ArrayList<LogicalLink>();
			List<LogicalLink> decLinkList = new ArrayList<LogicalLink>();
			{
				List<OfpConDeviceInfo> requestedNodes    = requestedTopology.getNodes();
				List<LogicalLink>      requestedLinkList = requestedTopology.getLinks();
				Set<LogicalLink>       currentLinkList   = new HashSet<LogicalLink>();

				/* Create current links */
				for (OfpConDeviceInfo requestedNode : requestedNodes) {
					String devName = requestedNode.getDeviceName();
					Set<LogicalLink> linkSet = this.getLogicalLink(conn, devName, false);
					if (linkSet != null) {
						currentLinkList.addAll(linkSet);
					}
				}
				this.normalizeLogicalLink(requestedNodes, currentLinkList);

				/* Set port number 0, because when run Collection.removeAll, port number remove influence. */
				for (LogicalLink link : requestedLinkList) {
					for (PortData port : link.getLink()) {
						port.setPortNumber(null);
					}
				}

				/* get difference between current and next */
				decLinkList.addAll(currentLinkList);
				decLinkList.removeAll(requestedLinkList);

				incLinkList.addAll(requestedLinkList);
				incLinkList.removeAll(currentLinkList);

				/* sort incliment links. 1st link have port name, final link no have port name. */
				Collections.sort(incLinkList, new Comparator<LogicalLink>() {
					@Override
					public int compare(LogicalLink link1, LogicalLink link2) {
						int score1 = 0;
						for (PortData port1 : link1.getLink()) {
							if (StringUtils.isBlank(port1.getPortName())) {
								score1++;
							}
						}
						int score2 = 0;
						for (PortData port2 : link2.getLink()) {
							if (StringUtils.isBlank(port2.getPortName())) {
								score2++;
							}
						}
						return score1 - score2;
					}
				});

				List<LogicalLink> trushIncLinkList = new ArrayList<LogicalLink>();
				for (LogicalLink incLink : incLinkList) {
					List<PortData> incPorts = incLink.getLink();
					for (LogicalLink decLink: decLinkList) {
						List<PortData> decPorts = decLink.getLink();
						if (OFPMUtils.PortDataNonStrictEquals(decPorts.get(0), incPorts.get(0)) && OFPMUtils.PortDataNonStrictEquals(decPorts.get(1), incPorts.get(1))) {
							decLinkList.remove(decLink);
							trushIncLinkList.add(incLink);
							break;
						} else if (OFPMUtils.PortDataNonStrictEquals(decPorts.get(0), incPorts.get(1)) && OFPMUtils.PortDataNonStrictEquals(decPorts.get(1), incPorts.get(0))) {
							decLinkList.remove(decLink);
							trushIncLinkList.add(incLink);
							break;
						}
					}
				}
				incLinkList.removeAll(trushIncLinkList);
			}

			/* update patch wiring and make patch link */
			for (LogicalLink link : decLinkList) {
				this.addDeclementLogicalLink(conn, link, reducedFlows);
			}
			for (LogicalLink link : incLinkList) {
				this.addInclementLogicalLink(conn, link, augmentedFlows);
			}

			/* Make nodes and links */
			List<LogicalLink>      linkList = new ArrayList<LogicalLink>();
			Set<LogicalLink>       linkSet  = new HashSet<LogicalLink>();
			List<OfpConDeviceInfo> nodeList    = requestedTopology.getNodes();
			for (OfpConDeviceInfo node : nodeList) {
				String devName = node.getDeviceName();
				Set<LogicalLink> links = this.getLogicalLink(conn, devName, false);
				if (links == null) {
					continue;
				}
				linkSet.addAll(links);
			}
			linkList.addAll(linkSet);

			LogicalTopology topology = new LogicalTopology();
			topology.setNodes(nodeList);
			topology.setLinks(linkList);

			// create response data
			res.setResult(topology);

			utilsJdbc.commit(conn);
		} catch (NoRouteException e) {
			utilsJdbc.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_NOTFOUND);
			res.setMessage(e.getMessage());
			return res.toJson();
		} catch (Exception e) {
			utilsJdbc.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			return res.toJson();
		} finally {
			utilsJdbc.close(conn);
			if (logger.isDebugEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, res));
			}
		}

		/* PHASE : Set flow to OFPS via OFC */
		try {
			for (Entry<String, List<SetFlowToOFC>> entry : reducedFlows.entrySet()) {
				OFCClient client = new OFCClientImpl();
				String ofpIp = (String)entry.getKey();
				for (SetFlowToOFC flow : entry.getValue()) {
					client.deleteFlows(ofpIp, flow);
				}
			}
			for (Entry<String, List<SetFlowToOFC>> entry : augmentedFlows.entrySet()) {
				OFCClient client = new OFCClientImpl();
				String ofpIp = (String)entry.getKey();
				for (SetFlowToOFC flow : entry.getValue()) {
					client.addFlows(ofpIp, flow);
				}
			}
		} catch (OFCClientException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.trace(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	@Override
	public String initFlow(String requestedData) {
		final String fname = "initFlow";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedData=%s)", fname, requestedData));
		}
		BaseResponse res = new BaseResponse();
		res.setStatus(STATUS_SUCCESS);

		/* PHASE 1: validation check */
		InitFlowIn req = null;
		try {
			req = InitFlowIn.fromJson(requestedData);
			InitFlowInValidate validator = new InitFlowInValidate();
			validator.checkValidation(req);
		} catch (Throwable t) {
			OFPMUtils.logErrorStackTrace(logger, t);
			{
				if (t instanceof JsonSyntaxException) {
					res.setStatus(STATUS_BAD_REQUEST);
					res.setMessage(INVALID_JSON);
				} else if (t instanceof ValidateException) {
					res.setStatus(STATUS_BAD_REQUEST);
					res.setMessage(t.getMessage());
				} else {
					res.setStatus(STATUS_INTERNAL_ERROR);
					res.setMessage(t.getMessage());
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s)", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 2:Set Flows into OFPS that is designated by datapathId. */
		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(true);

			Dao dao = new DaoImpl(utils);
			String devName = dao.getDeviceNameFromDatapathId(conn, (String)req.getDatapathId());
			if (null == devName) {
				res.setStatus(STATUS_NOTFOUND);
				res.setMessage(String.format(NOT_FOUND, (String)req.getDatapathId()));
				return res.toJson();
			}
			Map<String, Object> devInfo = dao.getNodeInfoFromDeviceName(conn, devName);
			if (null == devInfo) {
				res.setStatus(STATUS_NOTFOUND);
				res.setMessage(String.format(NOT_FOUND, devName));
				return res.toJson();
			}
			Long datapathId = Long.decode((String)devInfo.get("datapathId"));
			String ofcIp = (String) devInfo.get("ofcIp");
			OFCClient client = new OFCClientImpl();

			/* set all drop flow */
			SetFlowToOFC reqData = client.createRequestData(datapathId, OPENFLOW_FLOWENTRY_PRIORITY_DROP, null, null);
			client.addFlows(ofcIp, reqData);
			List<Map<String, Object>> routeList = dao.getRouteFromNodeRid(conn, (String) devInfo.get("rid"));

			for (Map<String, Object> route : routeList) {
				Integer sequence = (Integer)route.get("sequence_num");
				Integer inPortNumber = (Integer)route.get("in_port_number");
				Integer outPortNumber = (Integer)route.get("out_port_number");

				Map<String, Object> logicalLink = dao.getLogicalLinkFromRid(conn, (String) route.get("logical_link_id"));
				Long nwInstanceId = (Long)logicalLink.get("nw_instance_id");
				List<Map<String, Object>> path = dao.getRouteFromLogicalLinkId(conn, (String)route.get("logical_link_id"));

				/* port 2 port flow */
				if (path.size() == 1) {
					SetFlowToOFC requestData = client.createRequestData(datapathId, OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
					client.createMatchForInPort(requestData, inPortNumber.longValue());
					client.createActionsForOutputPort(requestData, outPortNumber.longValue());
					client.addFlows(ofcIp, requestData);

					requestData = client.createRequestData(datapathId, OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
					client.createMatchForInPort(requestData, outPortNumber.longValue());
					client.createActionsForOutputPort(requestData, inPortNumber.longValue());
					client.addFlows(ofcIp, requestData);

					continue;
				}
				/* not port 2 port flow */

				if (sequence == 1) {
					/* first patch switch */
					SetFlowToOFC requestData = client.createRequestData(datapathId, OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
					client.createMatchForInPort(requestData, inPortNumber.longValue());
					client.createActionsForPushVlan(requestData, outPortNumber.longValue(), nwInstanceId);
					client.addFlows(ofcIp, requestData);

					requestData = client.createRequestData(datapathId, OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
					client.createMatchForInPortDlVlan(requestData, outPortNumber.longValue(), nwInstanceId);
					client.createActionsForPopVlan(requestData, inPortNumber.longValue());
					client.addFlows(ofcIp, requestData);
				} else if (sequence == path.size()) {
					/* final patch switch */
					SetFlowToOFC requestData = client.createRequestData(datapathId, OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
					client.createMatchForInPort(requestData, outPortNumber.longValue());
					client.createActionsForPushVlan(requestData, inPortNumber.longValue(), nwInstanceId);
					client.addFlows(ofcIp, requestData);

					requestData = client.createRequestData(datapathId, OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
					client.createMatchForInPortDlVlan(requestData, inPortNumber.longValue(), nwInstanceId);
					client.createActionsForPopVlan(requestData, outPortNumber.longValue());
					client.addFlows(ofcIp, requestData);
				} else {
					Map<String, Object> ofpNodeDataMap = dao.getNodeInfoFromDeviceName(conn, route.get("node_name").toString());
						if(ofpNodeDataMap.get("type").equals(NODE_TYPE_SPINE))
						{
							SetFlowToOFC requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
							client.createMatchForInPortDlVlan(requestData, inPortNumber.longValue(), nwInstanceId);
							client.createActionsForOutputPort(requestData, outPortNumber.longValue());
							client.addFlows(ofcIp, requestData);

							requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
							client.createMatchForInPortDlVlan(requestData, outPortNumber.longValue(), nwInstanceId);
							client.createActionsForOutputPort(requestData, inPortNumber.longValue());
							client.addFlows(ofcIp, requestData);
						}
						else if(ofpNodeDataMap.get("type").equals(NODE_TYPE_AGGREGATE_SW))
						{

							List<Map<String,Object>> flows = null;
							flows = dao.getOutertagflows(conn,(String)ofpNodeDataMap.get("datapathId"));
							for(int i=0 ;i<flows.size();++i)
							{
								Map<String, Object> a = flows.get(i);

								Long ag_inport = Long.parseLong(a.get("ag_inport").toString());
								Long ag_outport = Long.parseLong(a.get("ag_outport").toString());
								Long localvlan = Long.parseLong(a.get("localvlan").toString());
								Long outer_tag = Long.parseLong(a.get("outer_tag").toString());

								SetFlowToOFC requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
								if(a.get("type").equals("push"))
								{
									client.createMatchForInPortDlVlan(requestData, ag_inport,localvlan);
									client.createActionsForPushOuter_tag(requestData, ag_outport, outer_tag,localvlan);
									client.addFlows(ofcIp, requestData);
								}
								else
								{
									client.createMatchForInPortDlVlan(requestData, ag_inport, outer_tag);
									client.createActionsForPopOuter_tag(requestData, ag_outport);
									client.addFlows(ofcIp, requestData);
								}
							}
						}
				}
			}

		} catch (SQLException | OFCClientException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			return res.toJson();
		} catch (Exception e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			return res.toJson();
		} finally {
			utils.close(conn);
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s)", fname, ret));
			}
		}

		return res.toJson();
	}

	/**
	 * Calculate new used-value when reduced patchWiring.
	 * @param conn
	 * @param link
	 * @param band
	 * @param client
	 * @param ofpmToken
	 * @return
	 * @throws SQLException
	 */
	private long calcReduceCableLinkUsed(Connection conn, Map<String, Object> link, long band) throws SQLException {
		final String fname = "updateCableLinkUsed";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(conn=%s, link=%s, band=%d) - start", fname, conn, link, band));
		}
		long used = (Integer)link.get("used");
		long inBand = this.getBandWidth(conn, (String)link.get("inDeviceName"), (String)link.get("inPortName"));
		long outBand = this.getBandWidth(conn, (String)link.get("outDeviceName"), (String)link.get("outPortName"));
		long useBand = (inBand < outBand)? inBand : outBand;
		used -= band;
		if (used > useBand) {
			used = useBand - band;
		}
		if (used < 0) {
			used = 0;
			// MEMO: output log message, however not throw exception. That's right?
			logger.warn(String.format("Used value was been under than zero, and the value modify zero. %s", link));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, used));
		}
		return used;
	}

	/**
	 * Get band width from Orient DB
	 * @param conn
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	private long getBandWidth(Connection conn, String deviceName, String portName) throws SQLException {
		long band = 0;
		try {
			band = Long.parseLong((String)(dao.getPortBandFromDeviceNamePortName(conn, deviceName, portName)));
		} catch (NullPointerException e) {
			throw new RuntimeException(String.format(NOT_FOUND, "port={deviceName:'" + deviceName + "', portName:'" + portName + "'}"));
		}
		return band;
	}

	/**
	 * Calc Vlan tag overhead
	 * @param band
	 * @return
	 */
	private long calcVlanTagOverhead(Long band) {
		long bandBps = band * CONVERT_MBPS_BPS;
		long maxEthFrameNum = bandBps / MIN_ETHER_FRAME_SIZE_BIT;
		long vlanTagOverheadBpsPerSec = maxEthFrameNum * VLAN_FEALD_SIZE_BIT;
		long ret = vlanTagOverheadBpsPerSec / CONVERT_MBPS_BPS;

		return ret;
	}

	/**
	 * Delete logical link, in fact remove patch wiring and update links used value.
	 * @param conn
	 * @param link
	 * @throws SQLException
	 */
	private void addDeclementLogicalLink(Connection conn, LogicalLink link, MultivaluedMap<String, SetFlowToOFC> reducedFlows) throws SQLException {
		PortData inPort  = link.getLink().get(0);

		/* get route */
		Map<String, Object> logicalLinkMap = dao.getLogicalLinkFromNodeNamePortName(conn, inPort.getDeviceName(), inPort.getPortName());
		List<Map<String, Object>> routeMapList = dao.getRouteFromLogicalLinkId(conn, (String)logicalLinkMap.get("rid"));

		if (routeMapList == null || routeMapList.isEmpty()) {
			throw new RuntimeException(String.format(NOT_FOUND, "route=" + link));
		}

		Map<String, Object> txRouteMap = routeMapList.get(0);
		Map<String, Object> rxRouteMap = routeMapList.get(routeMapList.size() - 1);

		/* calc patch band width */
		long bandOverHead = 0L;
		long band = 0L;
		{
			Map<String, Object> txLinkMap = dao.getCableLinkFromInPortRid(conn, (String)txRouteMap.get("in_port_id"));
			Map<String, Object> rxLinkMap = dao.getCableLinkFromInPortRid(conn, (String)rxRouteMap.get("out_port_id"));
			long txBand = this.getBandWidth(conn, (String)txLinkMap.get("inDeviceName"), (String)txLinkMap.get("inPortName"));
			long rxBand = this.getBandWidth(conn, (String)rxLinkMap.get("inDeviceName"), (String)rxLinkMap.get("inPortName"));
			long txOfpBand = this.getBandWidth(conn, (String)txLinkMap.get("outDeviceName"), (String)txLinkMap.get("outPortName"));
			long rxOfpBand = this.getBandWidth(conn, (String)rxLinkMap.get("outDeviceName"), (String)rxLinkMap.get("outPortName"));
			band = (txBand < rxBand)   ? txBand:    rxBand;
			band = (band   < txOfpBand)?   band: txOfpBand;
			band = (band   < rxOfpBand)?   band: rxOfpBand;
			bandOverHead = this.calcVlanTagOverhead(band);
		}

		/* update link-used-value and make patch link for ofc */
		List<String> alreadyProcCable = new ArrayList<String>();
		for (Map<String, Object> routeMap : routeMapList) {
			Map<String, Object> OfpsMap = dao.getNodeInfoFromDeviceName(conn, (String) routeMap.get("node_name"));
			long used = band + bandOverHead;
			String inPortRid  = (String)routeMap.get("in_port_id");
			Map<String, Object> inLink = dao.getCableLinkFromInPortRid(conn, inPortRid);
			String inCableRid = (String)inLink.get("rid");

			if (!alreadyProcCable.contains(inCableRid) && (OfpsMap.get("type").equals("Spine") || OfpsMap.get("type").equals("Leaf"))) {
				long newUsed = this.calcReduceCableLinkUsed(conn, inLink, used);
				dao.updateCableLinkUsedFromPortRid(conn, inPortRid, newUsed);
				alreadyProcCable.add(inCableRid);
			}

			String outPortRid = (String)routeMap.get("out_port_id");
			Map<String, Object> outLink = dao.getCableLinkFromOutPortRid(conn, outPortRid);
			String outCableRid = (String)outLink.get("rid");
			if (!alreadyProcCable.contains(outCableRid) && (OfpsMap.get("type").equals("Spine") || OfpsMap.get("type").equals("Leaf"))) {
				long newUsed = this.calcReduceCableLinkUsed(conn, outLink, used);
				dao.updateCableLinkUsedFromPortRid(conn, outPortRid, newUsed);
				alreadyProcCable.add(outCableRid);
			}
		}
		Map<String, Object> txOfpsMap = dao.getNodeInfoFromDeviceName(conn, (String) txRouteMap.get("node_name"));
		Map<String, Object> txInPortMap = dao.getPortInfoFromPortName(conn, (String) txRouteMap.get("node_name"), (String) txRouteMap.get("in_port_name"));
		Map<String, Object> txOutPortMap = dao.getPortInfoFromPortName(conn, (String) txRouteMap.get("node_name"), (String) txRouteMap.get("out_port_name"));

		Map<String, Object> rxOfpsMap = dao.getNodeInfoFromDeviceName(conn, (String) rxRouteMap.get("node_name"));
		Map<String, Object> rxOutPortMap = dao.getPortInfoFromPortName(conn, (String) rxRouteMap.get("node_name"), (String) rxRouteMap.get("out_port_name"));

		OFCClient client = new OFCClientImpl();

		if (routeMapList.size() == 1 ) {
			String ofcIp = (String)txOfpsMap.get("ip") + ":" + Integer.toString((Integer)txOfpsMap.get("port"));

			SetFlowToOFC requestData = client.createRequestData(Long.decode((String)txOfpsMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
			Integer inPortNumber = (Integer)txInPortMap.get("number");
			client.createMatchForInPort(requestData, inPortNumber.longValue());
			reducedFlows.add(ofcIp, requestData);

			Integer outPortNumber = (Integer)txOutPortMap.get("number");
			requestData = client.createRequestData(Long.decode((String)txOfpsMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
			client.createMatchForInPort(requestData, outPortNumber.longValue());
			reducedFlows.add(ofcIp, requestData);


			/* delete route */
			dao.deleteRouteFromLogicalLinkRid(conn, (String)logicalLinkMap.get("rid"));

			/* delete logical link */
			dao.deleteLogicalLinkFromNodeNamePortName(conn, inPort.getDeviceName(), inPort.getPortName());

			return;
		}

		/* make flow edge-switch tx side */
		{
			String ofcIp = (String)txOfpsMap.get("ip") + ":" + Integer.toString((Integer)txOfpsMap.get("port"));

			SetFlowToOFC requestData = client.createRequestData(Long.decode((String)txOfpsMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
			Integer inPortNumber = (Integer)txInPortMap.get("number");
			client.createMatchForInPort(requestData, inPortNumber.longValue());
			reducedFlows.add(ofcIp, requestData);

			requestData = client.createRequestData(Long.decode((String)txOfpsMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
			client.createMatchForDlVlan(requestData, (Long)logicalLinkMap.get("nw_instance_id"));
			reducedFlows.add(ofcIp, requestData);
		}

		/* make flow edge-switch rx side */
		{
			String ofcIp = (String)rxOfpsMap.get("ip") + ":" + Integer.toString((Integer)rxOfpsMap.get("port"));

			SetFlowToOFC requestData = client.createRequestData(Long.decode((String)rxOfpsMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
			Integer inPortNumber = (Integer)rxOutPortMap.get("number");
			client.createMatchForInPort(requestData, inPortNumber.longValue());
			reducedFlows.add(ofcIp, requestData);

			requestData = client.createRequestData(Long.decode((String)rxOfpsMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
			client.createMatchForDlVlan(requestData, (Long)logicalLinkMap.get("nw_instance_id"));
			reducedFlows.add(ofcIp, requestData);
		}

		/* make flow internal switch */
		{
			for (int i = 1; i < routeMapList.size() - 1; i++) {
				String node_name = (String)routeMapList.get(i).get("node_name");

				Map<String, Object> ofpsMap = dao.getNodeInfoFromDeviceName(conn, node_name);

				if(ofpsMap.get("type").equals(NODE_TYPE_SPINE))
				{
					String ofcIp = (String)ofpsMap.get("ip") + ":" + Integer.toString((Integer)ofpsMap.get("port"));

					SetFlowToOFC requestData = client.createRequestData(Long.decode((String)ofpsMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
					client.createMatchForDlVlan(requestData, (Long)logicalLinkMap.get("nw_instance_id"));
					reducedFlows.add(ofcIp, requestData);
				}
				else if(ofpsMap.get("type").equals(NODE_TYPE_AGGREGATE_SW))
				{
					String ofcIp = (String)ofpsMap.get("ip") + ":" + Integer.toString((Integer)ofpsMap.get("port"));

					SetFlowToOFC requestData = client.createRequestData(Long.decode((String)ofpsMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
					client.createMatchForDlVlan(requestData, (Long)logicalLinkMap.get("nw_instance_id"));
					reducedFlows.add(ofcIp, requestData);
				}
			}
		}

		/* delete route */
		dao.deleteRouteFromLogicalLinkRid(conn, (String)logicalLinkMap.get("rid"));

		/* delete logical link */
		dao.deleteLogicalLinkFromNodeNamePortName(conn, inPort.getDeviceName(), inPort.getPortName());

		/* delete Outer_tag table */
		//dao.DeleteOutertagflows(conn,(Long)logicalLinkMap.get("nw_instance_id"));


		return;
	}

	/**
	 * Create logical link, in fact insert patch wiring, update links used value, and then notify NCS.
	 * @param conn
	 * @param link
	 * @param client
	 * @param ofpmToken
	 * @param augmentedFlows
	 * @throws SQLException
	 * @throws NoRouteException
	 */
	private void addInclementLogicalLink(Connection conn, LogicalLink link, MultivaluedMap<String, SetFlowToOFC> augmentedFlows) throws SQLException, NoRouteException {
		PortData tx = link.getLink().get(0);
		PortData rx = link.getLink().get(1);
		/* get rid of txPort/rxPort */
		String txRid = null;
		String rxRid = null;
		String nwid = null;

		{
			Map<String, Object> txMap =
					(StringUtils.isBlank(tx.getPortName()))
					? dao.getNodeInfoFromDeviceName(conn, tx.getDeviceName())
					: dao.getPortInfoFromPortName(conn, tx.getDeviceName(), tx.getPortName());
			Map<String, Object> rxMap =
					(StringUtils.isBlank(rx.getPortName()))
					? dao.getNodeInfoFromDeviceName(conn, rx.getDeviceName())
					: dao.getPortInfoFromPortName(conn, rx.getDeviceName(), rx.getPortName());
			txRid = (String)txMap.get("rid");
			rxRid = (String)rxMap.get("rid");
		}

		Map<String, Object> txDeviceMap = dao.getNodeInfoFromDeviceName(conn, tx.getDeviceName());
		Map<String, Object> rxDeviceMap = dao.getNodeInfoFromDeviceName(conn, rx.getDeviceName());

		/* get shortest path */
		List<Map<String, Object>> path = dao.getShortestPath(conn, txRid, rxRid);
		String path_route = "";
		for(int i=0;i<path.size();i++)
		{
			if(path.get(i).get("node_name")!=null)
			{
				path_route += path.get(i).get("node_name")+"("+path.get(i).get("name")+")";
				if(i+1!=path.size())
				{
					path_route += "<->";
				}
			}
		}
		System.out.println(path_route);

		//search interpoint route
		String before_rid = "";
		String in_spine_id = "";
		String out_spine_id = "";
		List<String> network = new ArrayList<String>();

		for(int i=0;i<path.size();i++)
		{
			if(path.get(i).get("node_name")!=null)
			{
				Map<String,Object> route = dao.getNodeInfoFromDeviceName(conn, path.get(i).get("node_name").toString());
				if(!before_rid.equals(route.get("rid").toString()))
				{
					if(route.get("type").toString().equals("Spine") && in_spine_id.equals(""))
					{
						in_spine_id = route.get("rid").toString();
					}
					if(route.get("type").toString().equals("Aggregate_Switch"))
					{
						Map<String, Object> search_network = dao.getPortInfoFromPortName(conn,path.get(i).get("node_name").toString(),path.get(i).get("name").toString());
						if(search_network.get("network")!=null && !search_network.get("network").equals(""))
						{
							network.add(search_network.get("network").toString());
						}
					}
					if(route.get("type").toString().equals("Spine") && !network.isEmpty())
					{
						out_spine_id = route.get("rid").toString();
					}
				}
				before_rid=route.get("rid").toString();
			}
		}

		//catch of nwid.  make use of spineID from outer_tag class
		if(!network.isEmpty())
		{
			//search of Vlanid from path.
			List<Map<String, Object>> nwid1 = dao.getNetworkidFromSpineid(conn, in_spine_id, out_spine_id,network.get(0).toString());
			List<Map<String, Object>> nwid2 = dao.getNetworkidFromSpineid(conn, out_spine_id, in_spine_id,network.get(0).toString());

			//not asigned to vlanid from estimate path, asigned to vlanid.
			if((nwid1.isEmpty() && nwid2.isEmpty()))
			{
					String a = network.get(0);
					nwid = dao.payoutNetworkid(conn, in_spine_id, out_spine_id,a);
			}
			else if(!nwid1.isEmpty())
			{
				Map<String, Object> networkid = nwid1.get(0);
				nwid = networkid.get("outer_tag").toString();
			}
			else if(!nwid2.isEmpty())
			{
				Map<String, Object> networkid = nwid2.get(0);
				nwid = networkid.get("outer_tag").toString();
			}
		}

		/* search first/last port */
		int txPortIndex = (StringUtils.isBlank(tx.getPortName()))? 1: 0;
		int rxPortIndex = (StringUtils.isBlank(rx.getPortName()))? path.size() - 2: path.size() - 1;
		Map<String, Object> txPort = path.get(txPortIndex);
		Map<String, Object> rxPort = path.get(rxPortIndex);
		Map<String, Object> txPortMap = dao.getPortInfoFromPortName(conn, (String)txPort.get("node_name"), (String)txPort.get("name"));
		Map<String, Object> rxPortMap = dao.getPortInfoFromPortName(conn, (String)rxPort.get("node_name"), (String)rxPort.get("name"));

		/* check patch wiring exist */
		{
			boolean isTxPatch = dao.isContainsLogicalLinkFromDeviceNamePortName(conn, (String)txPort.get("node_name"), (String)txPort.get("name"));
			boolean isRxPatch = dao.isContainsLogicalLinkFromDeviceNamePortName(conn, (String)rxPort.get("node_name"), (String)rxPort.get("name"));
			if (isTxPatch || isRxPatch) {
				throw new NoRouteException(String.format(IS_NO_ROUTE, (String)txPort.get("node_name") + " " + (String)txPort.get("name"), (String)rxPort.get("node_name") + " " + (String)rxPort.get("name")));
			}
		}

		/* get band width of port info */
		Map<Map<String, Object>, Long> portBandMap = new HashMap<Map<String, Object>, Long>();
		for (Map<String, Object> current : path) {
			if (StringUtils.equals((String)current.get("class"), "port")) {
				long band = this.getBandWidth(conn, (String)current.get("node_name"), (String)current.get("name"));
				portBandMap.put(current, band);
			}
		}

		/* conmute need band-width for patching */
		long needBandOverHead = 0L;
		long needBand = 0L;
		{
			long txBand = portBandMap.get(txPort);
			long rxBand = portBandMap.get(rxPort);
			long txNextBand = portBandMap.get(path.get(txPortIndex + 1));
			long rxNextBand = portBandMap.get(path.get(rxPortIndex - 1));
			needBand = (  txBand <     rxBand)?   txBand:     rxBand;
			needBand = (needBand < txNextBand)? needBand: txNextBand;
			needBand = (needBand < rxNextBand)? needBand: rxNextBand;
			needBandOverHead = this.calcVlanTagOverhead(needBand);
		}

		/* Update links used value */
		for (int i = 1; i < path.size(); i++) {
			Map<String, Object> nowV = path.get(i);
			Map<String, Object> prvV = path.get(i - 1);
			String nowClass = (String)nowV.get("class");
			String prvClass = (String)prvV.get("class");
			if (!StringUtils.equals(nowClass, "port") || !StringUtils.equals(prvClass, "port")) {
				continue;
			}

			String nowPortRid = (String)nowV.get("rid");
			String nowVparentDevType = (String)nowV.get("type");
			String prvVparentDevType = (String)prvV.get("type");

			Map<String, Object> cableLink = dao.getCableLinkFromInPortRid(conn, nowPortRid);
			long nowUsed = (Integer)cableLink.get("used");
			long  inBand = portBandMap.get(nowV);
			long outBand = portBandMap.get(prvV);
			long maxBand = (inBand < outBand)? inBand: outBand;
			long newUsed = 0;

			if((StringUtils.equals(nowVparentDevType, NODE_TYPE_LEAF) && StringUtils.equals(prvVparentDevType, NODE_TYPE_SPINE)) ||
					  (StringUtils.equals(nowVparentDevType, NODE_TYPE_SPINE) && StringUtils.equals(prvVparentDevType, NODE_TYPE_LEAF))){
				//newUsed = nowUsed + needBand +needBandOverHead;
				newUsed = nowUsed + needBand;

				if (newUsed > maxBand) {
					throw new NoRouteException(String.format(NOT_FOUND, "Path"));
				}
				dao.updateCableLinkUsedFromPortRid(conn, nowPortRid, newUsed);
			  //Add balancing to AG - Sites_SW. Balancing is Used weight only.(not check of maxBand)
			} else if((StringUtils.equals(nowVparentDevType, NODE_TYPE_SITES_SW) && StringUtils.equals(prvVparentDevType, NODE_TYPE_AGGREGATE_SW)) ||
					  (StringUtils.equals(nowVparentDevType, NODE_TYPE_AGGREGATE_SW) && StringUtils.equals(prvVparentDevType, NODE_TYPE_SITES_SW))){
				newUsed = nowUsed + needBand +needBandOverHead;
			}else {
				continue;
			}
		}

		/* Make ofpatch index list */
		/* MEMO: Don't integrate to the loop for the above for easy to read. */
		List<Integer> ofpIndexList = new ArrayList<Integer>();
		for (int i = 1; i < path.size(); i++) {
			Map<String, Object> nowV = path.get(i);
			String nowClass = (String)nowV.get("class");
			String devType  = (String)nowV.get("type");
			if (!StringUtils.equals(nowClass, "node")) {
				continue;
			}
			if (!StringUtils.equals(devType, NODE_TYPE_LEAF) && !StringUtils.equals(devType, NODE_TYPE_SPINE) && !StringUtils.equals(devType, NODE_TYPE_AGGREGATE_SW)) {
				continue;
			}
			ofpIndexList.add(new Integer(i));
		}

		String nw_instance_type = NETWORK_INSTANCE_TYPE;
		Long nw_instance_id = dao.getNwInstanceId(conn);
		if (nw_instance_id < 0) {
			throw new NoRouteException(String.format(IS_FULL, "network instance id"));
		}

		/* insert logical link */
		dao.insertLogicalLink(conn,
				(String)txDeviceMap.get("rid"),
				(String)txDeviceMap.get("name"),
				(String)txPortMap.get("rid"),
				(String)txPortMap.get("name"),
				(String)rxDeviceMap.get("rid"),
				(String)rxDeviceMap.get("name"),
				(String)rxPortMap.get("rid"),
				(String)rxPortMap.get("name"),
				nw_instance_id,
				nw_instance_type);

		Map<String, Object> logicalLinkMap = dao.getLogicalLinkFromNodeNamePortName(conn, (String)txDeviceMap.get("name"), (String)txPortMap.get("name"));

		for (int seq = 0; seq < ofpIndexList.size(); seq++) {
			int i = ofpIndexList.get(seq);
			/* insert frowarding patch wiring */
			Map<String, Object>  inPortDataMap = path.get(i-1);
			Map<String, Object> ofpPortDataMap = path.get(i);
			Map<String, Object> outPortDataMap = path.get(i+1);

			dao.insertRoute(
					conn,
					seq + 1,
					(String)logicalLinkMap.get("rid"),
					(String)ofpPortDataMap.get("rid"),
					(String)ofpPortDataMap.get("name"),
					(String)inPortDataMap.get("rid"),
					(String)inPortDataMap.get("name"),
					(Integer)inPortDataMap.get("number"),
					(String)outPortDataMap.get("rid"),
					(String)outPortDataMap.get("name"),
					(Integer)outPortDataMap.get("number"));

		}

		/* make SetFlowToOFC list for each ofcIp */

		OFCClient client = new OFCClientImpl();

		/* port to port patching */
		if (ofpIndexList.size() == 1) {
			int i = ofpIndexList.get(0);
			Map<String, Object>  inPortDataMap = path.get(i - 1);
			Map<String, Object> ofpNodeData = path.get(i);
			Map<String, Object> ofpNodeDataMap = dao.getNodeInfoFromDeviceName(conn, (String)ofpNodeData.get("name"));
			Map<String, Object> outPortDataMap = path.get(i + 1);

			String ofcIp = (String)ofpNodeDataMap.get("ip") + ":" + Integer.toString((Integer)ofpNodeDataMap.get("port"));
			Integer inPortNumber = (Integer)inPortDataMap.get("number");
			Integer outPortNumber = (Integer)outPortDataMap.get("number");

			SetFlowToOFC requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
			client.createMatchForInPort(requestData, inPortNumber.longValue());
			client.createActionsForOutputPort(requestData, outPortNumber.longValue());
			augmentedFlows.add(ofcIp, requestData);

			requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
			client.createMatchForInPort(requestData, outPortNumber.longValue());
			client.createActionsForOutputPort(requestData, inPortNumber.longValue());
			augmentedFlows.add(ofcIp, requestData);
			return;
		}

		/* the first ofps flow */
		{
			int i = ofpIndexList.get(0);
			Map<String, Object>  inPortDataMap = path.get(i - 1);
			Map<String, Object> ofpNodeData = path.get(i);
			Map<String, Object> ofpNodeDataMap = dao.getNodeInfoFromDeviceName(conn, (String)ofpNodeData.get("name"));
			Map<String, Object> outPortDataMap = path.get(i + 1);

			String ofcIp = (String)ofpNodeDataMap.get("ip") + ":" + Integer.toString((Integer)ofpNodeDataMap.get("port"));
			Integer inPortNumber = (Integer)inPortDataMap.get("number");
			Integer outPortNumber = (Integer)outPortDataMap.get("number");

			SetFlowToOFC requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
			client.createMatchForInPort(requestData, inPortNumber.longValue());
			client.createActionsForPushVlan(requestData, outPortNumber.longValue(), nw_instance_id);
			augmentedFlows.add(ofcIp, requestData);

			requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
			client.createMatchForInPortDlVlan(requestData, outPortNumber.longValue(), nw_instance_id);
			client.createActionsForPopVlan(requestData, inPortNumber.longValue());
			augmentedFlows.add(ofcIp, requestData);
		}
		Boolean beforespinecheck = false;
		/* spine ofs flow */
		for (int i = 1; i < ofpIndexList.size() - 1; i++) {
			/* insert frowarding patch wiring */
			int index = ofpIndexList.get(i);
			Map<String, Object>  inPortDataMap = path.get(index-1);
			Map<String, Object> ofpNodeData = path.get(index);
			Map<String, Object> ofpNodeDataMap = dao.getNodeInfoFromDeviceName(conn, (String)ofpNodeData.get("name"));
			Map<String, Object> outPortDataMap = path.get(index+1);

			if(ofpNodeDataMap.get("type").equals("Spine"))
			{
				String ofcIp = (String)ofpNodeDataMap.get("ip") + ":" + Integer.toString((Integer)ofpNodeDataMap.get("port"));
				Integer inPortNumber = (Integer)inPortDataMap.get("number");
				Integer outPortNumber = (Integer)outPortDataMap.get("number");

				SetFlowToOFC requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
				client.createMatchForInPortDlVlan(requestData, inPortNumber.longValue(), nw_instance_id);
				client.createActionsForOutputPort(requestData, outPortNumber.longValue());
				augmentedFlows.add(ofcIp, requestData);

				requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
				client.createMatchForInPortDlVlan(requestData, outPortNumber.longValue(), nw_instance_id);
				client.createActionsForOutputPort(requestData, inPortNumber.longValue());
				augmentedFlows.add(ofcIp, requestData);
				beforespinecheck = true;
			}
			else if(ofpNodeDataMap.get("type").equals("Aggregate_Switch"))
			{
				String ofcIp = (String)ofpNodeDataMap.get("ip") + ":" + Integer.toString((Integer)ofpNodeDataMap.get("port"));

				Integer inPortNumber = (Integer)inPortDataMap.get("number");
				Integer outPortNumber = (Integer)outPortDataMap.get("number");

				SetFlowToOFC requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
				if(beforespinecheck.equals(true))
				{
					client.createMatchForInPortDlVlan(requestData, inPortNumber.longValue(),nw_instance_id);
					client.createActionsForPushOuter_tag(requestData, outPortNumber.longValue(),Long.parseLong(nwid),nw_instance_id);
					augmentedFlows.add(ofcIp, requestData);

					dao.insertOutertag(conn,nw_instance_id.toString(),nwid,(String)ofpNodeDataMap.get("datapathId"),inPortNumber.toString(),outPortNumber.toString(),"push",network.get(0).toString());

					requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
					client.createMatchForInPortDlVlan(requestData, outPortNumber.longValue(), Long.parseLong(nwid));
					client.createActionsForPopOuter_tag(requestData, inPortNumber.longValue());
					augmentedFlows.add(ofcIp, requestData);
					beforespinecheck = false;

					dao.insertOutertag(conn,nw_instance_id.toString(),nwid,(String)ofpNodeDataMap.get("datapathId"),outPortNumber.toString(),inPortNumber.toString(),"pop",network.get(0).toString());
				}
				else
				{
					client.createMatchForInPortDlVlan(requestData, outPortNumber.longValue(),nw_instance_id);
					client.createActionsForPushOuter_tag(requestData, inPortNumber.longValue(), Long.parseLong(nwid),nw_instance_id);
					augmentedFlows.add(ofcIp, requestData);
					dao.insertOutertag(conn,nw_instance_id.toString(),nwid,(String)ofpNodeDataMap.get("datapathId"),outPortNumber.toString(),inPortNumber.toString(),"push",network.get(0).toString());
					requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
					client.createMatchForInPortDlVlan(requestData, inPortNumber.longValue(), Long.parseLong(nwid));
					client.createActionsForPopOuter_tag(requestData, outPortNumber.longValue());
					augmentedFlows.add(ofcIp, requestData);
					dao.insertOutertag(conn,nw_instance_id.toString(),nwid,(String)ofpNodeDataMap.get("datapathId"),inPortNumber.toString(),outPortNumber.toString(),"pop",network.get(0).toString());
				}
			}
		}

		/* the final ofps flow */
		{
			int i = ofpIndexList.get(ofpIndexList.size() - 1);
			Map<String, Object>  inPortDataMap = path.get(i + 1);
			Map<String, Object> ofpNodeData = path.get(i);
			Map<String, Object> ofpNodeDataMap = dao.getNodeInfoFromDeviceName(conn, (String)ofpNodeData.get("name"));
			Map<String, Object> outPortDataMap = path.get(i - 1);
			String ofcIp = (String)ofpNodeDataMap.get("ip") + ":" + Integer.toString((Integer)ofpNodeDataMap.get("port"));
			Integer inPortNumber = (Integer)inPortDataMap.get("number");
			Integer outPortNumber = (Integer)outPortDataMap.get("number");

			SetFlowToOFC requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
			client.createMatchForInPort(requestData, inPortNumber.longValue());
			client.createActionsForPushVlan(requestData, outPortNumber.longValue(), nw_instance_id);
			augmentedFlows.add(ofcIp, requestData);

			requestData = client.createRequestData(Long.decode((String)ofpNodeDataMap.get("datapathId")), OPENFLOW_FLOWENTRY_PRIORITY_NORMAL, null, null);
			client.createMatchForInPortDlVlan(requestData, outPortNumber.longValue(), nw_instance_id);
			client.createActionsForPopVlan(requestData, inPortNumber.longValue());
			augmentedFlows.add(ofcIp, requestData);
		}
		return;
	}
}
