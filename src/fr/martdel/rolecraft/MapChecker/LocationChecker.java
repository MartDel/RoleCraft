package fr.martdel.rolecraft.MapChecker;

import fr.martdel.rolecraft.LocationInMap;
import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.database.DatabaseManager;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static fr.martdel.rolecraft.LocationInMap.*;

public class LocationChecker {

    private final RoleCraft plugin;
    private final Location loc;
    private final Player player;

    public LocationChecker(Location location, Player player, RoleCraft plugin){
        this.plugin = plugin;
        this.loc = location;
        this.player = player;
    }

    public List<LocationInMap> getType(){
        try {
            Connection db = plugin.getDB().getConnection();

            // Check protected map
            if(!isInProtectedMap(loc)) return Collections.singletonList(FREE_PLACE);

            // Check grounds
            List<LocationInMap> res = new ArrayList<>();
            LocationInMap[] to_check = {HOUSE, SHOP, FARM, BUILD};
            for (LocationInMap ground : to_check) {
                LocationInMap check = checkGround(loc, player.getUniqueId().toString(), db, ground);
                if(check != null) res.add(check);
            }

            if(res.size() == 0) return Collections.singletonList(FREE_PLACE);
            else return res;
        } catch (SQLException e) {
            DatabaseManager.error(e);
        }
        return Collections.singletonList(FREE_PLACE);
    }

    public OfflinePlayer getOwner(){ return null; }
    public List<OfflinePlayer> getBuilders(){ return null; }

    // Static methods

    public static boolean isInProtectedMap(Location location){
        // Get map borders
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

        // Check for all protected maps
        for(Integer[] map_locations : locations) {
            int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
            int x1 = Math.min(map_locations[0], map_locations[1]), x2 = Math.max(map_locations[0], map_locations[1]);
            int z1 = Math.min(map_locations[2], map_locations[3]), z2 = Math.max(map_locations[2], map_locations[3]);
            if( (x >= x1 && x <= x2) && (z >= z1 && z <= z2) && y >= MAP_YMIN ) return true;
        }
        return false;
    }

    // Private methods

    /**
     * Check if a location is contained in a specific ground type
     * @param location The location to check
     * @param uuid The player uuid
     * @param db The database to read
     * @param table The ground type to check
     * @return The ground type if location is contained, OWNED if the ground is owned by the player, null if nothing was found
     * @throws SQLException SQL / Database Error
     */
    private LocationInMap checkGround(Location location, String uuid, Connection db, LocationInMap table) throws SQLException {
        PreparedStatement search = db.prepareStatement("SELECT * FROM " + table.toString().toLowerCase() + "s");
        ResultSet result = search.executeQuery();
        LocationInMap res = null;
        while(result.next()) {
            System.out.println("found a " + table);
            Integer[] ground = {
                    result.getInt("x1"),
                    result.getInt("x2"),
                    result.getInt("z1"),
                    result.getInt("z2")
            };
            if(isIn(location, ground)) {
                if(result.getString("owner_uuid").equalsIgnoreCase(uuid)) res = OWNED;
                else res = table;
                break;
            }
        }
        search.close();
        return res;
    }

    /**
     * Check if a location is contained in a specified area
     * @param location The location to check
     * @param ground The area where the location is supposed to be
     * @return If the location is contained or not
     */
    private static boolean isIn(Location location, Integer[] ground) {
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
        int x1 = Math.min(ground[0], ground[1]), x2 = Math.max(ground[0], ground[1]);
        int z1 = Math.min(ground[2], ground[3]), z2 = Math.max(ground[2], ground[3]);
        return (x >= x1 && x <= x2) && (z >= z1 && z <= z2) && (y >= GROUND_YMIN && y <= GROUND_YMAX);
    }

}
