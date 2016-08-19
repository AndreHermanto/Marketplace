package service;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dao.CategoryDAO;
import dao.CategoryDAOImpl;

@RestController
@Configuration
public class CategoryServiceImpl implements CategoryService {
	
	private CategoryDAO categoryDAO = new CategoryDAOImpl();

	@RequestMapping("/createCategory")
	@Override
	public void createCategory(String title) {
		this.categoryDAO.createCategory(title);
	}

	@RequestMapping("/updateCategory")
	@Override
	public void updateCategory(String id, String title) {
		this.categoryDAO.updateCategory(id, title);
	}

	@RequestMapping("/deleteCategory")
	@Override
	public void deleteCategory(String id) {
		this.categoryDAO.deleteCategory(id);
	}

	@RequestMapping("/listCategories")
	@Override
	public @ResponseBody String listCategories() {
		return categoryDAO.getAdapter().toJson(categoryDAO.listCategories());
	}

}
