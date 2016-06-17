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

import java.util.List;
import java.util.Map;

import org.okinawaopenlabs.ofpm.exception.OFCClientException;
import org.okinawaopenlabs.ofpm.json.common.BaseResponse;
import org.okinawaopenlabs.ofpm.json.ofc.SetFlowToOFC;
import org.okinawaopenlabs.ofpm.json.ofc.SetFlowToOFC.Action;
import org.okinawaopenlabs.ofpm.json.ofc.SetFlowToOFC.Match;

public interface OFCClient {
	public BaseResponse addFlows(String ofIp, SetFlowToOFC requestData) throws OFCClientException;

	public BaseResponse deleteFlows(String ofIp, SetFlowToOFC requestData) throws OFCClientException;
	
	public SetFlowToOFC createRequestData(Long dpid, Long priority, Match match, List<Action> actions);
	
	public SetFlowToOFC createMatchForInPort(SetFlowToOFC requestData, Long inPort);
	
	public SetFlowToOFC createMatchForInPortDlVlan(SetFlowToOFC requestData, Long inPort, Long vlanId);

	public SetFlowToOFC createMatchForDlVlan(SetFlowToOFC requestData, Long vlanId);
	
	public SetFlowToOFC createActionsForPushVlan(SetFlowToOFC requestData, Long outPort, Long vlanId);
	public SetFlowToOFC createActionsForPushOuter_tag(SetFlowToOFC requestData, Long outPort, Long vlanId, Long dfvlanId);
	
	public SetFlowToOFC createActionsForPopVlan(SetFlowToOFC requestData, Long outPort);
	public SetFlowToOFC createActionsForPopOuter_tag(SetFlowToOFC requestData, Long outPort);
	
	public SetFlowToOFC createActionsForOutputPort(SetFlowToOFC requestData, Long outPort);
}
