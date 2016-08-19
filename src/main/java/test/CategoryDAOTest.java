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
import dao.CategoryDAOImpl;
import model.Category;

public class CategoryDAOTest {
	
	OrientGraph graph=DBController.connect("remote:localhost/test","admin","admin");
	
	public CategoryDAOTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testListCategories(){		
		CategoryDAOImpl tester = new CategoryDAOImpl();	
		for(Vertex t : graph.getVertices()){
			t.remove();
		}
				
		Vertex t1 = graph.addVertex("class:Category","title","test");
		Vertex t2 = graph.addVertex("class:Category","title","test2");
		Vertex t3 = graph.addVertex("class:Category","title","test3");
		
		graph.commit();
		List<String> titles = new ArrayList<String>();
		
		titles.add("test");
		titles.add("test2");
		titles.add("test3");

		List<Category> list = tester.listCategories();
		
		for (Category c : list){
			assertTrue("Test if the id is contained in the created vertices", titles.contains(c.getTitle()));
		}
		
		graph.removeVertex(t1);
		graph.removeVertex(t2);
		graph.removeVertex(t3);
		
		graph.commit();
		graph.shutdown();
	}
	
	@Test
	public void testCreateCategory(){	
		
		CategoryDAOImpl tester = new CategoryDAOImpl();
		tester.createCategory("test purpose");
		
		Vertex testVertex = null;
		
		for (Vertex x : graph.getVerticesOfClass("Category")) {
			if(x.getProperty("title").equals("test purpose")){
				testVertex = x;
				x.remove();
			}
	    }

		graph.commit();
		graph.shutdown();
		
		assertNotNull("Test to verify if the object exists", testVertex);
	
	}
	
	@Test
	public void testDeleteCategory(){		
		graph.addVertex("class:Category","title","test");
		graph.commit();
		
		Vertex testVertex = null;
		
		for (Vertex x : graph.getVerticesOfClass("Category")) {
			if(x.getProperty("title").equals("test")){
				testVertex = x;
				x.remove();
				graph.commit();
			}
	    }
		
		assertNull("Test if the object has been deleted", graph.getVertex(testVertex.getId()));
		graph.shutdown();
	}
	
	@Test
	public void testUpdateCategory(){
		CategoryDAOImpl tester = new CategoryDAOImpl();
		graph.addVertex("class:Category","title","Test");
		graph.commit();
		
		Vertex testVertex = null;
		Vertex updated = null;
		
		for (Vertex x : graph.getVerticesOfClass("Category")) {
			if(x.getProperty("title").equals("Test")){
				testVertex = x;
				tester.updateCategory(x.getId().toString(), "Modified Item");
				graph.commit();
			}
	    }
		
		for (Vertex x : graph.getVerticesOfClass("Category")) {
			if(x.getProperty("title").equals("Modified Item")){
				updated = x;
				x.remove();
			}
	    }
		
		graph.commit();
		graph.shutdown();
		
		assertEquals("Check if it is same object", updated.getId(), testVertex.getId());
		assertNotEquals("Check if the content is updated", testVertex.getProperty("title"), updated.getProperty("title"));

	}
}
	

