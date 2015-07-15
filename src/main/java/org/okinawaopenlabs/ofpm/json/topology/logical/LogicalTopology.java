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

package org.okinawaopenlabs.ofpm.json.topology.logical;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.ObjectUtils;

import org.okinawaopenlabs.ofpm.json.device.Node;
import org.okinawaopenlabs.ofpm.json.device.PortData;
import org.okinawaopenlabs.ofpm.json.device.PortInfo;

public class LogicalTopology implements Cloneable {
	private List<OfpConDeviceInfo> nodes;
	private List<LogicalLink> links;

	/* Setters and Getters */
	public List<OfpConDeviceInfo> getNodes() {
		return nodes;
	}
	public List<LogicalLink> getLinks() {
		return links;
	}
	public void setNodes(List<OfpConDeviceInfo> nodes) {
		this.nodes = nodes;
	}
	public void setLinks(List<LogicalLink> links) {
		this.links = links;
	}


	public LogicalTopology sub(LogicalTopology other) {
		LogicalTopology newObj = this.clone();
		newObj.nodes.removeAll(other.nodes);
		newObj.links.removeAll(other.links);
		return newObj;
	}


	@Override
	public LogicalTopology clone() {
		LogicalTopology newObj = null;
		try {
			newObj = (LogicalTopology)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		newObj.nodes = new ArrayList<OfpConDeviceInfo>();
		newObj.links = new ArrayList<LogicalLink>();

		for (OfpConDeviceInfo node : this.nodes) {
			newObj.nodes.add(node.clone());
		}
		for (LogicalLink link : links) {
			newObj.links.add(link.clone());
		}
		return newObj;
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<LogicalTopology>() {}.getType();
		return gson.toJson(this, type);
	}






	public static class OfpConDeviceInfo extends Node implements Cloneable {
		private List<OfpConPortInfo> ports;

		/* Setters and Getters */
		public List<OfpConPortInfo> getPorts() {
			return ports;
		}
		public void setPorts(List<OfpConPortInfo> ports) {
			this.ports = ports;
		}

		@Override
		public OfpConDeviceInfo clone() {
			OfpConDeviceInfo newObj = (OfpConDeviceInfo)super.clone();
			return newObj;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj == null) return false;
			if (obj.getClass() != this.getClass()) return false;
			OfpConDeviceInfo other = (OfpConDeviceInfo)obj;
			if (other.ports == this.ports) return true;
			if (other.ports == null) return false;
			if (this.ports  == null) return false;
			if (!other.ports.containsAll(this.ports)) return false;
			if (!this.ports.containsAll(other.ports)) return false;
			return true;
		}
		@Override
		public int hashCode() {
			int hash = super.hashCode();
			if (this.ports != null) {
				for (OfpConPortInfo port : ports) {
					hash += port.hashCode();
				}
			}
			return hash;
		}
		@Override
		public String toString() {
			Gson gson = new Gson();
			Type type = new TypeToken<OfpConDeviceInfo>() {}.getType();
			return gson.toJson(this, type);
		}
	}
	public static class OfpConPortInfo extends PortInfo implements Cloneable {
		private PortData ofpPortLink;

		/* Setters and Getters */
		public PortData getOfpPortLink() {
			return ofpPortLink;
		}
		public void setOfpPortLink(PortData ofpPortLink) {
			this.ofpPortLink = ofpPortLink;
		}

		@Override
		public OfpConPortInfo clone() {
			OfpConPortInfo newObj = (OfpConPortInfo)super.clone();
			newObj.ofpPortLink = this.ofpPortLink.clone();
			return newObj;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj == null) return false;
			if (obj.getClass() != this.getClass()) return false;
			OfpConPortInfo other = (OfpConPortInfo)obj;
			if (!ObjectUtils.equals(other.ofpPortLink, this.ofpPortLink)) return false;
			return super.equals(obj);
		}
		@Override
		public int hashCode() {
			int hash = super.hashCode();
			if (this.ofpPortLink != null) {
				hash += this.ofpPortLink.hashCode();
			}
			return hash;
		}
		@Override
		public String toString() {
			Gson gson = new Gson();
			return gson.toJson(this);
		}
	}
}
