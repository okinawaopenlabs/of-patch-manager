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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.okinawaopenlabs.ofpm.json.common.BaseResponse;

public class LogicalTopologyUpdateJsonOut extends BaseResponse {
	private LogicalTopology result = new LogicalTopology();

	public LogicalTopology getResult() {
		return result;
	}
	public void setResult(LogicalTopology result) {
		this.result = result;
	}

	public static LogicalTopologyUpdateJsonOut fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<LogicalTopologyUpdateJsonOut>(){}.getType();
		return gson.fromJson(json, type);
	}
	@Override
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<LogicalTopologyUpdateJsonOut>(){}.getType();
		return gson.toJson(this, type);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(this.getClass() != obj.getClass()) return false;
		LogicalTopologyUpdateJsonOut other = (LogicalTopologyUpdateJsonOut)obj;
		if(this.getStatus() != other.getStatus()) return false;
		if(! this.getMessage().equals(other.getMessage())) return false;
		return this.result.equals(other.result);
	}
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if(this.result != null) hash += this.result.hashCode();
		return hash;
	}
	@Override
	public String toString() {
		return this.toJson();
	}
}
