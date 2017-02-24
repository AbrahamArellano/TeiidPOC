package javaexecutionteiid;

import static de.redhat.poc.core.jdbc.JDBCUtils.execute;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;
import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.runtime.EmbeddedServer;

import de.redhat.poc.core.objectastable.TeamObject;


public class HibernateCaseTest {

	
	@Test
	public void complexTypeConnection() throws Exception {
		
		EmbeddedServer embeddedServer = new EmbeddedServer();
        embeddedServer.start(new EmbeddedConfiguration());
        embeddedServer.deployVDB(ObjectAsTableTest.class.getClassLoader().getResourceAsStream("object-vdb.xml"));

        Thread.sleep(1000);

        Connection connection = embeddedServer.getDriver().connect("jdbc:teiid:objectExampleVDB", null);

        execute(connection, "SELECT * from Team", false);
        
        execute(connection, "SELECT * from Player", true);
        
        embeddedServer.stop();
        
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	@Test
	public void hibernateConnection() throws Exception {
		
		EmbeddedServer embeddedServer = new EmbeddedServer();
        embeddedServer.start(new EmbeddedConfiguration());
        embeddedServer.deployVDB(ObjectAsTableTest.class.getClassLoader().getResourceAsStream("object-vdb.xml"));

        Thread.sleep(1000);

        Connection connection = embeddedServer.getDriver().connect("jdbc:teiid:objectExampleVDB", null);
        
        Configuration config = new Configuration();
		config.addPackage("de.redhat.poc.core.objectastable");

		SessionFactory buildSessionFactory = config.configure().buildSessionFactory();

		SessionBuilder sessionBuilder = buildSessionFactory.withOptions();
		Session teiidSession = sessionBuilder.connection(connection).openSession();

		Query namedQuery = teiidSession.getNamedQuery("TeamObject.findAll");

		List<TeamObject> resultList = namedQuery.list();
        
		assertNotNull(resultList);
        assertEquals(resultList.size(), 2);
        assertEquals("team1", resultList.get(0).getTeamName());
        assertEquals("team2", resultList.get(1).getTeamName());
        
        assertEquals(4, resultList.get(0).getPlayers().size());
        assertEquals(3, resultList.get(1).getPlayers().size());
        embeddedServer.stop();
        
	}
}
