package org.okinawaopenlabs.ofpm.business;

public interface PhysicalBusiness {

	public String connectPhysicalLink(String physicalLinkJson);

	public String disconnectPhysicalLink(String physicalLinkJson);
}
