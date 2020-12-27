package fr.martdel.rolecraft.powers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;

import fr.martdel.rolecraft.RoleCraft;

public class ShockWave {

	private final int DELAY = 2;
	private final int MAXRADIUS = 15;

	private Location center;
	private int radius = 1;

	private RoleCraft plugin;
	private BukkitScheduler scheduler;

	public ShockWave(RoleCraft plugin, Location center) {
		this.center = center;
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
	}

	public void launch() {
		scheduler.runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				// Up the blocs
				if((int) radius != MAXRADIUS - 1) {
					Location[] corners = getCorners(radius);
					for (int i = 0; i < corners.length; i++) {
						List<Location> blocs = getBlocsForRow(corners[i], getRowLength(radius), i);
						for (Location bloc : blocs) {
							Location under_bloc_loc = new Location(bloc.getWorld(), bloc.getBlockX(), bloc.getBlockY() - 1, bloc.getBlockZ());
							Block under_bloc = under_bloc_loc.getWorld().getBlockAt(under_bloc_loc);
							Material under_bloc_type = under_bloc.getType();
							bloc.getWorld().getBlockAt(bloc).setType(under_bloc_type);
						}
					}
				}
				
				// Down last blocs
				Location[] old_corners = getCorners(radius - 1);
				for (int i = 0; i < old_corners.length; i++) {
					List<Location> blocs = getBlocsForRow(old_corners[i], getRowLength(radius - 1), i);
					for (Location bloc : blocs) {
						bloc.getWorld().getBlockAt(bloc).setType(Material.AIR);
					}
				}
				radius++;
				if(radius < MAXRADIUS) scheduler.runTaskLater(plugin, this, DELAY);
			}
		}, DELAY);
	}
	
	private int getRowLength(int radius) {
		return (radius * 2) + 2;
	}
	
	private Location[] getCorners(int radius) {
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
	
	private List<Location> getBlocsForRow(Location starter, int length, int rank) {
		List<Location> blocs = new ArrayList<>();
		
		boolean increase = false;
		boolean increaseX = false;
		switch (rank) {
			case 0: increase = true; increaseX = true; break;
			case 1: increase = false; increaseX = false; break;
			case 2: increase = false; increaseX = true; break;
			case 3: increase = true; increaseX = false; break;
		}
		
		int starter_i = 0;
		if(increaseX) starter_i = starter.getBlockX();
		else starter_i = starter.getBlockZ();
		
		for (int i = 0; i < length; i++) {
			int new_value = 0;
			if(increase) new_value = starter_i + i;
			else new_value = starter_i - i;

			Location current_bloc = starter;
			if(increaseX) current_bloc.setX(new_value);
			else current_bloc.setZ(new_value);
			
			blocs.add(new Location(current_bloc.getWorld(), current_bloc.getBlockX(), current_bloc.getBlockY(), current_bloc.getBlockZ()));
		}
		
		return blocs;
	}

}
