package de.redhat.poc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;
import org.teiid.jdbc.RequestOptions;
import org.teiid.jdbc.StatementCallback;
import org.teiid.jdbc.TeiidPreparedStatement;
import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.runtime.EmbeddedServer;
import org.teiid.translator.jdbc.h2.H2ExecutionFactory;

import de.redhat.poc.core.jdbc.EmbeddedHelper;
import de.redhat.poc.core.jdbc.TeiidEmbeddedPortfolioJDBCExtensions;
import de.redhat.poc.core.udf.beans.Team;


public class ComplexCase {

	@Test
	public void test() {
		
	}
	
	@Test
	public void complexTypeConnection() throws Exception {
		
		String sql = "select complexObjectGenerate(343)";
		
		EmbeddedServer server = new EmbeddedServer();
	
		H2ExecutionFactory executionFactory = new H2ExecutionFactory();
		executionFactory.setSupportsDirectQueryProcedure(true);
		executionFactory.start();
		server.addTranslator("jdbc-simple", executionFactory);
		
		EmbeddedConfiguration config = new EmbeddedConfiguration();
		config.setTransactionManager(EmbeddedHelper.getTransactionManager());
		server.start(config);
	
		server.deployVDB(TeiidEmbeddedPortfolioJDBCExtensions.class.getClassLoader()
				.getResourceAsStream("onlyDBViewManual-vdb.xml"));
	
		Connection c = server.getDriver().connect("jdbc:teiid:ManualTest", null);
		testNonBlockingExecution(sql, c);
	}
	
	public void testNonBlockingExecution(String sql, Connection connection) throws SQLException, InterruptedException {

		System.out.println("JDBC Extensions Blocking Statement Execution");

		PreparedStatement stmt = connection.prepareStatement(sql);
		TeiidPreparedStatement tStmt = stmt.unwrap(TeiidPreparedStatement.class);
		
		tStmt.submitExecute(new StatementCallback() {

			int times = 1;

			@Override
			public void onRow(Statement s, ResultSet rs) throws Exception {
				Object team = rs.getObject(1);
				System.out.println(times++ + ": " + team);
				if (team.getClass().equals(Team.class)) {
					System.out.println(((Team)team).getName());
				}
			}

			@Override
			public void onException(Statement s, Exception e) throws Exception {
				s.close();
			}

			@Override
			public void onComplete(Statement s) throws Exception {
				s.close();
			}
		}, new RequestOptions());

		// wait callback be executed
		Thread.sleep(500);
		
	}
}
