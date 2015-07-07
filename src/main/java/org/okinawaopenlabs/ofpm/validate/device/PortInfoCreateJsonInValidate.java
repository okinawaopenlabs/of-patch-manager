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
		if (StringUtils.isBlank(portInfoJson.getBand())) {
			throw new ValidateException(String.format(IS_BLANK, "band"));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

}
