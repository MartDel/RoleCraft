package fr.martdel.rolecraft.powers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Ladder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import fr.martdel.rolecraft.RoleCraft;

public class ShockWave extends SquareBuilder {
	
	private static final Material ITEMTYPE = RoleCraft.getConfigMaterial("powers.shockwave.item_type");
	private static final int MAXRADIUS = RoleCraft.config.getInt("powers.shockwave.max_radius");
	private static final int DELAY = RoleCraft.config.getInt("powers.shockwave.speed");
	private static final int DAMAGE = RoleCraft.config.getInt("powers.shockwave.damage");

	public static final int COOLDOWN = RoleCraft.config.getInt("powers.shockwave.cooldown");
	public static final String ITEMNAME = RoleCraft.config.getString("powers.shockwave.item_name");

	private final Location center;
	private int radius = 1;

	private final RoleCraft plugin;
	private final BukkitScheduler scheduler;

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
				if(radius != MAXRADIUS - 1) {
					Location[] corners = getCorners(radius);
					for (int i = 0; i < corners.length; i++) {
						List<Location> blocs = getBlocsForRow(corners[i], getRowLength(radius), i);
						for (Location bloc : blocs) {
							World world = bloc.getWorld();
							assert world != null;
							Location under_bloc_loc = new Location(world, bloc.getBlockX(), bloc.getBlockY() - 1, bloc.getBlockZ());
							Block under_bloc = world.getBlockAt(under_bloc_loc);
							Material under_bloc_type = under_bloc.getType();
							Block current_bloc = world.getBlockAt(bloc);
							current_bloc.setType(under_bloc_type);
						}
					}
				}
				// Down last blocs
				Location[] old_corners = getCorners(radius - 1);
				for (int i = 0; i < old_corners.length; i++) {
					List<Location> blocs = getBlocsForRow(old_corners[i], getRowLength(radius - 1), i);
					for (Location bloc : blocs) {
						World world = bloc.getWorld();
						assert world != null;
						world.getBlockAt(bloc).setType(Material.AIR);
					}
				}
				
				radius++;
				if(radius < MAXRADIUS) scheduler.runTaskLater(plugin, this, DELAY);
			}
		}, DELAY);
	}
	
	public void makeDamages(Player damager) {
		World world = center.getWorld();
		assert world != null;
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
					mob.damage(DAMAGE);
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
		
		int starter_i;
		if(increaseX) starter_i = starter.getBlockX();
		else starter_i = starter.getBlockZ();
		
		for (int i = 0; i < length; i++) {
			int new_value;
			if(increase) new_value = starter_i + i;
			else new_value = starter_i - i;

			if(increaseX) starter.setX(new_value);
			else starter.setZ(new_value);
			
			blocs.add(new Location(starter.getWorld(), starter.getBlockX(), starter.getBlockY(), starter.getBlockZ()));
		}
		
		return blocs;
	}

	public static ItemStack getItemStack() {
		ItemStack item = new ItemStack(ITEMTYPE);
		ItemMeta itemmeta = item.getItemMeta();
		assert itemmeta != null;
		itemmeta.setDisplayName(ITEMNAME);
		itemmeta.setLore(Arrays.asList("Tirez sur un ennemi pour", "§dinvoquer un allié§r"));
		itemmeta.addEnchant(Enchantment.DURABILITY, 200, true);
		itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemmeta);
		return item;
	}

}
