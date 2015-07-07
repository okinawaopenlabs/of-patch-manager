package org.okinawaopenlabs.ofpm.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import org.okinawaopenlabs.ofpm.business.LogicalBusiness;
import org.okinawaopenlabs.ofpm.business.LogicalBusinessImpl;

@Component
public class LogicalServiceImpl implements LogicalService {
	private static final Logger logger = Logger.getLogger(LogicalServiceImpl.class);

	@Inject
	LogicalBusiness logiBiz;
	Injector injector;

	@Override
	public Response getLogicalTopology(String deviceNamesCSV) {
		final String fname = "getLogicalTopology";
		long time = 0L;
		if (logger.isInfoEnabled()) {
			time = System.currentTimeMillis();
			logger.info(String.format("###  REQUESTED ### %s ###", fname));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceNamesCSV=%s) - start", fname, deviceNamesCSV));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(LogicalBusiness.class).to(LogicalBusinessImpl.class);
			}
		});
		LogicalServiceImpl main = this.injector.getInstance(LogicalServiceImpl.class);
		String resLogiBiz = main.logiBiz.getLogicalTopology(deviceNamesCSV);


		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resLogiBiz));
		}
		if (logger.isInfoEnabled()) {
			time = System.currentTimeMillis() - time;
			logger.info(String.format("###     END    ### %s ### %s[ms] ###", fname, time));
		}
		return Response.ok(resLogiBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response updateLogicalTopology(String requestedTopologyJson) {
		final String fname = "updateLogicalTopology";
		long time = 0L;
		if (logger.isInfoEnabled()) {
			time = System.currentTimeMillis();
			logger.info(String.format("###  REQUESTED ### %s ###", fname));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedTopologyJson=%s) - start", fname, requestedTopologyJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(LogicalBusiness.class).to(LogicalBusinessImpl.class);
			}
		});
		LogicalServiceImpl main = this.injector.getInstance(LogicalServiceImpl.class);
		String resLogiBiz = main.logiBiz.updateLogicalTopology(requestedTopologyJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resLogiBiz));
		}
		if (logger.isInfoEnabled()) {
			time = System.currentTimeMillis() - time;
			logger.info(String.format("###     END    ### %s ### %s[ms] ###", fname, time));
		}
		return Response.ok(resLogiBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response setFlow(String requestedData) {
		final String fname = "setFlow";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedData=%s) - start", fname, requestedData));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(LogicalBusiness.class).to(LogicalBusinessImpl.class);
			}
		});
		LogicalServiceImpl main = this.injector.getInstance(LogicalServiceImpl.class);
		String resLogiBiz = main.logiBiz.setFlow(requestedData);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resLogiBiz));
		}
		return Response.ok(resLogiBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	public Response initFlow(String requestedData) {
		final String fname = "initFlow";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedData=%s) - start", fname, requestedData));
		}
		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(LogicalBusiness.class).to(LogicalBusinessImpl.class);
			}
		});
		LogicalServiceImpl main = this.injector.getInstance(LogicalServiceImpl.class);
		String resLogiBiz = main.logiBiz.initFlow(requestedData);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resLogiBiz));
		}
		return Response.ok(resLogiBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
}
