package fr.martdel.rolecraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeathRoom {

    private Location state_bloc;
    private Location spawnpoint;
    private Location itemsspawn;
    private Cinematic cinematic1;
    private Cinematic cinematic2;

    public DeathRoom(Location state_bloc, Location spawnpoint, Location itemsspawn, Cinematic c1, Cinematic c2){
        this.state_bloc = state_bloc;
        this.spawnpoint = spawnpoint;
        this.itemsspawn = itemsspawn;
        this.cinematic1 = c1;
        this.cinematic2 = c2;
    }

    public void setCurrentlyUsed(boolean used){
        Block bloc = state_bloc.getWorld().getBlockAt(state_bloc);
        bloc.setType(used ? Material.REDSTONE_BLOCK : Material.AIR);
    }
    public boolean isCurrentlyUsed(){
        Block bloc = state_bloc.getWorld().getBlockAt(state_bloc);
        Material type = bloc.getType();
        return type.equals(Material.REDSTONE_BLOCK);
    }

    public void spawnPlayer(CustomPlayer player, PlayerRespawnEvent event, RoleCraft plugin){
        event.setRespawnLocation(spawnpoint);
        spawnPlayer(player, plugin);
    }
    public void spawnPlayer(CustomPlayer player, RoleCraft plugin){
        player.setWaiting(2);
        player.playCinematic(spawnpoint, 60, null);
        setCurrentlyUsed(true);

        List<ItemStack> items = player.getItems();
        player.getPlayer().getInventory().clear();
    }

    @SuppressWarnings("unchecked")
    public static List<DeathRoom> getAllRooms(){
        List<DeathRoom> result = new ArrayList<>();
        List<Map<?, ?>> room_list = RoleCraft.config.getMapList("deathrooms");
        for(Map<?, ?> el : room_list) {
            Map<String, ?> room_config = (Map<String, ?>) el;

            // Get room_state
            Location state_bloc = RoleCraft.getConfigLocation(room_config.get("state_bloc"), false);

            // Get room spawn
            Location spawnpoint = RoleCraft.getConfigLocation(room_config.get("spawnpoint"), true);

            // Get items spawn
            Location itemspawn = RoleCraft.getConfigLocation(room_config.get("items_spawnpoint"), false);

            // Get cinematics
            Map<String, ?> cinematic1_config = (Map<String, ?>) room_config.get("cinematic1");
            Location cinematic1_loc = RoleCraft.getConfigLocation(cinematic1_config.get("location"), true);
            Integer cinematic1_time = (Integer) cinematic1_config.get("duration");
            Cinematic cinematic1 = new Cinematic(cinematic1_loc, cinematic1_time);

            Map<String, ?> cinematic2_config = (Map<String, ?>) room_config.get("cinematic1");
            Location cinematic2_loc = RoleCraft.getConfigLocation(cinematic2_config.get("location"), true);
            Integer cinematic2_time = (Integer) cinematic2_config.get("duration");
            Cinematic cinematic2 = new Cinematic(cinematic2_loc, cinematic2_time);

            result.add(new DeathRoom(state_bloc, spawnpoint, itemspawn, cinematic1, cinematic2));
        }
        return result;
    }

    public Location getState_bloc() {
        return state_bloc;
    }
    public Location getSpawnpoint() {
        return spawnpoint;
    }
}
