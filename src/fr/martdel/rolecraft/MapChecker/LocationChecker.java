package fr.martdel.rolecraft.MapChecker;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.database.DatabaseManager;
import org.bukkit.Bukkit;
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

import static fr.martdel.rolecraft.MapChecker.LocationInMap.*;

public class LocationChecker {

    public static final int MAP_YMIN = RoleCraft.config.getInt("ground.map");
    public static final int GROUND_YMIN = RoleCraft.config.getInt("ground.underground");
    public static final int GROUND_YMAX = RoleCraft.config.getInt("ground.floor");

    private final RoleCraft plugin;
    private final Location loc;
    private final Player player;
    private final String uuid;

    public LocationChecker(Location location, Player player, RoleCraft plugin){
        this.plugin = plugin;
        this.loc = location;
        this.player = player;
        this.uuid = player.getUniqueId().toString();
    }

    public LocationChecker(Player player, RoleCraft plugin){
        this.plugin = plugin;
        this.player = player;
        this.uuid = player.getUniqueId().toString();
        this.loc = player.getLocation();
    }

    /**
     * Check if "loc" is a location free to interact
     * @return if it's true then player can interact
     */
    public boolean isFree(){
        List<LocationInMap> types = getType();
        return types.contains(FREE_PLACE) || types.contains(OWNED);
    }

    /**
     * Get where "loc" is locate in the world
     * @return Free place or Protected map or a list of ground type (house, shop, farm, build)
     */
    public List<LocationInMap> getType(){
        try {
            Connection db = plugin.getDB().getConnection();

            // Check protected map
            if(!isInProtectedMap(loc)) return Collections.singletonList(FREE_PLACE);
            List<LocationInMap> res = new ArrayList<>();
            res.add(PROTECTED_MAP);

            // Check grounds
            LocationInMap[] to_check = {HOUSE, SHOP, FARM};
            for (LocationInMap ground_type : to_check) {
                Ground check = checkGround(db, ground_type);
                if(check.exist()){
                    res.add(check.getGroundType());
                    break;
                }
            }

            // Check build grounds
            Ground check = checkGround(db, BUILD);
            if(check.exist()) res.add(check.getGroundType());

            if(res.size() == 0) return Collections.singletonList(FREE_PLACE);
            else return res;
        } catch (SQLException e) {
            DatabaseManager.error(e);
        }
        return Collections.singletonList(FREE_PLACE);
    }

    /**
     * Get the player who owned the ground where "loc" is located
     * @return An OfflinePlayer instance or null
     */
    public OfflinePlayer getOwner(){
        try {
            Connection db = plugin.getDB().getConnection();

            // Check protected map
            if(!isInProtectedMap(loc)) return null;

            // Check grounds
            LocationInMap[] to_check = {HOUSE, SHOP, FARM};
            for (LocationInMap ground : to_check) {
                Ground check = checkGround(db, ground);
                if(check.exist()) return check.getPlayer();
            }
        } catch (SQLException e) {
            DatabaseManager.error(e);
        }
        return null;
    }

    /**
     * Get builders who can edit the ground
     * @return A list of OfflinePlayer instance : can be void
     */
    public List<OfflinePlayer> getBuilders(){
        List<OfflinePlayer> builders = new ArrayList<>();
        try {
            Connection db = plugin.getDB().getConnection();

            // Check protected map
            if(!isInProtectedMap(loc)) return builders;

            // Check build grounds
            PreparedStatement search = db.prepareStatement("SELECT * FROM builds");
            ResultSet result = search.executeQuery();
            while(result.next()) {
                Integer[] ground = {
                        result.getInt("x1"),
                        result.getInt("x2"),
                        result.getInt("z1"),
                        result.getInt("z2")
                };
                if(isIn(loc, ground)) {
                    if(result.getString("owner_uuid").equalsIgnoreCase(uuid)) builders.add(player);
                    else builders.add(Bukkit.getOfflinePlayer(result.getString("owner_uuid")));
                    break;
                }
            }
            search.close();
        } catch (SQLException e) {
            DatabaseManager.error(e);
        }
        return builders;
    }

    // Static methods

    /**
     * Check if the specified location is in protected maps
     * @param location The location to check
     * @return true if the location is in a protected map
     */
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
     * @param db The database to read
     * @param table The ground type to check
     * @return The ground type if location is contained, OWNED if the ground is owned by the player, null if nothing was found
     * @throws SQLException SQL / Database Error
     */
    private Ground checkGround(Connection db, LocationInMap table) throws SQLException {
        Ground res = new Ground();
        PreparedStatement search = db.prepareStatement("SELECT * FROM " + table.toString().toLowerCase() + "s");
        ResultSet result = search.executeQuery();
        while(result.next()) {
            System.out.println("found a " + table);
            Integer[] ground = {
                    result.getInt("x1"),
                    result.getInt("x2"),
                    result.getInt("z1"),
                    result.getInt("z2")
            };
            if(isIn(loc, ground)) {
                if(result.getString("owner_uuid").equalsIgnoreCase(uuid)){
                    res.setGroundType(OWNED);
                    res.setUuid(uuid);
                } else {
                    res.setGroundType(table);
                    res.setUuid(result.getString("owner_uuid"));
                }
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
