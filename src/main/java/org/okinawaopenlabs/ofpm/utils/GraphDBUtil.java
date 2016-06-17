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

import java.util.List;

import org.okinawaopenlabs.ofpm.json.common.GraphDevicePort;
import org.okinawaopenlabs.ofpm.json.device.GenericsLink;

public class GraphDBUtil {
	public static GraphDevicePort searchNeighborPort(String deviceName, String portName, List<GenericsLink<GraphDevicePort>> links) {
		return searchNeighborPortExec(deviceName, portName, 0, links, true);
	}

	public static GraphDevicePort searchNeighborPort(String deviceName, Integer portNumber, List<GenericsLink<GraphDevicePort>> links) {
		return searchNeighborPortExec(deviceName, "", portNumber, links, false);
	}

	private static GraphDevicePort searchNeighborPortExec(String deviceName, String portName, Integer portNumber, List<GenericsLink<GraphDevicePort>> links, boolean isPortName) {
		for (GenericsLink<GraphDevicePort> link : links) {
			List<GraphDevicePort> ports = link.getLink();
			for (int li = 0; li < ports.size(); li++) {
				GraphDevicePort port = ports.get(li);
				boolean findFlag = false;
				if (isPortName) {
					if (port.getDeviceName().equals(deviceName) && port.getPortName().equals(portName)) {
						findFlag = true;
					}
				} else {
					if (port.getDeviceName().equals(deviceName) && (port.getPortNumber() == portNumber)) {
						findFlag = true;
					}
				}
				if (findFlag) {
					int ri = li ^ 0x00000001;
					return ports.get(ri);
				}
			}
		}
		return null;
	}

}
