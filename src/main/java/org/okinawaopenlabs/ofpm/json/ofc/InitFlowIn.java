package org.okinawaopenlabs.ofpm.json.ofc;

import com.google.gson.Gson;

public class InitFlowIn {
	private String datapathId;

	public static InitFlowIn fromJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, InitFlowIn.class);
	}
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	@Override
	public String toString() {
		return this.toJson();
	}


	public String getDatapathId() {
		return datapathId;
	}
	public void setDatapathId(String datapathId) {
		this.datapathId = datapathId;
	}
}
