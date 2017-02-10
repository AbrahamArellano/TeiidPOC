package de.redhat.poc.core.objectastable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Karsten Gresch on 08.02.17.
 */
public class TeamObjectCollection
{

  // Consider that this is your interface that interacts solely with customer's
  // Dynamic Interface. The goal is this returns some TeamObjects
  public static Iterator<TeamObject> getTeam() {
      
    List<TeamObject> result = new LinkedList<TeamObject>();

    List<String> players = Arrays.asList("Paul Celan", "Pablo Neruda", "Ingeborg Bachmann", "Thomas Kling");
    TeamObject obj = new TeamObject();
    obj.setTeamName("team1");
    obj.setPlayers(players);
    
    result.add(obj);
    
    players = Arrays.asList("Ramesh Reddy", "Steven Hawkins", "Van Halbert");
    obj = new TeamObject();
    obj.setTeamName("team2");
    obj.setPlayers(players);
    result.add(obj);
    
    return result.iterator();
  }

}
