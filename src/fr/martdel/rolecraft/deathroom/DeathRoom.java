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
        setCurrentlyUsed(true);

        List<DeathKeys> keys = player.loadData().getKeys();
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
        player.getPlayer().openInventory(choose_key.getInventory());
    }
    public void spawnPlayer(CustomPlayer player, RoleCraft plugin, DeathKeys choosen_key){
        if(choosen_key != null){
            player.getPlayer().sendMessage("Vous avez choisi la clé " + choosen_key.toString());
            player.getPlayer().sendMessage("Vous avez perdu " + player.getRandomItems(choosen_key.getLost()) + " stack(s)");
            player.getPlayer().sendMessage("Vous pouvais recupérer " + player.getRandomItems(choosen_key.getRoomDrop()) + " stack(s) dans la salle");
            player.getPlayer().sendMessage("Vous pouvais recupérer " + player.getRandomItems(choosen_key.getDrop()) + " stack(s) à l'endroit de votre mort");
        } else player.getPlayer().sendMessage("Vous n'avez pas choisi de clé");
    }

    public static List<DeathRoom> getAllRooms(){
        List<DeathRoom> result = new ArrayList<>();
        List<Map<?, ?>> room_list = RoleCraft.config.getMapList("deathrooms");
        for (int i = 0; i < room_list.size(); i++){
            result.add(getRoomById(i));
        }
        return result;
    }
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
