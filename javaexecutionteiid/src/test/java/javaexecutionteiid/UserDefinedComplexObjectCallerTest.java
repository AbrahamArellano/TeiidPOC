package javaexecutionteiid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;
import org.teiid.jdbc.RequestOptions;
import org.teiid.jdbc.StatementCallback;
import org.teiid.jdbc.TeiidPreparedStatement;

import de.redhat.poc.core.udf.UserDefineCallerAbstract;
import de.redhat.poc.core.udf.beans.Team;

public class UserDefinedComplexObjectCallerTest extends UserDefineCallerAbstract{

	static String sql = "select complexObjectGenerate(343)";
	
	@Test
	public void executeNonBlockingUserDefinedFunction() throws Exception {
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
