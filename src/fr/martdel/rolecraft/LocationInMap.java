package fr.martdel.rolecraft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.martdel.rolecraft.database.DatabaseManager;

public enum LocationInMap {
	
	OWNED("votre terrain", 1),
	HOUSE("la maison d'un joueur", 2),
	FARM("la ferme d'un fermier", 2),
	SHOP("le magasin d'un joueur", 2),
	BUILD("le terrain de construction d'un builder", 2),
	PROTECTED_MAP("le village ou dans la périphérie", 3),
	FREE_PLACE("une zone libre", 4);
	
	public static final int MAP_YMIN = RoleCraft.config.getInt("ground.map");
	public static final int GROUND_YMIN = RoleCraft.config.getInt("ground.underground");
	public static final int GROUND_YMAX = RoleCraft.config.getInt("ground.floor");

	private String description;
	private int priority;

	LocationInMap(String desc, int priority) {
		this.description = desc;
		this.priority = priority;
	}
	
	public String getDescription() {
		return this.description;
	}
	public int getPriority() { return priority; }
	
	// Static methods
	
	/**
	 * Check if the player is in a protected place
	 * @param plugin RoleCraft plugin
	 * @param player The player to check
	 * @return boolean
	 */
	public static boolean isInProtectedPlace(RoleCraft plugin, Player player) {
		List<LocationInMap> player_places = getPlayerPlace(plugin, player);
		return !player_places.contains(OWNED) || !player_places.contains(FREE_PLACE);
	}
	public static boolean isInProtectedPlace(RoleCraft plugin, Player player, Location location) {
		List<LocationInMap> bloc_places = getBlocPlace(plugin, player, location);
		System.out.println(bloc_places);
		return !bloc_places.contains(OWNED) || !bloc_places.contains(FREE_PLACE);
	}

	public static Map<String, OfflinePlayer> getPlayerOwner(RoleCraft plugin, Player searchingPlayer, Location location) {
		List<LocationInMap> ground_type = getBlocPlace(plugin, searchingPlayer, location);
		if(ground_type.contains(FREE_PLACE) || ground_type.contains(OWNED)) return null;

		Map<String, OfflinePlayer> res = new HashMap<>();
		Connection db;
		try {
			db = plugin.getDB().getConnection();
			for(LocationInMap loc : ground_type){
				String table = loc.toString().toLowerCase();
				if(!table.equalsIgnoreCase("build")){
					PreparedStatement search = db.prepareStatement("SELECT * FROM " + table + "s");
					ResultSet result = search.executeQuery();
					while(result.next()) {
						Integer[] ground = {
							result.getInt("x1"),
							result.getInt("x2"),
							result.getInt("z1"),
							result.getInt("z2")
						};
						if(isIn(location, ground)) res.put(table, plugin.getServer().getOfflinePlayer(result.getString("uuid")));
					}
					search.close();
				}
			}
		} catch (SQLException e) {
			DatabaseManager.error(e);
		}

		return res;
	}
}
