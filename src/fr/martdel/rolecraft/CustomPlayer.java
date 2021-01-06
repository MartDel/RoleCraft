package fr.martdel.rolecraft;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	public CustomPlayer(Player player, RoleCraft rolecraft) {
		this.player = player;
		this.uuid = player.getUniqueId();
		
		this.plugin = rolecraft;
		this.db = rolecraft.getDB();
		this.scheduler = rolecraft.getServer().getScheduler();
		
		this.job = null;
	}
	
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
	
	public Integer getJob() {
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
		return null;
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
	public void addJob(Integer job) {
		try {
			PreparedStatement add = db.getConnection().prepareStatement("INSERT INTO players(uuid, pseudo, job) VALUES(?, ?, ?)");
			add.setString(1, uuid.toString());
			add.setString(2, player.getDisplayName());
			add.setInt(3, job);
			add.executeUpdate();
			this.job = job;
		} catch (SQLException e) {
			// TODO Add pushbullet notification
			e.printStackTrace();
		}
	}
	
	public void setPlayer(Player player) { this.player = player; }
	public Player getPlayer() { return player; }

}
