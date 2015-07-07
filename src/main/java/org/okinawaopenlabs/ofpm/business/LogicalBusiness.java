package org.okinawaopenlabs.ofpm.business;

public interface LogicalBusiness {
	public String getLogicalTopology(String deviceNames);

	public String updateLogicalTopology(String requestedTopologyJson);

	/**
	 * request set flow to each OFC
	 * @param requestedData ex.datapathId, inPort, srcMac, dstMac
	 * @return result set flow(json)
	 */
	public String setFlow(String requestedData);

	/**
	 * Set all flow in OFP-DB to OFPS that is presented by datapathId.
	 * @param requestedData datapathId
	 * @return
	 */
	public String initFlow(String requestedData);
}
