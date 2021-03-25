package fr.martdel.rolecraft.MapChecker;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Ground {

    private String uuid;
    private OfflinePlayer player;
    private LocationInMap ground_type;

    public Ground(){
        this.uuid = null;
        this.ground_type = null;
        this.player = null;
    }

    public boolean exist(){
        return uuid != null && ground_type != null;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public LocationInMap getGroundType() {
        return ground_type;
    }

    public void setGroundType(LocationInMap ground_type) {
        this.ground_type = ground_type;
    }
}
