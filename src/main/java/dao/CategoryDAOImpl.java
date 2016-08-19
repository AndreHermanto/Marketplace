package dao;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.orientechnologies.orient.core.id.ORecordId;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import controller.DBController;
import model.Category;
import test.Testing;

public class CategoryDAOImpl implements CategoryDAO {

	Boolean test = Testing.flag;

	@Override
	public void createCategory(String title) {
		OrientGraph graph;
		if(!test){
		 graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
		 graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try{
			graph.addVertex("class:Category", "title", title);
			graph.commit();
		} catch( Exception e ) {
			graph.rollback();
		} finally {
			graph.shutdown();
			}
		
	}

	@Override
	public void updateCategory(String id, String title) {
		OrientGraph graph;
		if(!test){
		 graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
		 graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try {
			Vertex x = graph.getVertex(id);
			x.setProperty("title", title);
			graph.commit();
		} finally {
			graph.shutdown();
		}
	}

	@Override
	public void deleteCategory(String id) {
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
	public List<Category> listCategories() {
		OrientGraph graph;
		if(!test){
		 graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
		 graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try {
			List<Category> listCategories = new ArrayList<Category>() ;
			for (Vertex x : graph.getVerticesOfClass("Category")) {
				System.out.println(x);
				listCategories.add(new Category((ORecordId) x.getId(), (String)x.getProperty("title")));
		    }
			
			return listCategories;
		} finally {
			graph.shutdown();
		}
	}

	@Override
	public Gson getAdapter(){
		GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Category.class, new CategoryAdapter());
        return gsonBuilder.create();
	}
}

class CategoryAdapter implements JsonSerializer<Category> {

	@Override
	public JsonElement serialize(Category category, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", category.getId().toString());
        jsonObject.addProperty("title", category.getTitle());
		return jsonObject;
	}
}
