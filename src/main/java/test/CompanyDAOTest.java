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
import dao.CompanyDAOImpl;
import model.Company;


public class CompanyDAOTest {
	OrientGraph graph=DBController.connect("remote:localhost/test","admin","admin");
	
	public CompanyDAOTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testCreateCompany(){
		CompanyDAOImpl tester = new CompanyDAOImpl();
		tester.createCompany("test","test purpose");
		
		Vertex testVertex = null;
		
		for (Vertex x : graph.getVerticesOfClass("Company")) {
			if(x.getProperty("name").equals("test") && x.getProperty("description").equals("test purpose")){
				testVertex = x;
				x.remove();
			}
	    }

		graph.commit();
		graph.shutdown();
		
		assertNotNull("Test to verify if the object exists", testVertex);
	}
	
	@Test
	public void testListCompanies(){
		CompanyDAOImpl tester = new CompanyDAOImpl();	
		
		Vertex t1 = graph.addVertex("class:Company","name","test","description","test purposes");
		Vertex t2 = graph.addVertex("class:Company","name","test2","description","test purposes");
		Vertex t3 = graph.addVertex("class:Company","name","test3","description","test purposes");
		
		graph.commit();
		List<String> titles = new ArrayList<String>();
		
		titles.add("test");
		titles.add("test2");
		titles.add("test3");

		List<Company> list = tester.listCompanies();
		
		for (Company c : list){
			assertTrue("Test if the id is contained in the created vertices", titles.contains(c.getName()));
		}
		
		graph.removeVertex(t1);
		graph.removeVertex(t2);
		graph.removeVertex(t3);
		
		graph.commit();
		graph.shutdown();
	}
	
	@Test
	public void testDeleteCompany(){
		graph.addVertex("class:Company", "name", "test","description","test purpose");
		graph.commit();
		
		Vertex testVertex = null;
		
		for (Vertex x : graph.getVerticesOfClass("Company")) {
			if(x.getProperty("name").equals("test")){
				testVertex = x;
				x.remove();
				graph.commit();
			}
	    }
		
		assertNull("Test if the object has been deleted", graph.getVertex(testVertex.getId()));
		graph.shutdown();
	}
	
	@Test
	public void testUpdateCompany(){
		CompanyDAOImpl tester = new CompanyDAOImpl();
		graph.addVertex("class:Company", "name", "test update","description","test purpose");
		graph.commit();
		
		Vertex testVertex = null;
		Vertex updated = null;
		
		for (Vertex x : graph.getVerticesOfClass("Company")) {
			if(x.getProperty("name").equals("test update")){
				testVertex = x;
				tester.updateCompany(x.getId().toString(), "Modified Item", "test only");
				graph.commit();
			}
	    }
		
		for (Vertex x : graph.getVerticesOfClass("Company")) {
			if(x.getProperty("name").equals("Modified Item")){
				updated = x;
				x.remove();
			}
	    }
		
		graph.commit();
		graph.shutdown();
		assertEquals("Check if it is same object", updated.getId(), testVertex.getId());
		assertNotEquals("Check if the content is updated", testVertex.getProperty("name"), updated.getProperty("name"));
		assertNotEquals("Check if the content is updated", testVertex.getProperty("description"), updated.getProperty("description"));
	
	}
	
	@Test
	public void testFindCompanyByName(){
		Vertex x = graph.addVertex("class:Company", "name", "test","description","test purpose");
		graph.commit();
		CompanyDAOImpl tester = new CompanyDAOImpl();
		Company tests = tester.findCompanyByName("test");
		assertEquals("Test Search","test" ,tests.getName());
		assertEquals("Test Search", "test purpose", tests.getDescription());
		graph.removeVertex(x);
		graph.commit();
		graph.shutdown();
	}

}
