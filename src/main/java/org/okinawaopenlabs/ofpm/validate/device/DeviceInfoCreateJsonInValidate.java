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
			if(!deviceInfo.getDatapathId().isEmpty())
			{
				datapathId = deviceInfo.getDatapathId();
				match = Pattern.matches(REGEX_DATAPATH_ID, datapathId);
				if(match==false)
				{
					throw new ValidateException(String.format(INVALID_PARAMETER, "datapathID"+datapathId));
				}
			}
		}
		catch(Exception e)
		{
			datapathId = "";
		}

		String tenant = deviceInfo.getTenant();
		if (StringUtils.isBlank(tenant)) {
			throw new ValidateException(String.format(IS_BLANK, "TenantName"));
		}

		//Check DeviceType
		if (! ArrayUtils.contains(ENABLE_DEVICE_TYPES, deviceType))
		{
			throw new ValidateException(String.format(INVALID_PARAMETER, "deviceType:" + deviceType));
		}

		//length check(Device Name)
		if (deviceInfo.getDeviceName().length()>DEVICE_NAME_MAX_LENGTH) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "deviceName"));
		}

		//length check(Location Name)
		if (deviceInfo.getLocation().length()>DEVICE_LOCATION_MAX_LENGTH) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "location"));
		}

		//length check(Tenant Name)
		if (deviceInfo.getTenant().length()>DEVICE_TENANT_MAX_LENGTH) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "TenantName"));
		}

		//chack datapathid
		if (ArrayUtils.contains(LEGACY_DEVICE_TYPES, deviceType) && datapathId.length() != 0)
		{
			throw new ValidateException(String.format(INVALID_PARAMETER, "datapathID"));
		}

		if (ArrayUtils.contains(OPEN_FLOW_DEVICE_TYPES, deviceType) && datapathId.length() != DEVICE_DATAPATHID_LENGTH)
		{
			throw new ValidateException(String.format(INVALID_PARAMETER, "datapathID"));
		}

		
		String getofcip = "";
		try	{
			if(!deviceInfo.getOfcIp().isEmpty())
			{
				getofcip = deviceInfo.getOfcIp();
				String[] port = getofcip.split(":",0);
				match = Pattern.matches(REGEX_IPADDRESS, getofcip);
				if(match==true)
				{
					String[] getip = getofcip.split("\\.",0);
					if(getip.length != 4)
					{
						throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
					}

					for(int i=0;i<getip.length;i++)
					{
						if(i==getip.length-1)
						{
							port = getip[i].split(":",0);
							getip[i] = port[0];
						}

						//check value (IPv4)
						if(Integer.parseInt(getip[i]) > MAX_IPADDRESS_VALUE)
						{
							throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
						}
					}
				}
				else if(port[0].equals("localhost"))
				{
					System.out.println("localhost");
				}
				else
				{
					throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));					
				}
				
				match = Pattern.matches(REGEX_NUMBER, port[1]);

				if(match==false || Integer.parseInt(port[1])<MIN_PORT_VALUE || MAX_PORT_VALUE < Integer.parseInt(port[1]))
				{
					throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
				}
			}

		}
		catch(Exception e)
		{
			getofcip = "";
		}

		//chack include ofc ip
		if (ArrayUtils.contains(LEGACY_DEVICE_TYPES, deviceType) && !getofcip.equals(""))
		{
			throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
		}

		if (ArrayUtils.contains(OPEN_FLOW_DEVICE_TYPES, deviceType) && getofcip.equals(""))
		{
			throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
