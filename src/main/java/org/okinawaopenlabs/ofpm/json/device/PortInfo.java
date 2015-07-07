package org.okinawaopenlabs.ofpm.json.device;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class PortInfo implements Cloneable {
	private String portName;
	private Integer portNumber;
	private String band;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;
		PortInfo other = (PortInfo)obj;
		if (!StringUtils.equals(other.portName,  this.portName)) return false;
		if (!ObjectUtils.equals(other.portNumber, this.portNumber)) return false;
		if (!StringUtils.equals(other.band,  this.band)) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int hash = 0;
		if (this.portName != null) {
			hash += this.portName.hashCode();
		}
		if (this.portNumber != null) {
			hash += this.portNumber;
		}
		if (this.band != null) {
			hash += this.band.hashCode();
		}
		return hash;
	}
	@Override
	public PortInfo clone() {
		PortInfo newObj;
		try {
			newObj = (PortInfo)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		newObj.portNumber = this.portNumber;
		if (this.portName != null) {
			newObj.portName = new String(this.portName);
		}
		if (this.band != null) {
			newObj.band = new String(this.band);
		}
		return newObj;
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this, new TypeToken<PortInfo>(){}.getType());
	}

	/* Setters and Getters */
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
	public String getBand() {
		return band;
	}
	public void setBand(String band) {
		this.band = band;
	}

}
