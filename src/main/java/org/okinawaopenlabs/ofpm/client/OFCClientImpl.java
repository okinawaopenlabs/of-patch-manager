package org.okinawaopenlabs.ofpm.client;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
	private WebResource resource;
	private String ip;

	public OFCClientImpl(String ip) {
		final String fname = "OFCClientImpl";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ip=%s) - start", fname, ip));
		}
		this.ip = ip;
		this.resource = Client.create().resource("http://" + ip + OFC_PATH);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

	/* (non-Javadoc)
	 * @see org.okinawaopenlabs.ofpm.client.OFCClient#setFlows(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, java.lang.Boolean)
	 */
	@Override
	public BaseResponse setFlows(String dpid, Integer inPort, String srcMac, String dstMac, Integer outPort, String modSrcMac, String modDstMac,
			Boolean packetIn, Boolean drop) throws OFCClientException {
		final String fname = "setFlows";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(dpid=%s,inPort=%s,srcMac=%s,dstMac=%s,outPort=%s,modSrcMac=%s,modDstMac=%s,packetIn=%s,drop=%s) - start",
					fname, dpid, inPort, srcMac, dstMac, outPort, modSrcMac, modDstMac, packetIn, drop));
		}

		BaseResponse ret = new BaseResponse();
		try {
			SetFlowToOFC requestData = new SetFlowToOFC();
			requestData.setDpid(dpid);
			Match match = requestData.new Match();
			Action action = requestData.new Action();
			match.setInPort(inPort);
			match.setSrcMac(srcMac);
			match.setDstMac(dstMac);
			action.setOutPort(outPort);
			action.setModSrcMac(modSrcMac);
			action.setModDstMac(modDstMac);
			if (!isNull(packetIn)) {
				action.setPacketIn(packetIn.toString());
			}
			if (!isNull(drop)) {
				action.setDrop(drop.toString());
			}
			requestData.setMatch(match);
			requestData.setAction(action);

			Builder resBuilder = this.resource.entity(requestData.toJson());
			resBuilder = resBuilder.accept(MediaType.APPLICATION_JSON);
			resBuilder = resBuilder.type(MediaType.APPLICATION_JSON);
			ClientResponse res = resBuilder.post(ClientResponse.class);

			if (res.getStatus() != STATUS_CREATED) {
				logger.error(res.getEntity(String.class));
				throw new OFCClientException(String.format(WRONG_RESPONSE, "OFC-" + this.ip));
			}
			ret = BaseResponse.fromJson(res.getEntity(String.class));
		} catch (UniformInterfaceException uie) {
			logger.error(uie.getMessage());
			throw new OFCClientException(String.format(CONNECTION_FAIL, "OFC-" + this.ip));
		} catch (ClientHandlerException che) {
			logger.error(che);
			throw new OFCClientException(String.format(CONNECTION_FAIL, "OFC-" + this.ip));
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
	 * @see org.okinawaopenlabs.ofpm.client.OFCClient#deleteFlows(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, java.lang.Boolean)
	 */
	@Override
	public BaseResponse deleteFlows(String dpid, Integer inPort, String srcMac, String dstMac, Integer outPort, String modSrcMac, String modDstMac,
			Boolean packetIn, Boolean drop) throws OFCClientException {
		final String fname = "deleteFlows";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(dpid=%s,inPort=%s,srcMac=%s,dstMac=%s,outPort=%s,modSrcMac=%s,modDstMac=%s,packetIn=%s,drop=%s) - start",
					fname, dpid, inPort, srcMac, dstMac, outPort, modSrcMac, modDstMac, packetIn, drop));
		}

		BaseResponse ret = new BaseResponse();
		try {
			MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();

			if (!isNullAndEmpty(dpid)) {
				queryParams.add("dpid", dpid);
			}
			if (!isNull(inPort)) {
				queryParams.add("inPort", inPort.toString());
			}
			if (!isNullAndEmpty(srcMac)) {
				queryParams.add("srcMac", srcMac);
			}
			if (!isNullAndEmpty(dstMac)) {
				queryParams.add("dstMac", dstMac);
			}
			if (!isNull(outPort)) {
				queryParams.add("outPort", outPort.toString());
			}
			if (!isNullAndEmpty(modSrcMac)) {
				queryParams.add("modSrcMac", modSrcMac);
			}
			if (!isNullAndEmpty(modDstMac)) {
				queryParams.add("modDstMac", modDstMac);
			}
			if (!isNull(packetIn)) {
				queryParams.add("packetIn", packetIn.toString());
			}
			if (!isNull(drop)) {
				queryParams.add("drop", drop.toString());
			}

			ClientResponse res = this.resource.queryParams(queryParams).delete(ClientResponse.class);

			if (res.getStatus() != STATUS_SUCCESS) {
				logger.error(res.getEntity(String.class));
				throw new OFCClientException(String.format(WRONG_RESPONSE, "OFC-" + this.ip));
			}
			ret = BaseResponse.fromJson(res.getEntity(String.class));
		} catch (UniformInterfaceException uie) {
			logger.error(uie.getMessage());
			throw new OFCClientException(String.format(CONNECTION_FAIL, "OFC-" + this.ip));
		} catch (ClientHandlerException che) {
			logger.error(che);
			throw new OFCClientException(String.format(CONNECTION_FAIL, "OFC-" + this.ip));
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new OFCClientException(UNEXPECTED_ERROR);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret.toJson()));
		}
		return ret;
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

	public String getIp() {
		return this.ip;
	}

	@Override
	public String toString() {
		return super.toString() + ":" + this.ip;
	}
}
