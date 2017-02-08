package de.redhat.poc.core;

import java.sql.Connection;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.runtime.EmbeddedServer;
import org.teiid.translator.ExecutionFactory;

import de.redhat.poc.core.TeamObject;

public class Engine 
{
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Hello World!" );
        TeamObject theTestObject = preparePlayers();
        Map<String, Object> testMap = new LinkedHashMap<String, Object>();
        testMap.put("teamObjectKey", theTestObject);

        // Teiid stuff
        EmbeddedServer server = new EmbeddedServer();
        server.start(new EmbeddedConfiguration());
        
        ExecutionFactory executionFactory = new ExecutionFactory();
        executionFactory.start();
        server.addTranslator("jdbc-simple", executionFactory);
        executionFactory.setSupportsDirectQueryProcedure(true);
        server.deployVDB(Engine.class.getClassLoader().getResourceAsStream("onlyDBViewManual-vdb.xml"));

        Properties properties = new Properties();
        properties.put("Name", "h2db");
        properties.put("Username", "sa");
        Connection c = server.getDriver().connect("jdbc:h2:tcp://localhost:9092/accounts", null);

        // execute(c, "SELECT performRuleOnData('org.teiid.example.drools.Message', 'Hello World', 0)", true);
//        execute(c, "SELECT * from Team.Team", true);
        server.stop();

    }


    private static TeamObject preparePlayers() {
      TeamObject teamObject = new TeamObject();
      teamObject.setTeamName("Poets");
      List<String> players = Arrays.asList("Paul Celan", "Pablo Neruda", "Ingeborg Bachmann", "Thomas Kling");
      teamObject.setPlayers(players);
      return teamObject;

    }


}