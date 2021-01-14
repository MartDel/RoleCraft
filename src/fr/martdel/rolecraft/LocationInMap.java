package fr.martdel.rolecraft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
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
	
	public static final int MAP_YMIN = 50;
	public static final int GROUND_YMIN = 50;
	public static final int GROUND_YMAX = 50;
	
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
		try {
			Connection db = plugin.getDB().getConnection();
			Location location = player.getLocation();
			
			// Check grounds
			LocationInMap[] to_check = {HOUSE, SHOP, BUILD, FARM};
			for (LocationInMap ground : to_check) {
				LocationInMap check = checkGround(location, player.getUniqueId().toString(), db, ground);
				if(check != null) return check;
			}
			
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
		return getPlayerPlace(plugin, player) != null;
	}
	
	// Private methods
	
	private static LocationInMap checkGround(Location location, String uuid, Connection db, LocationInMap table) throws SQLException {
		PreparedStatement search = db.prepareStatement("SELECT * FROM " + table.toString().toLowerCase() + "s");
		ResultSet result = search.executeQuery();
		while(result.next()) {
			Integer[] ground = {
				result.getInt("x1"),
				result.getInt("x2"),
				result.getInt("z1"),
				result.getInt("z2")
			};
			if(isIn(location, ground)) {
				if(result.getString("uuid").equalsIgnoreCase(uuid)) {
					return OWNED;
				} else {
					return table;
				}
			}
		}
		search.close();
		return null;
	}
	private static boolean isIn(Location location, Integer[] ground) {
		int xm = (ground[0] - ground[1]) / 2;
		int zm = (ground[2] - ground[3]) / 2;
		if(Math.abs(location.getBlockX() - xm) <= Math.abs(ground[0] - xm)
		&& Math.abs(location.getBlockZ() - zm) <= Math.abs(ground[2] - zm)
		&& location.getBlockY() > GROUND_YMIN && location.getBlockY() < GROUND_YMAX) {
			return true;
		}
		return false;
	}
	private static boolean isInMap(Location location, Integer[] ground) {
		int xm = (ground[0] - ground[1]) / 2;
		int zm = (ground[2] - ground[3]) / 2;
		if(Math.abs(location.getBlockX() - xm) <= Math.abs(ground[0] - xm)
		&& Math.abs(location.getBlockZ() - zm) <= Math.abs(ground[2] - zm)
		&& location.getBlockY() > MAP_YMIN) {
			return true;
		}
		return false;
	}

}
