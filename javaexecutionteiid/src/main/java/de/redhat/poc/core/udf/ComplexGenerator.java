package de.redhat.poc.core.udf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import de.redhat.poc.core.udf.beans.Team;

public class ComplexGenerator {
	
	public static List<Team> complexObjectGenerate(BigDecimal decimal) {
		List<Team> listOfTeams = new ArrayList<>();
		Team teamObj = new Team();
		teamObj.setName(decimal.toString() + "_" +System.currentTimeMillis());
		listOfTeams.add(teamObj);
		
		return listOfTeams;
	}

}
