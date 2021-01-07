package fr.martdel.rolecraft;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
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

	private Boolean is_admin;
	private Integer level;
	private Boolean has_spe;
	private Integer job;
	private Integer house_id;
	
	private Location[] house;
	private Map<String, Location[]> farms;
	private Map<String, Location[]> builds;

	public CustomPlayer(Player player, RoleCraft rolecraft) {
		this.player = player;
		this.uuid = player.getUniqueId();
		
		this.plugin = rolecraft;
		this.db = rolecraft.getDB();
		this.scheduler = rolecraft.getServer().getScheduler();
		
		this.is_admin = false;
		this.level = 1;
		this.job = null;
		this.has_spe = false;
		this.house_id = null;
		
		this.house = null;
		this.farms = new HashMap<>();
		this.builds = new HashMap<>();
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
	
	public TeamManager getTeam() {
		return TeamManager.getPlayerTeam(plugin, player, getStringJob("en"));
	}
	
	/*
	 * MANAGE DATABASE DATA
	 */
	public CustomPlayer loadData() {
		try {
			PreparedStatement query = db.getConnection().prepareStatement("SELECT * FROM players WHERE uuid=?");
			query.setString(1, uuid.toString());
			ResultSet result = query.executeQuery();
			if(result.next()) {
				this.is_admin = result.getByte("admin") == 1;
				this.level = result.getInt("level");
				this.job = result.getInt("job");
				this.has_spe = result.getByte("spe") == 1;
				
				// Get house
				this.house_id = result.getInt("house");
				PreparedStatement query0 = db.getConnection().prepareStatement("SELECT * FROM house WHERE id=?");
				query0.setInt(1, house_id);
				ResultSet result0 = query0.executeQuery();
				if(result0.next()) {
					int x1 = result0.getInt("x1");
					int y1 = result0.getInt("y1");
					int z1 = result0.getInt("z1");
					int x2 = result0.getInt("x2");
					int y2 = result0.getInt("y2");
					int z2 = result0.getInt("z2");
					World world = plugin.getServer().getWorld("NORMAL");
					System.out.println(world);
					
					Location l1 = new Location(world, x1, y1, z1);
					Location l2 = new Location(world, x2, y2, z2);
					Location[] locations = {l1, l2};
					
					this.house = locations;
				}
				query0.close();
				
				// Get farms
				farms.clear();
				PreparedStatement query1 = db.getConnection().prepareStatement("SELECT * FROM farms WHERE owner_uuid=?");
				query1.setString(1, uuid.toString());
				ResultSet result1 = query1.executeQuery();
				while(result1.next()) {
					String name = result1.getString("name");
					int x1 = result1.getInt("x1");
					int y1 = result1.getInt("y1");
					int z1 = result1.getInt("z1");
					int x2 = result1.getInt("x2");
					int y2 = result1.getInt("y2");
					int z2 = result1.getInt("z2");
					World world = plugin.getServer().getWorld("NORMAL");
					
					Location l1 = new Location(world, x1, y1, z1);
					Location l2 = new Location(world, x2, y2, z2);
					Location[] locations = {l1, l2};
					
					farms.put(name, locations);
				}
				query1.close();
				
				// Get builds
				builds.clear();
				PreparedStatement query2 = db.getConnection().prepareStatement("SELECT * FROM builds WHERE owner_uuid=?");
				query2.setString(1, uuid.toString());
				ResultSet result2 = query.executeQuery();
				while(result2.next()) {
					String name = result2.getString("name");
					int x1 = result2.getInt("x1");
					int y1 = result2.getInt("y1");
					int z1 = result2.getInt("z1");
					int x2 = result2.getInt("x2");
					int y2 = result2.getInt("y2");
					int z2 = result2.getInt("z2");
					World world = plugin.getServer().getWorld("NORMAL");
					
					Location l1 = new Location(world, x1, y1, z1);
					Location l2 = new Location(world, x2, y2, z2);
					Location[] locations = {l1, l2};
					
					builds.put(name, locations);
				}
				query2.close();
			}
			query.close();
		} catch (SQLException e) {
			DatabaseManager.error(e);
		}
		return this;
	}
	
	public void save() {
		try {
			PreparedStatement update0 = db.getConnection().prepareStatement("UPDATE players SET admin=?, level=?, job=?, spe=? WHERE uuid=?");
			update0.setByte(1, (byte) (is_admin ? 1 : 0));
			update0.setInt(2, level);
			update0.setInt(3, job);
			update0.setByte(4, (byte) (has_spe ? 1 : 0));
			update0.setString(5, uuid.toString());
			update0.executeUpdate();
			update0.close();
			
			// Update house table
			PreparedStatement update1 = db.getConnection().prepareStatement("UPDATE house SET x1=?, y1=?, z1=?, x2=?, y2=?, z2=? WHERE id=?");
			for(int i = 0; i < house.length; i++) {
				Location loc = house[i];
				update1.setInt(1 + (i*3), loc.getBlockX());
				update1.setInt(2 + (i*3), loc.getBlockY());
				update1.setInt(3 + (i*3), loc.getBlockZ());
			}
			update1.setString(7, uuid.toString());
			update1.executeUpdate();
			update1.close();
			
			// Update farm table
			if(!farms.isEmpty()) {
				// Delete old farms
				PreparedStatement delete1 = db.getConnection().prepareStatement("DELETE FROM farms WHERE owner_uuid=?");
				delete1.setString(1, uuid.toString());
				delete1.executeUpdate();
				delete1.close();
				
				// Insert new farms
				for (String farm_name : farms.keySet()) {
					Location[] locations = farms.get(farm_name);
					PreparedStatement insert1 = db.getConnection().prepareStatement("INSERT INTO farms(name, owner_uuid, x1, y1, z1, x2, y2, z2) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
					insert1.setString(1, farm_name);
					insert1.setString(2, uuid.toString());
					for(int i = 0; i < locations.length; i++) {
						Location loc = locations[i];
						insert1.setInt(3 + (i*3), loc.getBlockX());
						insert1.setInt(4 + (i*3), loc.getBlockY());
						insert1.setInt(5 + (i*3), loc.getBlockZ());
					}
					insert1.executeUpdate();
					insert1.close();
		        }
			}
			
			// Update build table
			if(!builds.isEmpty()) {
				// Delete old builds
				PreparedStatement delete2 = db.getConnection().prepareStatement("DELETE FROM builds WHERE owner_uuid=?");
				delete2.setString(1, uuid.toString());
				delete2.executeUpdate();
				delete2.close();
				
				// Insert new builds
				for (String build_name : builds.keySet()) {
					Location[] locations = builds.get(build_name);
					PreparedStatement insert2 = db.getConnection().prepareStatement("INSERT INTO builds(name, owner_uuid, x1, y1, z1, x2, y2, z2) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
					insert2.setString(1, build_name);
					insert2.setString(2, uuid.toString());
					for(int i = 0; i < locations.length; i++) {
						Location loc = locations[i];
						insert2.setInt(3 + (i*3), loc.getBlockX());
						insert2.setInt(4 + (i*3), loc.getBlockY());
						insert2.setInt(5 + (i*3), loc.getBlockZ());
					}
					insert2.executeUpdate();
					insert2.close();
		        }
			}
		} catch (SQLException e) {
			DatabaseManager.error(e);
		}
	}
	
	public void register() {
		try {
			PreparedStatement add = db.getConnection().prepareStatement("INSERT INTO players(uuid, pseudo) VALUES(?, ?)");
			add.setString(1, uuid.toString());
			add.setString(2, player.getDisplayName());
			add.executeUpdate();
			add.close();
		} catch (SQLException e) {
			DatabaseManager.error(e);
		}
	}
	public boolean isRegistered() {
		try {
			PreparedStatement query = db.getConnection().prepareStatement("SELECT * FROM players WHERE uuid=?");
			query.setString(1, uuid.toString());
			ResultSet result = query.executeQuery();
			if(result.next()) return true;
			query.close();
		} catch (SQLException e) {
			DatabaseManager.error(e);
		}
		return false;
	}
	
	/*
	 * GETTERS and SETTERS
	 */
	public Boolean isAdmin() { return this.is_admin; }
	public void setAdmin(Boolean is_admin) { this.is_admin = is_admin; }

	public Integer getLevel() { return level; }
	public void setLevel(Integer level) { this.level = level; }

	public Integer getJob() { return this.job; }
	public String getStringJob(String lang) {
		if(hasSpe()) {
			List<Map<?, ?>> spe_list = RoleCraft.config.getMapList("spe");
			@SuppressWarnings("unchecked")
			Map<String, String> spe_config = (Map<String, String>) spe_list.get(job);
			return spe_config.get(lang);
		} else {
			List<Map<?, ?>> job_list = RoleCraft.config.getMapList("jobs");
			@SuppressWarnings("unchecked")
			Map<String, String> job_config = (Map<String, String>) job_list.get(job);
			return job_config.get(lang);
		}
	}
	public void setJob(Integer job) { this.job = job; }
	
	public Boolean hasSpe() { return this.has_spe; }
	public void setSpe(Boolean spe) { this.has_spe = spe; }
	
	public Location[] getHouse() { return house; }
	public void setHouse(Location[] house) { this.house = house; }

	public Map<String, Location[]> getBuilds() { return builds; }
	public void addBuild(String name, Location l1, Location l2) {
		Location[] locations = {l1, l2};
		builds.put(name, locations);
	}
	public void removeBuild(String name) { builds.remove(name); }

	public Map<String, Location[]> getFarms() { return farms; }
	public void addFarm(String name, Location l1, Location l2) {
		Location[] locations = {l1, l2};
		farms.put(name, locations);
	}
	public void removeFarm(String name) { farms.remove(name); }
	
	public void setPlayer(Player player) { this.player = player; }
	public Player getPlayer() { return player; }

}
