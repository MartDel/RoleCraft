package fr.martdel.rolecraft.player;

import fr.martdel.rolecraft.RoleCraft;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class TeamManager {

	private Team tm;
	private String name;
	private final RoleCraft plugin;

	public TeamManager(RoleCraft main, String name) {
		ScoreboardManager sbm = main.getServer().getScoreboardManager();
		assert sbm != null;
		this.tm = sbm.getMainScoreboard().getTeam(name);
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
	 * @param player The player to check
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
	 * @param player The player to add
	 */
	@SuppressWarnings("deprecation")
	public void add(Player player) {
		tm.addPlayer(player);
	}
	
	/**
	 * Remove a player from this team
	 * @param player The player to remove
	 */
	@SuppressWarnings("deprecation")
	public void remove(Player player) {
		tm.removePlayer(player);
	}
	
	/**
	 * Move a player who is in a first team (this team) to a second team
	 * @param player The player to move
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
	 * @param plugin Instance of RoleCraft plugin
	 * @param player The player to work with
	 * @return TeamManager
	 */
	public static TeamManager getPlayerTeam(RoleCraft plugin, Player player) {
		ScoreboardManager sbm = plugin.getServer().getScoreboardManager();
		assert sbm != null;
		Team team = sbm.getMainScoreboard().getPlayerTeam(player);
		assert team != null;
		return new TeamManager(plugin, team);
	}
	
	// GETTERS AND SETTERS
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public Team getTeam() { return tm; }
	public void setTeam(Team team) { this.tm = team; }

}
