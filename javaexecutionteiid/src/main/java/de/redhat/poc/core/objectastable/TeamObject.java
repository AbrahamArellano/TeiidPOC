package de.redhat.poc.core.objectastable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Created by Karsten Gresch on 07.02.17.
 */
@Entity
@Table(name = "Team")
@NamedQuery(name = "TeamObject.findAll", query = "SELECT t FROM TeamObject t")
public class TeamObject {

	@Id
	@Column(name = "name")
	private String teamName;

	@ElementCollection()
	@CollectionTable(name = "Player", joinColumns = @JoinColumn(name = "teamname"))
	@Column(name = "playername")
	private List<String> players = new ArrayList<String>();

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public List<String> getPlayers() {
		return players;
	}

	public String[] getPlayersAsArray() {
		return players.toArray(new String[players.size()]);
	}

	public void setPlayers(List<String> players) {
		this.players = players;
	}
}
