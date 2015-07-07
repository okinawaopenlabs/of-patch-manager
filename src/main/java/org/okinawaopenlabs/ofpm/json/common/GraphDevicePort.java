package org.okinawaopenlabs.ofpm.json.common;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.okinawaopenlabs.ofpm.json.device.DeviceType;

public class GraphDevicePort {
	private String deviceName;
	private DeviceType deviceType;
	private Boolean ofpFlg;
	private String portName;
	private Integer portNumber;

	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<GraphDevicePort>() {}.getType();
		return gson.toJson(this, type);
	}

	/* Setter and Getter */
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public DeviceType getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
	public Boolean getOfpFlg() {
		return ofpFlg;
	}
	public void setOfpFlg(Boolean ofpFlg) {
		this.ofpFlg = ofpFlg;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public Integer getPortNumber() {
		return portNumber;
	}
	public void setPortNumber(Integer portNumber) {
		this.portNumber = portNumber;
	}
}
