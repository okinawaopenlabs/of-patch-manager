package org.okinawaopenlabs.ofpm.json.device;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PortInfoUpdateJsonIn extends PortInfo {
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this, new TypeToken<PortInfoUpdateJsonIn>(){}.getType());
	}
	public static PortInfoUpdateJsonIn fromJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, new TypeToken<PortInfoUpdateJsonIn>(){}.getType());
	}
	@Override
	public String toString() {
		return this.toJson();
	}
}
