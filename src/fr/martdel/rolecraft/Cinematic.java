package fr.martdel.rolecraft;

import org.bukkit.Location;
import org.bukkit.World;

public class Cinematic {

    private Location location;
    private int duration;

    public Cinematic(Location loc, int d){
        this.location = loc;
        this.duration = d;
    }
    public Cinematic(Location loc, float yaw, float pitch, int d){
        this.location = loc;
        this.location.setYaw(yaw);
        this.location.setPitch(pitch);
        this.duration = d;
    }
    public Cinematic(double x, double y, double z, float yaw, float pitch, int d){
        this.location = new Location(RoleCraft.OVERWORLD, x, y, z, yaw, pitch);
        this.duration = d;
    }
    public Cinematic(World w, double x, double y, double z, float yaw, float pitch, int d){
        this.location = new Location(w, x, y, z, yaw, pitch);
        this.duration = d;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
}
