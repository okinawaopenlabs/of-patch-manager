package org.okinawaopenlabs.ofpm.json.ofc;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SetFlowIn {
	private String dpid;
	private String inPort;
	private String srcMac;
	private String dstMac;

	public String getDpId() {
		return dpid;
	}
	public void setDpId(String dpid) {
		this.dpid = dpid;
	}
	public String getInPort() {
		return inPort;
	}
	public void setInPort(String inPort) {
		this.inPort = inPort;
	}
	public String getSrcMac() {
		return srcMac;
	}
	public void setSrcMac(String srcMac) {
		this.srcMac = srcMac;
	}
	public String getDstMac() {
		return dstMac;
	}
	public void setDstMac(String dstMac) {
		this.dstMac = dstMac;
	}

	public static SetFlowIn fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<SetFlowIn>(){}.getType();
		return gson.fromJson(json, type);
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<SetFlowIn>(){}.getType();
		return gson.toJson(this, type);
	}
}
