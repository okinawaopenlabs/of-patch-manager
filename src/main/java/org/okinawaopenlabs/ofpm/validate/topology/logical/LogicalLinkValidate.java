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

package org.okinawaopenlabs.ofpm.validate.topology.logical;

import java.util.List;

import org.apache.log4j.Logger;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.device.PortData;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalLink;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;

public class LogicalLinkValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(LogicalLinkValidate.class);

	public static void checkValidation(LogicalLink param) throws ValidateException {
		String fname = "checkValidation";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(LogicalLink=%s) - start", fname, param));
		}

		if (BaseValidate.checkNull(param)) {
			throw new ValidateException(String.format(IS_BLANK, "link"));
		}
		List<PortData> link = param.getLink();
		if (BaseValidate.checkNull(link)) {
			throw new ValidateException(String.format(IS_BLANK,  "link"));
		}
		if (link.size() != COLLECT_NUMBER_OF_DEVICE_NAMES_IN_LINK) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "Number of deviceName:LogicalLink"));
		}

		if (logger.isDebugEnabled()) {
    		logger.debug(String.format("checkValidation(ret=%s) - end "));
    	}
	}
}
