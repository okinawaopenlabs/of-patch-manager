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

package org.okinawaopenlabs.ofpm.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import org.okinawaopenlabs.ofpm.business.DeviceBusiness;
import org.okinawaopenlabs.ofpm.business.DeviceBusinessImpl;

@Component
public class DeviceServiceImpl implements DeviceService {
	private static final Logger logger = Logger.getLogger(DeviceServiceImpl.class);

	@Inject
	DeviceBusiness deviceBiz;
	Injector injector;

	@Override
	public Response createDevice(String newDeviceInfoJson) {
		final String fname = "createDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceInfoJson=%s) - start", fname, newDeviceInfoJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.createDevice(newDeviceInfoJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response deleteDevice(String deviceName) {
		final String fname = "deleteDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s) - start", fname, deviceName));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.deleteDevice(deviceName);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response updateDevice(String deviceName, String updateDeviceInfoJson) {
		final String fname = "updateDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s, deviceInfoJson=%s) - start", fname, deviceName, updateDeviceInfoJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.updateDevice(deviceName, updateDeviceInfoJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response readDevice(String deviceName) {
		final String fname = "readDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s) - start", fname, deviceName));
		}
		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.readDevice(deviceName);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response readDeviceList() {
		final String fname = "readDeviceList";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - start", fname));
		}
		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.readDeviceList();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response createPort(String deviceName, String newPortInfoJson) {
		final String fname = "createPort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portInfoJson=%s) - start", fname, newPortInfoJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = this.injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.createPort(deviceName, newPortInfoJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response deletePort(String deviceName, String portName) {
		final String fname = "deletePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s, portName=%s) - start", fname, deviceName, portName));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = this.injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.deletePort(deviceName, portName);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response updatePort(String deviceName, String portName, String updatePortInfoJson) {
		final String fname = "updatePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updatePortInfoJson=%s) - start", fname, updatePortInfoJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = this.injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.updatePort(deviceName, portName, updatePortInfoJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response getConnectedPortInfo(String deviceName) {
		final String fname = "getConnectedPortInfo";
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("%s(deviceName=%s) - start ", fname, deviceName));
    	}

        this.injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            	bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
            }
        });

        DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
        String resDeviceBiz = main.deviceBiz.getConnectedPortInfo(deviceName);

        if (logger.isDebugEnabled()) {
    		logger.debug(String.format("%s(ret=%s) - end ", fname, resDeviceBiz));
    	}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response createOfc(String newOfcInfoJson) {
		final String fname = "createOfc";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceInfoJson=%s) - start", fname, newOfcInfoJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.createOfc(newOfcInfoJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response deleteOfc(String ofcIpPort) {
		final String fname = "deleteOfc";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ofcIpPort=%s) - start", fname, ofcIpPort));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.deleteOfc(ofcIpPort);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ofcIpPort));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();	}

	@Override
	public Response updateOfc(String ofcIpPort, String updateOfcInfoJson) {
		final String fname = "updateOfc";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ofcIpPort=%s, updateOfcInfoJson=%s) - start", fname, ofcIpPort, updateOfcInfoJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.updateOfc(ofcIpPort, updateOfcInfoJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();	}

	@Override
	public Response readOfcList() {
		final String fname = "readOfcList";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - start", fname));
		}
		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.readOfcList();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();	}

	@Override
	public Response readOfc(String ofcIpPort) {
		final String fname = "readOfc";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ofcIpPort=%s) - start", fname, ofcIpPort));
		}
		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.readOfc(ofcIpPort);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build(); }

}
