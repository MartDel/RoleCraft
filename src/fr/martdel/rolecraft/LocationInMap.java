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
	
	OWNED, HOUSE, FARM, SHOP, BUILD, PROTECTED_MAP;	
	
	public static final int MAP_YMIN = 50;
	public static final int GROUND_YMIN = 50;
	public static final int GROUND_YMAX = 50;
	
	public static List<Integer[]> getProtectedMapLocations() {
		List<Integer[]> locations = new ArrayList<>();
		
		List<Map<?, ?>> maps_config = RoleCraft.config.getMapList("maps");
		for (Map<?, ?> map_config : maps_config) {
			@SuppressWarnings("unchecked")
			Map<String, Integer> map = (Map<String, Integer>) map_config;
			
			int x1 = map.get("x1");
			int z1 = map.get("z1");
			int x2 = map.get("x2");
			int z2 = map.get("z2");
			Integer[] locs = {x1, z1, x2, z2};
			
			locations.add(locs);
		}
		
		return locations;
	}
	
	public static LocationInMap getPlayerPlace(RoleCraft plugin, Player player) {		
		try {
			Connection db = plugin.getDB().getConnection();
			Location location = player.getLocation();
			
			checkGround(location, player.getUniqueId().toString(), db, HOUSE);
			
			PreparedStatement houses = db.prepareStatement("SELECT * FROM houses");
			ResultSet result = houses.executeQuery();
			while(result.next()) {
				int[] house = {
					result.getInt("x1"),
					result.getInt("x2"),
					result.getInt("z1"),
					result.getInt("z2")
				};
				if(isIn(location, house)) {
					if(result.getString("uuid").equalsIgnoreCase(player.getUniqueId().toString())) {
						return OWNED;
					} else {
						return HOUSE;
					}
				}
			}
			houses.close();
			
			PreparedStatement shops = db.prepareStatement("SELECT * FROM shops");
			result = shops.executeQuery();
			while(result.next()) {
				int[] shop = {
					result.getInt("x1"),
					result.getInt("x2"),
					result.getInt("z1"),
					result.getInt("z2")
				};
				if(isIn(location, shop)) {
					if(result.getString("uuid").equalsIgnoreCase(player.getUniqueId().toString())) {
						return OWNED;
					} else {
						return SHOP;
					}
				}
			}
			shops.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	private static LocationInMap checkGround(Location location, String uuid, Connection db, LocationInMap table) throws SQLException {
		PreparedStatement search = db.prepareStatement("SELECT * FROM " + table.toString().toLowerCase() + "s");
		ResultSet result = search.executeQuery();
		while(result.next()) {
			int[] ground = {
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
	
	private static boolean isIn(Location location, int[] house) {
		int xm = (house[0] - house[1]) / 2;
		int zm = (house[2] - house[3]) / 2;
		if(Math.abs(location.getBlockX() - xm) <= Math.abs(house[0] - xm)
		&& Math.abs(location.getBlockZ() - zm) <= Math.abs(house[2] - zm)
		&& location.getBlockY() > GROUND_YMIN && location.getBlockY() < GROUND_YMAX) {
			return true;
		}
		return false;
	}

	public static boolean isInProtectedPlace(RoleCraft plugin, Player player) {
		return getPlayerPlace(plugin, player) != null;
	}

}
