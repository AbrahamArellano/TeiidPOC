package de.redhat.poc.core.udf;

import java.math.BigDecimal;

import de.redhat.poc.core.udf.beans.Team;

public class ComplexGenerator {
	
	public static Team complexObjectGenerate(BigDecimal decimal) {
		Team t = new Team();
		t.setName("MY NAME + " + decimal.toString());
		return t;
	}

}
