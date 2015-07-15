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

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.device.DeviceInfoCreateJsonIn;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;

public class DeviceInfoCreateJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(DeviceInfoCreateJsonInValidate.class);

	public void checkValidation(DeviceInfoCreateJsonIn deviceInfo) throws ValidateException {
		String fname = "checkValidateion";
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
		String datapathId = deviceInfo.getDatapathId();
		if (StringUtils.isBlank(datapathId)) {
			if (datapathId.matches(REGEX_DATAPATH_ID)) {
				throw new ValidateException(String.format(INVALID_PARAMETER, "datapathId:" + datapathId));
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
