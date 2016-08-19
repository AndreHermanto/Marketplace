package service;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dao.PersonDAO;
import dao.PersonDAOImpl;

@RestController
@Configuration
public class PersonServiceImpl implements PersonService {
	
	private PersonDAO personDAO = new PersonDAOImpl();

	@RequestMapping("/createUser")
	@Override
	public @ResponseBody String createPerson(@RequestParam(value="firstname") String firstname, @RequestParam(value="lastname") String lastname, @RequestParam(value="companyId") String companyId) {
		return personDAO.getAdapter().toJson(this.personDAO.createPerson(firstname, lastname, companyId));
	}
	@RequestMapping("/updatePerson")
	@Override
	public @ResponseBody void updatePerson(@RequestParam(value="id")String id, @RequestParam(value="firstname") String firstname, @RequestParam(value="lastname") String lastname) {
		this.personDAO.updatePerson(id, firstname, lastname);
	}

	@Override
	public void deletePerson(String id) {
		this.personDAO.deletePerson(id);
	}

	@RequestMapping("/listPeople")
	@Override
	public @ResponseBody String listPeople() {
		return personDAO.getAdapter().toJson(personDAO.listPeople());
	}

	@RequestMapping("/connectUser")
	@Override
	public @ResponseBody String connectUser(@RequestParam(value="firstname") String firstname, @RequestParam(value="lastname") String lastname) {
		return personDAO.getAdapter().toJson(this.personDAO.connectUser(firstname, lastname));
	}
}
