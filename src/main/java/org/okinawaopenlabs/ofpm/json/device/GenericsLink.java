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
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

public class GenericsLink<T> {
	@SerializedName("link")
	private List<T> link = new ArrayList<T>(2);

	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<T>() {}.getType();
		return gson.toJson(this, type);
	}

	/* Setter and Getter */
	public List<T> getLink() {
		return link;
	}

	public void setLink(List<T> link) {
		this.link = link;
	}
}
