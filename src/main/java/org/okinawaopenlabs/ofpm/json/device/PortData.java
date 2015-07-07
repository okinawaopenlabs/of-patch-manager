package org.okinawaopenlabs.ofpm.json.device;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

public class PortData extends PortInfo implements Cloneable {
	private String deviceName;

	@Override
	public PortData clone() {
		PortData newObj = (PortData)super.clone();
		if (this.deviceName != null) {
			newObj.deviceName = new String(this.deviceName);
		}
		return newObj;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (obj.getClass() != this.getClass()) return false;
		PortData other = (PortData)obj;
		if (!StringUtils.equals(other.deviceName, this.deviceName)) return false;
		return super.equals(obj);
	}
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if (this.deviceName != null) {
			hash += this.deviceName.hashCode();
		}
		return hash;
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	/* Setters and Getters */
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
}
