package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import controller.DBController;
import dao.PersonDAOImpl;
import model.Person;

public class PersonDAOTest {
	
	OrientGraph graph=DBController.connect("remote:localhost/test","admin","admin");
	
	public PersonDAOTest() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testCreatePerson(){
		PersonDAOImpl tester = new PersonDAOImpl();
		tester.createPerson("Test", "tester", null);
		
		Vertex testVertex = null;
		
		for (Vertex x : graph.getVerticesOfClass("Person")) {
			if(x.getProperty("firstname").equals("Test") && x.getProperty("lastname").equals("tester")){
				testVertex = x;
				x.remove();
			}
	    }

		graph.commit();
		graph.shutdown();
		
		assertNotNull("Test to verify if the object exists", testVertex);
	}
	
	@Test
	public void testUpdatePerson(){
		PersonDAOImpl tester = new PersonDAOImpl();
		graph.addVertex("class:Person", "firstname", "test purpose","lastname","test","notify",false);
		graph.commit();
		
		Vertex testVertex = null;
		Vertex updated = null;
		
		for (Vertex x : graph.getVerticesOfClass("Person")) {
			if(x.getProperty("firstname").equals("test purpose") && x.getProperty("lastname").equals("test")){
				testVertex = x;
				tester.updatePerson(x.getId().toString(), "test update", "updated");
				graph.commit();
			}
	    }
		
		for (Vertex x : graph.getVerticesOfClass("Person")) {
			if(x.getProperty("firstname").equals("test update")  && x.getProperty("lastname").equals("updated")){
				updated = x;
				x.remove();
			}
	    }
		
		graph.commit();
		graph.shutdown();
		
		//assertSame("Matching objects", updated, testVertex);
		assertEquals("Check if it is same object", updated.getId(), testVertex.getId());
		assertNotEquals("Check if the content is updated", testVertex.getProperty("firstname"), updated.getProperty("firstname"));
		assertNotEquals("Check if the content is updated", testVertex.getProperty("lastname"), updated.getProperty("lastname"));
			
	}
	
	@Test
	public void testDeletePerson(){
		graph.addVertex("class:Person", "firstname", "test purpose","lastname","test","notify",false);
		graph.commit();
		
		Vertex testVertex = null;
		
		for (Vertex x : graph.getVerticesOfClass("Person")) {
			if(x.getProperty("firstname").equals("test purpose")&&x.getProperty("lastname").equals("test")){
				testVertex = x;
				x.remove();
				graph.commit();
			}
	    }
		
		assertNull("Test if the object has been deleted", graph.getVertex(testVertex.getId()));
		graph.shutdown();
	}
	
	@Test
	public void testConnectUser(){
		PersonDAOImpl tester = new PersonDAOImpl();
		Vertex x= graph.addVertex("class:Person", "firstname", "test purpose","lastname","test","notify",false);
		graph.commit();
		assertEquals("Check connect to correct user",x.getId().toString() , tester.connectUser("test purpose", "test"));
		graph.removeVertex(x);
		graph.commit();
		graph.shutdown();
	}
	
	@Test
	public void testListPeople(){		
		PersonDAOImpl tester = new PersonDAOImpl();	
		
		Vertex t1 = graph.addVertex("class:Person", "firstname", "test","lastname","test","notify",false);
		Vertex t2 = graph.addVertex("class:Person", "firstname", "test2","lastname","test","notify",false);
		Vertex t3 = graph.addVertex("class:Person", "firstname", "test3","lastname","test","notify",false);
		
		graph.commit();
		List<String> titles = new ArrayList<String>();
		
		titles.add("test");
		titles.add("test2");
		titles.add("test3");

		List<Person> list = tester.listPeople();
		
		for (Person c : list){
			assertTrue("Test if the id is contained in the created vertices", titles.contains(c.getFirstname()));
		}
		
		graph.removeVertex(t1);
		graph.removeVertex(t2);
		graph.removeVertex(t3);
		
		graph.commit();
		graph.shutdown();
	}
	
}

