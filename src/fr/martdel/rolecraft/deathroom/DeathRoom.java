package fr.martdel.rolecraft.deathroom;

import fr.martdel.rolecraft.Cinematic;
import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.GUI;
import fr.martdel.rolecraft.RoleCraft;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DeathRoom {

    private final int id;
    private final Location state_bloc;
    private final Location spawnpoint;
    private final Location itemsspawn;
    private final Cinematic cinematic1;
    private final Cinematic cinematic2;

    public DeathRoom(int id, Location state_bloc, Location spawnpoint, Location itemsspawn, Cinematic c1, Cinematic c2){
        this.id = id;
        this.state_bloc = state_bloc;
        this.spawnpoint = spawnpoint;
        this.itemsspawn = itemsspawn;
        this.cinematic1 = c1;
        this.cinematic2 = c2;
    }

    /**
     * Manage redstone block next to the room
     * to mark the room as used or not
     * @param used If the room is currently used or not
     */
    public void setCurrentlyUsed(boolean used){
        Block bloc = state_bloc.getWorld().getBlockAt(state_bloc);
        bloc.setType(used ? Material.REDSTONE_BLOCK : Material.AIR);
    }
    public boolean isCurrentlyUsed(){
        Block bloc = state_bloc.getWorld().getBlockAt(state_bloc);
        Material type = bloc.getType();
        return type.equals(Material.REDSTONE_BLOCK);
    }

    /**
     * Spawn a player into the room
     * @param player The player to spawn
     * @param event The respawn event to update (setRespawnPoint)
     * @param plugin Instance of RoleCraft plugin
     */
    public void spawnPlayer(CustomPlayer player, PlayerRespawnEvent event, RoleCraft plugin){
        event.setRespawnLocation(spawnpoint);
        spawnPlayer(player, plugin);
    }
    public void spawnPlayer(CustomPlayer player, RoleCraft plugin){
        player.setWaiting(2);
        setCurrentlyUsed(true);

        List<DeathKey> keys = player.loadData().getKeys();
        if(keys.isEmpty()) {
            spawnPlayer(player, plugin, null);
            return;
        }

        // Show GUI to choose or not to choose a key
        GUI choose_key = new GUI("§7Room " + id + " - §aChoisir une clé?", 9);
        for (int i = 0; i < keys.size(); i++) {
            choose_key.setItem(i, keys.get(i).getItem());
        }
        choose_key.setItem(8, GUI.createItem(Material.BARRIER, "§4Ne pas choisir de clé", new ArrayList<>(), 8));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.getPlayer().openInventory(choose_key.getInventory());
        }, 20);
    }

    /**
     * Update player inventory with the chosen DeathKey data
     * @param player The player to update (instance of CustomPlayer)
     * @param plugin Instance of RoleCraft plugin
     * @param chosen_key The key chosen by the player
     */
    public void spawnPlayer(CustomPlayer player, RoleCraft plugin, DeathKey chosen_key){
        if(chosen_key != null){
            Map<String, List<ItemStack>> inventory = player.getDeathDrops(chosen_key.getLost(), chosen_key.getRoomDrop(), chosen_key.getDrop());
            System.out.println(inventory.get("lost"));
            System.out.println(inventory.get("room"));
            System.out.println(inventory.get("drops"));

            List<String> msg = Arrays.asList(
                    "Vous avez choisi la clé §a" + chosen_key.toString() + "§r.",
                "Vous avez perdu §c" + inventory.get("lost").size() + " stack(s)§r.",
                "Vous pouvez recupérer §a" + inventory.get("room").size() + " stack(s)§r dans la salle.",
                "Vous pouvez recupérer §2" + inventory.get("drops").size() + " stack(s)§r à l'endroit de votre mort"
            );
            for (String s : msg) {
                player.getPlayer().sendMessage(s);
            }
        } else player.getPlayer().sendMessage("Vous n'avez pas choisi de clé.");
    }

    /**
     * Get all DeathRooms on the map
     * @return List<DeathRoom> All of the DeathRooms
     */
    public static List<DeathRoom> getAllRooms(){
        List<DeathRoom> result = new ArrayList<>();
        List<Map<?, ?>> room_list = RoleCraft.config.getMapList("deathrooms");
        for (int i = 0; i < room_list.size(); i++){
            result.add(getRoomById(i));
        }
        return result;
    }

    /**
     * Get a DeathRoom by its id
     * @param id The DeathRoom id
     * @return DeathRoom The DeathRoom to get
     */
    @SuppressWarnings("unchecked")
    public static DeathRoom getRoomById(int id){
        List<Map<?, ?>> room_list = RoleCraft.config.getMapList("deathrooms");
        Map<String, ?> room_config = (Map<String, ?>) room_list.get(id);

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

        return new DeathRoom(id, state_bloc, spawnpoint, itemspawn, cinematic1, cinematic2);
    }

    public int getId() { return id; }
    public Location getState_bloc() {
        return state_bloc;
    }
    public Location getSpawnpoint() {
        return spawnpoint;
    }
}
