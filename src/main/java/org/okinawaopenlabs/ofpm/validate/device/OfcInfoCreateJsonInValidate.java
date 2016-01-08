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
import org.okinawaopenlabs.ofpm.json.device.DeviceInfoCreateJsonIn;
import org.okinawaopenlabs.ofpm.json.device.OfcInfoCreateJsonIn;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;

public class OfcInfoCreateJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(OfcInfoCreateJsonInValidate.class);

	public void checkValidation(OfcInfoCreateJsonIn ofcInfo) throws ValidateException {
		String fname = "checkValidateion";
		boolean match;
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ofcInfoJson=%s) - start", fname, (ofcInfo == null)? "null" : ofcInfo.toJson()));
		}

		if (BaseValidate.checkNull(ofcInfo)) {
			throw new ValidateException(String.format(IS_BLANK, "Input parameter"));
		}
		if (StringUtils.isBlank(ofcInfo.getIp())) {
			throw new ValidateException(String.format(IS_BLANK, "ofcIp"));
		}

		if (ofcInfo.getPort() == null) {
			throw new ValidateException(String.format(IS_BLANK, "ofcPort"));
		}

		String getofcip = "";
		Integer getofcport = 0;
		
		if(!ofcInfo.getIp().isEmpty()){
			getofcip = ofcInfo.getIp();
			getofcport = ofcInfo.getPort();
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
			}
				
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
