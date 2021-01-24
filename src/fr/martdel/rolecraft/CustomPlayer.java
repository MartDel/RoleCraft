package fr.martdel.rolecraft;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
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
	private Integer score;
	private Boolean has_spe;
	private Integer job;
	private Integer house_id;
	private Integer shop_id;
	
	private Map<String, Integer> house;
	private Map<String, Integer> shop;
	private Map<String, Map<String, Integer>> farms;
	private Map<String, Map<String, Integer>> builds;
	private Map<String, Integer> admin_ground;

	public CustomPlayer(Player player, RoleCraft rolecraft) {
		this.player = player;
		this.uuid = player.getUniqueId();
		
		this.plugin = rolecraft;
		this.db = rolecraft.getDB();
		this.scheduler = rolecraft.getServer().getScheduler();
		
		this.is_admin = false;
		this.level = 1;
		this.score = 0;
		this.job = null;
		this.has_spe = false;
		this.house_id = null;
		this.shop_id = null;
		
		this.house = null;
		this.shop = null;
		this.farms = new HashMap<>();
		this.builds = new HashMap<>();
		this.admin_ground = null;
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
	
	/**
	 * Remove a specific book found with a given name in the player inventory
	 * @param name The book name
	 */
	public void removeBook(String name) {
		for(ItemStack stack : player.getInventory().getStorageContents()) {
			if(stack != null) {
				ItemMeta meta = stack.getItemMeta();
				if(meta instanceof BookMeta) {
					BookMeta book_meta = (BookMeta) meta;
					if(book_meta.getAuthor().equalsIgnoreCase("MartDel")
					&& book_meta.getTitle().equalsIgnoreCase(name)) player.getInventory().remove(stack);
				}
			}
		}
	}
	
	/**
	 * Load and get data from database
	 * @return this (CustomPlayer)
	 */
	public CustomPlayer loadData() {
		try {
			PreparedStatement query = db.getConnection().prepareStatement("SELECT * FROM players WHERE uuid=?");
			query.setString(1, uuid.toString());
			ResultSet result = query.executeQuery();
			if(result.next()) {
				this.is_admin = result.getByte("admin") == 1;
				this.level = result.getInt("level");
				this.score = result.getInt("score");
				this.job = result.getInt("job") == 0 ? null : result.getInt("job") - 1;
				this.has_spe = result.getByte("spe") == 1;
				
				// Get house
				this.house_id = result.getInt("house") == 0 ? null : result.getInt("house");
				if(house_id != null) this.house = loadGround("houses", house_id);
				
				// Get shop
				this.shop_id = result.getInt("shop") == 0 ? null : result.getInt("shop");
				if(shop_id != null) this.shop = loadGround("shops", shop_id);
				
				// Get farms
				this.farms = loadMultipleGrounds("farms");
				
				// Get builds
				this.builds = loadMultipleGrounds("farms");
				
				// Get admin_ground
				if(is_admin) {
					PreparedStatement query_admin = db.getConnection().prepareStatement("SELECT * FROM admin_grounds WHERE owner_uuid=?");
					query_admin.setString(1, uuid.toString());
					ResultSet result_admin = query_admin.executeQuery();
					if(result_admin.next()) {
						admin_ground = new HashMap<>();
						admin_ground.put("x1", result_admin.getInt("x1"));
						admin_ground.put("z1", result_admin.getInt("z1"));
						admin_ground.put("x2", result_admin.getInt("x2"));
						admin_ground.put("z2", result_admin.getInt("z2"));
					}
					query_admin.close();
				}
			}
			query.close();
		} catch (SQLException e) {
			DatabaseManager.error(e);
		}
		return this;
	}
	private Map<String, Integer> loadGround(String table, int id) throws SQLException{
		Map<String, Integer> locations = new HashMap<>();
		PreparedStatement query = db.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE id=?");
		query.setInt(1, id);
		ResultSet result = query.executeQuery();
		if(result.next()) {
			locations.put("x1", result.getInt("x1"));
			locations.put("z1", result.getInt("z1"));
			locations.put("x2", result.getInt("x2"));
			locations.put("z2", result.getInt("z2"));
		}
		query.close();
		return locations;
	}
	private Map<String, Map<String, Integer>> loadMultipleGrounds(String table) throws SQLException{
		Map<String, Map<String, Integer>> grounds = new HashMap<>();
		PreparedStatement query = db.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE owner_uuid=?");
		query.setString(1, uuid.toString());
		ResultSet result = query.executeQuery();
		while(result.next()) {
			Map<String, Integer> current_ground = new HashMap<>();
			current_ground.put("x1", result.getInt("x1"));
			current_ground.put("z1", result.getInt("z1"));
			current_ground.put("x2", result.getInt("x2"));
			current_ground.put("z2", result.getInt("z2"));
			grounds.put(result.getString("name"), current_ground);
		}
		query.close();
		return grounds;
	}
	
	/**
	 * Update the player row in the database
	 */
	public void save() {
		try {
			PreparedStatement update_player = db.getConnection().prepareStatement("UPDATE players SET admin=?, level=?, score=?, job=?, spe=? WHERE uuid=?");
			update_player.setByte(1, (byte) (is_admin ? 1 : 0));
			update_player.setInt(2, level);
			update_player.setInt(3, score);
			update_player.setInt(4, job + 1);
			update_player.setByte(5, (byte) (has_spe ? 1 : 0));
			update_player.setString(6, uuid.toString());
			update_player.executeUpdate();
			update_player.close();
			
			// Update house
			if(house_id != null) updateGround("houses", house_id, house);
			
			// Update shop
			if(shop_id != null) updateGround("shops", shop_id, shop);
			
			// Update farm table
			if(!farms.isEmpty()) updateMultipleGrounds("farms", farms);
			
			// Update build table
			if(!builds.isEmpty()) updateMultipleGrounds("builds", builds);
			
			// Update admin_grounds table
			if(is_admin && admin_ground != null) {
				PreparedStatement update_admin = db.getConnection().prepareStatement("UPDATE admin_grounds SET x1=?, z1=?, x2=?, z2=? WHERE owner_uuid=?");
				for (int i = 0; i < admin_ground.size(); i++) {
					Integer coord = (Integer) admin_ground.values().toArray()[i];
					update_admin.setInt(i+1, coord);
				}
				update_admin.setString(7, uuid.toString());
				update_admin.executeUpdate();
				update_admin.close();
			}
		} catch (SQLException e) {
			DatabaseManager.error(e);
		}
	}
	private void updateGround(String table, int id, Map<String, Integer> data) throws SQLException {
		PreparedStatement update;
		if(data != null) {
			update = db.getConnection().prepareStatement("UPDATE " + table + " SET owner_uuid=?, x1=?, z1=?, x2=?, z2=? WHERE id=?");
			update.setString(1, uuid.toString());
			
			Set<String> data_set = data.keySet();
			int i = 0;
			for (String c : data_set) {
				Integer coord = data.get(c);
				update.setInt(i + 2, coord);
				i++;
			}
			
			update.setInt(6, id);
		} else {
			update = db.getConnection().prepareStatement("UPDATE " + table + " SET owner_uuid=?, x1=NULL, z1=NULL, x2=NULL, z2=NULL WHERE id=?");
			update.setString(1, uuid.toString());
			update.setInt(2, id);
		}
		update.executeUpdate();
		update.close();
	}
	private void updateMultipleGrounds(String table, Map<String, Map<String, Integer>> data) throws SQLException {
		// Delete old grounds
		PreparedStatement delete = db.getConnection().prepareStatement("DELETE FROM " + table + " WHERE owner_uuid=?");
		delete.setString(1, uuid.toString());
		delete.executeUpdate();
		delete.close();
		
		// Insert new grounds
		for (String ground_name : farms.keySet()) {
			Map<String, Integer> locations = farms.get(ground_name);
			PreparedStatement insert = db.getConnection().prepareStatement("INSERT INTO " + table + "(x1, z1, x2, z2, name, owner_uuid) VALUES(?, ?, ?, ?, ?, ?)");
			for (int i = 0; i < locations.size(); i++) {
				Integer coord = (Integer) locations.values().toArray()[i];
				insert.setInt(i+1, coord);
			}
			insert.setString(5, ground_name);
			insert.setString(6, uuid.toString());
			insert.executeUpdate();
			insert.close();
        }
	}
	
	/**
	 * Add the player to the database
	 */
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
	public TeamManager getTeam() { return TeamManager.getPlayerTeam(plugin, player); }
	public Wallet getWallet() { return new Wallet(player); }
	
	public Boolean isAdmin() { return this.is_admin; }
	public void setAdmin(Boolean is_admin) { this.is_admin = is_admin; }

	public Integer getLevel() { return level; }
	public void setLevel(Integer level) {
		plugin.getLvl().setScore(player, level);
		this.level = level;
	}
	
	public Integer getScore() { return score; }
	public void setScore(Integer score) {
		this.score = score;
		int after = this.score;
		
		// First LVL
		if(after < 25) {
			this.level = 1;
			return;
		}
		
		int new_lvl = this.level;
		int old_lvl = new_lvl;
		int new_lvlScore = Score.getLVLScore(new_lvl);
		while(new_lvlScore <= after) {
			new_lvl ++;
			new_lvlScore = Score.getLVLScore(new_lvl);
		}
		this.level = new_lvl;
		
		// New lvl message
		if(old_lvl < new_lvl) {
			player.sendMessage("§aBravo !!§r Vous êtes à present niveau §a" + new_lvl);
		}
		
		/*
		 * UNLOCK SPE
		 */
		int spe_limit = RoleCraft.config.getInt("spe_limit");
		if(new_lvl >= spe_limit && !has_spe) {
			has_spe = true;
			String spe_str = getStringJob("fr");
			getTeam().move(player, RoleCraft.firstLetterToUpperCase(spe_str));
			String spe_color = getTeam().getColor();
			
			switch (job) {
			case 0:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + player.getDisplayName() + " written_book{pages:['[\"\",{\"text\":\"Bravo ! Tu es officiellement un joueur expérimenté de ce serveur, on espère sincèrement que cette aventure te plait et qu\\'elle va continuer à te plaire, c\\'est pour cela que dès à présent, au vu de ton niveau, tu deviens un \"},{\"text\":\"Éleveur \",\"color\":\"green\"},{\"text\":\"!\",\"color\":\"reset\"}]','{\"text\":\"Tu as donc le droit de faire des élevages d\\'animaux, de crafter des épées, de pêcher, de tuer des poissons, de traire des vaches, etc...\"}','{\"text\":\"Il est peut-être temps pour toi si ce n\\'est pas déjà fait de t\\'acheter une plus grande maison ou une boutique plus attrayante ou mieux placée en centre ville et aussi bien sur de profiter de tes nouvelles habilitées de travailleur expérimenté.\"}'],title:Eleveur,author:MartDel}");
				break;
			case 1:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + player.getDisplayName() + " written_book{pages:['[\"\",{\"text\":\"Bravo ! Tu es officiellement un joueur expérimenté de ce serveur, on espère sincèrement que cette aventure te plait et qu\\'elle va continuer à te plaire, c\\'est pour cela que dès à présent, au vu de ton niveau, tu deviens un \"},{\"text\":\"Armurier \",\"color\":\"red\"},{\"text\":\"!\",\"color\":\"reset\"}]','{\"text\":\"Tu as donc le droit de produire et vendre des armures en diamant, des potions, de faire des enchantements, je pense d\\'ailleurs que les aventuriers et gardes vont t\\'apprécier.\"}','{\"text\":\"Il est peut-être temps pour toi si ce n\\'est pas déjà fait de t\\'acheter une plus grande maison ou une boutique plus attrayante ou mieux placée en centre ville et aussi bien sur de profiter de tes nouvelles habilitées de travailleur expérimenté.\"}'],title:Armurier,author:MartDel}");
				break;
			case 2:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + player.getDisplayName() + " written_book{pages:['[\"\",{\"text\":\"Bravo ! Tu es officiellement un joueur expérimenté de ce serveur, on espère sincèrement que cette aventure te plait et qu\\'elle va continuer à te plaire, c\\'est pour cela que dès à présent, au vu de ton niveau, tu deviens un \"},{\"text\":\"Garde\",\"color\":\"yellow\"},{\"text\":\" !\",\"color\":\"reset\"}]','{\"text\":\"Tu es protecteur officiel des citoyens et de la ville. Tu peux dès à présent effectuer des missions de protection rémunérées, des règlements de comptes (à faire payer très cher),tu as également accès aux plastrons en diamant.\"}','{\"text\":\"Il est peut-être temps pour toi si ce n\\'est pas déjà fait de t\\'acheter une plus grande maison ou une boutique plus attrayante ou mieux placée en centre ville et aussi bien sur de profiter de tes nouvelles habilitées de travailleur expérimenté.\"}'],title:Garde,author:MartDel}");
				break;
			case 3:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + player.getDisplayName() + " written_book{pages:['[\"\",{\"text\":\"Bravo ! Tu es officiellement un joueur expérimenté de ce serveur, on espère sincèrement que cette aventure te plait et qu\\'elle va continuer à te plaire, c\\'est pour cela que dès à présent, au vu de ton niveau, tu deviens un \"},{\"text\":\"Ingénieur \",\"color\":\"light_purple\"},{\"text\":\"!\",\"color\":\"reset\"}]','{\"text\":\"Tu as donc le droit de produire et de créer des systèmes de redstone et de les proposer aux joueurs, que ce soit pour le style ou des fermes optimisées, ils apprécieront sûrement tes nouvelles constructions.\"}','{\"text\":\"Il est peut-être temps pour toi si ce n\\'est pas déjà fait de t\\'acheter une plus grande maison mieux placée en centre ville et aussi bien sur de profiter de tes nouvelles habilitées de travailleur expérimenté.\"}'],title:\"Ingénieur\",author:MartDel}");
				break;
			}
			
			player.sendMessage("§aBravo !!§r Vous avez obtenu votre spécialité : §" + spe_color + spe_str);
			return;
		}
	}

	public Integer getJob() { return this.job; }
	public String getStringJob(String lang) {
		if(job != null) {
			if(has_spe) {
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
		} else return "Nouveau";
	}
	public void setJob(Integer job) { this.job = job; }
	
	public Boolean isNew() { return this.job == null; }
	
	public Boolean hasSpe() { return this.has_spe; }
	public void setSpe(Boolean spe) { this.has_spe = spe; }
	
	public Map<String, Integer> getHouse() { return house; }
	public Integer getHouseId() { return house_id; }
	public void setHouse(Map<String, Integer> house) { this.house = house; }

	public Map<String, Integer> getShop() { return shop; }
	public Integer getShopId() { return shop_id; }
	public void setShop(Map<String, Integer> shop) { this.shop = shop; }

	public Map<String, Integer> getAdmin_ground() { return admin_ground; }
	public void setAdminGround(Map<String, Integer> admin_ground) { this.admin_ground = admin_ground; }
	
	public Map<String, Map<String, Integer>> getBuilds() { return builds; }
	public void addBuild(String name, Map<String, Integer> build) { builds.put(name, build); }
	public void removeBuild(String name) { builds.remove(name); }

	public Map<String, Map<String, Integer>> getFarms() { return farms; }
	public void addFarm(String name, Map<String, Integer> farm) { farms.put(name, farm); }
	public void removeFarm(String name) { farms.remove(name); }
	
	public void setPlayer(Player player) { this.player = player; }
	public Player getPlayer() { return player; }

}
