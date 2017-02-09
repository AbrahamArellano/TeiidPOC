package de.redhat.poc.core.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.teiid.jdbc.RequestOptions;
import org.teiid.jdbc.StatementCallback;
import org.teiid.jdbc.TeiidPreparedStatement;

public class JDBCUtils {

	public static Connection getDriverConnection(String driver, String url, String user, String pass) throws Exception {
		Class.forName(driver);
		return DriverManager.getConnection(url, user, pass);
	}

	public static void close(Connection conn) throws SQLException {
		close(null, null, conn);
	}

	public static void close(Statement stmt) throws SQLException {
		close(null, stmt, null);
	}

	public static void close(ResultSet rs) throws SQLException {
		close(rs, null, null);
	}

	public static void close(Statement stmt, Connection conn) throws SQLException {
		close(null, stmt, conn);
	}

	public static void close(ResultSet rs, Statement stmt) throws SQLException {
		close(rs, stmt, null);
	}

	public static void close(ResultSet rs, Statement stmt, Connection conn) throws SQLException {

		if (null != rs) {
			rs.close();
			rs = null;
		}

		if (null != stmt) {
			stmt.close();
			stmt = null;
		}

		if (null != conn) {
			conn.close();
			conn = null;
		}
	}

	public static void execute(Connection connection, String sql, boolean closeConn) throws Exception {

		System.out.println("SQL: " + sql); //$NON-NLS-1$

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.createStatement();
			boolean hasResults = stmt.execute(sql);
			if (hasResults) {
				rs = stmt.getResultSet();
				ResultSetMetaData metadata = rs.getMetaData();
				int columns = metadata.getColumnCount();
				for (int row = 1; rs.next(); row++) {
					System.out.print(row + ": ");
					for (int i = 0; i < columns; i++) {
						if (i > 0) {
							System.out.print(", ");
						}
						System.out.print(rs.getObject(i + 1));
					}
					System.out.println();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, stmt);
			if (closeConn)
				close(connection);
		}
		System.out.println();
	}

	public static void executeQuery(Connection conn, String sql) throws SQLException {

		System.out.println("Query SQL: " + sql); //$NON-NLS-1$

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData metadata = rs.getMetaData();
			int columns = metadata.getColumnCount();
			for (int row = 1; rs.next(); row++) {
				System.out.print(row + ": ");
				for (int i = 0; i < columns; i++) {
					if (i > 0) {
						System.out.print(",");
					}
					System.out.print(rs.getString(i + 1));
				}
				System.out.println();
			}
		} finally {
			close(rs, stmt);
		}

		System.out.println();

	}

	public static boolean executeUpdate(Connection conn, String sql) throws SQLException {

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} finally {
			close(stmt);
		}
		return true;
	}

	public static Entity executeQueryCount(Connection conn, String sql) throws SQLException {

		Entity entity = new Entity();
		entity.setSql(sql);

		long start = System.currentTimeMillis();

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			entity.setQueryTime(System.currentTimeMillis() - start);
			int columns = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				for (int i = 0; i < columns; ++i) {
					rs.getObject(i + 1);
				}
			}
		} finally {
			JDBCUtils.close(rs, stmt);
		}

		entity.setDeserializeTime(System.currentTimeMillis() - start - entity.getQueryTime());

		return entity;
	}

	public static void executeNonBlocking(Connection connection, String sql) throws SQLException, InterruptedException {

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

	public static List<Object> executeObject(Connection connection, String sql, boolean closeConn) throws SQLException {

		System.out.println("SQL: " + sql);

		List<Object> result = new LinkedList<Object>();

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.createStatement();
			boolean hasResults = stmt.execute(sql);
			if (hasResults) {
				rs = stmt.getResultSet();
				ResultSetMetaData metadata = rs.getMetaData();
				int columns = metadata.getColumnCount();
				for (int row = 1; rs.next(); row++) {
					System.out.print(row + ": ");
					for (int i = 0; i < columns; i++) {
						if (i > 0) {
							System.out.print(", ");
						}
						result.add(rs.getObject(i + 1));
					}
					System.out.println("\n");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs, stmt);
			if (closeConn)
				close(connection);
		}

		return result;
	}

	public static class Entity {

		private String sql;

		private long queryTime;

		private long deserializeTime;

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		public long getQueryTime() {
			return queryTime;
		}

		public void setQueryTime(long queryTime) {
			this.queryTime = queryTime;
		}

		public long getDeserializeTime() {
			return deserializeTime;
		}

		public void setDeserializeTime(long deserializeTime) {
			this.deserializeTime = deserializeTime;
		}

		@Override
		public String toString() {
			return "PerfEntity [sql=" + sql + ", queryTime=" + queryTime + ", deserializeTime=" + deserializeTime + "]";
		}

	}
}