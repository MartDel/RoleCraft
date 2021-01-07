package fr.martdel.rolecraft;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import fr.martdel.rolecraft.database.DatabaseManager;

public class CustomPlayer {
	
	public static final int DEFAULTHEARTS = 10;
	
	private Player player;
	private UUID uuid;
	
	private RoleCraft plugin;
	private BukkitScheduler scheduler;
	private DatabaseManager db;

	private Integer job;
	private Boolean spe;

	public CustomPlayer(Player player, RoleCraft rolecraft) {
		this.player = player;
		this.uuid = player.getUniqueId();
		
		this.plugin = rolecraft;
		this.db = rolecraft.getDB();
		this.scheduler = rolecraft.getServer().getScheduler();
		
		this.job = null;
		this.spe = null;
	}
	
	/**
	 * Start a cinematic for the player
	 * Tp every tic the player at a specific location
	 * @param to Where the player must look 
	 * @param time How many time before stopping the cinematic
	 */
	public void playCinematic(Location to, int time) {
		final int delay = 1;
		scheduler.runTaskLater(plugin, new Runnable() {
			private int t = 0;
			@Override
			public void run() {
				player.teleport(to);
				t++;
				if(t < time) scheduler.runTaskLater(plugin, this, delay);
			}
		}, delay);
	}
	
	/*
	 * MANAGE HEARTS NUMBER
	 */
	@SuppressWarnings("deprecation")
	public void setMaxHearts(int hearts) {
		if(getMaxHearts() > hearts) {
			player.setHealth(hearts * 2);
			player.setMaxHealth(hearts * 2);
		} else {	
			player.setMaxHealth(hearts * 2);
			player.setHealth(hearts * 2);
		}
	}
	@SuppressWarnings("deprecation")
	public int getMaxHearts() { return (int) player.getMaxHealth() / 2; }
	
	/*
	 * MANAGE PLAYER JOB
	 */
	public Integer getJob() {
		if(this.job == null) {
			try {
				PreparedStatement query = db.getConnection().prepareStatement("SELECT job FROM players WHERE uuid=?");
				query.setString(1, uuid.toString());
				ResultSet result = query.executeQuery();
				if(result.next()) {
					this.job = result.getInt("job");
					return this.job;
				}
			} catch (SQLException e) {
				// TODO Add pushbullet notification
				e.printStackTrace();
			}
		} else return this.job;
		return null;
	}
	public String getStringJob(String lang) {
		if(hasSpe()) {
			List<Map<?, ?>> spe_list = RoleCraft.config.getMapList("spe");
			@SuppressWarnings("unchecked")
			Map<String, String> spe_config = (Map<String, String>) spe_list.get(getJob());
			return spe_config.get(lang);
		} else {
			List<Map<?, ?>> job_list = RoleCraft.config.getMapList("jobs");
			@SuppressWarnings("unchecked")
			Map<String, String> job_config = (Map<String, String>) job_list.get(getJob());
			return job_config.get(lang);
		}
	}
	public void setJob(Integer job) {
		try {
			PreparedStatement set = db.getConnection().prepareStatement("UPDATE players SET job=? WHERE uuid=?");
			set.setInt(1, job);
			set.setString(2, uuid.toString());
			set.executeUpdate();
			this.job = job;
		} catch (SQLException e) {
			// TODO Add pushbullet notification
			e.printStackTrace();
		}
	}
	
	/*
	 * MANAGE PLAYER SPE
	 */
	public Boolean hasSpe() {
		if(this.spe == null) {
			try {
				PreparedStatement query = db.getConnection().prepareStatement("SELECT spe FROM players WHERE uuid=?");
				query.setString(1, uuid.toString());
				ResultSet result = query.executeQuery();
				if(result.next()) {
					this.spe = result.getByte("spe") == 1 ? true : false;
					return this.spe;
				}
			} catch (SQLException e) {
				// TODO Add pushbullet notification
				e.printStackTrace();
			}
		} else return this.spe;
		return null;
	}
	public void setSpe(Boolean spe) {
		try {
			PreparedStatement set = db.getConnection().prepareStatement("UPDATE players SET spe=? WHERE uuid=?");
			set.setInt(1, spe ? 1 : 0);
			set.setString(2, uuid.toString());
			set.executeUpdate();
			this.spe = spe;
		} catch (SQLException e) {
			// TODO Add pushbullet notification
			e.printStackTrace();
		}
	}
	
	/*
	 * MANAGE PLAYER TEAM
	 */
	public TeamManager getTeam() {
		return TeamManager.getPlayerTeam(plugin, player, getStringJob("en"));
	}
	
	/*
	 * MANAGE PLAYER IN THE DATABASE
	 */
	public boolean isSaved() {
		try {
			PreparedStatement query = db.getConnection().prepareStatement("SELECT * FROM players WHERE uuid=?");
			query.setString(1, uuid.toString());
			ResultSet result = query.executeQuery();
			if(result.next()) {
				this.job = result.getInt("job");
				return true;
			}
		} catch (SQLException e) {
			// TODO Add pushbullet notification
			e.printStackTrace();
		}
		return false;
	}
	public void save() {
		try {
			PreparedStatement add = db.getConnection().prepareStatement("INSERT INTO players(uuid, pseudo) VALUES(?, ?)");
			add.setString(1, uuid.toString());
			add.setString(2, player.getDisplayName());
			add.executeUpdate();
		} catch (SQLException e) {
			// TODO Add pushbullet notification
			e.printStackTrace();
		}
	}
	
	public void setPlayer(Player player) { this.player = player; }
	public Player getPlayer() { return player; }

}
