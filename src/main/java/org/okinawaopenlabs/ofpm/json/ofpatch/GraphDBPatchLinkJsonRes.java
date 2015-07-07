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
