package de.redhat.poc.core.jdbc;

import static de.redhat.poc.core.jdbc.JDBCUtils.*;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.h2.tools.RunScript;
import org.teiid.jdbc.RequestOptions;
import org.teiid.jdbc.StatementCallback;
import org.teiid.jdbc.TeiidPreparedStatement;
import org.teiid.resource.adapter.file.FileManagedConnectionFactory;
import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.runtime.EmbeddedServer;
import org.teiid.translator.file.FileExecutionFactory;
import org.teiid.translator.jdbc.h2.H2ExecutionFactory;

@SuppressWarnings("nls")
public class TeiidEmbeddedPortfolioJDBCExtensions {

	static String sql = "select * from Stock Order by product_id";

	public static void main(String[] args) throws Exception {

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

		Connection c = server.getDriver().connect("jdbc:teiid:ManualTest", null);

		testNonBlockingExecution(c);

		testContinuousExecution(c);

		close(c);

	}

	static void testContinuousExecution(Connection connection) throws SQLException, InterruptedException {

		System.out.println("JDBC Extensions Continuous Execution");

		PreparedStatement stmt = connection.prepareStatement(sql);
		TeiidPreparedStatement tStmt = stmt.unwrap(TeiidPreparedStatement.class);
		RequestOptions requestOptions = new RequestOptions();
		tStmt.submitExecute(new StatementCallback() {

			int times = 1;

			@Override
			public void onRow(Statement s, ResultSet rs) throws Exception {

				System.out.println(times++ + ": " + rs.getObject(1) + ", " + rs.getObject(2) + ", " + rs.getObject(3)
						+ ", " + rs.getObject(4));
			}

			@Override
			public void onException(Statement s, Exception e) throws Exception {
				s.close();
			}

			@Override
			public void onComplete(Statement s) throws Exception {
				s.close();
			}
		}, requestOptions.continuous(true));

		// wait callback be executed
		Thread.sleep(500);
	}

	static void testNonBlockingExecution(Connection connection) throws SQLException, InterruptedException {

		System.out.println("JDBC Extensions Blocking Statement Execution");

		PreparedStatement stmt = connection.prepareStatement(sql);
		TeiidPreparedStatement tStmt = stmt.unwrap(TeiidPreparedStatement.class);
		
		tStmt.submitExecute(new StatementCallback() {

			int times = 1;

			@Override
			public void onRow(Statement s, ResultSet rs) throws Exception {
				System.out.println(times++ + ": " + rs.getObject(1) + ", " + rs.getObject(2) + ", " + rs.getObject(3)
						+ ", " + rs.getObject(4));
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
