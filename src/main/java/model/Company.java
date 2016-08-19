package model;

import com.orientechnologies.orient.core.id.ORecordId;

public class Company {
	private ORecordId vertexId;
	private String name;
	private String description;
	
	public Company(ORecordId vertexId, String name, String description) {
		super();
		this.vertexId = vertexId;
		this.name = name;
		this.description = description;
	}
	
	public ORecordId getId() {
		return vertexId;
	}

	public void setId(ORecordId vertexId) {
		this.vertexId = vertexId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
