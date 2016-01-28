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

import javax.ws.rs.core.MediaType;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;

import org.okinawaopenlabs.ofpm.exception.OFCClientException;
import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.device.DeviceInfoCreateJsonIn;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

public class DeviceInfoCreateJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(DeviceInfoCreateJsonInValidate.class);

	public void checkValidation(DeviceInfoCreateJsonIn deviceInfo) throws ValidateException {
		String fname = "checkValidateion";
		boolean match;
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceInfoJson=%s) - start", fname, (deviceInfo == null)? "null" : deviceInfo.toJson()));
		}

		if (BaseValidate.checkNull(deviceInfo)) {
			throw new ValidateException(String.format(IS_BLANK, "Input parameter"));
		}
				
		if (StringUtils.isBlank(deviceInfo.getDeviceName())) {
			throw new ValidateException(String.format(IS_BLANK, "deviceName"));
		}
		String deviceType = deviceInfo.getDeviceType();
		if (StringUtils.isBlank(deviceType)) {
			throw new ValidateException(String.format(IS_BLANK, "deviceType"));
		}
		if (! ArrayUtils.contains(ENABLE_DEVICE_TYPES, deviceType)) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "deviceType:" + deviceType));
		}
		String location = deviceInfo.getLocation();
		if (StringUtils.isBlank(location)) {
			throw new ValidateException(String.format(IS_BLANK, "location"));
		}
		
		String datapathId = "";
		try	{
			if(!deviceInfo.getDatapathId().isEmpty()){
				/*
				 * Check DatapathId
				 * REGEX_DATAPATH_ID = "0x[0-9a-fA-F]{1,16}"
				 */
				datapathId = deviceInfo.getDatapathId();
				match = Pattern.matches(REGEX_DATAPATH_ID, datapathId);
				if(match==false){
					throw new ValidateException(String.format(INVALID_PARAMETER, "datapathID"+datapathId));
				}
			}
		}
		catch(Exception e){
			datapathId = "";
		}

		String tenant = deviceInfo.getTenant();
		if (StringUtils.isBlank(tenant)) {
			throw new ValidateException(String.format(IS_BLANK, "TenantName"));
		}

		/* 
		 * Check DeviceType
		 * ENABLE_DEVICE_TYPES = {"Server", "Switch", "Leaf", "Spine", "Aggregate_Switch", "Sites_Switch"}
		 */
		if (! ArrayUtils.contains(ENABLE_DEVICE_TYPES, deviceType)){
			throw new ValidateException(String.format(INVALID_PARAMETER, "deviceType:" + deviceType));
		}

		/*
		 * length check(Device Name)
		 * DEVICE_NAME_MAX_LENGTH = 30
		 */
		if (deviceInfo.getDeviceName().length()>DEVICE_NAME_MAX_LENGTH) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "deviceName"));
		}

		/*
		 * 2 byte character check(Device Name)
		 */
		if (deviceInfo.getDeviceName().length() != deviceInfo.getDeviceName().getBytes().length) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "deviceName"));
		}
		
		/*
		 * length check(Location Name)
		 * DEVICE_LOCATION_MAX_LENGTH = 30
		 */
		if (deviceInfo.getLocation().length()>DEVICE_LOCATION_MAX_LENGTH) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "location"));
		}

		/*
		 * 2 byte character check(Location Name)
		 */
		if (deviceInfo.getLocation().length() != deviceInfo.getLocation().getBytes().length) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "location"));
		}
		
		/*
		 * length check(Tenant Name)
		 * DEVICE_TENANT_MAX_LENGTH = 50
		 */
		if (deviceInfo.getTenant().length()>DEVICE_TENANT_MAX_LENGTH) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "TenantName"));
		}

		/*
		 * 2 byte character check(Tenant Name)
		 */
		if (deviceInfo.getTenant().length() != deviceInfo.getTenant().getBytes().length) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "TenantName"));
		}

		/*
		 * Chehck containing datapathid.
		 * LEGACY_DEVICE_TYPES("Server", "Switch", "Aggregate_Switch", "Sites_Switch") don't need datapathid,
		 * OPEN_FLOW_DEVICE_TYPES("Leaf", "Spine") need datapathid.
		 */
		if (ArrayUtils.contains(LEGACY_DEVICE_TYPES, deviceType) && datapathId.length() != 0){
			throw new ValidateException(String.format(INVALID_PARAMETER, "datapathID"));
		}

		if (ArrayUtils.contains(OPEN_FLOW_DEVICE_TYPES, deviceType) && datapathId.length() != DEVICE_DATAPATHID_LENGTH){
			throw new ValidateException(String.format(INVALID_PARAMETER, "datapathID"));
		}
		
		String getofcip = "";
		try	{
			if(!deviceInfo.getOfcIp().isEmpty()){
				/*
				 * Check Ip address.
				 * REGEX_IPADDRESS = "[0-9,\\.,:]+"
				 */
				getofcip = deviceInfo.getOfcIp();
				String[] port = getofcip.split(":",0);
				match = Pattern.matches(REGEX_IPADDRESS, getofcip);
				if(match==true){
					String[] getip = getofcip.split("\\.",0);
					if(getip.length != 4)
					{
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

		}
		catch(Exception e){
			getofcip = "";
		}

		/*
		 * Chehck containing ofcip.
		 * LEGACY_DEVICE_TYPES("Server", "Switch", "Aggregate_Switch", "Sites_Switch") don't need ofcip,
		 * OPEN_FLOW_DEVICE_TYPES("Leaf", "Spine") need ofcip.
		 */
		if (ArrayUtils.contains(LEGACY_DEVICE_TYPES, deviceType) && !getofcip.equals("")){
			throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
		}

		if (ArrayUtils.contains(OPEN_FLOW_DEVICE_TYPES, deviceType) && getofcip.equals("")){
			throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
