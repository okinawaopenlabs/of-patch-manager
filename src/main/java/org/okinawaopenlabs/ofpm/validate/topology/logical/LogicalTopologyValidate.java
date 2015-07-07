package org.okinawaopenlabs.ofpm.validate.topology.logical;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.device.PortData;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalLink;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopology;
import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopology.OfpConDeviceInfo;
import org.okinawaopenlabs.ofpm.utils.OFPMUtils;
import org.okinawaopenlabs.ofpm.validate.common.BaseValidate;

public class LogicalTopologyValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(LogicalTopologyValidate.class);

	public void checkValidationRequestIn(LogicalTopology logicalTopology) throws ValidateException {
		String fname = "checkValidation";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(%s) - start", fname, logicalTopology));
		}

		if (BaseValidate.checkNull(logicalTopology)) {
			throw new ValidateException(String.format(IS_NULL, "LogicalTopology"));
		}

		List<OfpConDeviceInfo> nodes = logicalTopology.getNodes();
		List<LogicalLink> links = logicalTopology.getLinks();
		if (BaseValidate.checkNull(nodes)) {
			throw new ValidateException(String.format(IS_NULL, "nodes"));
		}
		if (BaseValidate.checkNull(links)) {
			throw new ValidateException(String.format(IS_NULL, "links"));
		}

		for (int ni = 0; ni < nodes.size(); ni++) {
			OfpConDeviceInfo device = nodes.get(ni);
			if (BaseValidate.checkNull(device)) {
				throw new ValidateException(String.format(IS_NULL, "nodes[" + ni + "]"));
			}
			if (StringUtils.isBlank(device.getDeviceName())) {
				throw new ValidateException(String.format(IS_NULL, "nodes[" + ni + "].deviceName"));
			}
		}

		String nowParamStr = null;
		for (int i = 0; i < links.size(); i++) {
			nowParamStr = "links[" + i + "]";
			LogicalLink link = links.get(i);
			List<PortData> ports = link.getLink();
			if (BaseValidate.checkNull(ports)) {
				throw new ValidateException(String.format(IS_NULL, nowParamStr + ".link"));
			}
			if (ports.size() != COLLECT_NUMBER_OF_DEVICE_NAMES_IN_LINK) {
				throw new ValidateException(String.format(INVALID_PARAMETER, nowParamStr + ".link"));
			}

			for (int pi = 0; pi < ports.size(); pi++) {
				PortData port = ports.get(pi);
				nowParamStr ="links[" + i + "].link[" + pi + "]";

				if (StringUtils.isBlank(port.getDeviceName())) {
					throw new ValidateException(String.format(IS_BLANK, nowParamStr + ".deviceName"));
				}
				if (!OFPMUtils.nodesContainsPort(nodes, port.getDeviceName(), null)) {
					throw new ValidateException(String.format(NOT_FOUND, nowParamStr + " in nodes"));
				}
			}
		}

		/* Check overlapped node */
		for (int ni = 0; ni < nodes.size(); ni++) {
			OfpConDeviceInfo node = nodes.get(ni);
			for (int tni = ni + 1; tni < nodes.size(); tni++) {
				OfpConDeviceInfo tnode = nodes.get(tni);
				if (StringUtils.equals(node.getDeviceName(), tnode.getDeviceName())) {
					throw new ValidateException(String.format(IS_OVERLAPPED, "node: deviceName=" + node.getDeviceName()));
				}
			}
		}

		for (int li = 0; li < links.size(); li++) {
			LogicalLink link = links.get(li);
			for (PortData port: link.getLink()) {
				for (int tli = li + 1; tli < links.size(); tli++) {
					LogicalLink tlink = links.get(tli);
					if (port.getPortName() == null) {
						continue;
					}
					for (PortData tport: tlink.getLink()) {
						if (!StringUtils.equals(port.getDeviceName(), tport.getDeviceName())) {
							continue;
						}
						if (!StringUtils.equals(port.getPortName(), tport.getPortName())) {
							continue;
						}
						throw new ValidateException(String.format(IS_OVERLAPPED, "links-port: deviceName=" + port.getDeviceName() + ", portName=" + port.getPortName()));
					}
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

}