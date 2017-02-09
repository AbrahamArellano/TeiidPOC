package javaexecutionteiid;

import static de.redhat.poc.core.jdbc.JDBCUtils.executeObject;

import java.sql.Connection;
import java.util.List;

import org.junit.Test;
import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.runtime.EmbeddedServer;
import org.teiid.translator.ExecutionFactory;

import de.redhat.poc.core.jdbc.TeiidEmbeddedPortfolioJDBCExtensions;


public class ComplexCaseTest {

	
	@Test
	public void complexTypeConnection() throws Exception {
		
		String sql = "select complexObjectGenerate(343)";
		
		
		ExecutionFactory executionFactory = new ExecutionFactory();
		executionFactory.start();
		
		EmbeddedConfiguration config = new EmbeddedConfiguration();
		
		EmbeddedServer server = new EmbeddedServer();
		server.start(config);
	
		server.deployVDB(TeiidEmbeddedPortfolioJDBCExtensions.class.getClassLoader()
				.getResourceAsStream("userfunctions-vdb.xml"));
	
		Connection connection = server.getDriver().connect("jdbc:teiid:UserFunctionVDB", null);
		List<Object> listObjects = executeObject(connection, sql, true);
		System.out.println(listObjects);
	}
}
