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

public class PortInfoUpdateJsonIn extends PortInfo {
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this, new TypeToken<PortInfoUpdateJsonIn>(){}.getType());
	}
	public static PortInfoUpdateJsonIn fromJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, new TypeToken<PortInfoUpdateJsonIn>(){}.getType());
	}
	@Override
	public String toString() {
		return this.toJson();
	}
}
