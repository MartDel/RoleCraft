package fr.martdel.rolecraft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.martdel.rolecraft.database.DatabaseManager;

public enum LocationInMap {
	
	OWNED("votre terrain"),
	HOUSE("la maison d'un joueur"),
	FARM("la ferme d'un fermier"),
	SHOP("le magasin d'un joueur"),
	BUILD("le terrain de construction d'un builder"),
	PROTECTED_MAP("le village ou dans la périphérie"),
	FREE_PLACE("une zone libre");
	
	public static final int MAP_YMIN = RoleCraft.config.getInt("ground.map");
	public static final int GROUND_YMIN = RoleCraft.config.getInt("ground.underground");
	public static final int GROUND_YMAX = RoleCraft.config.getInt("ground.floor");
	
	private String description;
	
	private LocationInMap(String desc) {
		this.description = desc;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	// Static methods
	
	/**
	 * Get the protected maps coordinates
	 * @return List<Integer[]>
	 */
	public static List<Integer[]> getProtectedMapLocations() {
		List<Integer[]> locations = new ArrayList<>();
		
		List<Map<?, ?>> maps_config = RoleCraft.config.getMapList("maps");
		for (Map<?, ?> map_config : maps_config) {
			@SuppressWarnings("unchecked")
			Map<String, Integer> map = (Map<String, Integer>) map_config;
			
			int x1 = map.get("x1");
			int x2 = map.get("x2");
			int z1 = map.get("z1");
			int z2 = map.get("z2");
			Integer[] locs = {x1, x2, z1, z2};
			
			locations.add(locs);
		}
		
		return locations;
	}
	
	/**
	 * Find where the player is in the map
	 * @param plugin RoleCraft plugin
	 * @param player The player to check
	 * @return LocationInMap
	 */
	public static LocationInMap getPlayerPlace(RoleCraft plugin, Player player) {		
		return getBlocPlace(plugin, player, player.getLocation());
	}
	public static LocationInMap getBlocPlace(RoleCraft plugin, Player player, Location location) {		
		try {
			Connection db = plugin.getDB().getConnection();
			
			// Check grounds
			LocationInMap check = null;
			LocationInMap[] to_check = {HOUSE, SHOP, BUILD, FARM};
			for (LocationInMap ground : to_check) {
				check = checkGround(location, player.getUniqueId().toString(), db, ground);
			}
			if(check != null) return check;

			// Check protected map
			for(Integer[] map_locations : getProtectedMapLocations()) {
				if(isInMap(location, map_locations)) return PROTECTED_MAP;
			}
			
		} catch (SQLException e) {
			DatabaseManager.error(e);
		}
		return FREE_PLACE;
	}
	
	/**
	 * Check if the player is in a protected place
	 * @param plugin RoleCraft plugin
	 * @param player The player to check
	 * @return boolean
	 */
	public static boolean isInProtectedPlace(RoleCraft plugin, Player player) {
		LocationInMap player_place = getPlayerPlace(plugin, player);
		return player_place != OWNED && player_place != FREE_PLACE;
	}
	public static boolean isInProtectedPlace(RoleCraft plugin, Player player, Location location) {
		LocationInMap player_place = getBlocPlace(plugin, player, location);
		return player_place != OWNED && player_place != FREE_PLACE;
	}

	public static OfflinePlayer getPlayerOwner(RoleCraft plugin, Player searchingPlayer, Location location) {
		LocationInMap ground_type = getBlocPlace(plugin, searchingPlayer, location);
		if(ground_type.equals(FREE_PLACE) || ground_type.equals(OWNED) || ground_type.equals(PROTECTED_MAP)) return null;
		String table = ground_type.toString().toLowerCase();

		OfflinePlayer r = null;
		Connection db;
		try {
			db = plugin.getDB().getConnection();
			PreparedStatement search = db.prepareStatement("SELECT * FROM " + table + "s");
			ResultSet result = search.executeQuery();
			while(result.next()) {
				Integer[] ground = {
						result.getInt("x1"),
						result.getInt("x2"),
						result.getInt("z1"),
						result.getInt("z2")
				};
				if(isIn(location, ground)) r = plugin.getServer().getOfflinePlayer(result.getString("uuid"));
			}
			search.close();
		} catch (SQLException e) {
			DatabaseManager.error(e);
		}

		return r;
	}
	
	// Private methods
	
	private static LocationInMap checkGround(Location location, String uuid, Connection db, LocationInMap table) throws SQLException {
		PreparedStatement search = db.prepareStatement("SELECT * FROM " + table.toString().toLowerCase() + "s");
		ResultSet result = search.executeQuery();
		LocationInMap r = null;
		while(result.next()) {
			System.out.println("found a " + table);
			Integer[] ground = {
				result.getInt("x1"),
				result.getInt("x2"),
				result.getInt("z1"),
				result.getInt("z2")
			};
			if(isIn(location, ground)) {
				if(result.getString("owner_uuid").equalsIgnoreCase(uuid))  r = OWNED;
				else r = table;
			}
		}
		search.close();
		return r;
	}
	private static boolean isIn(Location location, Integer[] ground) {
		int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
		int x1 = Math.min(ground[0], ground[1]), x2 = Math.max(ground[0], ground[1]);
		int z1 = Math.min(ground[2], ground[3]), z2 = Math.max(ground[2], ground[3]);
		return (x >= x1 && x <= x2) && (z >= z1 && z <= z2) && (y >= GROUND_YMIN && y <= GROUND_YMAX);
	}
	private static boolean isInMap(Location location, Integer[] ground) {
		int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
		int x1 = Math.min(ground[0], ground[1]), x2 = Math.max(ground[0], ground[1]);
		int z1 = Math.min(ground[2], ground[3]), z2 = Math.max(ground[2], ground[3]);
		return (x >= x1 && x <= x2) && (z >= z1 && z <= z2) && y >= MAP_YMIN;
	}

}
