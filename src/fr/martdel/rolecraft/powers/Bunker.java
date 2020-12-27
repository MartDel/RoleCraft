package fr.martdel.rolecraft.powers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitScheduler;

import fr.martdel.rolecraft.RoleCraft;

public class Bunker {
	
	private final Material BLOCKTYPE = Material.BEDROCK;
	private final int RADIUS = 2;
	private final int BUILD_DELAY = 2;
	
	private Location center;

	private BukkitScheduler scheduler;
	private RoleCraft plugin;

	public Bunker(RoleCraft plugin, Location center) {
		this.center = center;
		
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
	}
	
	public void build() {
		Location[] corners = getCorners();
		for (int i = 0; i < corners.length; i++) {
			List<Location> blocs = getBlocsForRow(corners[i], getRowLength(), i);
			scheduler.runTaskLater(plugin, new Runnable() {
				private int i = 0;
				@Override
				public void run() {
					Location bloc = blocs.get(i);
					bloc.getWorld().getBlockAt(bloc).setType(BLOCKTYPE);
					i++;
					if(i < blocs.size()) scheduler.runTaskLater(plugin, this, BUILD_DELAY);
				}
			}, BUILD_DELAY);
		}
	}
	
	private int getRowLength() {
		return (RADIUS * 2) + 2;
	}
	
	private Location[] getCorners() {
		World world = center.getWorld();
		int x = center.getBlockX();
		int y = center.getBlockY();
		int z = center.getBlockZ();
		int r = RADIUS + 1;
		
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
		
		int y = starter.getBlockY();
		for (int j = 0; j < 2; j++) {			
			y += j * 2;
			for (int i = 0; i < length; i++) {
				int new_value = 0;
				if(increase) new_value = starter_i + i;
				else new_value = starter_i - i;

				Location current_bloc = starter;
				if(increaseX) current_bloc.setX(new_value);
				else current_bloc.setZ(new_value);
				current_bloc.setY(y);
				blocs.add(new Location(current_bloc.getWorld(), current_bloc.getBlockX(), current_bloc.getBlockY(), current_bloc.getBlockZ()));
			}
		}
		
		return blocs;
	}

}
