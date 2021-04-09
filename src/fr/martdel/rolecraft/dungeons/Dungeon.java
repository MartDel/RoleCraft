package fr.martdel.rolecraft.dungeons;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.player.CustomPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {

    private int name;
    private int type;
    private List<Integer> required_jobs;
    private Location spawnpoint;

    private RoleCraft plugin;

    private List<Player> players;

    public Dungeon(int name, RoleCraft plugin) {
        this.name = name;
        this.players = new ArrayList<>();

        // Get config infos
        this.type = RoleCraft.config.getInt("dungeons." + name + ".type");
        this.required_jobs = RoleCraft.config.getIntegerList("dungeons." + name + ".required_jobs");
        this.spawnpoint = RoleCraft.getConfigLocation("dungeons." + name + ".spawpoint", true);
    }

    /**
     * Check if the dungeon is running
     * @return The dungeon is running or not
     */
    public boolean isActive(){
        return !players.isEmpty();
    }

    /**
     * Check if a player can join the dungeon
     * @param player The player to check
     * @return If the player can join or not
     */
    public boolean canAccess(Player player){
        CustomPlayer customPlayer = new CustomPlayer(player, plugin);
        int playerjob = customPlayer.getJob();
        if(!required_jobs.contains(playerjob)) return false;
        boolean job_is_contained = false;
        for (Player p : players){
            if(new CustomPlayer(p, plugin).getJob() == playerjob) job_is_contained = true;
        }
        return !job_is_contained;
    }

    /**
     * Join a player to a dungeon team
     * @param player
     */
    public void joinPlayer(Player player){
        if(!canAccess(player)) return;
        players.add(player);
        if(isActive()){
            // Waiting room
        } else {
            // Start cinematics

            // Manage player's inventory
            // Backup his inventory


            // The player is tp to the dungeon spawnpoint
            player.teleport(spawnpoint);
        }
    }
}
