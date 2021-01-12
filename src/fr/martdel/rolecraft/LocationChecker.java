package fr.martdel.rolecraft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LocationChecker {
	
	private Player player;
	private RoleCraft plugin;

	public LocationChecker(RoleCraft rolecraft, Player p) {
		this.player = p;
		this.plugin = rolecraft;
	}
	
	public boolean inProtectedMap(Location loc) {
		return false;
	}
	
	public boolean inOwnedGround(Location loc) {
		Connection db = plugin.getDB().getConnection();
		
		PreparedStatement houses = db.prepareStatement("SELECT * FROM houses");
		houses.setInt(1, house_id);
		ResultSet result0 = houses.executeQuery();
		if(result0.next()) {
			int x1 = result0.getInt("x1");
			int y1 = result0.getInt("y1");
			int z1 = result0.getInt("z1");
			int x2 = result0.getInt("x2");
			int y2 = result0.getInt("y2");
			int z2 = result0.getInt("z2");
			World world = plugin.getServer().getWorld("NORMAL");
			System.out.println(world);
			
			Location l1 = new Location(world, x1, y1, z1);
			Location l2 = new Location(world, x2, y2, z2);
			Location[] locations = {l1, l2};
			
		}
		houses.close();
		return false;
	}

}
