package fr.martdel.rolecraft.deathroom;

import fr.martdel.rolecraft.cinematics.Cinematic;
import fr.martdel.rolecraft.player.CustomPlayer;
import fr.martdel.rolecraft.GUI;
import fr.martdel.rolecraft.RoleCraft;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DeathRoom {

    private int id;
    private Location state_bloc;
    private Location spawnpoint;
    private Location itemsspawn1;
    private Location itemsspawn2;
    private List<Cinematic> cinematic_list;

    public static int NORMALLOST = RoleCraft.config.getInt("deathkeys.normal.lost");
    public static int NORMALROOM = RoleCraft.config.getInt("deathkeys.normal.room");
    public static int NORMALDROPS = RoleCraft.config.getInt("deathkeys.normal.drops");

    public DeathRoom(int id, Location state_bloc, Location spawnpoint, Location itemsspawn1, Location itemsspawn2, List<Cinematic> cinematic_list){
        this.id = id;
        this.state_bloc = state_bloc;
        this.spawnpoint = spawnpoint;
        this.itemsspawn1 = itemsspawn1;
        this.itemsspawn2 = itemsspawn2;
        this.cinematic_list = cinematic_list;
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
     * @param customPlayer The player to update (instance of CustomPlayer)
     * @param plugin Instance of RoleCraft plugin
     * @param chosen_key The key chosen by the player
     */
    public void spawnPlayer(CustomPlayer customPlayer, RoleCraft plugin, DeathKey chosen_key){
        Player player = customPlayer.getPlayer();

        // Manage player's inventory
        Map<String, List<ItemStack>> inventory;
        if(chosen_key != null){
            inventory = customPlayer.getDeathDrops(chosen_key.getLost(), chosen_key.getRoomDrop(), chosen_key.getDrop());
        } else {
            inventory = customPlayer.getDeathDrops(NORMALLOST, NORMALROOM, NORMALDROPS);
        }
        player.getInventory().clear();

        // Play room cinematic
        cinematic_list.get(0).play(player, plugin, new Runnable() {
            private int i = 0;
            @Override
            public void run() {
                i++;
                if(i < cinematic_list.size()) cinematic_list.get(i).play(player, plugin, this);
                else {
                    player.teleport(spawnpoint);
                    if(chosen_key != null){
                        player.sendMessage("Vous avez choisi la clé §a" + chosen_key.toString() + "§r.");
                    } else {
                        player.sendMessage("Vous n'avez pas choisi de clé.");
                    }
                    List<String> msg = Arrays.asList(
                        "Vous avez perdu §c" + inventory.get("lost").size() + " stack(s)§r.",
                        "Vous pouvez recupérer §a" + inventory.get("room").size() + " stack(s)§r dans la salle.",
                        "Vous pouvez recupérer §2" + inventory.get("drops").size() + " stack(s)§r à l'endroit de votre mort"
                    );
                    for (String s : msg) {
                        player.sendMessage(s);
                    }
                }
            }
        });

        // Spawn room items
        List<ItemStack> room_items = inventory.get("room");
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskLater(plugin, new Runnable() {
            private int i = 0;
            @Override
            public void run() {
                ItemStack item = room_items.get(i);
                if(i % 2 == 0){
                    // First spawn
                    RoleCraft.OVERWORLD.dropItem(itemsspawn1, item);
                } else {
                    // Second spawn
                    RoleCraft.OVERWORLD.dropItem(itemsspawn2, item);
                }
                i++;
                if(i < room_items.size()) scheduler.runTaskLater(plugin, this, 10);
            }
        }, 10);

        // Spawn drop items
        for (ItemStack item : inventory.get("drops")){
            RoleCraft.OVERWORLD.dropItem(customPlayer.getDeathLocation(), item);
        }
    }

    /**
     * Get all DeathRooms on the map
     * @param plugin Instance of RoleCraft plugin
     * @return List<DeathRoom> All of the DeathRooms
     */
    public static List<DeathRoom> getAllRooms(RoleCraft plugin){
        List<DeathRoom> result = new ArrayList<>();
        List<Map<?, ?>> room_list = RoleCraft.config.getMapList("deathrooms");
        for (int i = 0; i < room_list.size(); i++){
            result.add(getRoomById(i, plugin));
        }
        return result;
    }

    /**
     * Get a DeathRoom by its id
     * @param id The DeathRoom id
     * @param plugin Instance of RoleCraft plugin
     * @return DeathRoom The DeathRoom to get
     */
    @SuppressWarnings("unchecked")
    public static DeathRoom getRoomById(int id, RoleCraft plugin){
        List<Map<?, ?>> room_list = RoleCraft.config.getMapList("deathrooms");
        Map<String, ?> room_config = (Map<String, ?>) room_list.get(id);

        // Get room_state
        Location state_bloc = RoleCraft.getConfigLocation(room_config.get("state_bloc"), false);

        // Get room spawn
        Location spawnpoint = RoleCraft.getConfigLocation(room_config.get("spawnpoint"), true);

        // Get items spawn
        Location itemspawn1 = RoleCraft.getConfigLocation(room_config.get("items_spawnpoint1"), false);
        Location itemspawn2 = RoleCraft.getConfigLocation(room_config.get("items_spawnpoint2"), false);

        // Get cinematics
        List<String> cinematic_names = (List<String>) room_config.get("cinematics");
        List<Cinematic> cinematic_list = new ArrayList<>();
        for (String name : cinematic_names){
            cinematic_list.add(plugin.getCinematicList().get(name));
        }

        return new DeathRoom(id, state_bloc, spawnpoint, itemspawn1, itemspawn2, cinematic_list);
    }

    public int getId() { return id; }
    public Location getState_bloc() {
        return state_bloc;
    }
    public Location getSpawnpoint() {
        return spawnpoint;
    }
}
