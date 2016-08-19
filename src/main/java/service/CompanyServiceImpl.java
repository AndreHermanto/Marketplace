package service;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dao.CompanyDAO;
import dao.CompanyDAOImpl;

@RestController
@Configuration
public class CompanyServiceImpl implements CompanyService {

	private CompanyDAO companyDAO = new CompanyDAOImpl();
	
	@RequestMapping("/createCompany")
	public @ResponseBody void createCompany(@RequestParam(value="name", required=false, defaultValue="asdasdasd") String name,@RequestParam(value="description", required=false, defaultValue="asdasd") String description) {
		this.companyDAO.createCompany(name,description);
	}

	@RequestMapping("/updateCompany")
	public void updateCompany(@RequestParam(value="id") String id, @RequestParam(value="name") String name, @RequestParam(value="description") String description) {
		this.companyDAO.updateCompany(id, name, description);
	}

	@RequestMapping("/deleteCompany")
	public void deleteCompany(@RequestParam(value="id") String id) {
		this.companyDAO.deleteCompany(id);
	}
	
	@RequestMapping("/listCompanies")
	@Override
	public @ResponseBody String listCompanies() {
		return companyDAO.getAdapter().toJson(companyDAO.listCompanies());
	}
}
