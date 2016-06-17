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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestBody;

@Path("/physical_topology")
public interface PhysicalService {

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getPhysicalTopology();

	@POST
	@Path("/connect")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response connectPhysicalLink(@RequestBody String physicalLinkJson);

	@POST
	@Path("/disconnect")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response disconnectPhysicalLink(@RequestBody String physicalLinkJson);

	@POST
	@Path("/addnetwork")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response addnetworkid(@RequestBody String physicalLinkJson);

	@POST
	@Path("/delnetwork")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response delnetworkid(@RequestBody String physicalLinkJson);

	@GET
	@Path("/getnetwork")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getnetworkid();

}
