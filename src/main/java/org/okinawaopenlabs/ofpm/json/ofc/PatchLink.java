package org.okinawaopenlabs.ofpm.json.ofc;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PatchLink {
	private String deviceName;
	private List<Integer> portName = new ArrayList<Integer>();

	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public List<Integer> getPortName() {
		return portName;
	}
	public void setPortName(
			List<Integer> portName) {
		this.portName = portName;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<PatchLink>() {}.getType();
		return gson.toJson(this, type);
	}
}
