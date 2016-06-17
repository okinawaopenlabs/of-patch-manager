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

package org.okinawaopenlabs.ofpm.validate.ofc;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.ofc.InitFlowIn;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;


public class InitFlowInValidate extends BaseValidate {
	private Logger logger = Logger.getLogger(InitFlowInValidate.class);

	public void checkValidation(InitFlowIn data) throws ValidateException {
		final String fname = "checkValidation";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(data=%s) - start", fname, data));
		}
		if (BaseValidate.checkNull(data)) {
			throw new ValidateException(String.format(IS_NULL, "Input params"));
		}
		if (StringUtils.isBlank(data.getDatapathId())) {
			throw new ValidateException(String.format(IS_BLANK, "datapathId"));
		}
		if (!data.getDatapathId().matches(REGEX_DATAPATH_ID)) {
			throw new ValidateException(String.format(INVALID_PARAMETER, "datapathId"));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

}
