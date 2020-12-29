package fr.martdel.rolecraft.powers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.superclass.SquareBuilder;

public class ShockWave extends SquareBuilder {
	
	private static final Material ITEMTYPE = Material.TNT;
	private static final int MAXRADIUS = RoleCraft.config.getInt("powers.shockwave.max_radius");
	private static final int DELAY = RoleCraft.config.getInt("powers.shockwave.speed");
	private static final int DAMAGE = RoleCraft.config.getInt("powers.shockwave.damage");

	public static final int COOLDOWN = RoleCraft.config.getInt("powers.shockwave.cooldown");

	private Location center;
	private int radius = 1;

	private RoleCraft plugin;
	private BukkitScheduler scheduler;

	public ShockWave(RoleCraft plugin, Location center) {
		super(center, MAXRADIUS);
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
							Block current_bloc = bloc.getWorld().getBlockAt(bloc);
							current_bloc.setType(under_bloc_type);
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
	
	public void makeDamages(Player damager) {
		World world = center.getWorld();
		Collection<Entity> entities = world.getNearbyEntities(center, MAXRADIUS, MAXRADIUS, 3);
		for (Entity entity : entities) {
			if(entity instanceof Damageable) {
				Damageable mob = (Damageable) entity;
				if(mob instanceof Player) {
					Player player = (Player) mob;
					if(!player.isSneaking() && !player.equals(damager)) {
						player.damage(DAMAGE);
					}
				} else {
					mob.damage(DAMAGE);;
				}
			}
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

	public static ItemStack getItemStack() {
		ItemStack item = new ItemStack(ITEMTYPE);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(RoleCraft.config.getString("powers.shockwave.item_name"));
		itemmeta.addEnchant(Enchantment.DAMAGE_ALL, 200, true);
		itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemmeta);
		return item;
	}

}
