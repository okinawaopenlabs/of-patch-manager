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

package org.okinawaopenlabs.ofpm.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.IllegalFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import static org.okinawaopenlabs.constants.OrientDBDefinition.*;
import org.okinawaopenlabs.ofpm.json.device.PortData;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopology.OfpConDeviceInfo;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopology.OfpConPortInfo;

public class OFPMUtils {
	/**
	 * Check port contains into nodes.
	 * @param nodes
	 * @param deviceName port device name.
	 * @param portName port name. If this param is null, check only device name.
	 * @return
	 */
	public static boolean nodesContainsPort(Collection<OfpConDeviceInfo> nodes, String deviceName, String portName) {
		for (OfpConDeviceInfo device : nodes) {
			if (device.getDeviceName().equals(deviceName)) {
				if (StringUtils.isBlank(portName)) {
					return true;
				}
				for (OfpConPortInfo ofpConPort : device.getPorts()) {
					if (ofpConPort.getPortName().equals(portName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * transfer macAddress→long
	 * @param mac String ex.AA:BB:CC:DD:EE:FF
	 * @return long transfered
	 * @throws NullPointerException mac is null
	 * @throws NumberFormatException if the String does not contain a parsable long
	 */
	public static long macAddressToLong(String mac) throws NullPointerException, NumberFormatException {
		//String macTmp = StringUtils.join(mac.split(":"));
		String hexMac = mac.replace(":", "");
		long longMac = Long.decode("0x" + hexMac);
		if (longMac < MIN_MACADDRESS_VALUE || MAX_MACADDRESS_VALUE < longMac) {
			String errMsg = String.format(PARSE_ERROR, mac);
			throw new NumberFormatException(errMsg);
		}
		return longMac;
	}

	/**
	 * transfer macAddress→long
	 * @param longMac long
	 * @return macAddress transfered
	 * @throws IllegalFormatException If a format string contains an illegal syntax
	 * @throws NullPointerException longMac is null
	 * @throws NumberFormatException if the String does not contain a parsable long
	 */
	public static String longToMacAddress(long longMac) throws IllegalFormatException, NullPointerException, NumberFormatException {
		if (longMac < MIN_MACADDRESS_VALUE || MAX_MACADDRESS_VALUE < longMac) {
			String errMsg = String.format(PARSE_ERROR, longMac);
			throw new NumberFormatException(errMsg);
		}
		String hex = "000000000000" + Long.toHexString(longMac);
		StringBuilder hexBuilder = new StringBuilder(hex.substring(hex.length()-12));

		for (int i=2; i < 16; i=i+3) {
			hexBuilder.insert(i, ":");
		}
		return hexBuilder.toString();
	}

	/**
	 * Charactor is represented band-width converted to value of Mbps. For example, 1.2Gbps is  converted to 1229.
	 * Available range is from Mbps to Ybps.
	 * @param bandWidth [Number][kMGTPEZY]bps
	 * @return
	 */
	public static long bandWidthToBaseMbps(String bandWidth) {
		String reg = "([0-9]+)([MGTPEZY])bps";
		Pattern pat = Pattern.compile(reg);
		Matcher mat = pat.matcher(bandWidth);
		if (!mat.find()) {
			throw new NumberFormatException(String.format(PARSE_ERROR, bandWidth));
		}
		String numb = mat.group(1);
		String base = mat.group(2);

		long value = Long.parseLong(numb);
		if (base.equals("G")) {
			value *= 1024L;
		} else if (base.equals("T")) {
			value *= 1048576L;
		} else if (base.equals("P")) {
			value *= 1073741824L;
		} else if (base.equals("E")) {
			value *= 1099511627776L;
		} else if (base.equals("Z")) {
			value *= 1125899906842624L;
		} else if (base.equals("Y")) {
			value *= 1152921504606846976L;
		}
		return value;
	}

	/**
	 * Logging stack-trace when error happend.
	 * @param logger
	 * @param t
	 */
	public static void logErrorStackTrace(Logger logger, Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		logger.error(t);
		logger.error(sw.toString());
	}

	public static boolean isNodeTypeOfpSwitch(String nodeType) {
		return (StringUtils.equals(nodeType, NODE_TYPE_LEAF) || StringUtils.equals(nodeType, NODE_TYPE_SPINE));
	}

	/**
	 * Compare PortData. If one-sided portName is blank, compare only deviceName.
	 * @param port1
	 * @param port2
	 * @return
	 */
	public static boolean PortDataNonStrictEquals(PortData port1, PortData port2) {
		boolean ret = false;
		if (StringUtils.isBlank(port1.getPortName()) || StringUtils.isBlank(port2.getPortName())) {
			ret = StringUtils.equals(port1.getDeviceName(), port2.getDeviceName());
		} else {
			ret = port1.equals(port2);
		}
		return ret;
	}
}
