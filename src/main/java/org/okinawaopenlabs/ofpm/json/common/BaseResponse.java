package org.okinawaopenlabs.ofpm.json.common;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class BaseResponse {
	private int status;
	private String message;

	public String getMessage() {
		return this.message;
	}
	public void setMessage(final String message) {
		this.message = message;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	@Override
	public String toString() {
		return this.toJson();
	}

	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<BaseResponse>() {}.getType();
		return gson.toJson(this, type);
	}
	public static BaseResponse fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<BaseResponse>() {}.getType();
		return gson.fromJson(json, type);
	}

}
