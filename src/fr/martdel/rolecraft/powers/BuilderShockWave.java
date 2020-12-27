package fr.martdel.rolecraft.powers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class BuilderShockWave {
	
	private final Material BLOCKTYPE = Material.STONE;
	private final int STARTRADIUS = 2;
	
	private Location center;

	public BuilderShockWave(Location center) {
		this.center = center;
	}
	
	public void setStartCircle() {
		Location[] start_corners = getCorners(STARTRADIUS);
		for (int i = 0; i < start_corners.length; i++) {
			setRow(start_corners[i], getRowLength(STARTRADIUS), i);
		}
	}
	
	private int getRowLength(int radius) {
		return (radius * 2) + 1;
	}
	
	private Location[] getCorners(int r) {
		World world = center.getWorld();
		int x = center.getBlockX();
		int y = center.getBlockY();
		int z = center.getBlockZ();
		
		Location[] corners = {
			new Location(world, x - r, y, z + (r+1)),
			new Location(world, x + (r+1), y, z + r),
			new Location(world, x + r, y, z - (r+1)),
			new Location(world, x - (r+1), y, z - r)
		};
		
		return corners;
	}
	
	private void setRow(Location starter, int length, int rank) {
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
			y += j;
			
			for (int i = 0; i < length; i++) {
				int new_value = 0;
				if(increase) new_value = starter_i + i;
				else new_value = starter_i - i;

				Location current_bloc = starter;
				if(increaseX) current_bloc.setX(new_value);
				else current_bloc.setZ(new_value);
				current_bloc.setY(y);
				
				current_bloc.getWorld().getBlockAt(current_bloc).setType(BLOCKTYPE);
			}
		}
	}

}
