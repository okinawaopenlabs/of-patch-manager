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

public class NetworkId implements Cloneable {
	private int networkId;
	private String networkType;
	private String UseRoute;

	public int getnetworkId() {
		return networkId;
	}
	public void setnetworkId(int networkId) {
		this.networkId = networkId;
	}

	public String getnetworkType() {
		return networkType;
	}
	public void setnetworkType(String networkType) {
		this.networkType = networkType;
	}
	public String getUseRoute() {
		return UseRoute;
	}
	public void setUseRoute(String UseRoute) {
		this.UseRoute = UseRoute;
	}

	@Override
	public NetworkId clone() {
		NetworkId newNode = null;
		try {
			newNode = (NetworkId)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		if (this.networkId != -1) {
			newNode.networkId = new Integer(this.networkId);
		}
		if (this.networkType != null) {
			newNode.networkType = new String(this.networkType);
		}
		return newNode;
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<NetworkId>() {}.getType();
		return gson.toJson(this, type);
	}
}
