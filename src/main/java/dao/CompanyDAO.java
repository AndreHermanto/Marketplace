package dao;

import java.util.List;

import com.google.gson.Gson;

import model.Company;

public interface CompanyDAO {
	public void createCompany(String name, String description);
	public void updateCompany(String id, String name, String description);
	public void deleteCompany(String id);
	
	public List<Company> listCompanies();
	public Company findCompanyByName(String name);
	
	public Gson getAdapter();
}
