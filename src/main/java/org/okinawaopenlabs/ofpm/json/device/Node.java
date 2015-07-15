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

public class Node implements Cloneable {
	private String deviceName;
	private String deviceType;

	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public Node clone() {
		Node newNode = null;
		try {
			newNode = (Node)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		if (this.deviceName != null) {
			newNode.deviceName = new String(this.deviceName);
		}
		if (this.deviceType != null) {
			newNode.deviceType = new String(this.deviceType);
		}
		return newNode;
	}
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(this.getClass() != obj.getClass()) return false;
		Node other = (Node)obj;
		if (!StringUtils.equals(other.deviceName, this.deviceName)) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int hash = 0;
		if (this.deviceName != null) {
			hash += this.deviceName.hashCode();
		}
		if (this.deviceType != null) {
			hash += this.deviceType.hashCode();
		}
		return hash;
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<Node>() {}.getType();
		return gson.toJson(this, type);
	}
}
