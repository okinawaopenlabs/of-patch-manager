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
}
