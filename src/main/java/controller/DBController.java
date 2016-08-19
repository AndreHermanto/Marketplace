package controller;

import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class DBController {
	

	private static OrientGraph graph = null;


	public static OrientGraph connect(String url,String user,String password){
		OrientGraphFactory factory = new OrientGraphFactory(url, user, password).setupPool(1, 10);
		graph = factory.getTx();

		return graph;
	}
	
	@SuppressWarnings("unchecked")
	public static void populateDB(){
		String datapack ="";
		
		try {
			String filePath = new File("").getAbsolutePath();
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(filePath + "/src/main/java/service/mse025-export.json"));

			JSONArray jsonObject = (JSONArray) obj;
			datapack = jsonObject.toString();

		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		graph.createVertexType("Person");
		graph.createVertexType("Company");
		graph.createVertexType("Item");
		graph.createVertexType("Category");
		
		graph.getVertexType("Category").createProperty("title",OType.STRING);
		
		graph.getVertexType("Item").createProperty("title", OType.STRING);
		graph.getVertexType("Item").createProperty("subtitle", OType.STRING);
		graph.getVertexType("Item").createProperty("description", OType.STRING);
		graph.getVertexType("Item").createProperty("category", OType.STRING);
		graph.getVertexType("Item").createProperty("datapack", OType.STRING);
		graph.getVertexType("Item").createProperty("rate", OType.DOUBLE);
		
		graph.createKeyIndex("title", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Category"));
		graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Person"));
		graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Company"));
		
		graph.createEdgeType("HasInstalled");
		graph.createEdgeType("Rated");
		graph.createEdgeType("Commented");
		graph.createEdgeType("UploadedBy");
		
		graph.getEdgeType("HasInstalled").createProperty("update_available", OType.BOOLEAN);
		graph.getEdgeType("HasInstalled").createProperty("discarded", OType.BOOLEAN);
		graph.getEdgeType("Rated").createProperty("rating", OType.DOUBLE);
		graph.getEdgeType("Commented").createProperty("comment", OType.STRING);

		Vertex michael = graph.addVertex("class:Person", "name", "Michael Dorkhom", "firstname", "Michael", "lastname", "Dorkhom");
		Vertex andre = graph.addVertex("class:Person", "name", "Andre Hermanto", "firstname", "Andre", "lastname", "Hermanto");
		Vertex steven = graph.addVertex("class:Person", "name", "Steven Rowlands", "firstname", "Steven", "lastname", "Rowlands");
		
		Vertex CATERPILLAR = graph.addVertex("class:Company", "name", "Caterpillar");
		Vertex ABB = graph.addVertex("class:Company", "name", "ABB");
		Vertex SAP = graph.addVertex("class:Company", "name", "SAP");
		Vertex GOOGLE = graph.addVertex("class:Company", "name", "Google");
		
		Vertex BORALDP = graph.addVertex("class:Item", "title", "Boral Datapack", "subtitle", "This datapack contains all asset information related to Boral products", "description", "Test entry", "datapack", datapack, "installed", false, "rate", 0);
		Vertex HANSONDP = graph.addVertex("class:Item", "title", "Hanson Datapack", "subtitle", "Hanson's main datapack on the Ellipse Marketplace", "description", "Test entry", "datapack", datapack, "installed", false, "rate", 0);
		Vertex VULCANDP = graph.addVertex("class:Item", "title", "Vulcan Datapack", "subtitle", "Provides the information you need when purchasing Vulcan assets", "description", "Test entry", "datapack", datapack, "installed", false, "rate", 0);
		Vertex PARCHEMDP = graph.addVertex("class:Item", "title", "Parchem Datapack", "subtitle", "Parchem's datapck for the Ellipse software", "description", "Test entry", "datapack", datapack, "installed", false, "rate", 0);
		Vertex ABBDP = graph.addVertex("class:Item", "title", "ABB Datapack", "subtitle", "Datapack containing all info related to ABB assets", "description", "test entry", "datapack","", "installed", false, "rate", 0);
			
		Vertex BP1 = graph.addVertex("class:Item", "title", "Google's Business Process", "subtitle", "Optimal business process", "description", "First Item uploaded by Google", "datapack","", "installed", false, "rate", 0);
		Vertex BP2 = graph.addVertex("class:Item", "title", "IBM's Business Process", "subtitle", "Process used for IBM's transactions", "description", "First Item uploaded by IBM", "datapack","", "installed", false, "rate", 0);
		Vertex BP3 = graph.addVertex("class:Item", "title", "IKEA's Business Process", "subtitle", "The iterative process developped by IKEA", "description", "First Item uploaded by IKEA", "datapack","", "installed", false, "rate", 0);
		Vertex BP4 = graph.addVertex("class:Item", "title", "Telstra's Business Process", "subtitle", "Update of the initial version", "description", "First Item uploaded by Telstra", "datapack","", "installed", false, "rate", 0);
		Vertex BP5 = graph.addVertex("class:Item", "title", "Suncorp's Business Process", "subtitle", "Suncorp's major business process", "description", "First Item uploaded by Suncorp", "datapack","", "installed", false, "rate", 0);

		Vertex DATAPACKCAT = graph.addVertex("class:Category", "title", "Datapack");
		Vertex BUSINESSPROCESSCAT = graph.addVertex("class:Category", "title", "Business Process");
		
		graph.addEdge(null, michael, ABB, "WorksAt");
		graph.addEdge(null, andre, ABB, "WorksAt");
		graph.addEdge(null, steven, ABB, "WorksAt");
		
		graph.addEdge(null, DATAPACKCAT, ABBDP, "Contains");
		graph.addEdge(null, DATAPACKCAT, BORALDP, "Contains");
		graph.addEdge(null, DATAPACKCAT, HANSONDP, "Contains");
		graph.addEdge(null, DATAPACKCAT, VULCANDP, "Contains");
		graph.addEdge(null, DATAPACKCAT, PARCHEMDP, "Contains");
		
		graph.addEdge(null, BUSINESSPROCESSCAT, BP1, "Contains");
		graph.addEdge(null, BUSINESSPROCESSCAT, BP2, "Contains");
		graph.addEdge(null, BUSINESSPROCESSCAT, BP3, "Contains");
		graph.addEdge(null, BUSINESSPROCESSCAT, BP4, "Contains");
		graph.addEdge(null, BUSINESSPROCESSCAT, BP5, "Contains");
		
		graph.commit();
	}
}
