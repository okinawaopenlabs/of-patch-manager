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
import org.okinawaopenlabs.ofpm.business.PhysicalBusiness;
import org.okinawaopenlabs.ofpm.business.PhysicalBusinessImpl;

@Component
public class PhysicalServiceImpl implements PhysicalService {
	private static final Logger logger = Logger.getLogger(PhysicalServiceImpl.class);

	@Inject
	PhysicalBusiness physBiz;
	Injector injector;

	@Override
	public Response getPhysicalTopology() {
		final String fname = "getPhysicalTopology";
		long time = 0L;
		if (logger.isInfoEnabled()) {
			time = System.currentTimeMillis();
			logger.info(String.format("%s", fname));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - start", fname));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(PhysicalBusiness.class).to(PhysicalBusinessImpl.class);
			}
		});
		PhysicalServiceImpl main = this.injector.getInstance(PhysicalServiceImpl.class);
		String resPhysBiz = main.physBiz.getPhysicalTopology();

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resPhysBiz));
		}
		if (logger.isInfoEnabled()) {
			time = System.currentTimeMillis() - time;
			logger.info(String.format("%s %s[ms]", fname, time));
		}
		return Response.ok(resPhysBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response connectPhysicalLink(String physicalLinkJson, String tokenId) {
		String fname = "connectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(req=%s) - start", fname, physicalLinkJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(PhysicalBusiness.class).to(PhysicalBusinessImpl.class);
			}
		});
		PhysicalServiceImpl main = this.injector.getInstance(PhysicalServiceImpl.class);
		String resPhysBiz = main.physBiz.connectPhysicalLink(physicalLinkJson, tokenId);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resPhysBiz));
		}
		return Response.ok(resPhysBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response disconnectPhysicalLink(String physicalLinkJson, String tokenId) {
		String fname = "disconnectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(req=%s) - start", fname, physicalLinkJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(PhysicalBusiness.class).to(PhysicalBusinessImpl.class);
			}
		});
		PhysicalServiceImpl main = this.injector.getInstance(PhysicalServiceImpl.class);
		String resPhysBiz = main.physBiz.disconnectPhysicalLink(physicalLinkJson, tokenId);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resPhysBiz));
		}
		return Response.ok(resPhysBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
}
