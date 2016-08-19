package dao;

import java.util.List;

import com.google.gson.Gson;

import model.Category;

public interface CategoryDAO {
	public void createCategory(String title);
	public void updateCategory(String id, String title);
	public void deleteCategory(String id);
	
	public List<Category> listCategories();
	
	public Gson getAdapter();
}
