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
	public Response connectPhysicalLink(String physicalLinkJson) {
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
		String resPhysBiz = main.physBiz.connectPhysicalLink(physicalLinkJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resPhysBiz));
		}
		return Response.ok(resPhysBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response disconnectPhysicalLink(String physicalLinkJson) {
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
		String resPhysBiz = main.physBiz.disconnectPhysicalLink(physicalLinkJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resPhysBiz));
		}
		return Response.ok(resPhysBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

}
