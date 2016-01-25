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


package org.okinawaopenlabs.ofpm.json.ofc;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.okinawaopenlabs.ofpm.json.topology.logical.LogicalTopology.OfpConDeviceInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SetFlowToOFC {

	private Long dpid;
	private Long priority;
	

	public Long getDpid() {
		return dpid;
	}
	public void setDpid(Long dpid) {
		this.dpid = dpid;
	}

	public Long getPriority() {
		return priority;
	}
	public void setPriority(Long priority) {
		this.priority = priority;
	}

	public class Match {
		private Long in_port;
		private Long dl_vlan;

		public Long getIn_port() {
			return in_port;
		}
		public void setIn_port(Long in_port) {
			this.in_port = in_port;
		}

		public Long getDl_vlan() {
			return dl_vlan;
		}
		public void setDl_vlan(Long dl_vlan) {
			this.dl_vlan = dl_vlan;
		}
	}

	private Match match;

	public Match getMatch() {
		return match;
	}
	public void setMatch(Match match) {
		this.match = match;
	}

	public class Action {
		private String type;
		private Long port;
		private Long ethertype;
		private String field;
		private Long value;

		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}

		public Long getPort() {
			return port;
		}
		public void setPort(Long port) {
			this.port = port;
		}

		public Long getEthertype() {
			return ethertype;
		}
		public void setEthertype(Long ethertype) {
			this.ethertype = ethertype;
		}

		public String getField() {
			return field;
		}
		public void setField(String field) {
			this.field = field;
		}

		public Long getValue() {
			return value;
		}
		public void setValue(Long value) {
			this.value = value;
		}
	}

	private List<Action> actions = new ArrayList<Action>();

	public List<Action> getActions() {
		return actions;
	}
	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public static SetFlowToOFC fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<SetFlowToOFC>(){}.getType();
		return gson.fromJson(json, type);
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<SetFlowToOFC>(){}.getType();
		return gson.toJson(this, type);
	}
}
