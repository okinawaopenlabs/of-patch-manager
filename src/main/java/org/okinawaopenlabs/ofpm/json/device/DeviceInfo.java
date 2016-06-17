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

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

public class DeviceInfo extends Node implements Cloneable {
	private String datapathId;
	private String ofcIp;

	public String getDatapathId() {
		return datapathId;
	}

	public void setDatapathId(String datapathId) {
		this.datapathId = datapathId;
	}
	
	public String getOfcIp() {
		return ofcIp;
	}

	public void setOfcIp(String ofcIp) {
		this.ofcIp = ofcIp;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<DeviceInfo>() {}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!super.equals(obj)) return false;
		if (obj.getClass() != this.getClass()) return false;
		DeviceInfo other = (DeviceInfo)obj;
		if (!StringUtils.equals(other.datapathId, this.datapathId)) return false;
		if (!StringUtils.equals(other.ofcIp,      this.ofcIp     )) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if (this.datapathId != null) {
			hash += this.datapathId.hashCode();
		}
		if (this.ofcIp != null) {
			hash += this.ofcIp.hashCode();
		}
		return hash;
	}
	@Override
	public DeviceInfo clone() {
		DeviceInfo newObj = (DeviceInfo)super.clone();
		if (this.datapathId != null) {
			newObj.datapathId = new String(this.datapathId);
		}
		if (this.ofcIp != null) {
			newObj.ofcIp      = new String(this.ofcIp);
		}
		return newObj;
	}
}
