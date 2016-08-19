package dao;

import java.util.List;

import com.google.gson.Gson;

import model.Person;

public interface PersonDAO {
	public String createPerson(String firstname, String lastname, String companyId);
	public void updatePerson(String id, String firstname, String lastname);
	public void deletePerson(String id);
	
	public String connectUser(String firstname, String lastname);
	public List<Person> listPeople();
	public Gson getAdapter();
}
