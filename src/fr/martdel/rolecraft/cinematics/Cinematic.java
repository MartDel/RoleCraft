package fr.martdel.rolecraft.cinematics;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.Score;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Cinematic {

    public static final String PATH = "assets/cinematic";

    private List<Location> data;
    private Integer duration;
    private boolean dynamic;

    public Cinematic(Location loc, int d){
        this.data = Collections.singletonList(loc);
        this.duration = d;
        this.dynamic = false;
    }
    public Cinematic(List<Location> loc){
        this.data = loc;
        this.duration = null;
        this.dynamic = true;
    }

    /**
     * Start the cinematic
     * @param player The player to move
     * @param plugin Instance of RoleCraft plugin
     * @param callback What to execute after the cinematic
     */
    public void play(Player player, RoleCraft plugin, Runnable callback){
        final GameMode old_gamemode = player.getGameMode();
        final List<Location> locations = this.data;
        player.setGameMode(GameMode.SPECTATOR);
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        if (dynamic) {
            scheduler.runTaskLater(plugin, new Runnable() {
                private int t = 0;
                @Override
                public void run() {
                    player.teleport(locations.get(t));
                    t++;
                    if(t < locations.size()) scheduler.runTaskLater(plugin, this, 1);
                    else {
                        player.setGameMode(old_gamemode);
                        if(callback != null) callback.run();
                    }
                }
            }, 1);
        } else {
            scheduler.runTaskLater(plugin, new Runnable() {
                private int t = 0;
                @Override
                public void run() {
                    player.teleport(locations.get(0));
                    t++;
                    if(t < duration) scheduler.runTaskLater(plugin, this, 1);
                    else {
                        player.setGameMode(old_gamemode);
                        if(callback != null) callback.run();
                    }
                }
            }, 1);
        }
    }

    /**
     * Save the cinematic on the server files
     * @param name The cinematic name
     * @param plugin Instance of RoleCraft plugin
     * @return boolean If it's a success or not
     */
    public boolean save(String name, RoleCraft plugin){
        try {
            File file = new File(PATH + "/" + name + ".json");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            for (Location loc : this.data){
                writer.write(
                "{" +
                        "\"world\":\"" + loc.getWorld().getName() + "\"," +
                        "\"x\":\"" + loc.getX() + "\"," +
                        "\"y\":\"" + loc.getY() + "\"," +
                        "\"z\":\"" + loc.getZ() + "\"," +
                        "\"yaw\":\"" + loc.getYaw() + "\"," +
                        "\"pitch\":\"" + loc.getPitch() + "\"" +
                    "}\n"
                );
            }
            writer.flush();
            writer.close();
            Map<String, Cinematic> cinematic_list = plugin.getCinematicList();
            if(cinematic_list.containsKey(name)) cinematic_list.remove(name);
            cinematic_list.put(name, this);
            plugin.setCinematicList(cinematic_list);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Delete the cinematic from the server files
     * @param name The cinematic name
     * @param plugin Instance of RoleCraft plugin
     * @return boolean If it's a success or not
     */
    public boolean delete(String name, RoleCraft plugin){
        plugin.getCinematicList().remove(name);
        File file = new File(PATH + "/" + name + ".json");
        return file.delete();
    }

    /**
     * Record a new cinematic
     * @param player The player to record
     * @param plugin Instance of RoleCraft plugin
     * @param callback What to run after the recording
     */
    public static void record(Player player, RoleCraft plugin, Runnable callback) {
        List<Location> locations = new ArrayList<>();
        Score recording = plugin.getRecording();
        recording.setScore(player, 1);
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskLater(plugin, new Runnable() {
            private int t = 0;
            @Override
            public void run() {
                locations.add(player.getLocation());
                t++;
                if(recording.getScore(player) == 1) scheduler.runTaskLater(plugin, this, 1);
                else {
                    Cinematic cinematic = new Cinematic(locations);
                    cinematic.save("last", plugin);
                    if(callback != null) callback.run();
                }
            }
        }, 1);
    }

    public List<Location> getData() {
        return data;
    }
    public void setData(List<Location> locations){
        this.data = locations;
    }
}
