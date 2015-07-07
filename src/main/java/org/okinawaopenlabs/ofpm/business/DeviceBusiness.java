package org.okinawaopenlabs.ofpm.business;

public interface DeviceBusiness {
	/* Device */
	/**
	 * Create Device business Logic
	 * @param newDeviceInfoJson String
	 * @return response entity Json String
	 */
	public String createDevice(String newDeviceInfoJson);

	/**
	 * delete Device business Logic
	 * @param deviceName String
	 * @return response entity Json String
	 */
	public String deleteDevice(String deviceName);

	/**
	 * Update Device business Logic
	 * @param deviceName String
	 * @param updateDeviceInfoJson String
	 * @return response entity Json String
	 */
	public String updateDevice(String deviceName, String updateDeviceInfoJson);

	/**
	 * Read Device from db.
	 * @param deviceName
	 * @return
	 */
	public String readDevice(String deviceName);

	/**
	 * Read Devices list from db.
	 * @return
	 */
	public String readDeviceList();

	/* Port */
	/**
	 * Create Port business Logic
	 * @param newPortInfoJson String
	 * @return response entity Json String
	 */
	public String createPort(String deviceName, String newPortInfoJson);

	/**
	 * delete Port business Logic
	 * @param deviceName String
	 * @param portName String
	 * @return response entity Json String
	 */
	public String deletePort(String deviceName, String portName);

	/**
	 * update Device business Logic
	 * @param deviceName String
	 * @param portName String]
	 * @param updatePortInfoJson String
	 * @return response entity Json String
	 */
	public String updatePort(String deviceName, String portName, String updatePortInfoJson);

	/* Connect */
	/**
	 * get Port which connected device business Logic
	 * @param deviceName String
	 * @return response entity Json String
	 */
	public String getConnectedPortInfo(String deviceName);
}
