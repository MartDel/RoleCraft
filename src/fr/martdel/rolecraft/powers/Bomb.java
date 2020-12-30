package fr.martdel.rolecraft.powers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.superclass.SquareBuilder;

public class Bomb extends SquareBuilder {

	private static final Material BOMBTYPE = Material.getMaterial(RoleCraft.config.getString("powers.bomb.bomb_type"));;
	private static final int RADIUS = RoleCraft.config.getInt("powers.bomb.radius");
	
	public static final String BOMBNAME = RoleCraft.config.getString("powers.bomb.bomb_name");

	public Bomb(Location center) {
		super(center, RADIUS);
	}
	
	public void explode() {
		List<Location> to_explode = getCrackedBlocs();
		for (Location bloc : to_explode) {
//			bloc.getWorld().getBlockAt(bloc).setType(Material.AIR);
			bloc.getWorld().getBlockAt(bloc).breakNaturally();
		}
	}
	
	public List<Location> getCrackedBlocs(){
		List<Location> blocs = new ArrayList<Location>();
		
		for (int d = -RADIUS+1; d <= RADIUS+1; d++) {
			for (int r = 0; r <= RADIUS; r++) {
				Location[] corners = getCorners(r);
				for (int i = 0; i < corners.length; i++) {
					corners[i].setY(corners[i].getBlockY() + d);
					List<Location> raw_blocs = getBlocsForRow(corners[i], getRowLength(r), i);
					for (Location bloc : raw_blocs) {
						Block current_bloc = bloc.getWorld().getBlockAt(bloc);
						Material bloctype = current_bloc.getType();
						if(bloctype.toString().contains("CRACKED")) {
							blocs.add(bloc);
						}
					}
				}
			}
		}
		
		return blocs;
	}

	@Override
	public List<Location> getBlocsForRow(Location starter, int length, int rank) {
		List<Location> blocs = new ArrayList<>();
		int y = starter.getBlockY() - 1;
		
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
			current_bloc.setY(y);
			blocs.add(new Location(current_bloc.getWorld(), current_bloc.getBlockX(), current_bloc.getBlockY(), current_bloc.getBlockZ()));
		}
		
		return blocs;
	}

	public static ItemStack getItemStack() {
		ItemStack item = new ItemStack(BOMBTYPE);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(BOMBNAME);
		itemmeta.addEnchant(Enchantment.DURABILITY, 200, true);
		itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemmeta);
		return item;
	}

}
