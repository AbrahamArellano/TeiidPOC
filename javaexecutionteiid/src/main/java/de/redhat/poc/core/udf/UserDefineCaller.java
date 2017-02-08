package de.redhat.poc.core.udf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.teiid.jdbc.RequestOptions;
import org.teiid.jdbc.StatementCallback;
import org.teiid.jdbc.TeiidPreparedStatement;

public class UserDefineCaller extends UserDefineCallerAbstract {
	
	static String sql = "select celsiusToFahrenheit(2)";
	
	
	public static void main(String[] args) throws Exception {
		common();
		testNonBlockingExecution(c);
	}

	static void testNonBlockingExecution(Connection connection) throws SQLException, InterruptedException {

		System.out.println("JDBC Extensions Blocking Statement Execution");

		PreparedStatement stmt = connection.prepareStatement(sql);
		TeiidPreparedStatement tStmt = stmt.unwrap(TeiidPreparedStatement.class);
		tStmt.submitExecute(new StatementCallback() {

			int times = 1;

			@Override
			public void onRow(Statement s, ResultSet rs) throws Exception {
				System.out.println(times++ + ": " + rs.getObject(1));
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
