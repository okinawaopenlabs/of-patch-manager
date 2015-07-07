package org.okinawaopenlabs.ofpm.client;

import org.okinawaopenlabs.ofpm.exception.OFCClientException;
import org.okinawaopenlabs.ofpm.json.common.BaseResponse;

public interface OFCClient {
	public BaseResponse setFlows(String dpid, Integer inPort, String srcMac, String dstMac, Integer outPort, String modSrcMac, String modDstMac, Boolean packetIn, Boolean drop) throws OFCClientException;

	public BaseResponse deleteFlows(String dpid, Integer inPort, String srcMac, String dstMac, Integer outPort, String modSrcMac, String modDstMac, Boolean packetIn, Boolean drop) throws OFCClientException;

	public String getIp();
}
