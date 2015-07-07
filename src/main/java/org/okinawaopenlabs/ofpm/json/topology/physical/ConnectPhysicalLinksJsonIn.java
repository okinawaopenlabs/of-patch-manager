package org.okinawaopenlabs.ofpm.json.topology.physical;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ConnectPhysicalLinksJsonIn {
	private List<PhysicalLink> links;

	public static ConnectPhysicalLinksJsonIn fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<ConnectPhysicalLinksJsonIn>() {}.getType();
		return gson.fromJson(json, type);
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<ConnectPhysicalLinksJsonIn>() {}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public String toString() {
		return this.toJson();
	}


	/* Setters and Getters */
	public List<PhysicalLink> getLinks() {
		return links;
	}
	public void setLinks(List<PhysicalLink> links) {
		this.links = links;
	}
}
