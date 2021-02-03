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
        player.setWaiting(2);
        event.setRespawnLocation(spawnpoint);
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

                if(!stop) scheduler.runTaskLater(plugin, this, 20);
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
            Map<String, Integer> spawnpoint_info = (Map<String, Integer>) room_config.get("spawnpoint");
            Map<String, Integer> orientation = (Map<String, Integer>) room_config.get("cinematic_view");
            Location spawnpoint = new Location(
                RoleCraft.OVERWORLD,
                spawnpoint_info.get("x"),
                spawnpoint_info.get("y"),
                spawnpoint_info.get("z"),
                orientation.get("yaw").floatValue(),
                orientation.get("pitch").floatValue()
            );

            // Get items spawns
            Map<String, Integer> weaponspawn_info = (Map<String, Integer>) room_config.get("weapons_spawnpoint");
            Map<String, Integer> items_spawnpoint1 = (Map<String, Integer>) ((Map<String, ?>) room_config.get("items_spawnpoints")).get("1");
            Map<String, Integer> items_spawnpoint2 = (Map<String, Integer>) ((Map<String, ?>) room_config.get("items_spawnpoints")).get("2");
            Location weaponspawn = new Location(RoleCraft.OVERWORLD, weaponspawn_info.get("x"), weaponspawn_info.get("y"), weaponspawn_info.get("z"));
            Location[] itemspawns = {
                new Location(RoleCraft.OVERWORLD, items_spawnpoint1.get("x"), items_spawnpoint1.get("y"), items_spawnpoint1.get("z")),
                new Location(RoleCraft.OVERWORLD, items_spawnpoint2.get("x"), items_spawnpoint2.get("y"), items_spawnpoint2.get("z"))
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
