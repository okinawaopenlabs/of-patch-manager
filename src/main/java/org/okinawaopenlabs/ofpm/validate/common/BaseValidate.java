package org.okinawaopenlabs.ofpm.validate.common;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import org.okinawaopenlabs.ofpm.exception.ValidateException;

public abstract class BaseValidate {
	/**
	 * Determine if Object is null.
	 *
	 * @param value
	 * @return
	 * true : Not null. <br>
	 * false: null.
	 */
	protected static boolean checkNull(Object value) {
		return value == null;
	}

	/**
	 * Determine if length of string is less than or equal length.
	 *
	 * @param value
	 * @param length
	 * @return
	 * true : Not over. <br>
	 * false: Over.
	 */
	protected static boolean checkOverLength(String value, int length) {
		if (value.length() > length) {
			return true;
		}
		return false;
	}

	/**
	 * Determine if string contains multi-byte charactor.
	 *
	 * @param value
	 * @return
	 * true : Not contains multi-byte charactor. <br>
	 * false: Contains multi-byte charactor.
	 */
	protected static boolean checkHalfNum(String value) {
		if (value == null || !value.matches("^[0-9]+$")) {
			return false;
		}
		return true;
	}

	public static void checkArrayStringBlank(List<String> params) throws ValidateException {
		for (String param : params) {
			if (StringUtils.isBlank(param)) {
				throw new ValidateException(String.format(THERE_IS_BLANK, "parameter"));
			}
		}
	}
	public static void checkArrayOverlapped(List<String> params) throws ValidateException {
		int size = params.size();
		for (int dni = 0; dni < size; dni++) {
			for (int ci = dni + 1; ci < size; ci++) {
				if (params.get(dni).equals(params.get(ci))) {
					throw new ValidateException(String.format(THERE_ARE_OVERLAPPED, params.get(dni)));
				}
			}
		}
	}

	public static void checkStringBlank(String param) throws ValidateException {
		if (StringUtils.isBlank(param)) {
			throw new ValidateException(String.format(IS_BLANK, "parameter"));
		}
	}}
