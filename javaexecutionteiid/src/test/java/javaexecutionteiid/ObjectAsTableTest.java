package javaexecutionteiid;

import static de.redhat.poc.core.jdbc.JDBCUtils.execute;

import java.sql.Connection;

import org.junit.Test;
import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.runtime.EmbeddedServer;

public class ObjectAsTableTest {

	@Test
	public void testObjectAsTable() throws Exception {
		EmbeddedServer embeddedServer = new EmbeddedServer();
        embeddedServer.start(new EmbeddedConfiguration());
        embeddedServer.deployVDB(ObjectAsTableTest.class.getClassLoader().getResourceAsStream("object-vdb.xml"));

        Thread.sleep(1000);

        Connection connection = embeddedServer.getDriver().connect("jdbc:teiid:objectExampleVDB", null);

        execute(connection, "SELECT * from Team", false);
        
        execute(connection, "SELECT * from Player", true);
        
        embeddedServer.stop();
	}
}
