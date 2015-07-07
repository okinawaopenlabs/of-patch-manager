package org.okinawaopenlabs.ofpm.json.topology.logical;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.okinawaopenlabs.ofpm.json.device.PortData;

public class LogicalLink implements Cloneable {
	private List<PortData> link;

	/* Setters and Getters */
	public List<PortData> getLink() {
		return link;
	}
	public void setLink(List<PortData> link) {
		this.link = link;
	}


	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(this.getClass() != obj.getClass()) return false;
		LogicalLink other = (LogicalLink)obj;
		if (other.link == this.link) return true;
		if (other.link == null) return false;
		if (this.link  == null) return false;
		if (!other.link.containsAll(this.link)) return false;
		if (!this.link.containsAll(other.link)) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int hash = 0;
		if (this.link != null) {
			for (PortData port : this.link) {
				hash += port.hashCode();
			}
		}
		return hash;
	}
	@Override
	public LogicalLink clone() {
		LogicalLink newObj = new LogicalLink();
		if (this.link != null) {
			newObj.link = new ArrayList<PortData>();
			for (PortData port : this.link) {
				newObj.link.add(port.clone());
			}
		}
		return newObj;
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<LogicalLink>(){}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public String toString() {
		return this.toJson();
	}
}