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

package org.okinawaopenlabs.ofpm.json.topology.physical;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DisconnectPhysicalLinksJsonIn {
	private List<PhysicalLink> links;

	public static DisconnectPhysicalLinksJsonIn fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<DisconnectPhysicalLinksJsonIn>() {}.getType();
		return gson.fromJson(json, type);
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<DisconnectPhysicalLinksJsonIn>() {}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public String toString() {
		return this.toJson();
	}


	/* Setters and Getters */
	public List<PhysicalLink> getLinks() {
		return links;
	}
	public void setLinks(List<PhysicalLink> links) {
		this.links = links;
	}
}
