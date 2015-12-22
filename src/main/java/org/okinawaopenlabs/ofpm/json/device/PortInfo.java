/*
 *   Copyright 2015 Okinawa Open Laboratory, General Incorporated Association
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.okinawaopenlabs.ofpm.json.device;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class PortInfo implements Cloneable {
	private String portName;
	private Integer portNumber;
	private Integer band;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;
		PortInfo other = (PortInfo)obj;
		if (!StringUtils.equals(other.portName,  this.portName)) return false;
		if (!ObjectUtils.equals(other.portNumber, this.portNumber)) return false;
		if (!ObjectUtils.equals(other.band,  this.band)) return false;
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
		newObj.band = this.band;
		if (this.portName != null) {
			newObj.portName = new String(this.portName);
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
	public Integer getBand() {
		return band;
	}
	public void setBand(Integer band) {
		this.band = band;
	}
}
