package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import controller.DBController;
import dao.ItemDAO;
import dao.ItemDAOImpl;
import model.Item;

public class ItemDAOTest {
	
	OrientGraph graph=DBController.connect("remote:localhost/test","admin","admin");
	
	public ItemDAOTest() {
		
	}
	
	@Test
	public void testCreateItem(){		
		ItemDAOImpl tester = new ItemDAOImpl();
		boolean check = true;
		for(Vertex a : graph.getVerticesOfClass("Category")){
			if(a.getProperty("title").toString().equals("datapack")){
				check =false;
			}
		}
		if(check){
		graph.commit();}
		Vertex TESTUSER = graph.addVertex("class:Person", "firstname", "Test", "lastname", "User");
		tester.createItem((String)TESTUSER.getId(), "test", "test", "test", "datapack", "a");

		Vertex testVertex = null;
		
		for (Vertex x : graph.getVerticesOfClass("Item")) {
			if(x.getProperty("title").equals("test") && x.getProperty("subtitle").equals("test")){
				testVertex = x;				
				graph.removeVertex(x);
				graph.commit();
			}
	    }

		graph.shutdown();
		
		assertNotNull("Test to verify if the object exists", testVertex);
	}
	
	@Test
	public void testUpdateItem(){
//		ItemDAO tester = new ItemDAOImpl();
//		tester.createItem("Test Item", "Created for test purpose", "This item is not real", 2, "datapack", "a");
//		Vertex item = null;
//		for(Vertex a : graph.getVerticesOfClass("Item")){
//			if(a.getProperty("title").toString().equals("Test Item")){
//				item = a;
//			}
//		}
//		Vertex testVertex = null;
//		Vertex updated = null;
//		
//		graph.commit();
//		
//		for (Vertex x : graph.getVerticesOfClass("Item")) {
//			if(x.getProperty("title").equals("Test Item") && x.getProperty("subtitle").equals("Created for test purpose") && x.getProperty("description").equals("This item is not real") &&
//					(long)x.getProperty("size") == 2 && x.getProperty("datapack").equals("a") ){
//				System.out.println("check");
//				testVertex = x;
//				tester.updateItem(x.getId().toString(), "Modified Item", "Created for test purpose", "This item is not real", 2, "datapack", "a", true);
//				
//			}
//	    }
//		
//		for (Vertex x : graph.getVerticesOfClass("Item")) {
//			if(x.getProperty("title").equals("Modified Item") && x.getProperty("subtitle").equals("Created for test purpose") && x.getProperty("description").equals("This item is not real") &&
//					(long)x.getProperty("size") == 2 && x.getProperty("datapack").equals("a")){
//				System.out.println("test");
//				updated = x;
//				x.remove();
//			}
//	    }
//		
//		graph.commit();
//		graph.shutdown();
//		
//		assertEquals("Check if it refers to the same object", testVertex.getId().toString(), updated.getId().toString());
//		assertNotEquals("Check if the content is updated", testVertex.getProperty("title"), updated.getProperty("title"));
	}
	
	@Test
	public void testDeleteItem(){
		ItemDAO tester = new ItemDAOImpl();
	
		
		graph.addVertex("class:Item", "title", "Test Item", "subtitle", "Created for test purpose", "description", "This item is not real", "datapack", "test", "installed", false);
		graph.commit();
		
		Vertex testVertex = null;
		
		for (Vertex x : graph.getVerticesOfClass("Item")) {
			if(x.getProperty("title").equals("Test Item") && x.getProperty("subtitle").equals("Created for test purpose") && x.getProperty("description").equals("This item is not real") &&
					 x.getProperty("datapack").equals("test") ){
				System.out.println("asdasd");
				tester.deleteItem(x.getId().toString());
				testVertex = x;
			}
	    }
		
		assertNull("Test if the object has been deleted", graph.getVertex(testVertex.getId()));
		graph.shutdown();
	}
	
	@Test
	public void testInstallItem(){
		ItemDAO tester = new ItemDAOImpl();	
		
		Vertex TESTUSER = graph.addVertex("class:Person", "firstname", "Test", "lastname", "User");
		Vertex CATERPILLARDP = graph.addVertex("class:Item", "title", "Caterpilar Datapack", "subtitle", "First Datapack on the market", "description", "Test entry", "datapack", "test", "installed", false);
		graph.commit();

		tester.installItem(CATERPILLARDP.getId().toString(),TESTUSER.getId().toString());
		
		for(Edge edge : CATERPILLARDP.getEdges(Direction.IN, "HasInstalled")){
			assertTrue("Test if the user has installed the item", edge.getVertex(Direction.OUT).equals(TESTUSER));
		}
		
		graph.removeVertex(TESTUSER);
		graph.removeVertex(CATERPILLARDP);
		graph.commit();
		graph.shutdown();
	}

	@Test
	public void testListItems(){
		ItemDAO tester = new ItemDAOImpl();		
		
		Vertex TESTUSER = graph.addVertex("class:Person", "firstname", "Test", "lastname", "User");
		
		Vertex CATERPILLARDP = graph.addVertex("class:Item", "title", "Caterpilar Datapack", "subtitle", "First Datapack on the market", "description", "Test entry", "datapack", "test", "installed", false,"discarded", false);
		Vertex BORALDP = graph.addVertex("class:Item", "title", "Boral Datapack", "subtitle", "This datapack contains all asset information related to Boral products", "description", "Test entry", "datapack", "test", "installed", false,"discarded", false);
		Vertex HANSONDP = graph.addVertex("class:Item", "title", "Hanson Datapack", "subtitle", "Hanson's main datapack on the Ellipse Marketplace", "description", "Test entry", "datapack", "test", "installed", false,"discarded", false);
		

		Edge x = graph.addEdge(null, TESTUSER, CATERPILLARDP, "HasInstalled");
		Edge y =graph.addEdge(null, TESTUSER, BORALDP, "HasInstalled");
		Edge z =graph.addEdge(null, TESTUSER, HANSONDP, "HasInstalled");

		x.setProperty("discarded", false);
		y.setProperty("discarded", false);
		z.setProperty("discarded", false);
		graph.commit();
		
		List<String> titles = new ArrayList<String>();
		
		titles.add("Caterpilar Datapack");
		titles.add("Boral Datapack");
		titles.add("Hanson Datapack");
		List<Item> list = tester.listItems(TESTUSER.getId().toString());
		
		for (Item item : list){
			assertTrue("Test if the id is contained in the created vertices", titles.contains(item.getTitle()));
		}

		graph.removeVertex(TESTUSER);
		graph.removeVertex(CATERPILLARDP);
		graph.removeVertex(BORALDP);
		graph.removeVertex(HANSONDP);
		
		graph.commit();
		graph.shutdown();
	}
	
	@Test
	public void testListItemsByCategory(){
		ItemDAO tester = new ItemDAOImpl();		
		
		Vertex TESTUSER = graph.addVertex("class:Person", "firstname", "Test", "lastname", "User");
		
		Vertex DATAPACKCAT = graph.addVertex("class:Category", "title", "Datapack");
		
		Vertex CATERPILLARDP = graph.addVertex("class:Item", "title", "Caterpilar Datapack", "subtitle", "First Datapack on the market", "description", "Test entry", "datapack", "test", "installed", false,"discarded", false);
		Vertex BORALDP = graph.addVertex("class:Item", "title", "Boral Datapack", "subtitle", "This datapack contains all asset information related to Boral products", "description", "Test entry", "datapack", "test", "installed", false,"discarded", false);
		Vertex HANSONDP = graph.addVertex("class:Item", "title", "Hanson Datapack", "subtitle", "Hanson's main datapack on the Ellipse Marketplace", "description", "Test entry", "datapack", "test", "installed", false,"discarded", false);
		
		graph.addEdge(null, DATAPACKCAT, CATERPILLARDP, "Contains");
		graph.addEdge(null, DATAPACKCAT, BORALDP, "Contains");
		graph.addEdge(null, DATAPACKCAT, HANSONDP, "Contains");
		
		graph.commit();

		
		List<String> titles = new ArrayList<String>();
		
		titles.add("Caterpilar Datapack");
		titles.add("Boral Datapack");
		titles.add("Hanson Datapack");
		
		List<Item> list = tester.listItemsByCategory((String)TESTUSER.getId(), "Datapack");
		
		for (Item item : list){
			assertTrue("Test if the id is contained in the created vertices", titles.contains(item.getTitle()));
		}
		
		graph.removeVertex(DATAPACKCAT);
		graph.removeVertex(CATERPILLARDP);
		graph.removeVertex(BORALDP);
		graph.removeVertex(HANSONDP);
		
		graph.commit();
		graph.shutdown();
	}
	
	@Test
	public void testListItemsUploaded(){
		ItemDAO tester = new ItemDAOImpl();	
		
		Vertex TESTUSER = graph.addVertex("class:Person", "firstname", "Test", "lastname", "User");
		Vertex CATERPILLARDP = graph.addVertex("class:Item", "title", "Caterpilar Datapack", "subtitle", "First Datapack on the market", "description", "Test entry", "datapack", "test", "installed", false,"discarded", false);
		Vertex BORALDP = graph.addVertex("class:Item", "title", "Boral Datapack", "subtitle", "This datapack contains all asset information related to Boral products", "description", "Test entry", "datapack", "test", "installed", false,"discarded", false);

		graph.addEdge(null, TESTUSER, CATERPILLARDP, "UploadedBy");
		graph.addEdge(null, TESTUSER, BORALDP, "UploadedBy");
		
		int uploadedItems = 2;
		
		graph.commit();
		
		List<Item> uploaded = tester.listItemsUploaded((String)TESTUSER.getId());
		
		assertTrue("Test if the number of uploaded items retrived from the database matches the number of item updated", uploaded.size() == uploadedItems);
		
	}
	
	@Test
	public void testDiscardNotification() {
		ItemDAO tester = new ItemDAOImpl();	
		
		Vertex TESTUSER = graph.addVertex("class:Person", "firstname", "Test", "lastname", "User");
		Vertex CATERPILLARDP = graph.addVertex("class:Item", "title", "Caterpilar Datapack", "subtitle", "First Datapack on the market", "description", "Test entry", "datapack", "test", "installed", false,"discarded", false);
		
		graph.addEdge(null, TESTUSER, CATERPILLARDP, "HasInstalled");
		
		graph.commit();
		
		boolean existing = false;
		
		for (Edge z : TESTUSER.getEdges(Direction.OUT, "HasInstalled")) {
			if(z.getVertex(Direction.IN).getId().toString().equals((String)TESTUSER.getId())){
				existing = true;
				tester.updateItem((String)CATERPILLARDP.getId(), "TestTitle", "TestSubtitle", "TestDescription", "datapack", null, true);
			}
		}
		
		assertTrue("Test if the item has been installed", existing);
		
		tester.discardNotification((String)CATERPILLARDP.getId(), (String)TESTUSER.getId());
		
		for (Edge z : TESTUSER.getEdges(Direction.OUT, "HasInstalled")) {
			if(z.getVertex(Direction.IN).getId().toString().equals((String)TESTUSER.getId())){
				assertTrue("Test if the notification has been discarded", z.getProperty("discarded"));
			}
		}
		
		graph.removeVertex(TESTUSER);
		graph.removeVertex(CATERPILLARDP);
		
		graph.commit();
		graph.shutdown();
	}
	
	@Test
	public void testCheckUpdates() {
		ItemDAO tester = new ItemDAOImpl();	
		
		Vertex TESTUSER = graph.addVertex("class:Person", "firstname", "Test", "lastname", "User");
		Vertex CATERPILLARDP = graph.addVertex("class:Item", "title", "Caterpilar Datapack", "subtitle", "First Datapack on the market", "description", "Test entry", "datapack", "test", "installed", false,"discarded", false);
		Vertex BORALDP = graph.addVertex("class:Item", "title", "Boral Datapack", "subtitle", "This datapack contains all asset information related to Boral products", "description", "Test entry", "datapack", "test", "installed", false,"discarded", false);

		graph.addEdge(null, TESTUSER, CATERPILLARDP, "HasInstalled");
		graph.addEdge(null, TESTUSER, BORALDP, "HasInstalled");
		
		int numberItems = 2;
		
		graph.commit();
		
		tester.updateItem((String)CATERPILLARDP.getId(), "Cat. equipment datapack", "First Datapack on the market", "Test entry", "datapack", "test", true);
		tester.updateItem((String)BORALDP.getId(), "TestTitle", "TestSubtitle", "TestDescription", "datapack", "test", true);
		
		List<Item> updates = tester.checkUpdates((String)TESTUSER.getId());
		
		assertTrue("Test if the number of updated items matches the number of item updated", updates.size() == numberItems);
	}
	
	@Test
	public void testRateItem(){
		ItemDAO tester = new ItemDAOImpl();	
		
		Vertex TESTUSER = graph.addVertex("class:Person", "firstname", "Test", "lastname", "User");
		Vertex TESTUSER2 = graph.addVertex("class:Person", "firstname", "Test2", "lastname", "User2");
		Vertex CATERPILLARDP = graph.addVertex("class:Item", "title", "Caterpilar Datapack", "subtitle", "First Datapack on the market", "description", "Test entry", "datapack", "test", "installed", false,"discarded", false);
		
		graph.addEdge(null, TESTUSER, CATERPILLARDP, "HasInstalled");
		graph.addEdge(null, TESTUSER2, CATERPILLARDP, "HasInstalled");
		
		graph.commit();
		
		tester.rateItem((String)CATERPILLARDP.getId(), (String)TESTUSER.getId(), 5);
		graph.commit();
		
		assertTrue("Test if the rate is equal to 5", (int)graph.getVertex(CATERPILLARDP.getId()).getProperty("rate") == 5);
		
		tester.rateItem((String)CATERPILLARDP.getId(), (String)TESTUSER2.getId(), 3);
		graph.commit();
		
		assertTrue("Test if the rate is equal to 3, average computed", (int)graph.getVertex(CATERPILLARDP.getId()).getProperty("rate") == 3);
		
		graph.removeVertex(TESTUSER);
		graph.removeVertex(TESTUSER2);
		graph.removeVertex(CATERPILLARDP);
		
		graph.commit();
		graph.shutdown();
	}
}
