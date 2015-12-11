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

package org.okinawaopenlabs.constants;

public class OrientDBDefinition {
	/* Routing */
	public static final int DIJKSTRA_WEIGHT_NO_ROUTE = 100;
	public static final int DIJKSTRA_WEIGHT_AVAILABLE_ROUTE = 1;
	public static final long USED_BLOCKING_VALUE = 1000000000000L;
	public static final long SPINE_BUS_USED_VALUE = 10240000L;

	/* Node Type */
	public static final String NODE_TYPE_SERVER = "Server";
	public static final String NODE_TYPE_SWITCH = "Switch";
	public static final String NODE_TYPE_EX_SWITCH = "ExSwitch";
	public static final String NODE_TYPE_LEAF = "Leaf";
	public static final String NODE_TYPE_SPINE = "Spine";

	/* DB Response */
	public static final int DB_RESPONSE_STATUS_OK = 200;
	public static final int DB_RESPONSE_STATUS_EXIST = 210;
	public static final int DB_RESPONSE_STATUS_NOT_FOUND = 404;
	public static final int DB_RESPONSE_STATUS_USED = 220;
	public static final int DB_RESPONSE_STATUS_FORBIDDEN = 403;
	public static final int DB_RESPONSE_STATUS_FAIL = 500;

	/* SQL Node Key */
	public static final String SQL_NODE_KEY_NAME = "name";
	public static final String SQL_NODE_KEY_TYPE = "type";
	public static final String SQL_NODE_KEY_FLAG = "ofpFlag";

	/* select */
//	public static final String SQL_GET_NODE_INFO_LIST = "select @rid.asString(), name, type, datapathId, ofcIp, from node";
	public static final String SQL_GET_NODE_INFO_LIST_FM_SYS = "select system_resource_id.node_id.name as name, system_resource_id.node_id.location as location, system_resource_id.type as type, system_resource_id.tenant as tenant, dpid as datapathId, ofc_id.ip as ip, ofc_id.port as port from ofs";
	public static final String SQL_GET_NODE_INFO_LIST_FM_RENT = "select node_id.name as name, node_id.location as location, type, tenant from rent_resource";
	public static final String SQL_GET_NODE_INFO_FROM_DEVICE_NAME = "select @rid.asString(), name, type, datapathId, ofcIp from node where name=?";
	public static final String SQL_GET_NODE_INFO_FROM_DEVICE_RID  = "select @rid.asString(), name, type, datapathId, ofcIp from node where @rid=?";
	public static final String SQL_GET_NODE_RID_FROM_DEVICENAME   = "select @rid.asString() from node where name=?";
	public static final String SQL_GET_DEVICENAME_FROM_DATAPATHID = "select name from node where datapathId = ?";
	public static final String SQL_GET_DEVICE_INFO_FROM_DEVICERID = "select name, type, datapathId, ofcIp from node where @RID = ?";

	public static final String SQL_GET_PORT_INFO_FROM_PORTRID    = "select @rid.asString(), name, number, deviceName from port where @RID = ?";
	public static final String SQL_GET_PORT_INFO_FROM_DEVICENAME = "select @rid.asString(), name, number, deviceName from port where deviceName=?";
	public static final String SQL_GET_PORT_INFO_FROM_PORT_NAME  = "select @rid.asString(), name, number, deviceName from port where name = ? and deviceName = ?";
	public static final String SQL_GET_PORT_RID_FROM_DEVICENAME_PORTNAME  = "select @rid.asString() from port where name = ? and deviceName = ?";
	public static final String SQL_GET_PORTRID_FROM_DEVICENAME_PORTNUMBER = "select @rid.asString() from port where deviceName = ? and number = ?";
	public static final String SQL_GET_PORT_BAND_FROM_DEVICENAME_PORTNAME  = "select band from port where name = ? and deviceName = ?";

	public static final String SQL_GET_CABLE_FROM_IN_PORTRID =
			"select in.deviceName as inDeviceName, in.name as inPortName, in.number as inPortNumber, "
			+ "out.deviceName as outDeviceName, out.name as outPortName, out.number as outPortNumber, @RID.asString(), band, used "
			+ "from link where in.@RID = ? and in.@class='port' and out.@class='port'";
	public static final String SQL_GET_CABLE_FROM_OUT_PORTRID =
			"select in.deviceName as inDeviceName, in.name as inPortName, in.number as inPortNumber, "
			+ "out.deviceName as outDeviceName, out.name as outPortName, out.number as outPortNumber, @RID.asString(), band, used "
			+ "from link where out.@RID = ? and in.@class='port' and out.@class='port'";
	public static final String SQL_GET_CABLE_LINKS    =
			"select in.deviceName as inDeviceName, in.name as inPortName, in.number as inPortNumber, "
			+ "out.deviceName as outDeviceName, out.name as outPortName, out.number as outPortNumber, @RID, band, used "
			+ "from link where in.deviceName = ? and out.@class = 'port'";

	public static final String SQL_GET_PATCH_WIRINGS_FROM_DEVICENAME          =
			"select out, in, parent, sequence, inDeviceName, inPortName, outDeviceName, outPortName from patchWiring where inDeviceName=?";
	public static final String SQL_GET_PATCH_WIRINGS_FROM_DEVICENAME_PORTNAME =
			"select out, in, parent, sequence, inDeviceName, inPortName, outDeviceName, outPortName from patchWiring " +
			"where inDeviceName=? and inPortName=? order by sequence asc";
	public static final String SQL_GET_PATCH_WIRINGS_FROM_NODERID =
			"select out, in, parent, sequence, inDeviceName, inPortName, outDeviceName, outPortName from patchWiring where parent=?";
	public static final String SQL_GET_PATCH_WIRINGS_FROM_PORTRID =
			"select out, in, parent, sequence, inDeviceName, inPortName, outDeviceName, outPortName from patchWiring where out=? or in=?";
	public static final String SQL_GET_PATCH_INFO_FROM_PATCHWIRING_PORTRID =
			"select out, in, parent, sequence, inDeviceName, inPortName, outDeviceName, outPortName from patchWiring where in = ?";
	public static final String SQL_GET_PATCH_INFO_FROM_PATCHWIRING_DEVICENAME_PORTNAME =
			"select out, in, parent, sequence from patchWiring " +
			"where inDeviceName = ? and inPortName = ? and outDeviceName = ? and outPortName = ? order by sequence desc";
	public static final String SQL_GET_PATCH_WIRINGS_FROM_PARENTRID =
			"select out, in, parent, sequence, inDeviceName, inPortName, outDeviceName, outPortName from patchWiring " +
			"where parent=? order by sequence asc limit=100000";

	public static final String SQL_GET_MAX_INTERNALMAC = "select max(internalMac) as maxInternalMac from internalMacMap";
	public static final String SQL_GET_INTERNALMAC_FROM_SRCMAC_DSTMAC_INPORT_DEVICENAME =
			"select internalMac from internalMacMap where deviceName = ? and inPort = ? and srcMac = ? and dstMac = ?";
	public static final String SQL_GET_INTERNALMAC_LIST_FROM_DEVICENAME_INPORT =
			"select internalMac from internalMacMap where deviceName = ? and inPort = ?";
	public static final String SQL_GET_INTERNALMAC_INFO_LIST_FROM_DEVICENAME_PORTNUMBER =
			"select srcMac, dstMac, internalMac from internalMacMap where deviceName = ? and inPort = ? limit = 100000";

	public static final String SQL_GET_DIJKSTRA_PATH_FLATTEN  =
			"select @rid.asString() as rid, name, number, deviceName, type, datapathId, ofcIp, @class " +
			"from (select flatten(dijkstra) from (select dijkstra(?,?,'used')))";
	public static final String SQL_GET_NEIGHBOR_PORT_INFO_FROM_PORT_RID =
			"select out.@rid.asString() as rid, out.name as name, out.deviceName as deviceName, out.number as number from cable where in.@RID = ?";

	/* insert */
	public static final String SQL_INSERT_NODE      = "create vertex node set name = '%s', type = '%s', datapathId = '%s', ofcIp = '%s'";
	public static final String SQL_INSERT_NODE_INFO = "create vertex node set name = ?, type = ?, datapathId = ?, ofcIp = ?";
	public static final String SQL_INSERT_PORT      = "create vertex port set name = '%s', number = %s, band = %s, deviceName = '%s'";
	public static final String SQL_INSERT_PORT_INFO = "create vertex port set name = ?, number = ?, band = ?, deviceName = ?";
	public static final String SQL_INSERT_UBUS  = "create edge ubus from ? to ? set used = ?";
	public static final String SQL_INSERT_DBUS  = "create edge dbus from ? to ? set used = ?";
	public static final String SQL_INSERT_CABLE = "create edge cable from ? to ? set used = 0";
	public static final String SQL_INSERT_PATCH_WIRING_2 = "insert into patchWiring(in, out, parent, inDeviceName, inPortName, outDeviceName, outPortName, sequence) values (?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String SQL_INSERT_INTERNALMAC = "insert into internalMacMap(deviceName, inPort, srcMac, dstMac, internalMac) values (?, ?, ?, ?, ?)";

	/* delete */
	public static final String SQL_DELETE_NODE_FROM_NODERID = "delete vertex node where @RID = ?";
	public static final String SQL_DELETE_PORT_FROM_PORTRID = "delete vertex port where @RID = ?";
	public static final String SQL_DELETE_PORT_FROM_DEVICENAME = "delete vertex port where deviceName = ?";
	public static final String SQL_DELETE_BUS_FROM_ONE_PORTRID = "delete edge bus where out = ? or in = ?";
	public static final String SQL_DELETE_CABLE_FROM_ONE_PORTRID = "delete edge cable where out = ? and in = ?";
	public static final String SQL_DELETE_PATCH_WIRING_FROM_DEVICE_NAME_PORT_NAME = "delete from patchWiring where (inDeviceName=? and inPortName=?) or (outDeviceName=? and outPortName=?)";
	public static final String SQL_DELETE_INTERNALMAC_FROM_DEVICE_NAME_PORT_NAME = "delete from internalMacMap where deviceName=? and inPort=?";

	/* update */
	public static final String SQL_UPDATE_NODE_INFO_FROM_RID = "update node set name = ?, datapathId = ? , ofcIp = ? where @RID = ?";
	public static final String SQL_UPDATE_PORT_INFO_FROM_RID = "update port set name = ?, number = ?, band = ?, where @RID = ?";
	public static final String SQL_UPDATE_PORT_DEVICENAME = "update port set deviceName = ? where deviceName = ?";
	public static final String SQL_UPDATE_CALBE_LINK_USED_VALUE_FROM_PORT_RID = "update link set used = ? where out.@class='port' and in.@class='port' and (in.@RID = ? or out.@RID = ?)";
	public static final String SQL_UPDATE_PATCH_WIRING_INDEVICENAME  = "update patchWiring set  inDeviceName = ? where  inDeviceName = ?";
	public static final String SQL_UPDATE_PATCH_WIRING_OUTDEVICENAME = "update patchWiring set outDeviceName = ? where outDeviceName = ?";
	public static final String SQL_UPDATE_PATCH_WIRING_INPORTNAME  = "update patchWiring set  inPortName = ? where  inPortName = ? and  inDeviceName = ?";
	public static final String SQL_UPDATE_PATCH_WIRING_OUTPORTNAME = "update patchWiring set outPortName = ? where outPortName = ? and outDeviceName = ?";

	/* OFP Flag */
	public static final String OFP_FLAG_TRUE  = "true";
	public static final String OFP_FLAG_FALSE = "false";
}
