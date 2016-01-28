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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;

import java.util.regex.Pattern;

import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.device.PortInfoCreateJsonIn;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;

public class PortInfoCreateJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(PortInfoCreateJsonInValidate.class);

	public void checkValidation(String deviceName, PortInfoCreateJsonIn portInfoJson) throws ValidateException {
		String fname = "checkValidateion";
		boolean match;
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portInfoJson=%s) - start", fname, portInfoJson));
		}

		/*
		 * Check DeviceName
		 */
		try{
			if (StringUtils.isBlank(deviceName)) {
				throw new ValidateException(String.format(IS_BLANK, "deviceName"));
			}
		}
		catch(Exception e){
			throw new ValidateException(String.format(INVALID_PARAMETER, "deviceName"));
		}

		/*
		 * Check Input Parameter
		 */
		try{
			if (BaseValidate.checkNull(portInfoJson)) {
				throw new ValidateException(String.format(IS_BLANK, "Input parameter"));
			}
		}
		catch(Exception e){
			throw new ValidateException(String.format(INVALID_PARAMETER, "Input parameter"));
		}

		/*
		 * Check port name
		 * PORT_NAME_MAX_LENGTH = 30
		 */
		try{
			if (StringUtils.isBlank(portInfoJson.getPortName())) {
				throw new ValidateException(String.format(IS_BLANK, "portName"));
			}

			if (portInfoJson.getPortName().length() > PORT_NAME_MAX_LENGTH){
				throw new ValidateException(String.format(INVALID_PARAMETER, "portName"));				
			}
		}
		catch(Exception e){
			throw new ValidateException(String.format(INVALID_PARAMETER, "portName"));
		}

		/*
		 * 2 byte character check(Port Name)
		 */
		if (portInfoJson.getPortName().length() != portInfoJson.getPortName().getBytes().length) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "portName"));
		}		
		
		/*
		 * Check PortNumber
		 */
		try{
			System.out.println(portInfoJson.getPortNumber());
			if(portInfoJson.getPortNumber()==0){
				throw new ValidateException(String.format(IS_BLANK, "portNumber"));
			}
		}
		catch(Exception e)
		{
			throw new ValidateException(String.format(INVALID_PARAMETER, "portNumber"));
		}

		/*
		 * Check Band
		 */
		try{
			if (portInfoJson.getBand()==0) {
				throw new ValidateException(String.format(IS_BLANK, "band"));
			}
		}
		catch(Exception e)
		{
			throw new ValidateException(String.format(INVALID_PARAMETER, "band"));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

}
