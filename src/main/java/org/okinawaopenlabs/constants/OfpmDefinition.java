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

public class OfpmDefinition {
	/* Define version */
	public static final String CONFIG_KEY_VERSION = "version";

	/* Define static http code */
	public static final int STATUS_SUCCESS        = 200;
	public static final int STATUS_CREATED        = 201;
	public static final int STATUS_BAD_REQUEST    = 400;
	public static final int STATUS_UNAUTHORIZED   = 401;
	public static final int STATUS_FORBIDDEN      = 403;
	public static final int STATUS_NOTFOUND       = 404;
	public static final int STATUS_CONFLICT       = 409;
	public static final int STATUS_INTERNAL_ERROR = 500;

	public static final String HTTP_METHOD_GET    = "GET";
	public static final String HTTP_METHOD_PUT    = "PUT";
	public static final String HTTP_METHOD_POST   = "POST";
	public static final String HTTP_METHOD_DELETE = "DELETE";

	/* Define property file */
	public static final String DEFAULT_PROPERTIY_FILE = "ofpm.properties";

	/* Define openflow controller config */
	public static final String OFC_ADD_FLOWENTRY_PATH = "/stats/flowentry/add";
	public static final String OFC_DELETE_FLOWENTRY_PATH = "/stats/flowentry/delete";
	public static final String OFC_PATH = "/ofc/ryu/ctrl";

	/* Define Database url */
	public static final String CONFIG_KEY_DB_URL = "db.url";
	public static final String CONFIG_KEY_DB_USER = "db.user";
	public static final String CONFIG_KEY_DB_DRIVER = "db.driver";
	public static final String CONFIG_KEY_DB_PASSWORD = "db.password";
	public static final String CONFIG_KEY_DB_MAX_ACTIVE_CONN = "db.conn.active.max";
	public static final String CONFIG_KEY_DB_WAIT = "db.conn.wait";
	

	/* Define DMDB node traffic type */
	public static final String DEVICE_TRAFFIC_TYPE_CLIENT  = "001";
	public static final String DEVICE_TRAFFIC_TYPE_SERVER  = "002";
	public static final String DEVICE_TRAFFIC_TYPE_MEASURE = "003";
	public static final String DEVICE_TRAFFIC_TYPE_PLANE   = "004";
	public static final String DEVICE_TRAFFIC_TYPE_SWITCH  = "005";

	/* Define validation parameters */
	public static final int COLLECT_NUMBER_OF_DEVICE_NAMES_IN_LINK = 2;
	public static final String[] ENABLE_OFP_FLAGS    = {"true", "false"};
	public static final String[] ENABLE_DEVICE_TYPES = {"Server", "Switch", "Leaf", "Spine", "Aggregate_Switch", "Sites_Switch"};

	public static final String[] LEGACY_DEVICE_TYPES = {"Server", "Switch", "Aggregate_Switch", "Sites_Switch"};
	public static final String[] OPEN_FLOW_DEVICE_TYPES = {"Leaf", "Spine"};

	public static final String CSV_SPLIT_REGEX = ",";
	public static final String REGEX_NUMBER = "[0-9]+";
	public static final String REGEX_IPADDRESS = "[0-9,\\.,:]+";
	public static final String REGEX_DATAPATH_ID = "0x[0-9a-fA-F]{1,16}";

	public static final int DEVICE_NAME_MAX_LENGTH = 30;
	public static final int DEVICE_LOCATION_MAX_LENGTH = 30;
	public static final int DEVICE_TENANT_MAX_LENGTH = 50;
	public static final int DEVICE_DATAPATHID_LENGTH = 18;
	
	public static final String DEVICE_IPADDRESS_VALUE = "[0-255]";
	public static final String DEVICE_PORT_VALUE = "[1024-65535]";
	
	public static final int PORT_NAME_MAX_LENGTH = 30;
	public static final int MIN_PORT_VALUE = 1024;
	public static final int MAX_PORT_VALUE = 65535;
	public static final int MAX_IPADDRESS_VALUE = 255;
	
	public static final int BAND_VALUE_1G = 1024;
	public static final int BAND_VALUE_10G = 10240;


	/* Define max macaddress value */
	public static final long MIN_MACADDRESS_VALUE = -140737488355329L;
	public static final long MAX_MACADDRESS_VALUE = 140737488355328L;

	/* Define max link ratio */
	public static final long LINK_MAXIMUM_USED_RATIO = 10000L;

	/* Define network instance type */
	public static final String NETWORK_INSTANCE_TYPE = "VLAN";
	public static final long MIN_ETHER_PACKET_SIZE_BIT = 512L;
	
	public static final long CONVERT_MBPS_BPS = 1048576L;	// 1024 * 1024
	public static final long MIN_ETHER_FRAME_SIZE_BIT = 512L;
	public static final long VLAN_FEALD_SIZE_BIT = 32L;	// VLAN field 4byte = 32bit
}
