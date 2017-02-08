package de.redhat.poc.core.udf;

import java.sql.Connection;

import javax.sql.DataSource;

import org.teiid.resource.adapter.file.FileManagedConnectionFactory;
import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.runtime.EmbeddedServer;
import org.teiid.translator.file.FileExecutionFactory;
import org.teiid.translator.jdbc.h2.H2ExecutionFactory;

import de.redhat.poc.core.jdbc.EmbeddedHelper;
import de.redhat.poc.core.jdbc.TeiidEmbeddedPortfolioJDBCExtensions;

public class UserDefineCallerAbstract {

	protected static Connection c;
	
	public static void common() throws Exception {		
		
		DataSource ds = EmbeddedHelper.newDataSource("org.h2.Driver", "jdbc:h2:tcp://localhost:9092/accounts", "sa",
				"sa");
		// RunScript.execute(ds.getConnection(), new
		// InputStreamReader(TeiidEmbeddedPortfolioJDBCExtensions.class.getClassLoader().getResourceAsStream("data/customer-schema.sql")));
	
		EmbeddedServer server = new EmbeddedServer();
	
		H2ExecutionFactory executionFactory = new H2ExecutionFactory();
		executionFactory.setSupportsDirectQueryProcedure(true);
		executionFactory.start();
		server.addTranslator("jdbc-simple", executionFactory);
	
		server.addConnectionFactory("java:/h2db1_DS", ds);
	
		FileExecutionFactory fileExecutionFactory = new FileExecutionFactory();
		fileExecutionFactory.start();
		server.addTranslator("file", fileExecutionFactory);
	
		FileManagedConnectionFactory managedconnectionFactory = new FileManagedConnectionFactory();
		managedconnectionFactory.setParentDirectory("src/main/resources/data");
		server.addConnectionFactory("java:/marketdata-file", managedconnectionFactory.createConnectionFactory());
	
		EmbeddedConfiguration config = new EmbeddedConfiguration();
		config.setTransactionManager(EmbeddedHelper.getTransactionManager());
		server.start(config);
	
		server.deployVDB(TeiidEmbeddedPortfolioJDBCExtensions.class.getClassLoader()
				.getResourceAsStream("onlyDBViewManual-vdb.xml"));
	
		c = server.getDriver().connect("jdbc:teiid:ManualTest", null);
		
	
	}


}