package fr.martdel.rolecraft.player;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.database.DatabaseManager;
import fr.martdel.rolecraft.deathroom.DeathKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CustomPlayer {
	public static final int DEFAULTHEARTS = 10;

	private Player player;
	private UUID uuid;
	private String ip;
	private boolean loaded;

	private RoleCraft plugin;
	private BukkitScheduler scheduler;
	private DatabaseManager db;

	private Boolean is_admin;
	private Integer level;
	private Integer score;
	private Boolean has_spe;
	private Integer job;

	private Location death_location;

	private Map<String, Integer> house;
	private Map<String, Integer> shop;
	private Map<String, Map<String, Integer>> farms;
	private Map<String, Map<String, Integer>> builds;
	private Map<String, Integer> admin_ground;

	private List<DeathKey> keys;

	public CustomPlayer(Player player, RoleCraft rolecraft) {
		this.player = player;
		this.uuid = player.getUniqueId();
		this.ip = null;
		this.loaded = false;

		this.plugin = rolecraft;
		this.db = rolecraft.getDB();
		this.scheduler = rolecraft.getServer().getScheduler();

		this.is_admin = false;
		this.level = 1;
		this.score = 0;
		this.has_spe = false;
		this.job = null;

		this.death_location = null;

		// Grounds
		this.house = null;
		this.shop = null;
		this.farms = new HashMap<>();
		this.builds = new HashMap<>();
		this.admin_ground = null;

		// Keys
		this.keys = new ArrayList<>();
	}

	/**
	 * Get all of items contains in the player's inventory
	 * @return List<ItemStack> A list of not null ItemStack
	 */
	public List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<>();
		for (ItemStack i : player.getInventory().getStorageContents()) {
			if (i != null) items.add(i);
		}
		for (ItemStack i : player.getInventory().getArmorContents()) {
			if (i != null) items.add(i);
		}
		for (ItemStack i : player.getInventory().getExtraContents()) {
			if (i != null) items.add(i);
		}
		return items;
	}
	/**
	 * Get all of weapons and armor contains in the player's inventory
	 * @return List<ItemStack> A list of not null ItemStack
	 */
	public List<ItemStack> getWeaponsAndArmor() {
		List<ItemStack> items = new ArrayList<>();
		for (ItemStack i : player.getInventory().getStorageContents()) {
			if (i != null){
				String type = i.getType().toString();
				if(type.contains("SWORD") || type.contains("BOW") || type.contains("ARROW")) items.add(i);
			}
		}
		for (ItemStack i : player.getInventory().getArmorContents()) {
			if (i != null) items.add(i);
		}
		for (ItemStack i : player.getInventory().getExtraContents()) {
			if (i != null) items.add(i);
		}
		return items;
	}
	/**
	 * Get all of items contains in the player's inventory (weapons and armor are excluded)
	 * @return List<ItemStack> A list of not null ItemStack
	 */
	public List<ItemStack> getMiscellaneousItems() {
		List<ItemStack> items = getItems();
		List<ItemStack> excludes = getWeaponsAndArmor();
		List<ItemStack> result = new ArrayList<>();
		for(ItemStack i : items){
			if(!excludes.contains(i)) result.add(i);
		}
		return result;
	}

	/**
	 * Get random items in the player's inventory and remove them
	 * @param lost_per Percentage of how many items will be lost
	 * @param room_per Percentage of how many items will drop into the room
	 * @param drops_per Percentage of how many items will be drop to the death location
	 * @return Map<String, List<ItemStack>> Sorted items
	 */
	public Map<String, List<ItemStack>> getDeathDrops(int lost_per, int room_per, int drops_per){
		List<ItemStack> lost = new ArrayList<>(), room = new ArrayList<>(), drops = new ArrayList<>();
		List<ItemStack> items = getItems(), temp = getItems();
		int nb_lost = partof(lost_per, items.size()), nb_room = partof(room_per, items.size());
		for (int i = 0; i < items.size(); i++) {
			int rand = new Random().nextInt(temp.size());
			if (i < nb_lost) lost.add(temp.get(rand));
			else if (i < (nb_lost + nb_room)) room.add(temp.get(rand));
			else drops.add(temp.get(rand));
			temp.remove(rand);
		}
		Map<String, List<ItemStack>> result = new HashMap<>();
		result.put("lost", lost); result.put("room", room); result.put("drops", drops);
		return result;
	}
	private int partof(int percentage, int total){
		return ((Double) Math.floor(total * (percentage/100.0))).intValue();
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
	 * Update the player's scoreboard
	 */
	public void updateScoreboard(){
		MainScoreboard sb = new MainScoreboard();
		sb.setObjective(this);
	}

	public void setWaiting(int score){
		plugin.getWaiting().setScore(player, score);
	}
	public int getWaiting(){
		return plugin.getWaiting().getScore(player);
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
				this.ip = result.getString("IP");
				this.is_admin = result.getByte("admin") == 1;
				this.level = result.getInt("level");
				this.score = result.getInt("score");
				this.job = result.getInt("job") == 0 ? null : result.getInt("job") - 1;
				this.has_spe = result.getByte("spe") == 1;

				// Load last death location
				PreparedStatement death_location = db.getConnection().prepareStatement("SELECT world, x, y, z FROM death_locations WHERE uuid=?");
				death_location.setString(1, uuid.toString());
				ResultSet result_death = death_location.executeQuery();
				if(result_death.next() && result_death.getString("world") != null) {
					this.death_location = new Location(
						Bukkit.getWorld(result_death.getString("world")),
						result_death.getInt("x"),
						result_death.getInt("y"),
						result_death.getInt("z")
					);
				}
				death_location.close();
				
				// Get house
				this.house = loadGround("houses");
				
				// Get shop
				this.shop = loadGround("shops");
				
				// Get farms
				this.farms = loadMultipleGrounds("farms");
				
				// Get builds
				this.builds = loadMultipleGrounds("builds");
				
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

				// Get keys
				PreparedStatement query_keys = db.getConnection().prepareStatement("SELECT * FROM death_keys WHERE owner_uuid=?");
				query_keys.setString(1, uuid.toString());
				ResultSet result_keys = query_keys.executeQuery();
				while(result_keys.next()) {
					DeathKey key = DeathKey.getKeyById(result_keys.getInt("key_id"));
					keys.add(key);
				}
				query_keys.close();
			}
			query.close();
			this.loaded = true;
		} catch (SQLException e) {
			DatabaseManager.error(e);
		}
		return this;
	}
	private Map<String, Integer> loadGround(String table) throws SQLException{
		Map<String, Integer> locations = new HashMap<>();
		PreparedStatement query = db.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE owner_uuid=?");
		query.setString(1, uuid.toString());
		ResultSet result = query.executeQuery();
		if(result.next()) {
			locations.put("x1", result.getInt("x1"));
			locations.put("z1", result.getInt("z1"));
			locations.put("x2", result.getInt("x2"));
			locations.put("z2", result.getInt("z2"));
		}
		query.close();
		return locations.isEmpty() ? null : locations;
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
		if(!loaded){
			try {
				throw new Exception("Player isn't loaded and you are tying to save it !");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		try {
			PreparedStatement update_player = db.getConnection().prepareStatement("UPDATE players SET IP=?, admin=?, level=?, score=?, job=?, spe=? WHERE uuid=?");
			update_player.setString(1, ip);
			update_player.setByte(2, (byte) (is_admin ? 1 : 0));
			update_player.setInt(3, level);
			update_player.setInt(4, score);
			update_player.setInt(5, job == null ? 0 : job + 1);
			update_player.setByte(6, (byte) (has_spe ? 1 : 0));
			update_player.setString(7, uuid.toString());
			update_player.executeUpdate();
			update_player.close();

			// Update last death location
			if(death_location != null){
				PreparedStatement update_death = db.getConnection().prepareStatement("UPDATE death_locations SET world=?, x=?, y=?, z=? WHERE uuid=?");
				update_death.setString(1, this.death_location.getWorld().getName());
				update_death.setInt(2, this.death_location.getBlockX());
				update_death.setInt(3, this.death_location.getBlockY());
				update_death.setInt(4, this.death_location.getBlockZ());
				update_death.setString(5, uuid.toString());
				update_death.executeUpdate();
				update_death.close();
			}
			
			// Update house
			updateGround("houses", house);

			// Update shop
			updateGround("shops", shop);
			
			// Update farm table
			updateMultipleGrounds("farms", farms);
			
			// Update build table
			updateMultipleGrounds("builds", builds);
			
			// Update admin_grounds table
			if(is_admin && admin_ground != null) {
				PreparedStatement update_admin = db.getConnection().prepareStatement("UPDATE admin_grounds SET x1=?, z1=?, x2=?, z2=? WHERE owner_uuid=?");
				update_admin.setInt(1, admin_ground.get("x1"));
				update_admin.setInt(2, admin_ground.get("z1"));
				update_admin.setInt(3, admin_ground.get("x2"));
				update_admin.setInt(4, admin_ground.get("z2"));
				update_admin.setString(5, uuid.toString());
				update_admin.executeUpdate();
				update_admin.close();
			}

			// Delete all owned keys
			PreparedStatement delete_keys = db.getConnection().prepareStatement("DELETE FROM death_keys WHERE owner_uuid=?");
			delete_keys.setString(1, uuid.toString());
			delete_keys.executeUpdate();
			delete_keys.close();
			if(!keys.isEmpty()) {
				// Insert keys
				for (DeathKey key : keys) {
					PreparedStatement insert_keys = db.getConnection().prepareStatement("INSERT INTO death_keys(key_id, owner_uuid) VALUES(?, ?)");
					insert_keys.setInt(1, key.getId());
					insert_keys.setString(2, uuid.toString());
					insert_keys.executeUpdate();
					insert_keys.close();
				}
			}
		} catch (SQLException e) {
			DatabaseManager.error(e);
		}
	}
	private void deleteGrounds(String table) throws SQLException {
		PreparedStatement delete = db.getConnection().prepareStatement("DELETE FROM " + table + " WHERE owner_uuid=?");
		delete.setString(1, uuid.toString());
		delete.executeUpdate();
		delete.close();
	}
	private void updateGround(String table, Map<String, Integer> data) throws SQLException {
		// Delete old grounds
		deleteGrounds(table);

		if(data == null) return;
		PreparedStatement insert;
		insert = db.getConnection().prepareStatement("INSERT INTO " + table + "(owner_uuid, x1, z1, x2, z2) VALUES(?,?,?,?,?)");
		insert.setString(1, uuid.toString());
		insert.setInt(2, data.get("x1"));
		insert.setInt(3, data.get("z1"));
		insert.setInt(4, data.get("x2"));
		insert.setInt(5, data.get("z2"));
		insert.executeUpdate();
		insert.close();
	}
	private void updateMultipleGrounds(String table, Map<String, Map<String, Integer>> data) throws SQLException {
		// Delete old grounds
		deleteGrounds(table);
		
		if(data.isEmpty()) return;
		// Insert new grounds
		for (String ground_name : data.keySet()) {
			Map<String, Integer> locations = data.get(ground_name);
			PreparedStatement insert = db.getConnection().prepareStatement("INSERT INTO " + table + "(x1, z1, x2, z2, name, owner_uuid) VALUES(?, ?, ?, ?, ?, ?)");
			insert.setInt(1, locations.get("x1"));
			insert.setInt(2, locations.get("z1"));
			insert.setInt(3, locations.get("x2"));
			insert.setInt(4, locations.get("z2"));
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
		setWaiting(0);
		try {
			PreparedStatement add = db.getConnection().prepareStatement("INSERT INTO players(uuid, pseudo) VALUES(?, ?)");
			add.setString(1, uuid.toString());
			add.setString(2, player.getDisplayName());
			add.executeUpdate();
			add.close();
			PreparedStatement death_location = db.getConnection().prepareStatement("INSERT INTO death_locations(uuid) VALUES(?)");
			death_location.setString(1, uuid.toString());
			death_location.executeUpdate();
			death_location.close();
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
	public String getIp() { return ip; }
	public void setIp(String ip) { this.ip = ip; }
	public boolean isLoaded() { return this.loaded; }
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

	public Location getDeathLocation() { return this.death_location; }
	public void setDeathLocation(Location death_location) { this.death_location = death_location; }
	
	public Map<String, Integer> getHouse() { return house; }
	public void setHouse(Map<String, Integer> house) { this.house = house; }

	public Map<String, Integer> getShop() { return shop; }
	public void setShop(Map<String, Integer> shop) { this.shop = shop; }

	public Map<String, Integer> getAdmin_ground() { return admin_ground; }
	public void setAdminGround(Map<String, Integer> admin_ground) { this.admin_ground = admin_ground; }
	
	public Map<String, Map<String, Integer>> getBuilds() { return builds; }
	public void addBuild(String name, Map<String, Integer> build) { builds.put(name, build); }
	public void removeBuild(String name) { builds.remove(name); }

	public Map<String, Map<String, Integer>> getFarms() { return farms; }
	public void addFarm(String name, Map<String, Integer> farm) { farms.put(name, farm); }
	public void removeFarm(String name) { farms.remove(name); }

	public List<DeathKey> getKeys() { return keys; }
	public void addKey(DeathKey key) { keys.add(key); }
	public void removeKey(DeathKey key) {
		for (int i = 0; i < keys.size(); i++){
			if(keys.get(i).equals(key)) {
				keys.remove(i);
				return;
			}
		}
	}
	public boolean hasKey(DeathKey key){ return keys.contains(key); }

	public void setPlayer(Player player) { this.player = player; }
	public Player getPlayer() { return player; }
}
