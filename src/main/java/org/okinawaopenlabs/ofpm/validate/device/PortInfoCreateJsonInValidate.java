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
import org.apache.log4j.Logger;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.device.PortInfoCreateJsonIn;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;

public class PortInfoCreateJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(PortInfoCreateJsonInValidate.class);

	public void checkValidation(String deviceName, PortInfoCreateJsonIn portInfoJson) throws ValidateException {
		String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portInfoJson=%s) - start", fname, portInfoJson));
		}

		if (StringUtils.isBlank(deviceName)) {
			throw new ValidateException(String.format(IS_BLANK, "deviceName"));
		}
		if (BaseValidate.checkNull(portInfoJson)) {
			throw new ValidateException(String.format(IS_BLANK, "Input parameter"));
		}
		if (StringUtils.isBlank(portInfoJson.getPortName())) {
			throw new ValidateException(String.format(IS_BLANK, "portName"));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

}
