package org.okinawaopenlabs.ofpm.json.device;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.okinawaopenlabs.ofpm.json.common.GenericsRestResultResponse;
import org.okinawaopenlabs.ofpm.json.common.GraphDevicePort;

public class ConnectedPortGetJsonOut extends GenericsRestResultResponse<GenericsLink<GraphDevicePort>> {
	public static ConnectedPortGetJsonOut fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<ConnectedPortGetJsonOut>(){}.getType();
		return gson.fromJson(json, type);
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<ConnectedPortGetJsonOut>(){}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public String toString() {
		return this.toJson();
	}
}
