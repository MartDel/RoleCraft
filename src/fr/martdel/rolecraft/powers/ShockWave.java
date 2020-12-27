package fr.martdel.rolecraft.powers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;

import fr.martdel.rolecraft.RoleCraft;

public class ShockWave {

	private final int DELAY = 15;
	private final int MAXRADIUS = 15;

	private Location center;
	private int radius = 1;
	
	private Map<Integer, Integer> forbidden_x;
	private Map<Integer, Integer> forbidden_z;
	private Map<Integer, Integer[]> forbidden_corners;

	private RoleCraft plugin;
	private BukkitScheduler scheduler;

	public ShockWave(RoleCraft plugin, Location center) {
		this.center = center;
		System.out.println("Centre: x=" + center.getBlockX() + " z=" + center.getBlockZ());
		
		this.forbidden_x = new HashMap<>();
		this.forbidden_z = new HashMap<>();
		this.forbidden_corners = new HashMap<>();
		
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
	}

	public void launch() {
		scheduler.runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				// Down last blocs
				Location[] old_corners = getCorners(radius - 1);
				for (int i = 0; i < old_corners.length; i++) {
					List<Location> blocs = getBlocsForRow(old_corners[i], getRowLength(radius - 1), i);
					for (Location bloc : blocs) {
						if(checkRules(bloc)) bloc.getWorld().getBlockAt(bloc).setType(Material.AIR);
					}
				}
				
				// Up the blocs
				if((int) radius != MAXRADIUS - 1) {
					Location[] corners = getCorners(radius);
					for (int i = 0; i < corners.length; i++) {
						List<Location> blocs = getBlocsForRow(corners[i], getRowLength(radius), i);
						for (Location bloc : blocs) {
							Location under_bloc_loc = new Location(bloc.getWorld(), bloc.getBlockX(), bloc.getBlockY() - 1, bloc.getBlockZ());
							Block under_bloc = under_bloc_loc.getWorld().getBlockAt(under_bloc_loc);
							Material under_bloc_type = under_bloc.getType();
							Block current_bloc = bloc.getWorld().getBlockAt(bloc);
							if(addRules(bloc) && checkRules(bloc)) current_bloc.setType(under_bloc_type);
						}
					}
				}
				radius++;
				System.out.println(radius);
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
	
	private boolean addRules(Location bloc) {
		Integer x = bloc.getBlockX();
		Integer z = bloc.getBlockZ();
		
		// Check if the bloc is a forbidden bloc or not
		Block b = bloc.getWorld().getBlockAt(bloc);
		if(!b.getType().equals(Material.AIR)) {
			// Add rule
			System.out.println("Bloc détecté");
			
			// Check corners
			Location[] corners = getCorners(radius);
			for (int i = 0; i < corners.length; i++) {
				if(corners[i].equals(bloc)) {
					System.out.println("dans un coin : " + i + " " + radius);
					Integer[] coord = {bloc.getBlockX() - center.getBlockX(), bloc.getBlockZ() - center.getBlockZ()};
					forbidden_corners.put(i, coord);
					System.out.println("Nouvelle règle : i=" + i + " x=" + coord[0] + " z=" + coord[1]);
					return false;
				}
			}
			
			// Check x
			if((z - center.getBlockZ()) > radius || (z - center.getBlockZ()) < (0 - radius)) {
				System.out.println("en X");
				Integer z_state = getZState(z, center.getBlockZ());
				forbidden_x.put(x, z_state);				
				return false;
			}
			
			// Check z
			if((x - center.getBlockX()) > radius || (x - center.getBlockX()) < (0 - radius)) {
				System.out.println("en Z");
				Integer x_state = getZState(x, center.getBlockX());
				forbidden_z.put(z, x_state);
				return false;
			}
		}
		return true;
	}
	
	private boolean checkRules(Location bloc) {
		Integer x = bloc.getBlockX();
		Integer z = bloc.getBlockZ();
		
		// Check corners
		Set<Integer> keys = forbidden_corners.keySet();
		for (Integer i : keys) {
			Integer[] coord = forbidden_corners.get(i);
			switch (i) {
			case 0: 
				if((x - center.getBlockX()) <= coord[0] && (z - center.getBlockZ()) >= coord[1]) {
					return false;
				}
				break;
			case 1:
				if((x - center.getBlockX()) >= coord[0] && (z - center.getBlockZ()) >= coord[1]) {
					return false;
				}
				break;
			case 2: 
				if((x - center.getBlockX()) >= coord[0] && (z - center.getBlockZ()) <= coord[1]) {
					return false;	
				}
				break;
			case 3: 
				if((x - center.getBlockX()) <= coord[0] && (z - center.getBlockZ()) <= coord[1]) {
					return false;
				}
				break;
			}
		}
		
		// Check x
		if(forbidden_x.containsKey(x)) {
			Integer z_state = getZState(z, center.getBlockZ());
			if(forbidden_x.get(x).equals(z_state)) return false;
		}
		
		// Check z
		if(forbidden_z.containsKey(z)) {
			Integer x_state = getZState(x, center.getBlockX());
			if(forbidden_z.get(z).equals(x_state)) return false;
		}
		
		return true;
	}
	
	private Integer getZState(Integer z, Integer playerZ) {
		if(z - playerZ > 0) return 1;
		else if (z - playerZ < 0) return -1;
		return 0;
	}

}
