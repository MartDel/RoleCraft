package fr.martdel.rolecraft.powers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import fr.martdel.rolecraft.RoleCraft;

public class Bunker extends SquareBuilder {
	
	private static final Material ITEMTYPE = RoleCraft.getConfigMaterial("powers.bunker.item_type");
	private static final Material BLOCKTYPE = RoleCraft.getConfigMaterial("powers.bunker.block_type");
	private static final int RADIUS = RoleCraft.config.getInt("powers.bunker.radius");
	private static final int BUILD_DELAY = RoleCraft.config.getInt("powers.bunker.speed");
	private static final int LIFE = RoleCraft.config.getInt("powers.bunker.life");

	public static final int COOLDOWN = RoleCraft.config.getInt("powers.bunker.cooldown");
	public static final String ITEMNAME = RoleCraft.config.getString("powers.bunker.item_name");

	private final RoleCraft plugin;
	private final BukkitScheduler scheduler;

	public Bunker(RoleCraft plugin, Location center) {
		super(center, RADIUS);		
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
	}
	
	public void build() {
		Location[] corners = getCorners(RADIUS);
		for (int i = 0; i < corners.length; i++) {
			List<Location> blocs = getBlocsForRow(corners[i], getRowLength(RADIUS), i);
			scheduler.runTaskLater(plugin, new Runnable() {
				private int i = 0;
				@Override
				public void run() {
					Location bloc = blocs.get(i);
					World world = bloc.getWorld();
					assert world != null;
					world.getBlockAt(bloc).setType(BLOCKTYPE);
					i++;
					if(i < blocs.size()) scheduler.runTaskLater(plugin, this, BUILD_DELAY);
					else scheduler.runTaskLater(plugin, () -> destroy(), LIFE);
				}
			}, BUILD_DELAY);
		}
	}
	
	public void destroy() {
		Location[] corners = getCorners(RADIUS);
		for (int i = 0; i < corners.length; i++) {
			List<Location> blocs = getBlocsForRow(corners[i], getRowLength(RADIUS), i);
			scheduler.runTaskLater(plugin, new Runnable() {
				private int i = 0;
				@Override
				public void run() {
					Location bloc = blocs.get(i);
					World world = bloc.getWorld();
					assert world != null;
					world.getBlockAt(bloc).setType(Material.AIR);
					i++;
					if(i < blocs.size()) scheduler.runTaskLater(plugin, this, BUILD_DELAY);
				}
			}, BUILD_DELAY);
		}
	}
	
	public List<Location> getBlocsForRow(Location starter, int length, int rank) {
		List<Location> blocs = new ArrayList<>();
		
		boolean increase = false;
		boolean increaseX = false;
		switch (rank) {
			case 0: increase = true; increaseX = true; break;
			case 1: increase = false; increaseX = false; break;
			case 2: increase = false; increaseX = true; break;
			case 3: increase = true; increaseX = false; break;
		}
		
		int starter_i;
		if(increaseX) starter_i = starter.getBlockX();
		else starter_i = starter.getBlockZ();
		
		int y = starter.getBlockY();
		for (int j = 0; j < 2; j++) {			
			y += j * 2;
			for (int i = 0; i < length; i++) {
				int new_value;
				if(increase) new_value = starter_i + i;
				else new_value = starter_i - i;

				if(increaseX) starter.setX(new_value);
				else starter.setZ(new_value);
				starter.setY(y);
				blocs.add(new Location(starter.getWorld(), starter.getBlockX(), starter.getBlockY(), starter.getBlockZ()));
			}
		}
		
		return blocs;
	}
	
	public static ItemStack getItemStack() {
		ItemStack item = new ItemStack(ITEMTYPE);
		ItemMeta itemmeta = item.getItemMeta();
		assert itemmeta != null;
		itemmeta.setDisplayName(ITEMNAME);
		itemmeta.addEnchant(Enchantment.DURABILITY, 200, true);
		itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemmeta);
		return item;
	}

}
