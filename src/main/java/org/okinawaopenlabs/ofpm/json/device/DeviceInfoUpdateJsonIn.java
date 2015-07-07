package org.okinawaopenlabs.ofpm.json.device;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DeviceInfoUpdateJsonIn extends DeviceInfo {
	public static DeviceInfoUpdateJsonIn fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<DeviceInfoUpdateJsonIn>(){}.getType();
		return gson.fromJson(json, type);
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<DeviceInfoUpdateJsonIn>(){}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public String toString() {
		return this.toJson();
	}
}