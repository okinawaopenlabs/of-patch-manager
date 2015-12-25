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
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updateOfcInfo=%s) - start", fname, updateOfcInfo));
		}

		if (StringUtils.isBlank(ofcIpPort)) {
			throw new ValidateException(String.format(IS_BLANK, "ofcIpPort"));
		}

		if (BaseValidate.checkNull(updateOfcInfo)) {
			throw new ValidateException(String.format(IS_BLANK, "parameter"));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
