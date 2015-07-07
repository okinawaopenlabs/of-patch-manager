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
