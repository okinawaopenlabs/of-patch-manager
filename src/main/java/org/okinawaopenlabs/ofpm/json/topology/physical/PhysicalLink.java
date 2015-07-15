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

import org.okinawaopenlabs.ofpm.json.device.PortData;

public class PhysicalLink {
	private String band;
	private List<PortData> link;

	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<PhysicalLink>(){}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (obj.getClass() != this.getClass()) return false;
		PhysicalLink other = (PhysicalLink)obj;
		if (!other.band.equals(this.band)) return false;

		if (other.link == this.link) return true;
		if (other.link == null) return false;
		if (this.link  == null) return false;
		if (other.link.size() != this.link.size()) return false;
		if (!other.link.containsAll(this.link)) return false;
		if (!this.link.containsAll(other.link)) return false;
		return true;
	}
	@Override
	public int hashCode() {
		return link.hashCode();
	}

	/* Setters and Getters */
	public List<PortData> getLink() {
		return link;
	}
	public void setLink(List<PortData> link) {
		this.link = link;
	}

	public String getBand() {
		return band;
	}
	public void setBand(String band) {
		this.band = band;
	}
}
