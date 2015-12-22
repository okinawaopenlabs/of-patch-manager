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
import java.util.List;
import java.util.Map;

public interface Dao {

	/**
	 * set connectionUtilsJdbc
	 * @param utils
	 */
	void setConnectionUtilsJdbc(ConnectionUtilsJdbc utils);

	void close() throws SQLException;

	/**
	 * get device name from datapathid
	 * @param datapathId
	 * @return device name
	 * @throws SQLException failed sql
	 */
	String getDeviceNameFromDatapathId(Connection conn, String datapathId) throws SQLException;

	/**
	 * get port RID from deviceName and portNumber
	 * @param deviceName
	 * @param portNumber
	 * @return port Rid
	 * @throws SQLException
	 */
	String getPortRidFromDeviceNamePortNumber(Connection conn, String deviceName, Integer portNumber) throws SQLException;

	/**
	 * @param portRid
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Map<String, Object>>> getDevicePortInfoSetFlowFromPortRid(Connection conn, String portRid) throws SQLException;

	/**
	 * get internalmac (if exist then to return it, not if generate internalmac)
	 * @param deviceName
	 * @param inPort
	 * @param srcMac
	 * @param dstMac
	 * @return internalmac String
	 * @throws SQLException
	 */
	//static synchronized String getInternalMacFromDeviceNameInPortSrcMacDstMac(ConnectionUtilsJdbc utilsJdbc, Connection conn, String deviceName, String inPort, String srcMac, String dstMac) throws SQLException;

	/**
	 * get internal-mac-address list. if not exist, return empty list.
	 * @param conn
	 * @param deviceName
	 * @param inPort
	 * @return
	 * @throws SQLException
	 */
	List<String> getInternalMacListFromDeviceNameInPort(Connection conn, String deviceName, String inPort) throws SQLException;

	/**
	 * Get internal-mac record list. if not exist, return empty list.
	 * @param conn
	 * @param deviceName
	 * @param PortNumber
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getInternalMacInfoListFromDeviceNameInPort(Connection conn, String deviceName, Integer PortNumber) throws SQLException;

	/**
	 * Delete internal-mac-address.
	 * @param conn
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	int deleteInternalMac(Connection conn, String deviceName, int inPort) throws SQLException;

	/*********************************************************************************
	 * ----------------------------------------------------------------------------- *
	 *                               JDBC
	 * ----------------------------------------------------------------------------- *
	 *********************************************************************************/

	/**
	 * Get patchWiring-list that is connected to other devices.
	 * @param devName
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getPatchWiringsFromDeviceName(Connection conn, String deviceName) throws SQLException;

	/**
	 * Check if contains pair of deviceName and portName into patchWiring .
	 * @param conn
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	boolean isContainsPatchWiringFromDeviceNamePortName(Connection conn, String deviceName, String portName) throws SQLException;

	/**
	 * Check if contains device name into patchWirings inDeviceName or outDeviceName
	 * @param conn
	 * @param deviceName
	 * @return
	 * @throws SQLException
	 */
	boolean isDeviceNameContainedIntoPatchWiring(Connection conn, String deviceName) throws SQLException;

	/**
	 * Check if contains device rid into patchWirings parent
	 * @param conn
	 * @param nodeRid
	 * @return
	 * @throws SQLException
	 */
	boolean isNodeRidContainedIntoPatchWiring(Connection conn, String nodeRid) throws SQLException;

	/**
	 * Check if containts port rid into patchWirings in or out.
	 * @param conn
	 * @param portRid
	 * @return
	 * @throws SQLException
	 */
	boolean isPortRidContainedIntoPatchWiring(Connection conn, String portRid) throws SQLException;

	/**
	 * Insert patch-wiring infromation into db.
	 * @param ofpRid RID of of-patch switch.
	 * @param in RID of of-patchs in port.
	 * @param out RID of of-patchs out port.
	 * @param inDeviceName
	 * @param inPortName
	 * @param outDeviceName
	 * @param outPortName
	 * @param sequence
	 * @param Connection conn
	 * @throws SQLException
	 */
	int insertPatchWiring(Connection conn, String ofpRid, String in, String out, String inDeviceName, String inPortName, String outDeviceName, String outPortName, int sequence) throws SQLException;

	/**
	 * Delete patchWiring-list from devices port.
	 * @param conn
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	int deletePatchWiring(Connection conn, String deviceName, String portName) throws SQLException;

	/**
	 * Get patchWiring-list from devices port.
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getPatchWiringsFromDeviceNamePortName(Connection conn, String deviceName, String portName) throws SQLException;

	/**
	 * Ge patchWiring-list from parent rid.
	 * @param conn
	 * @param parentRid
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getPatchWiringsFromParentRid(Connection conn, String parentRid) throws SQLException;


	/*============= __     __
	 *  CableLink   #_=====_#
	 *=============           */
	/**
	 * Get link-list that  is connected to other devices port.
	 * The link is correspond to LAN-cable or SPF-cable.
	 * @param devName
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getCableLinksFromDeviceName(Connection conn, String deviceName) throws SQLException;

	/**
	 * Get link-list that is connected to other devices port from in-ports rid.
	 * The link is correspond to LAN-cable or SPF-cable.
	 * @param conn
	 * @param inPortRid
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getCableLinkFromInPortRid(Connection conn, String inPortRid) throws SQLException;

	/**
	 * Get link-list that is connected to other devices port from out-ports rid.
	 * The link is correspond to LAN-cable or SPF-cable.
	 * @param conn
	 * @param outPortRid
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getCableLinkFromOutPortRid(Connection conn, String outPortRid) throws SQLException;

	/**
	 * Create cable link by device name and port name.
	 * Used value set to 0.
	 * @param conn
	 * @param deviceName0
	 * @param portName0
	 * @param deviceName1
	 * @param portName1
	 * @return
	 * @throws SQLException
	 */
	int createCableLink(Connection conn, String deviceName0, String portName0, String deviceName1, String portName1) throws SQLException;

	/**
	 * Delete cable link by device name and port name.
	 * If cable link is used to patchWiring, not delete.
	 * @param conn
	 * @param deviceName0
	 * @param portName0
	 * @param deviceName1
	 * @param portName1
	 * @return
	 * @throws SQLException
	 */
	int deleteCableLink(Connection conn, String deviceName0, String portName0, String deviceName1, String portName1) throws SQLException;

	/**
	 * Modify used-value of cable-link that include ports-rid.
	 * Calbe-link represent LAN-Cable, SFP-Cable.
	 * @param conn
	 * @param portRid
	 * @param newUsed
	 * @throws SQLException
	 */
	void updateCableLinkUsedFromPortRid(Connection conn, String portRid, long newUsed) throws SQLException;



	/**
	 * Get port-to-port path that computed by dijkstra.
	 * @param conn
	 * @param ridA
	 * @param ridZ
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getShortestPath(Connection conn, String ridA, String ridZ) throws SQLException;


	/*================ |1_2 3_4 5_6 7_8   RS-485 |
	 *  NodeInfo I/F   |[_] [_] [_] [_]  _______ |
	 *================ |[_]_[_]_[_]_[_]__\_::::_/| */
	/**
	 * Get DeviceInfo from device name.
	 * @param conn
	 * @param deviceName
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getNodeInfoFromDeviceName(Connection conn, String deviceName) throws SQLException;

	/**
	 * Get DeviceInfo from devices rid.
	 * @param conn
	 * @param ofpRid
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getNodeInfoFromDeviceRid(Connection conn, String ofpRid) throws SQLException;

	/**
	 * Get DeviceInfo list in db.
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getNodeInfoList(Connection conn) throws SQLException;

	/**
	 * Get node rid from device name.
	 * @param conn
	 * @param deviceName
	 * @return
	 * @throws SQLException
	 */
	String getNodeRidFromDeviceName(Connection conn, String deviceName) throws SQLException;


	/**
	 * Create DeviceInfo.
	 * @param conn
	 * @param deviceName
	 * @param deviceType
	 * @param datapathId
	 * @param ofcIp
	 * @return
	 */
	int createNodeInfo(Connection conn, String deviceName, String deviceType, String location, String tenant, String datapathId, String ofcIp) throws SQLException;

	/**
	 * Update DeviceInfo.
	 * @param conn
	 * @param keyDeviceName current device name.
	 * @param deviceName new device name
	 * @param datapathId new datapath id
	 * @param ofcIp new openflow controller ip
	 * @return
	 * @throws SQLException
	 */
	int updateNodeInfo(Connection conn, String keyDeviceName, String deviceName, String location, String tenant, String datapathId, String ofcIp) throws SQLException;

	/**
	 * Delete DeviceInfo
	 * @param conn
	 * @param deviceName deleted device name
	 * @return
	 * @throws SQLException
	 */
	int deleteNodeInfo(Connection conn, String deviceName) throws SQLException;


	/*================ | _|    |_ |
	 *  PortInfo I/F   ||        ||
	 *================ ||__====__|| */
	/**
	 * Create PortInfo.
	 * @param conn
	 * @param portName
	 * @param portNumber
	 * @param band
	 * @param deviceName
	 * @return
	 */
	int createPortInfo(Connection conn, String portName, Integer portNumber, Integer band, String deviceName) throws SQLException;

	/**
	 * Update PortInfo
	 * @param conn
	 * @param keyPortName
	 * @param keyDeviceName
	 * @param portName
	 * @param portNumber
	 * @param band
	 * @return
	 * @throws SQLException
	 */
	int updatePortInfo(Connection conn, String keyPortName, String keyDeviceName, String portName, Integer portNumber, Integer band) throws SQLException;

	/**
	 * Delete PortInfo
	 * @param conn
	 * @param portName
	 * @param deviceName
	 * @return
	 * @throws SQLException
	 */
	int deletePortInfo(Connection conn, String portName, String deviceName) throws SQLException;

	/**
	 * Get port info from device name and port name.
	 * @param conn
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getPortInfoFromPortName(Connection conn, String deviceName, String portName) throws SQLException;

	/**
	 * Get port info from rid.
	 * @param conn
	 * @param rid
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getPortInfoFromPortRid(Connection conn, String rid) throws SQLException;

	/**
	 * Get port info from device name.
	 * @param conn
	 * @param deviceName
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getPortInfoListFromDeviceName(Connection conn, String deviceName) throws SQLException;

	/**
	 * Get neighbor port from port rid.
	 * @param conn
	 * @param portRid
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getNeighborPortFromPortRid(Connection conn, String portRid) throws SQLException;

	/**
	 * Get node info from rid.
	 * @param conn
	 * @param rid
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getDeviceInfoFromDeviceRid(Connection conn, String rid) throws SQLException;


	/**
	 * Get port rid from device name and port name
	 * @param conn
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	String getPortRidFromDeviceNamePortName(Connection conn, String deviceName, String portName) throws SQLException;

	/**
	 * Get port band from device name and port name
	 * @param conn
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	String getPortBandFromDeviceNamePortName(Connection conn, String deviceName, String portName) throws SQLException;

	/**
	 * Get ofc rid from ofcIp
	 * @param conn
	 * @param ofcIp
	 * @return
	 * @throws SQLException
	 */
	String getOfcRid(Connection conn, String ofcIp) throws SQLException;

	/**
	 * Get OfcInfo list in db.
	 * @param conn
	 * @return
	 * @throws SQLException
	 */	
	List<Map<String, Object>> getOfcInfoList(Connection conn) throws SQLException;

	/**
	 * Get OfcInfo .
	 * @param conn
	 * @return
	 * @throws SQLException
	 */		
	Map<String, Object> getOfcInfo(Connection conn, String ofcIpPort) throws SQLException;
	
	/**
	 * Create OfcInfo.
	 * @param conn
	 * @param ip
	 * @param port
	 * @return
	 * @throws SQLException
	 */	
	int createOfcInfo(Connection conn, String ip, Integer port) throws SQLException;

	/**
	 * Delete ofc
	 * @param conn
	 * @param ofcIpPort
	 * @return
	 * @throws SQLException
	 */
	int deleteOfcInfo(Connection conn, String ofcIpPort) throws SQLException;

}
