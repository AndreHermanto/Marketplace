package model;

import com.orientechnologies.orient.core.id.ORecordId;

public class Category {
	
	private ORecordId vertexId;
	private String title;
	
	public Category(ORecordId vertexId, String title) {
		super();
		this.vertexId = vertexId;
		this.title = title;
	}

	public ORecordId getId() {
		return vertexId;
	}

	public void setId(ORecordId vertexId) {
		this.vertexId = vertexId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
