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
	 * read Port list business Logic
	 * @param newPortInfoJson String
	 * @return response entity Json String
	 */
	public String readPortList(String deviceName);

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

	/**
	 * Create Ofc business Logic
	 * @param newOfcInfoJson String
	 * @return response entity Json String
	 */	
	public String createOfc(String newOfcInfoJson);

	/**
	 * delete Ofc business Logic
	 * @param ofcIpPort String
	 * @return response entity Json String
	 */	
	public String deleteOfc(String ofcIpPort);

	/**
	 * Update Ofc business Logic
	 * @param ofcIpPort String
	 * @param updateOfcInfoJson String
	 * @return response entity Json String
	 */	
	public String updateOfc(String ofcIpPort, String updateOfcInfoJson);

	/**
	 * Read ofces list from db.
	 * @return
	 */
	public String readOfcList();

	/**
	 * Read ofc from db.
	 * @param ofcIpPort
	 * @return
	 */
	public String readOfc(String ofcIpPort);
}
