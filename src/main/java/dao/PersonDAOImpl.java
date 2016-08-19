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
import model.Person;
import test.Testing;

public class PersonDAOImpl implements PersonDAO {
	Boolean test=Testing.flag;
	@Override
	public String createPerson(String firstname, String lastname, String companyId){
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		Vertex user = null;
		try{
			user = graph.addVertex("class:Person", "firstname", firstname, "lastname", lastname);

			if(companyId != null) {
				graph.addEdge(null, user, graph.getVertex(companyId), "WorksAt");
			}

			graph.commit();
		} catch( Exception e ) {
			graph.rollback();
		} finally {
			graph.shutdown();
		}
		return user.getId().toString();
	}

	@Override
	public void updatePerson(String id, String firstname, String lastname) {
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try {
			Vertex x = graph.getVertex(id);
			x.setProperty("firstname", firstname);
			x.setProperty("lastname", lastname);
			graph.commit();
		} finally {
			graph.shutdown();
		}
	}

	@Override
	public void deletePerson(String id){
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try{
			Vertex x = graph.getVertex(id);
			graph.removeVertex(x);
			graph.commit();
		} catch( Exception e ) {
			graph.rollback();
		}	
	}

	@Override
	public String connectUser(String firstname, String lastname){
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try{
			for (Vertex x : graph.getVerticesOfClass("Person")) {
				if(x.getProperty("firstname").toString().equals(firstname) && x.getProperty("lastname").toString().equals(lastname)){

					return x.getId().toString();
				}
			}
		} catch( Exception e ) {
			graph.rollback();
		}
		return null;	
	}

	@Override
	public List<Person> listPeople() {
		OrientGraph graph;
		if(!test){
			graph = DBController.connect("remote:localhost/OK","admin","admin");
		}
		else{
			graph = DBController.connect("remote:localhost/test","admin","admin");
		}
		try {
			List<Person> listPeople = new ArrayList<Person>() ;
			for (Vertex x : graph.getVerticesOfClass("Person")) {
				listPeople.add(new Person((ORecordId) x.getId(), (String)x.getProperty("firstname"), (String)x.getProperty("lastname")));
			}
			return listPeople;
		} finally {
			graph.shutdown();
		}
	}

	@Override
	public Gson getAdapter(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Person.class, new PersonAdapter());
		return gsonBuilder.create();
	}
}

class PersonAdapter implements JsonSerializer<Person> {

	@Override
	public JsonElement serialize(Person person, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", person.getId().toString());
		jsonObject.addProperty("firstname", person.getFirstname());
		jsonObject.addProperty("lastname", person.getLastname());
		return jsonObject;
	}
}
