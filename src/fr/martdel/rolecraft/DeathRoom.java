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
    private Location[] itemsspawns;
    private Location weaponspawn;

    public DeathRoom(Location state_bloc, Location spawnpoint, Location[] itemsspawns, Location weaponspawn){
        this.state_bloc = state_bloc;
        this.spawnpoint = spawnpoint;
        this.itemsspawns = itemsspawns;
        this.weaponspawn = weaponspawn;
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
        player.playCinematic(spawnpoint, 60);
        setCurrentlyUsed(true);

        List<ItemStack> items = player.getMiscellaneousItems();
        List<ItemStack> weapons = player.getWeaponsAndArmor();
        player.getPlayer().getInventory().clear();

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskLater(plugin, new Runnable() {
            private int i = 0;
            @Override
            public void run() {
                boolean stop = true;

                if(i < items.size()){
                    // Spawn miscellaneous items
                    if(i % 2 == 0) RoleCraft.OVERWORLD.dropItem(itemsspawns[0], items.get(i));
                    else RoleCraft.OVERWORLD.dropItem(itemsspawns[1], items.get(i));
                    stop = false;
                }
                if(i < weapons.size()){
                    // Spawn weapons and armor
                    RoleCraft.OVERWORLD.dropItem(weaponspawn, weapons.get(i));
                    stop = false;
                }

                if(!stop && isCurrentlyUsed()) scheduler.runTaskLater(plugin, this, 20);
                i++;
            }
        }, 20);
    }

    @SuppressWarnings("unchecked")
    public static List<DeathRoom> getAllRooms(){
        List<DeathRoom> result = new ArrayList<>();
        List<Map<?, ?>> room_list = RoleCraft.config.getMapList("deathrooms");
        for(Map<?, ?> el : room_list) {
            Map<String, ?> room_config = (Map<String, ?>) el;

            // Get room_state
            Map<String, Integer> state_bloc_info = (Map<String, Integer>) room_config.get("state_bloc");
            Location state_bloc = new Location(RoleCraft.OVERWORLD, state_bloc_info.get("x"), state_bloc_info.get("y"), state_bloc_info.get("z"));

            // Get room spawn
            Location spawnpoint = RoleCraft.getConfigLocation(room_config.get("spawnpoint"));
            Map<String, Integer> orientation = (Map<String, Integer>) room_config.get("cinematic_view");
            spawnpoint.setYaw(orientation.get("yaw").floatValue());
            spawnpoint.setPitch(orientation.get("pitch").floatValue());

            // Get items spawns
            Location weaponspawn = RoleCraft.getConfigLocation(room_config.get("weapons_spawnpoint"));
            Location[] itemspawns = {
                RoleCraft.getConfigLocation(((Map<String, ?>) room_config.get("items_spawnpoints")).get("1")),
                RoleCraft.getConfigLocation(((Map<String, ?>) room_config.get("items_spawnpoints")).get("2"))
            };

            result.add(new DeathRoom(state_bloc, spawnpoint, itemspawns, weaponspawn));
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
