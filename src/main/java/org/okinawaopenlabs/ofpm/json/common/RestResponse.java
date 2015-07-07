package org.okinawaopenlabs.ofpm.json.common;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RestResponse {
	private Integer status;
	private String message;

	public RestResponse() {
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static RestResponse fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken() {
		}.getType();
		return (RestResponse) gson.fromJson(json, type);
	}

	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken() {
		}.getType();
		return gson.toJson(this, type);
	}
}

