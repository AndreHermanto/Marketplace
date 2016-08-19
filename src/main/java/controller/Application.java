package controller;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.mincom.ellipse.edoi.ejb.EDOIDatabaseUtil;
import com.mincom.ellipse.edoi.ejb.EDOIFacade;
import com.mincom.ellipse.edoi.ejb.EDOIFacadeBean;
import com.mincom.ellipse.edoi.jdbc.SQLUtil;
import com.mincom.eql.dialect.DialectFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import dao.ItemDAO;
import dao.ItemDAOImpl;

@SpringBootApplication
@ComponentScan({"service","controller"})
public class Application {

	public static void main(String[] args) {
		OrientGraph graph = DBController.connect("remote:localhost/OK","admin","admin");
		
		if (graph.countVertices() == 0){
			DBController.populateDB();
		}
		
		
		SpringApplication.run(Application.class, args);
	}
	
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "ellipse";
    private static final String PROPERTY_NAME_DATABASE_URL = "jdbc:oracle:thin:@awsdevorahome.dev.mincom.com:1521/prddb01";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "el8dev";
	
    private JdbcTemplate  jdbcTemplate;

	
	@Bean
	public EDOIFacade edoi() {
		EDOIFacadeBean bean = new EDOIFacadeBean();
		SQLUtil sqlUtil = new SQLUtil(this.ellipseDataSource());
		bean.setDbUtil(new EDOIDatabaseUtil());
		bean.setJdbcTemplate(jdbc());
		bean.setSqlUtil(sqlUtil);
		return bean;
	}

	@Bean
	public DialectFactory dialect(){
		DialectFactory instance = DialectFactory.getInstance();
		
		instance.setDataSource(ellipseDataSource());
		return instance;
	}
	
	@Bean
	public ItemDAO itemDAO(){ 
		return new ItemDAOImpl();
	}
	
	@Bean
	public JdbcOperations jdbc() {
		this.jdbcTemplate = new JdbcTemplate();
		this.jdbcTemplate.setDataSource(ellipseDataSource());
		JdbcOperations operations = this.jdbcTemplate;
		return operations;
	}
	
	@Bean
	public DataSource ellipseDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        dataSource.setDriverClassName(PROPERTY_NAME_DATABASE_DRIVER);
        dataSource.setUrl(PROPERTY_NAME_DATABASE_URL);
        dataSource.setUsername(PROPERTY_NAME_DATABASE_USERNAME);
        dataSource.setPassword(PROPERTY_NAME_DATABASE_PASSWORD);
         
        return dataSource;
	}
}