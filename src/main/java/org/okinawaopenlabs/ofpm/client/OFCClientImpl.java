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

package org.okinawaopenlabs.ofpm.client;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.okinawaopenlabs.constants.OfcClientDefinition;
import org.okinawaopenlabs.ofpm.exception.OFCClientException;
import org.okinawaopenlabs.ofpm.json.common.BaseResponse;
import org.okinawaopenlabs.ofpm.json.ofc.SetFlowToOFC;
import org.okinawaopenlabs.ofpm.json.ofc.SetFlowToOFC.Action;
import org.okinawaopenlabs.ofpm.json.ofc.SetFlowToOFC.Match;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class OFCClientImpl implements OFCClient {
	private static final Logger logger = Logger.getLogger(OFCClientImpl.class);

	/* (non-Javadoc)
	 * @see org.okinawaopenlabs.ofpm.client.OFCClient#setFlows(Map<String, Object>)
	 */
	@Override
	public BaseResponse addFlows(String ofIp, SetFlowToOFC requestData) throws OFCClientException {
		final String fname = "setFlows";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ofIp=%s, requestData=%s) - start", fname, ofIp, requestData));
		}

		BaseResponse ret = new BaseResponse();
		ret.setStatus(STATUS_INTERNAL_ERROR);
		try {

			String url = "http://" + ofIp + OFC_ADD_FLOWENTRY_PATH;
			ret = postRequest(url, requestData);

		} catch (UniformInterfaceException uie) {
			logger.error(uie.getMessage());
			throw new OFCClientException(String.format(CONNECTION_FAIL, "OFC-" + ofIp));
		} catch (ClientHandlerException che) {
			logger.error(che);
			throw new OFCClientException(String.format(CONNECTION_FAIL, "OFC-" + ofIp));
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new OFCClientException(UNEXPECTED_ERROR);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret.toJson()));
		}
		return ret;

	}

	/* (non-Javadoc)
	 * @see org.okinawaopenlabs.ofpm.client.OFCClient#deleteFlows(Map<String, Object> flow)
	 */
	@Override
	public BaseResponse deleteFlows(String ofIp, SetFlowToOFC requestData) throws OFCClientException {
		final String fname = "deleteFlows";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ofIp=%s, requestData=%s) - start", fname, ofIp, requestData));
		}

		BaseResponse ret = new BaseResponse();
		ret.setStatus(STATUS_INTERNAL_ERROR);
		try {

			String url = "http://" + ofIp + OFC_DELETE_FLOWENTRY_PATH;
			ret = postRequest(url, requestData);

		} catch (UniformInterfaceException uie) {
			logger.error(uie.getMessage());
			throw new OFCClientException(String.format(CONNECTION_FAIL, "OFC-" + ofIp));
		} catch (ClientHandlerException che) {
			logger.error(che);
			throw new OFCClientException(String.format(CONNECTION_FAIL, "OFC-" + ofIp));
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new OFCClientException(UNEXPECTED_ERROR);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret.toJson()));
		}
		return ret;
	}

	public BaseResponse postRequest(String url, SetFlowToOFC requestData) throws OFCClientException {
		final String fname = "setFlows";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(url=%s, requestData=%s) - start", fname, url, requestData));
		}

		BaseResponse ret = new BaseResponse();
		try {
			WebResource resource = Client.create().resource(url);
			Builder resBuilder = resource.entity(requestData.toJson());
			resBuilder = resBuilder.accept(MediaType.APPLICATION_JSON);
			resBuilder = resBuilder.type(MediaType.APPLICATION_JSON);
//			ClientResponse res = resBuilder.post(ClientResponse.class);
//			if (res.getStatus() != STATUS_CREATED) {
//				logger.error(res.getEntity(String.class));
//				throw new OFCClientException(String.format(WRONG_RESPONSE, "OFC-" + url));
//			}
//			ret = BaseResponse.fromJson(res.getEntity(String.class));
			ret.setStatus(STATUS_SUCCESS);
		} catch (UniformInterfaceException uie) {
			logger.error(uie.getMessage());
			throw new OFCClientException(String.format(CONNECTION_FAIL, "OFC-" + url));
		} catch (ClientHandlerException che) {
			logger.error(che);
			throw new OFCClientException(String.format(CONNECTION_FAIL, "OFC-" + url));
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new OFCClientException(UNEXPECTED_ERROR);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret.toJson()));
		}
		return ret;
	}

	public static SetFlowToOFC createRequestData(Long dpid, Long priority, Match match, List<Action> actions) {
		final String fname = "createRequestData";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(dpid=%s, priority=%s, match=%s, actions=%s) - start", fname, dpid, priority, match, actions));
		}

		SetFlowToOFC requestData = new SetFlowToOFC();
		requestData.setDpid(dpid);
		requestData.setPriority(priority);
		if (match != null) {
			requestData.setMatch(match);
		} else {
			requestData.setMatch(requestData.new Match());			
		}
		if (actions != null) {
			requestData.setActions(actions);
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestData=%s) - end", fname, requestData.toJson()));
		}
		return requestData;
	}

	public static SetFlowToOFC createMatchForInPort(SetFlowToOFC requestData, Long inPort) {
		final String fname = "createMatchForInPort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestData=%s, inPort=%s) - start", fname, requestData, inPort));
		}

		Match match = requestData.getMatch();
		match.setIn_port(inPort);
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, requestData.toJson()));
		}
		return requestData;
	}
	
	public static SetFlowToOFC createMatchForInPortDlVlan(SetFlowToOFC requestData, Long inPort, Long vlanId) {
		final String fname = "createMatchForInPortDlVlan";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestData=%s, inPort=%s, vlanId=%s) - start", fname, requestData, inPort, vlanId));
		}

		Match match = requestData.getMatch();
		match.setIn_port(inPort);
		match.setDl_vlan(vlanId);
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, requestData.toJson()));
		}
		return requestData;
	}

	public static SetFlowToOFC createMatchForDlVlan(SetFlowToOFC requestData, Long vlanId) {
		final String fname = "createMatchForDlVlan";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestData=%s, vlanId=%s) - start", fname, requestData, vlanId));
		}

		Match match = requestData.getMatch();
		match.setDl_vlan(vlanId);
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, requestData.toJson()));
		}
		return requestData;
	}

	public static SetFlowToOFC createActionsForPushVlan(SetFlowToOFC requestData, Long outPort, Long vlanId) {
		final String fname = "createActionsForPushVlan";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestData=%s, outPort=%s, vlanId=%s) - start", fname, requestData, outPort, vlanId));
		}

		List<SetFlowToOFC.Action> retActions = requestData.getActions();
		
		Action pushVlanAction = requestData.new Action();
		pushVlanAction.setType(OfcClientDefinition.ACTION_TYPE_PUSH_VLAN);
		pushVlanAction.setValue(OfcClientDefinition.ACTION_TYPE_PUSH_VLAN_ETH_TYPE);
		retActions.add(pushVlanAction);
		
		Action pushSetFieldAction = requestData.new Action();
		pushSetFieldAction.setType(OfcClientDefinition.ACTION_TYPE_SET_FIELD);
		pushSetFieldAction.setField(OfcClientDefinition.ACTION_TYPE_SET_FIELD_VLAN_VID);
		pushSetFieldAction.setValue(vlanId);
		retActions.add(pushSetFieldAction);
		
		Action pushOutputAction = requestData.new Action();
		pushOutputAction.setType(OfcClientDefinition.ACTION_TYPE_OUTPUT);
		pushOutputAction.setValue(outPort);
		retActions.add(pushOutputAction);
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, requestData.toJson()));
		}
		return requestData;
	}

	public static SetFlowToOFC createActionsForPopVlan(SetFlowToOFC requestData, Long outPort) {
		final String fname = "createActionsForPopVlan";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestData=%s, outPort=%s) - start", fname, requestData, outPort));
		}

		List<SetFlowToOFC.Action> retActions = requestData.getActions();
		
		Action popVlanAction = requestData.new Action();
		popVlanAction.setType(OfcClientDefinition.ACTION_TYPE_POP_VLAN);
		retActions.add(popVlanAction);

		Action pushOutputAction = requestData.new Action();
		pushOutputAction.setType(OfcClientDefinition.ACTION_TYPE_OUTPUT);
		pushOutputAction.setValue(outPort);
		retActions.add(pushOutputAction);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, requestData.toJson()));
		}
		return requestData;
	}

	public static SetFlowToOFC createActionsForOutputPort(SetFlowToOFC requestData, Long outPort) {
		final String fname = "createActionsForPopVlan";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestData=%s, outPort=%s) - start", fname, requestData, outPort));
		}

		List<SetFlowToOFC.Action> retActions = requestData.getActions();

		Action pushOutputAction = requestData.new Action();
		pushOutputAction.setType(OfcClientDefinition.ACTION_TYPE_OUTPUT);
		pushOutputAction.setValue(outPort);
		retActions.add(pushOutputAction);
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, requestData.toJson()));
		}
		return requestData;
	}

	private boolean isNullAndEmpty(String param) {
		if (!StringUtils.isBlank(param)) {
			return false;
		}
		return true;
	}

	private boolean isNull(Boolean param) {
		if (param != null) {
			return false;
		}
		return true;
	}

	private boolean isNull(Integer param) {
		if (param != null) {
			return false;
		}
		return true;
	}
}
