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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SetFlowToOFC {

	private String dpid;

	public String getDpid() {
		return dpid;
	}
	public void setDpid(String dpid) {
		this.dpid = dpid;
	}

	public class Match {
		private Integer inPort;
		private String srcMac;
		private String dstMac;

		public Integer getInPort() {
			return inPort;
		}
		public void setInPort(Integer inPort) {
			this.inPort = inPort;
		}
		public String getSrcMac() {
			return srcMac;
		}
		public void setSrcMac(String srcMac) {
			this.srcMac = srcMac;
		}
		public String getDstMac() {
			return dstMac;
		}
		public void setDstMac(
				String dstMac) {
			this.dstMac = dstMac;
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
		private Integer outPort;
		private String modSrcMac;
		private String modDstMac;
		private String packetIn;
		private String drop;

		public Integer getOutPort() {
			return outPort;
		}
		public void setOutPort(Integer outPort) {
			this.outPort = outPort;
		}

		public String getModSrcMac() {
			return modSrcMac;
		}
		public void setModSrcMac(String modSrcMac) {
			this.modSrcMac = modSrcMac;
		}

		public String getModDstMac() {
			return modDstMac;
		}
		public void setModDstMac(String modDstMac) {
			this.modDstMac = modDstMac;
		}

		public String getPacketIn() {
			return packetIn;
		}
		public void setPacketIn(String packetIn) {
			this.packetIn = packetIn;
		}

		public String getDrop() {
			return drop;
		}
		public void setDrop(String drop) {
			this.drop = drop;
		}
	}

	private Action action;

	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
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
