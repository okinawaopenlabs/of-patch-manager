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

package org.okinawaopenlabs.ofpm.validate.topology.physical;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.device.PortData;
import org.okinawaopenlabs.ofpm.json.topology.physical.AddNetworkId;
import org.okinawaopenlabs.ofpm.json.topology.physical.PhysicalLink;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;

public class AddNetworkIdJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(AddNetworkIdJsonInValidate.class);

	public void checkValidation(AddNetworkId inParam) throws ValidateException {
		final String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(connectPhysicalLink=%s) - start", fname, inParam));
		}

		if (BaseValidate.checkNull(inParam)) {
			throw new ValidateException(String.format(IS_NULL, "Input parameter"));
		}
						
		if(inParam.GetStart()<0 || 4096 < inParam.GetStart())
		{
			throw new ValidateException(String.format(INVALID_PARAMETER, "NetworkID Start"));				

		}
		if(inParam.GetEnd()<0 || 4096 < inParam.GetEnd() || inParam.GetStart() > inParam.GetEnd())
		{
			throw new ValidateException(String.format(INVALID_PARAMETER, "NetworkID End"));				

		}

		if(!inParam.GetType().toString().equals("OIX") && !inParam.GetType().toString().equals("ToT"))
		{
			throw new ValidateException(String.format(INVALID_PARAMETER, "NetworkType"));							
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
