package dao;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mincom.ellipse.edoi.ejb.EDOIFacade;
import com.mincom.eql.common.Rec;
import com.mincom.eql.importexport.RecImportExporter;
import com.orientechnologies.orient.core.id.ORecordId;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import controller.DBController;
import model.Item;
import test.Testing;

public class ItemDAOImpl implements ItemDAO {

	Boolean test=Testing.flag;

	@Autowired
	private EDOIFacade bean;

	@Override
	public void createItem(String userId, String title, String subtitle, String description, String category, String datapack) {
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try{
			for (Vertex cat : graph.getVerticesOfClass("Category")) {
				if (cat.getProperty("title").toString().equals(category)){
					Vertex x = graph.addVertex("class:Item", "title", title, "subtitle", subtitle, "description", description, "datapack", datapack,"installed", false, "rate", 0);
					graph.addEdge(null, cat, x, "Contains");
					graph.addEdge(null, graph.getVertex(userId), x, "UploadedBy");
				}
			}		
			graph.commit();
		} catch( Exception e ) {
			graph.rollback();
		} finally {
			graph.shutdown();
		}
	}

	@Override
	public void updateItem(String id, String title, String subtitle, String description, String category, String datapack, boolean installed) {
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		
		try {
			Vertex x=null;
			for (Vertex cat : graph.getVertices("Category.title", category)) {
				x = graph.getVertex(id);
				x.setProperty("title", title);
				x.setProperty("subtitle", subtitle);
				x.setProperty("description", description);
				x.setProperty("datapack", datapack);
				for (Edge e : x.getEdges(Direction.IN, "HasInstalled")){
					e.setProperty("update_available", true);							
				}
			}
			
			graph.commit();

		} finally {
			graph.shutdown();
		}
	}

	@Override
	public void deleteItem(String id) {
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try {
			graph.getVertex(id).remove();
			graph.commit();
		} finally {
			graph.shutdown();
		}
	}

	@Override
	public List<Item> checkUpdates(String userId){
		OrientGraph graph;
		String uploadedBy = "";
		boolean discarded = false;
		boolean update = false;
		List<Item> results = new ArrayList<Item>();
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}

		Vertex user = graph.getVertex(userId);
		for (Vertex y : user.getVertices(Direction.OUT, "WorksAt")) {
			for (Edge z : y.getEdges(Direction.OUT, "HasInstalled")) {
				if((boolean)z.getProperty("update_available") == true){
					Vertex temp = z.getVertex(Direction.IN);
					String cat = "";
					for (Vertex x : temp.getVertices(Direction.IN, "Contains")) {
						cat += x.getProperty("title");
					}
					
					for (Vertex upBy : temp.getVertices(Direction.IN, "UploadedBy")) {
						uploadedBy = upBy.getProperty("firstname") + " " + upBy.getProperty("lastname");
					}

					if((boolean)z.getProperty("discarded")){
						discarded = true;
					}
					
					if((boolean)z.getProperty("update_available")){
						update = true;
					}

					results.add(new Item((ORecordId)temp.getId(),temp.getProperty("title").toString(),temp.getProperty("subtitle").toString(),temp.getProperty("description").toString(), cat, temp.getProperty("datapack"),uploadedBy, (boolean)temp.getProperty("installed"), discarded, update, (double)temp.getProperty("rate")));
				}
			}
		}

		return results;
	}
	
	@Override
	public void discardNotification(String id, String userId){
		OrientGraph graph;
		
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		
		Vertex user = graph.getVertex(userId);
		
		for (Vertex y : user.getVertices(Direction.OUT, "WorksAt")) {
			for(Edge e : y.getEdges(Direction.OUT, "HasInstalled")){
				if(e.getVertex(Direction.IN).equals(graph.getVertex(id))){
					e.setProperty("discarded", "true");
				}
			}
		}
		
		graph.commit();
		graph.shutdown();
	}

	@Override
	public List<Item> listItems(String userId) {
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		String uploadedBy = "";
		boolean discarded = false;
		boolean update = false;

		Vertex user = graph.getVertex(userId);

		try {
			List<Item> listItems = new ArrayList<Item>() ;
			for (Vertex x : graph.getVerticesOfClass("Item")) {
				String cat = "";
				boolean installed = false;
				for (Vertex z : x.getVertices(Direction.IN, "Contains")) {
					cat += z.getProperty("title");
				}

				for (Vertex y : user.getVertices(Direction.OUT, "WorksAt")) {
					for (Vertex z : x.getVertices(Direction.IN, "HasInstalled")) {
						if(z.getId().equals(y.getId())){
							installed = true;
						}
					}
				}
				
				for (Vertex upBy : x.getVertices(Direction.IN, "UploadedBy")) {
					uploadedBy = upBy.getProperty("firstname") + " " + upBy.getProperty("lastname");
				}

				Iterable<Edge> listUpdate = x.getEdges(Direction.IN, "HasInstalled");
				for(Edge up:listUpdate){
					if((boolean)up.getProperty("discarded")){
						discarded = true;
					}
					if((boolean)up.getProperty("update_available")){
						update = true;
					}
				}
				listItems.add(new Item((ORecordId) x.getId(), (String)x.getProperty("title"), (String)x.getProperty("subtitle"), (String)x.getProperty("description"), cat, (String)x.getProperty("datapack"),uploadedBy, installed, discarded, update, (double)x.getProperty("rate")));
				discarded = false;
				update = false;
			}
			
			listItems = sortItem(listItems);
			return listItems;
		} finally {
			graph.shutdown();
		}
	}

	@Override
	public List<Item> listItemsByCategory(String userId, String category) {
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		String uploadedBy = "";
		boolean discarded = false;
		boolean update = false;
		
		Vertex user = graph.getVertex(userId);
		
		try {
			List<Item> listItems = new ArrayList<Item>() ;
			for (Edge y : graph.getEdgesOfClass("Contains")) {
				String cat = "";
				boolean installed = false;
				
				Vertex x = y.getVertex(Direction.IN);
				if(y.getVertex(Direction.OUT).getProperty("title").toString().equals(category)){
					for (Vertex z : x.getVertices(Direction.IN, "Contains")) {
						cat += z.getProperty("title");
					}
					
					for (Vertex w : user.getVertices(Direction.OUT, "WorksAt")) {
						for (Vertex z : x.getVertices(Direction.IN, "HasInstalled")) {
							if(z.getId().equals(w.getId())){
								installed = true;
							}
						}
					}
					
					for (Vertex upBy : x.getVertices(Direction.IN, "UploadedBy")) {
						uploadedBy = upBy.getProperty("firstname") + " " + upBy.getProperty("lastname");
					}

					Iterable<Edge> listUpdate = x.getEdges(Direction.IN, "HasInstalled");
					for(Edge up:listUpdate){
						if((boolean)up.getProperty("discarded")){
							discarded = true;
						}
						if((boolean)up.getProperty("update_available")){
							update = true;
						}
						
					}

					listItems.add(new Item((ORecordId) x.getId(), (String)x.getProperty("title"), (String)x.getProperty("subtitle"), (String)x.getProperty("description"), cat, (String)x.getProperty("datapack"), uploadedBy, installed, discarded, update,(double)x.getProperty("rate")));
					discarded = false;
					update = false;
				}
			}
			listItems = sortItem(listItems);
			return listItems;
		} finally {
			graph.shutdown();
		}
	}

	@Override
	public Gson getAdapter(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Item.class, new ItemAdapter());
		gsonBuilder.registerTypeAdapter(String.class, new StringAdapter());
		return gsonBuilder.create();
	}

	@Override
	public void installItem(String id, String userId) {
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");

		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}

		try {
			Vertex x = graph.getVertex(id);
			if(!test){
			RecImportExporter rec = new RecImportExporter();
			List<Rec> recs = rec.importRecs(x.getProperty("datapack"));
			bean.deleteAll(recs);
			bean.createAll(recs);
				}
			
			Vertex user = graph.getVertex(userId);
			boolean existing = false;

			for (Vertex y : user.getVertices(Direction.OUT, "WorksAt")) {
				for (Edge z : y.getEdges(Direction.OUT, "HasInstalled")) {
					if(z.getVertex(Direction.IN).getId().toString().equals(id)){
						z.setProperty("update_available", false);
						existing = true;
					}
				}
				
				if(!existing){
					Edge e = graph.addEdge(null, y, x, "HasInstalled");
					e.setProperty("update_available", false);
					e.setProperty("discarded", false);
					e.setProperty("rating", 0);
				}
			}

			graph.commit();

		}
		catch(Exception e){
			System.err.print(e);
		}
		finally {
			graph.shutdown();
		}
	}
	
	@Override
	public void rateItem(String id, String userId,double rate){
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");

		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		
		Vertex user = graph.getVertex(userId);
		Vertex item = graph.getVertex(id);
		boolean hasInstalled = false;
		boolean exist = false;
		Edge r =null;
		
		for (Vertex y : user.getVertices(Direction.OUT, "WorksAt")) {
			for (Edge z : y.getEdges(Direction.OUT, "HasInstalled")) {
				if(z.getVertex(Direction.IN).getId().toString().equals(id)){
					hasInstalled = true;
				}
			}
		}
		for(Edge e : user.getEdges(Direction.OUT, "Rated")){
			for(Edge a : item.getEdges(Direction.IN, "Rated")){
					if(e.equals(a)){
					exist = true;
					r=e;
					
					}
			}
		}
		
		if(hasInstalled){
			if(!exist){
			 r = graph.addEdge(null, user, item, "Rated");
			 }
			r.setProperty("rating", rate);
		}
		graph.commit();
		graph.shutdown();
	}
	
	@Override
	public double checkItemRate(String id){
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");

		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		
		Vertex item = graph.getVertex(id);
		double data = 0;
		double totItem =0;
		double res = 0;
		for(Edge a : item.getEdges(Direction.IN, "Rated")){
			data += (double)a.getProperty("rating");
			totItem += 1;
		}
		
		if(totItem>0){
			res = data/totItem;
		}
		item.setProperty("rate", res);
		graph.commit();
		graph.shutdown();
		return res;
	}
	
	@Override
	public List<Item> sortItem(List<Item> item){
		int n = item.size();
		Item temp =null;
		
		for(int i=0;i<n;i++){
			for(int j=1;j<(n-1);j++){
				if(item.get(j-1).getRate() < item.get(j).getRate()){
					temp = item.get(j-1);
					item.set(j-1, item.get(j));
					item.set(j, temp);
				}
			}
		}
		
		return item;
		
	}

	@Override
	public List<Item> listItemsUploaded(String userId) {
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		String uploadedBy = "";
		boolean discarded = false;
		boolean update = false;

		Vertex user = graph.getVertex(userId);

		try {
			List<Item> listItems = new ArrayList<Item>();
	
			for (Vertex x : user.getVertices(Direction.OUT, "UploadedBy")) {
				String cat = "";
				boolean installed = false;
				for (Vertex z : x.getVertices(Direction.IN, "Contains")) {
					cat += z.getProperty("title");
				}

				for (Vertex y : user.getVertices(Direction.OUT, "WorksAt")) {
					for (Vertex z : x.getVertices(Direction.IN, "HasInstalled")) {
						if(z.getId().equals(y.getId())){
							installed = true;
						}
					}
				}
				
				for (Vertex upBy : x.getVertices(Direction.IN, "UploadedBy")) {
					uploadedBy = upBy.getProperty("firstname") + " " + upBy.getProperty("lastname");
				}

				Iterable<Edge> listUpdate = x.getEdges(Direction.IN, "HasInstalled");
				for(Edge up:listUpdate){
					if((boolean)up.getProperty("discarded")){
						discarded = true;
					}
					if((boolean)up.getProperty("update_available")){
						update = true;
					}
				}
				listItems.add(new Item((ORecordId) x.getId(), (String)x.getProperty("title"), (String)x.getProperty("subtitle"), (String)x.getProperty("description"), cat, (String)x.getProperty("datapack"),uploadedBy, installed, discarded, update, (double)x.getProperty("rate")));
				discarded = false;
				update = false;
			}
			
			listItems = sortItem(listItems);
			return listItems;
		} finally {
			graph.shutdown();
		}
	}
	
	@Override
	public void addComment(String id, String userId, String comment) {
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");

		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		
		Vertex user = graph.getVertex(userId);
		Vertex item = graph.getVertex(id);
		boolean hasInstalled = false;
		Edge r =null;
		//Check whether user has install the item
		for (Vertex y : user.getVertices(Direction.OUT, "WorksAt")) {
			for (Edge z : y.getEdges(Direction.OUT, "HasInstalled")) {
				if(z.getVertex(Direction.IN).getId().toString().equals(id)){
					hasInstalled = true;
				}
			}
		}
		
		if(hasInstalled){
			 r = graph.addEdge(null, user, item, "Commented");
			 r.setProperty("comment", comment);
		}
		graph.commit();
		graph.shutdown();
	}
	
	@Override
	public List<String> listComments(String id) {
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		List<String> result = new ArrayList<String>();
		Vertex item = graph.getVertex(id);
		Vertex userTemp = null;
		String data = "";
		
		for(Edge e : item.getEdges(Direction.IN, "Commented")){
			data = "";
			userTemp = e.getVertex(Direction.OUT);
			Vertex companyTemp = null;
			for(Vertex i : userTemp.getVertices(Direction.OUT, "WorksAt")){
				companyTemp =i;
			}
			data += userTemp.getProperty("firstname").toString()+","+userTemp.getProperty("lastname").toString()+","+companyTemp.getProperty("name").toString()+","+e.getProperty("comment").toString();
			result.add(data);
		}
		
		return result;
		
	}
	
	@Override
	public void deleteComment(String id, String userId, String comment){
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		Vertex user = graph.getVertex(userId);
		Vertex item = graph.getVertex(id);
		
		for(Edge e: user.getEdges(Direction.OUT, "Commented")){
			for(Edge a:item.getEdges(Direction.IN, "Commented")){
			if(e.equals(a)&&e.getProperty("comment").toString().equals(comment)){
			graph.removeEdge(e);
				}
			}
		}
		graph.commit();
		graph.shutdown();
		
	}
	
}

class ItemAdapter implements JsonSerializer<Item> {
	@Override
	public JsonElement serialize(Item item, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", item.getId().toString());
		jsonObject.addProperty("title", item.getTitle());
		jsonObject.addProperty("subtitle", item.getSubtitle());
		jsonObject.addProperty("description", item.getDescription());
		jsonObject.addProperty("datapack", item.getDatapack());
		jsonObject.addProperty("category", item.getCategory());
		jsonObject.addProperty("uploadedBy", item.getUploadedBy());
		jsonObject.addProperty("installed", item.installed());
		jsonObject.addProperty("discarded", item.discarded());
		jsonObject.addProperty("update", item.updated());
		jsonObject.addProperty("rate", item.getRate());
		return jsonObject;
	}
}
class StringAdapter implements JsonSerializer<String> {
	@Override
	public JsonElement serialize(String str, Type typeOfSrc, JsonSerializationContext context) {
	JsonObject jsonObject = new JsonObject();
	jsonObject.addProperty("data", str);
	return jsonObject;
	}
}
