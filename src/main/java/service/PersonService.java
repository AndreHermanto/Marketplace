package service;

public interface PersonService {
	public String createPerson(String firstname, String lastname, String companyId);
	public void updatePerson(String id, String firstname, String lastname);
	public void deletePerson(String id);
	public String listPeople();
	
	public String connectUser(String firstname, String lastname);
}
