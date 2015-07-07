package org.okinawaopenlabs.constants;

public class OfpmDefinition {
	/* Define version */
	public static final int MAJOR_VERSION         = 0;
	public static final int MINOR_VERSION         = 1;
	public static final int BUILD_VERSION         = 1;

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
	public static final String[] ENABLE_DEVICE_TYPES = {"Server", "Switch", "ExSwitch", "Leaf", "Spine"};
	public static final String CSV_SPLIT_REGEX = ",";
	public static final String REGEX_NUMBER = "[0-9]+";
	public static final String REGEX_DATAPATH_ID = "[0-9a-fA-F]{1,16}";

	/* Define max macaddress value */
	public static final long MIN_MACADDRESS_VALUE = -140737488355329L;
	public static final long MAX_MACADDRESS_VALUE = 140737488355328L;

	/* Define max link ratio */
	public static final long LINK_MAXIMUM_USED_RATIO = 10000L;
}
