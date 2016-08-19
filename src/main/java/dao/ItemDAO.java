package dao;

import java.util.List;

import com.google.gson.Gson;

import model.Item;

public interface ItemDAO {
	public void createItem(String userId, String title, String subtitle, String description, String category, String datapack);
	public void updateItem(String id, String title, String subtitle, String description, String category, String datapack, boolean installed);
	public void deleteItem(String id);
	public void installItem(String id, String userId);
	public void discardNotification(String id, String userId);
	public List<Item> checkUpdates(String userId);
	
	public List<Item> listItems(String userId);
	public List<Item> listItemsUploaded(String userId);
	public List<Item> listItemsByCategory(String userId, String category);
	
	public void rateItem(String id, String userId,double rate);
	public double checkItemRate(String id);
	public void addComment(String id, String userId, String comment);
	public List<String> listComments(String id);
	public void deleteComment(String id, String userId, String comment);
	
	public List<Item> sortItem(List<Item> item);
	public Gson getAdapter();
}
