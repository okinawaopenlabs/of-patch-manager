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

import org.okinawaopenlabs.ofpm.json.common.BaseResponse;

public class DeviceManagerGetConnectedPortInfoJsonOut extends BaseResponse {

	@SerializedName("result")
	private List<ResultData> resultData = new ArrayList<ResultData>();

	public List<ResultData> getResultData() {
		return resultData;
	}

	public void setResultData(final List<ResultData> resultData) {
		this.resultData = resultData;
	}

	public void addResultData(final ResultData resultData) {
		this.resultData.add(resultData);
	}

	@Override
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<DeviceManagerGetConnectedPortInfoJsonOut>() {}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public String toString() {
		return this.toJson();
	}

	public class ResultData {
		@SerializedName("link")
		private List<LinkData> linkData = new ArrayList<LinkData>();

		public List<LinkData> getLinkData() {
			return linkData;
		}

		public void setLinkData(final List<LinkData> linkData) {
			this.linkData = linkData;
		}

		public void addLinkData(final LinkData linkData) {
			this.linkData.add(linkData);
		}

		public class LinkData extends Node {
			private String portName;
			private Integer portNumber;
			private String ofpFlag;

			public String getPortName() {
				return portName;
			}
			public void setPortName(final String portName) {
				this.portName = portName;
			}
			public Integer getPortNumber() {
				return portNumber;
			}
			public void setPortNumber(final Integer portNumber) {
				this.portNumber = portNumber;
			}
			public String getOfpFlag() {
				return ofpFlag;
			}
			public void setOfpFlag(final String ofpFlag) {
				this.ofpFlag = ofpFlag;
			}
		}
	}
}
