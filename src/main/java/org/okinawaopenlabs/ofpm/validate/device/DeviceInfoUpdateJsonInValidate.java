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

package org.okinawaopenlabs.ofpm.validate.device;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.regex.Pattern;


import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.device.DeviceInfoUpdateJsonIn;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;

public class DeviceInfoUpdateJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(DeviceInfoUpdateJsonInValidate.class);

	public void checkValidation(String deviceName, DeviceInfoUpdateJsonIn newDeviceInfo) throws ValidateException {
		String fname = "checkValidateion";
		boolean match;
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newDeviceInfo=%s) - start", fname, newDeviceInfo));
		}

		/* 
		 * Check DeviceType
		 * ENABLE_DEVICE_TYPES = {"Server", "Switch", "Leaf", "Spine", "Aggregate_Switch", "Sites_Switch"}
		 */
		String deviceType = "";
		try{
			deviceType = newDeviceInfo.getDeviceType();
		}
		catch(Exception e)
		{
			throw new ValidateException(String.format(IS_BLANK, "deviceType"));
		}

		if (deviceType.isEmpty()) {
			throw new ValidateException(String.format(IS_BLANK, "deviceType"));
		}
		if (! ArrayUtils.contains(ENABLE_DEVICE_TYPES, deviceType))
		{
			throw new ValidateException(String.format(INVALID_PARAMETER, "deviceType:" + deviceType));
		}

		/*
		 * Check Location
		 * DEVICE_LOCATION_MAX_LENGTH = 30
		 */
		String location =  "";
		try{
			location = newDeviceInfo.getLocation();
		}
		catch(Exception e)
		{
			location =  "";
		}
		if (location.length()>DEVICE_LOCATION_MAX_LENGTH) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "location"));
		}

		/*
		 * Chehck containing datapathid.
		 * LEGACY_DEVICE_TYPES("Server", "Switch", "Aggregate_Switch", "Sites_Switch") don't need datapathid,
		 * OPEN_FLOW_DEVICE_TYPES("Leaf", "Spine") need datapathid.
		 */
		String datapath = "";
		datapath = newDeviceInfo.getDatapathId();

		if(datapath == null){
			datapath = "";
		}

		if(!datapath.isEmpty()){
			if (ArrayUtils.contains(OPEN_FLOW_DEVICE_TYPES, deviceType) && datapath.length() != DEVICE_DATAPATHID_LENGTH){
				throw new ValidateException(String.format(INVALID_PARAMETER, "datapathID"));
			}
		}

		if (ArrayUtils.contains(LEGACY_DEVICE_TYPES, deviceType) && datapath.length() != 0){
			throw new ValidateException(String.format(INVALID_PARAMETER, "datapathID"));
		}

		/*
		 * Check tennant name
		 * DEVICE_TENANT_MAX_LENGTH = 50
		 */
		String tenant = "";
		try{
			tenant = newDeviceInfo.getTenant();
		}
		catch(Exception e){
			tenant = "";
		}
		if (tenant.length()>DEVICE_TENANT_MAX_LENGTH) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "TenantName"));
		}

		/*
		 * length check(Device Name)
		 * DEVICE_NAME_MAX_LENGTH = 30
		 */
		String deviceNames = "";
		try{
			deviceNames = newDeviceInfo.getDeviceName();
			System.out.println(deviceNames.length());
		}
		catch(Exception e){
			deviceNames = "";
		}

		if(deviceNames.length()>DEVICE_NAME_MAX_LENGTH) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "deviceName"));
		}

		String datapathId = "";
		datapathId = newDeviceInfo.getDatapathId();

		if(datapathId == null){
			datapathId = "";
		}

		if(!datapathId.isEmpty()){
			if (ArrayUtils.contains(OPEN_FLOW_DEVICE_TYPES, datapathId) && datapathId.length() != DEVICE_DATAPATHID_LENGTH){
				throw new ValidateException(String.format(INVALID_PARAMETER, "datapathID"));
			}
		}

		if (ArrayUtils.contains(LEGACY_DEVICE_TYPES, datapathId) && datapathId.length() != 0){
			throw new ValidateException(String.format(INVALID_PARAMETER, "datapathID"));
		}

		
		
		String getofcip = "";
		getofcip = newDeviceInfo.getOfcIp();
		if(getofcip == null){
			getofcip = "";
		}
		
		if(!getofcip.isEmpty()){
			/*
			 * Check Ip address.
			 * REGEX_IPADDRESS = "[0-9,\\.,:]+"
			 */
			getofcip = newDeviceInfo.getOfcIp();
			String[] port = getofcip.split(":",0);
			match = Pattern.matches(REGEX_IPADDRESS, getofcip);
			if(match==true){
				String[] getip = getofcip.split("\\.",0);
				if(getip.length != 4){
					throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
				}

				for(int i=0;i<getip.length;i++){
					if(i==getip.length-1){
						port = getip[i].split(":",0);
						getip[i] = port[0];
					}
					/*
					 * Check value (IPv4)
					 * MAX_IPADDRESS_VALUE = 255
					 */
					if(Integer.parseInt(getip[i]) > MAX_IPADDRESS_VALUE){
						throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
					}
				}
			}
			else if(port[0].equals("localhost")){
				System.out.println("localhost");
			}
			else{
				throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));					
			}
				
			/*
			 * Check port number
			 * REGEX_NUMBER = "[0-9]+",
			 * MIN_PORT_VALUE = 1024, MAX_PORT_VALUE = 65535
			 */
			match = Pattern.matches(REGEX_NUMBER, port[1]);
			if(match==false || Integer.parseInt(port[1])<MIN_PORT_VALUE || MAX_PORT_VALUE < Integer.parseInt(port[1])){
				throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
			}
		}

		/*
		 * Chehck containing ofcip.
		 * LEGACY_DEVICE_TYPES("Server", "Switch", "Aggregate_Switch", "Sites_Switch") don't need ofcip,
		 * OPEN_FLOW_DEVICE_TYPES("Leaf", "Spine") need ofcip.
		 */
		if (ArrayUtils.contains(LEGACY_DEVICE_TYPES, deviceType) && !getofcip.equals("")){
			throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
