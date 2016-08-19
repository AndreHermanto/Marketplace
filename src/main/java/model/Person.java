package model;

import com.orientechnologies.orient.core.id.ORecordId;

public class Person {

	private ORecordId vertexId;
	private String firstname;
	private String lastname;;

	public Person(ORecordId vertexId, String firstname, String lastname) {
		super();
		this.vertexId = vertexId;
		this.firstname = firstname;
		this.lastname = lastname;
	}
	

	public ORecordId getId() {
		return vertexId;
	}

	public void setId(ORecordId vertexId) {
		this.vertexId = vertexId;
	}
	
	public String getFirstname() {
		return firstname;
	}
	
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getLastname() {
		return lastname;
	}
	
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

}
