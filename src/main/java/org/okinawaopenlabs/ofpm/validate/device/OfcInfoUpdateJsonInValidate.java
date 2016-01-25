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

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.MAX_IPADDRESS_VALUE;
import static org.okinawaopenlabs.constants.OfpmDefinition.MAX_PORT_VALUE;
import static org.okinawaopenlabs.constants.OfpmDefinition.MIN_PORT_VALUE;
import static org.okinawaopenlabs.constants.OfpmDefinition.REGEX_IPADDRESS;
import static org.okinawaopenlabs.constants.OfpmDefinition.REGEX_NUMBER;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.device.OfcInfoUpdateJsonIn;
import org.okinawaopenlabs.ofpm.json.device.PortInfoUpdateJsonIn;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;

public class OfcInfoUpdateJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(OfcInfoUpdateJsonInValidate.class);

	/**
	 * @param updatePortInfo
	 * @throws ValidateException
	 */
	public void checkValidation(String ofcIpPort, OfcInfoUpdateJsonIn updateOfcInfo) throws ValidateException {
		String fname = "checkValidateion";
		boolean match;
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updateOfcInfo=%s) - start", fname, updateOfcInfo));
		}

		String getofcip = "";
		Integer getofcport = 0;
		
		if(!updateOfcInfo.getIp().isEmpty()){
			getofcip = updateOfcInfo.getIp();
			match = Pattern.matches(REGEX_IPADDRESS, getofcip);
			if(match==true){
				String[] getip = getofcip.split("\\.",0);
				if(getip.length != 4){
					throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
				}
				for(int i=0;i<getip.length;i++){
					//check value (IPv4)
					if(Integer.parseInt(getip[i]) > MAX_IPADDRESS_VALUE){
						throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));
					}
				}
			}else if(!getofcip.equals("localhost")) {
				throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_ip"));				
			}
		}
		
		if(updateOfcInfo.getPort() != null){
			getofcport = updateOfcInfo.getPort();
			match = Pattern.matches(REGEX_NUMBER, getofcport.toString());
			if(match==false || getofcport < MIN_PORT_VALUE || MAX_PORT_VALUE < getofcport){
				throw new ValidateException(String.format(INVALID_PARAMETER, "OFC_port"));
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
