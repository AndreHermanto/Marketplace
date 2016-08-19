package model;

import com.orientechnologies.orient.core.id.ORecordId;

public class Item {
	
	private ORecordId vertexId;
	private String title;
	private String subtitle;
	private String description;
	private String category;
	private String datapack;
	private String uploadedBy;
	private boolean installed;
	private boolean discarded;
	private boolean update;
	private double rate;
	
	public Item(ORecordId vertexId, String title, String subtitle, String description, String category, String datapack, String uploadedBy, boolean installed, boolean discarded, boolean update,double rate) {
		super();
		this.vertexId = vertexId;
		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.category = category;
		this.datapack = datapack;
		this.uploadedBy = uploadedBy;
		this.installed = installed;
		this.discarded = discarded;
		this.update = update;
		this.rate = rate;
	}
	
	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
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

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getDatapack() {
		return datapack;
	}

	public void setDatapack(String datapack) {
		this.datapack = datapack;
	}
	
	public boolean installed() {
		return installed;
	}

	public void setInstalled(boolean installed) {
		this.installed = installed;
	}
	
	public boolean discarded() {
		return discarded;
	}

	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}
	
	public boolean updated() {
		return update;
	}

	public void setUpdated(boolean update) {
		this.update = update;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
}


