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

package org.okinawaopenlabs.ofpm.json.ofpatch;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.okinawaopenlabs.ofpm.json.common.BaseResponse;
import org.okinawaopenlabs.ofpm.json.ofc.PatchLink;

public class GraphDBPatchLinkJsonRes extends BaseResponse {
	private List<PatchLink> result = new ArrayList<PatchLink>();

	public List<PatchLink> getResult() {
		return result;
	}

	public void setResult(
			List<PatchLink> result) {
		this.result = result;
	}

	public static GraphDBPatchLinkJsonRes fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<GraphDBPatchLinkJsonRes>(){}.getType();
		return gson.fromJson(json, type);
	}
	@Override
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<GraphDBPatchLinkJsonRes>(){}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public String toString() {
		return this.toJson();
	}
}
