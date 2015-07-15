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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.device.PortData;
import org.okinawaopenlabs.ofpm.json.topology.physical.ConnectPhysicalLinksJsonIn;
import org.okinawaopenlabs.ofpm.json.topology.physical.PhysicalLink;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;

public class ConnectPhysicalLinksJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(ConnectPhysicalLinksJsonInValidate.class);

	public void checkValidation(ConnectPhysicalLinksJsonIn connectPhysicalLink) throws ValidateException {
		final String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(connectPhysicalLink=%s) - start", fname, connectPhysicalLink));
		}

		if (BaseValidate.checkNull(connectPhysicalLink)) {
			throw new ValidateException(String.format(IS_NULL, "Input parameter"));
		}

		List<PhysicalLink> links = connectPhysicalLink.getLinks();
		if (BaseValidate.checkNull(links)) {
			throw new ValidateException(String.format(IS_NULL, "links"));
		}
		for (int i = 0; i < links.size(); i++) {
			String msgLinks = "links[" + i + "]";
			PhysicalLink physicalLink = links.get(i);
			if (BaseValidate.checkNull(physicalLink)) {
				throw new ValidateException(String.format(IS_NULL, msgLinks));
			}

			List<PortData> ports = physicalLink.getLink();
			if (BaseValidate.checkNull(ports)) {
				throw new ValidateException(String.format(IS_NULL, msgLinks + ".link"));
			}
			if (ports.size() != COLLECT_NUMBER_OF_DEVICE_NAMES_IN_LINK) {
				throw new ValidateException(String.format(INVALID_PARAMETER, "Length of " + msgLinks + ".link"));
			}
			for (int pi = 0; pi < ports.size(); pi++) {
				String msgLink = msgLinks + ".link[" + pi + "]";
				PortData port = ports.get(pi);
				if (BaseValidate.checkNull(port)) {
					throw new ValidateException(String.format(IS_NULL,  msgLink));
				}
				if (StringUtils.isBlank(port.getDeviceName())) {
					throw new ValidateException(String.format(IS_BLANK, msgLink + ".deviceName:" + port.getDeviceName()));
				}
				if (StringUtils.isBlank(port.getPortName())) {
					throw new ValidateException(String.format(IS_BLANK, msgLink + ".portName:" + port.getPortName()));
				}
			}
			if (ports.get(0).equals(ports.get(1))) {
				PortData port = ports.get(0);
				throw new ValidateException(String.format(THERE_ARE_OVERLAPPED, port.getDeviceName() + '.' + port.getPortName() + " in " + msgLinks));
			};
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
