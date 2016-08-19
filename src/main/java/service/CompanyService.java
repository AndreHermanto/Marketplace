package service;

public interface CompanyService {
	public void createCompany(String name, String description);
	public void updateCompany(String id, String name, String description);
	public void deleteCompany(String id);
	
	public String listCompanies();
}
