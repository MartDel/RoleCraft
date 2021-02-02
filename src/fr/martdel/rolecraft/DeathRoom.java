package fr.martdel.rolecraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeathRoom {

    private Location state_bloc;
    private Location spawnpoint;

    public DeathRoom(Location state_bloc, Location spawnpoint){
        this.state_bloc = state_bloc;
        this.spawnpoint = spawnpoint;
    }

    public void setCurrentlyUsed(boolean used){
        Block bloc = state_bloc.getWorld().getBlockAt(state_bloc);
        bloc.setType(used ? Material.REDSTONE_BLOCK : Material.AIR);
    }
    public boolean isCurrentlyUsed(){
        Block bloc = state_bloc.getWorld().getBlockAt(state_bloc);
        Material type = bloc.getType();
        System.out.println(type.equals(Material.REDSTONE_BLOCK));
        return type.equals(Material.REDSTONE_BLOCK);
    }

    public static List<DeathRoom> getAllRooms(){
        List<DeathRoom> result = new ArrayList<>();
        List<Map<?, ?>> room_list = RoleCraft.config.getMapList("deathrooms");
        for(Map<?, ?> el : room_list) {
            Map<String, ?> room_config = (Map<String, ?>) el;

            // Get room infos
            Map<String, Integer> state_bloc_info = (Map<String, Integer>) room_config.get("state_bloc");
            Location state_bloc = new Location(RoleCraft.OVERWORLD, state_bloc_info.get("x"), state_bloc_info.get("y"), state_bloc_info.get("z"));
            Map<String, Integer> spawnpoint_info = (Map<String, Integer>) room_config.get("spawnpoint");
            Location spawnpoint = new Location(RoleCraft.OVERWORLD, spawnpoint_info.get("x"), spawnpoint_info.get("y"), spawnpoint_info.get("z"));

            result.add(new DeathRoom(state_bloc, spawnpoint));
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
