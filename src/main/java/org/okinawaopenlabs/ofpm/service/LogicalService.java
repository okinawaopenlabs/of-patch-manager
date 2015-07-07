package org.okinawaopenlabs.ofpm.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestBody;

@Path("/logical_topology")
public interface LogicalService {
	/**
	 * Get LogicalTopology
	 * @param deviceNamesCSV String list of deviceName split comma
	 * @return Http Response
	 */
	@GET
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getLogicalTopology(@QueryParam("deviceNames") String deviceNamesCSV);

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateLogicalTopology(@RequestBody String requestedTopologyJson);

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/setFlow")
	public Response setFlow(@RequestBody String requestedData);

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/initFlow")
	public Response initFlow(@RequestBody String requestedData);
}
