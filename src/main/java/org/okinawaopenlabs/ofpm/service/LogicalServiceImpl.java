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

import org.okinawaopenlabs.ofpm.business.LogicalBusiness;
import org.okinawaopenlabs.ofpm.business.LogicalBusinessImpl;

@Component
public class LogicalServiceImpl implements LogicalService {
	private static final Logger logger = Logger.getLogger(LogicalServiceImpl.class);

	@Inject
	LogicalBusiness logiBiz;
	Injector injector;

	@Override
	public Response getLogicalTopology(String deviceNamesCSV, String tokenId) {
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
		String resLogiBiz = main.logiBiz.getLogicalTopology(deviceNamesCSV, tokenId);


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
	public Response updateLogicalTopology(String requestedTopologyJson, String tokenId) {
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
		String resLogiBiz = main.logiBiz.updateLogicalTopology(requestedTopologyJson, tokenId);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resLogiBiz));
		}
		if (logger.isInfoEnabled()) {
			time = System.currentTimeMillis() - time;
			logger.info(String.format("###     END    ### %s ### %s[ms] ###", fname, time));
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
