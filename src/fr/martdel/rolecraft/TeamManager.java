package fr.martdel.rolecraft;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class TeamManager {
	
	private Team tm;
	private String name;
	private RoleCraft plugin;

	public TeamManager(RoleCraft main, String name) {
		this.tm = main.getServer().getScoreboardManager().getMainScoreboard().getTeam(name);
		this.name = name;
		this.plugin = main;
	}
	public TeamManager(RoleCraft main, Team tm) {
		this.tm = tm;
		this.name = tm.getName();
		this.plugin = main;
	}
	
	/**
	 * Return true if the player is in this team
	 * @param player
	 * @return boolean
	 */
	@SuppressWarnings("deprecation")
	public boolean isIn(Player player) {
		for(OfflinePlayer p : tm.getPlayers()) {
			if(p.equals(player)) return true;
		}
		return false;
	}
	
	/**
	 * Add a player to this team
	 * @param player
	 */
	@SuppressWarnings("deprecation")
	public void add(Player player) {
		tm.addPlayer(player);
	}
	
	/**
	 * Remove a player from this team
	 * @param player
	 */
	@SuppressWarnings("deprecation")
	public void remove(Player player) {
		tm.removePlayer(player);
	}
	
	/**
	 * Move a player who is in a first team (this team) to a second team
	 * @param player
	 * @param otherTeam The second team (destination)
	 */
	public void move(Player player, TeamManager otherTeam) {
		this.remove(player);
		otherTeam.add(player);
	}
	public void move(Player player, String teamname) {
		move(player, new TeamManager(plugin, teamname));
	}
	
	/**
	 * Get the team color to print it
	 * @return String
	 */
	public String getColor() {
		return RoleCraft.config.getString("team_colors." + name);
	}
	
	/**
	 * Get the player's team
	 * @param plugin
	 * @param player
	 * @return TeamManager
	 */
	public static TeamManager getPlayerTeam(RoleCraft plugin, Player player) {
		@SuppressWarnings("deprecation")
		Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
		return new TeamManager(plugin, team);
	}
	
	// GETTERS AND SETTERS
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public Team getTeam() { return tm; }
	public void setTeam(Team team) { this.tm = team; }
	
}
