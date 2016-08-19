package service;

public interface CategoryService {
	public void createCategory(String title);
	public void updateCategory(String id, String title);
	public void deleteCategory(String id);
	
	public String listCategories();
}
