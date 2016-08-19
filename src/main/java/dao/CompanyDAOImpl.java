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
import test.Testing;
import model.Company;

public class CompanyDAOImpl implements CompanyDAO {
	Boolean test=Testing.flag;
	@Override
	public void createCompany(String name,String description) {
		OrientGraph graph;
		if(!test){
		 graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
		 graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try{
			graph.addVertex("class:Company", "name", name, "description", description);
			graph.commit();
		} catch( Exception e ) {
			graph.rollback();
		} finally {
			graph.shutdown();
		}
	}

	@Override
	public void updateCompany(String id, String name, String description) {
		OrientGraph graph;
		if(!test){
		 graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
		 graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try {
			Vertex x = graph.getVertex(id);
			x.setProperty("name", name);
			x.setProperty("description", description);
			graph.commit();
		} finally {
			graph.shutdown();
		}
	}

	@Override
	public void deleteCompany(String id) {
		OrientGraph graph;
		if(!test){
		 graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
		 graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try {
			Vertex x = graph.getVertex(id);
			graph.removeVertex(x);
			graph.commit();
		} finally {
			graph.shutdown();
		}
	}

	@Override
	public List<Company> listCompanies() {
		OrientGraph graph;
		if(!test){
		 graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
		 graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try {
			List<Company> listCompanies = new ArrayList<Company>() ;
			for (Vertex x : graph.getVerticesOfClass("Company")) {
				listCompanies.add(new Company((ORecordId) x.getId(), (String)x.getProperty("name"), (String)x.getProperty("description")));
		    }
			return listCompanies;
		} finally {
			graph.shutdown();
		}
	}

	@Override
	public Company findCompanyByName(String name) {
		OrientGraph graph;
		if(!test){
		 graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
		 graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try {
			for (Vertex x : graph.getVertices("Company.name", name)) {
				return new Company((ORecordId) x.getId(), (String)x.getProperty("name"), (String)x.getProperty("description"));
		    }
		} finally {
			graph.shutdown();
		}
		return null;
	}

	@Override
	public Gson getAdapter(){
		GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Company.class, new CompanyAdapter());
        return gsonBuilder.create();
	}
}

class CompanyAdapter implements JsonSerializer<Company> {

	@Override
	public JsonElement serialize(Company company, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", company.getId().toString());
        jsonObject.addProperty("name", company.getName());
        jsonObject.addProperty("description", company.getDescription());
		return jsonObject;
	}
}
