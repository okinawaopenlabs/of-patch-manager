package org.okinawaopenlabs.ofpm.json.device;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.okinawaopenlabs.ofpm.json.common.BaseResponse;

public class DeviceInfoListReadJsonOut extends BaseResponse {
	private List<DeviceInfo> result = null;

	@Override
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<DeviceInfoListReadJsonOut>() {}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public String toString() {
		return this.toJson();
	}

	public List<DeviceInfo> getResult() {
		return result;
	}

	public void setResult(List<DeviceInfo> result) {
		this.result = result;
	}
}
