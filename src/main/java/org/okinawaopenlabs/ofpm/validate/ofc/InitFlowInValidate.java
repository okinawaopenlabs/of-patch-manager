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
