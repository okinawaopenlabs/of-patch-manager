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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestBody;

@Path("/device_mng")
public interface DeviceService {

	// device service
	/**
	 * Create Device
	 * @param newDeviceInfoJson String
	 * @return Http Response
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createDevice(@RequestBody String newDeviceInfoJson);

	/**
	 * Delete Device
	 * @param deviceName String
	 * @return Http Response
	 */
	@DELETE
	@Path("/{deviceName}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteDevice(@PathParam("deviceName") String deviceName);

	/**
	 * Update Device
	 * @param deviceName String
	 * @param updateDeviceInfoJson String
	 * @return Http Response
	 */
	@PUT
	@Path("/{deviceName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateDevice(@PathParam("deviceName") String deviceName, @RequestBody String updateDeviceInfoJson);

	/**
	 * Read Device
	 * @param deviceName
	 * @return
	 */
	@GET
	@Path("/{deviceName}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response readDevice(@PathParam("deviceName") String deviceName);

	/**
	 * Read Devices list.
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response readDeviceList();

	// port service
	/**
	 * Create Port
	 * @param deviceName String
	 * @param newPortInfoJson String
	 * @return Http Response
	 */
	@POST
	@Path("/port/{deviceName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createPort(@PathParam("deviceName") String deviceName, @RequestBody String newPortInfoJson);

	/**
	 * Read ports list.
	 * @return
	 */
	@GET
	@Path("/port/{deviceName}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response readPortList(@PathParam("deviceName") String deviceName);
	
	/**
	 * Delete Port
	 * @param deviceName String
	 * @param portName String
	 * @return Http Response
	 */
	@DELETE
	@Path("/port/{deviceName}/{portName}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deletePort(@PathParam("deviceName") String deviceName, @PathParam("portName") String portName);

	/**
	 * Update Port
	 * @param deviceName String
	 * @param portName String
	 * @param updatePortInfoJson String
	 * @return Http Response
	 */
	@PUT
	@Path("/port/{deviceName}/{portName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updatePort(@PathParam("deviceName") String deviceName, @PathParam("portName") String portName, @RequestBody String updatePortInfoJson);

	// ofc service
	/**
	 * Create ofc
     * @param newOfcInfoJson String
	 * @return Http Response
	 */
	@POST
	@Path("/ofc")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createOfc(@RequestBody String newOfcInfoJson);

	/**
	 * Delete ofc
	 * @param ofcIpPort String
	 * @return Http Response
	 */
	@DELETE
	@Path("/ofc/{ofcIpPort}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteOfc(@PathParam("ofcIpPort") String ofcIpPort);
	
	/**
	 * Update ofc
	 * @param ofcIpPort String
	 * @param updateOfcInfoJson String
	 * @return Http Response
	 */
	@PUT
	@Path("/ofc/{ofcIpPort}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateOfc(@PathParam("ofcIpPort") String ofcIpPort, @RequestBody String updateOfcInfoJson);

	/**
	 * Read ofc List
	 * @return 
	 */
	@GET
	@Path("/ofc")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response readOfcList();
	
	/**
	 * Read ofc
	 * @param ofcIpPort
	 * @return Http Response
	 */
	@GET
	@Path("/ofc/{ofcIpPort}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response readOfc(@PathParam("ofcIpPort") String ofcIpPort);

	// other service
	/**
	 * get Port which connected device
	 * @param deviceName String
	 * @return Http Response
	 */
	@GET
	@Path("/connectedPort/{deviceName}")
	@Produces({ MediaType.APPLICATION_JSON })
	Response getConnectedPortInfo(@PathParam("deviceName") String deviceName);

}
