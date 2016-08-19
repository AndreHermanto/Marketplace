package service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import dao.ItemDAO;

@RestController
@Configuration
public class ItemServiceImpl implements ItemService{

	@Autowired
	private ItemDAO itemDAO;

	@RequestMapping("/createItem")
	@Override
	public void createItem(@RequestParam(value="userId") String userId, @RequestParam(value="title") String title, @RequestParam(value="subtitle") String subtitle, @RequestParam(value="description") String description, @RequestParam(value="category") String category, @RequestParam(value="datapack") String datapack) {
		this.itemDAO.createItem(userId, title, subtitle, description, category, datapack);
	}

	@RequestMapping("/updateItem")
	@Override
	public @ResponseBody void updateItem(@RequestParam("id") String id,@RequestParam("title") String title,@RequestParam("subtitle") String subtitle, @RequestParam("description") String description, @RequestParam("category") String category, @RequestParam("datapack")String datapack, @RequestParam("installed") boolean installed) {
		this.itemDAO.updateItem(id, title, subtitle, description, category, datapack, installed);
	}

	@RequestMapping("/deleteItem")
	@Override
	public void deleteItem(@RequestParam("id") String id) {
		this.itemDAO.deleteItem(id);
	}
	
	@RequestMapping("/listItems")
	@Override
	public @ResponseBody String listItemsServ(@RequestParam(value="userId") String userId) {
		return itemDAO.getAdapter().toJson(itemDAO.listItems(userId));
	}
	
	@RequestMapping("/listItemsUploaded")
	@Override
	public @ResponseBody String listItemsUploaded(@RequestParam(value="userId") String userId) {
		return itemDAO.getAdapter().toJson(itemDAO.listItemsUploaded(userId));
	}
	
	@RequestMapping(value="/listItemsByCategory")
	@Override
	public @ResponseBody String listItemsByCategory(@RequestParam(value="userId") String userId, @RequestParam(value="category") String category) {
		return itemDAO.getAdapter().toJson(itemDAO.listItemsByCategory(userId, category));
	}
	
	@RequestMapping("/checkUpdates")
	@Override
	public @ResponseBody String checkUpdates(@RequestParam(value="userId") String userId) {
		return itemDAO.getAdapter().toJson(this.itemDAO.checkUpdates(userId));
	}
	
	@RequestMapping("/discardNotification")
	@Override
	public @ResponseBody void discardNotification(@RequestParam(value="id")String id,@RequestParam(value="userId")String userId) {
		this.itemDAO.discardNotification(id, userId);
	}
	
	@RequestMapping(value="/installItem")
	@Override
	public @ResponseBody void installItem(@RequestParam(value="id") String id, @RequestParam(value="userId") String userId) {
		// TODO Auto-generated method stub
		this.itemDAO.installItem(id, userId);
	}
	
	@RequestMapping(value="/rateItem")
	@Override
	public @ResponseBody void rateItem(@RequestParam(value="id") String id, @RequestParam(value="userId") String userId, @RequestParam(value="rate")double rate){
		this.itemDAO.rateItem(id, userId, rate);
	}
	
	@RequestMapping("/checkItemRate")
	@Override
	public @ResponseBody double checkItemRate(@RequestParam(value="id") String id){
		return this.itemDAO.checkItemRate(id);
	}
	
	@RequestMapping(value="/addComment")
	@Override
	public @ResponseBody void addComment(@RequestParam(value="id") String id, @RequestParam(value="userId") String userId, @RequestParam(value="comment")String comment){
		this.itemDAO.addComment(id, userId, comment);
	}
	
	@RequestMapping(value="/listComments")
	@Override
	public @ResponseBody String listComments(@RequestParam(value="id")String id){
		return itemDAO.getAdapter().toJson(itemDAO.listComments(id));
	}
	
	@RequestMapping(value="/deleteComment")
	@Override
	public @ResponseBody void deleteComment(String id, String userId, String comment){
		this.itemDAO.deleteComment(id, userId, comment);
	}
}
