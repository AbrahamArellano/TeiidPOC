package javaexecutionteiid;

import static de.redhat.poc.core.jdbc.JDBCUtils.executeNonBlocking;

import org.junit.Test;

import de.redhat.poc.core.udf.UserDefineCallerAbstract;

public class UserDefineCallerTest extends UserDefineCallerAbstract {
	
	static String sql = "select celsiusToFahrenheit(2)";
	
	@Test
	public void executeNonBlockingUserDefinedFunction() throws Exception {
		common();
		executeNonBlocking(c, sql);
	}


}
