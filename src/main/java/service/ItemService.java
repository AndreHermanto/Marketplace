package service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public interface ItemService {
	public void createItem(String userId, String title, String subtitle, String description, String category, String datapack);
	public void updateItem(String id, String title, String subtitle, String description, String category,String datapack, boolean installed);
	public void deleteItem(String id);
	public void installItem(String id, String userId);
	public String checkUpdates(String userId);
	
	public String listItemsServ(String userId);
	public String listItemsByCategory(String userId, String category);
	public void discardNotification(String id, String userId);
	public void rateItem(String id, String userId, double rate);
	public double checkItemRate(String id);
	public String listItemsUploaded(String userId);
	public void addComment(String id, String userId,String comment);
	public void deleteComment(String id, String userId, String comment);
	public String listComments(String id);
}
