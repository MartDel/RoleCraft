package fr.martdel.rolecraft.superclass;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class SquareBuilder {
	
	private Location center;
	private int maxradius;

	public SquareBuilder(Location center, int radius) {
		this.center = center;
		this.maxradius = radius;
	}

	public abstract List<Location> getBlocsForRow(Location starter, int length, int rank);
	
	public boolean checkWaveEnvironment() { return checkEnvironmentBis(0); }
	public boolean checkBunkerEnvironment() { return checkEnvironmentBis(1); }
	private boolean checkEnvironmentBis(int plus) {
		for (int r = 0; r < maxradius + plus; r++) {
			Location[] corners = getCorners(r);
			for (int i = 0; i < corners.length; i++) {
				List<Location> blocs = getBlocsForRow(corners[i], getRowLength(r), i);
				for (Location bloc : blocs) {
					Block current_bloc = bloc.getWorld().getBlockAt(bloc);
					if(!current_bloc.getType().equals(Material.AIR)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public int getRowLength(int radius) {
		return (radius * 2) + 2;
	}
	
	public Location[] getCorners(int radius) {
		World world = center.getWorld();
		int x = center.getBlockX();
		int y = center.getBlockY();
		int z = center.getBlockZ();
		int r = radius + 1;
		
		Location[] corners = {
			new Location(world, x - r, y, z + r),
			new Location(world, x + r, y, z + r),
			new Location(world, x + r, y, z - r),
			new Location(world, x - r, y, z - r)
		};
		
		return corners;
	}	

}
