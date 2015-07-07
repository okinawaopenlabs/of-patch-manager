package org.okinawaopenlabs.ofpm.validate.device;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.device.DeviceInfoUpdateJsonIn;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;

public class DeviceInfoUpdateJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(DeviceInfoUpdateJsonInValidate.class);

	public void checkValidation(String deviceName, DeviceInfoUpdateJsonIn newDeviceInfo) throws ValidateException {
		String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newDeviceInfo=%s) - start", fname, newDeviceInfo));
		}

		if (StringUtils.isBlank(deviceName)) {
			throw new ValidateException(String.format(IS_BLANK, "Target deviceName"));
		}

		if (BaseValidate.checkNull(newDeviceInfo)) {
			throw new ValidateException(String.format(IS_BLANK, "parameter"));
		}

		String datapathId = newDeviceInfo.getDatapathId();
		if (!StringUtils.isBlank(datapathId)) {
			if (!datapathId.matches(REGEX_DATAPATH_ID)) {
				throw new ValidateException(String.format(INVALID_PARAMETER, "datapathId:" + datapathId));
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
