package fr.martdel.rolecraft.powers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import fr.martdel.rolecraft.RoleCraft;

public class Fertility {
	
	private static final Material ITEMTYPE = RoleCraft.getConfigMaterial("powers.fertility.item_type");
	private static final int MAXRADIUS = RoleCraft.config.getInt("powers.fertility.radius");

	private static final String ENDMSG = RoleCraft.config.getString("powers.fertility.end_msg");
	private static final String NOTFOUNDMSG = RoleCraft.config.getString("powers.fertility.not_found_msg");
	
	private static final Material TREEROOT = RoleCraft.getConfigMaterial("powers.fertility.tree");
	private static final int TREEHEIGHT = RoleCraft.config.getInt("powers.fertility.tree_height");

	public static final String ITEMNAME = RoleCraft.config.getString("powers.fertility.item_name");

	private final Player player;
	private final Location center;
	private final World world;
	private final RoleCraft plugin;
	private final BukkitScheduler scheduler;
	
	public Fertility(RoleCraft plugin, Player player) {
		this.player = player;
		this.center = player.getLocation();
		this.world = center.getWorld();
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
	}
	
	public void run() {
		for (int x = -MAXRADIUS; x <= MAXRADIUS; x++) {
			for (int y = -MAXRADIUS; y <= MAXRADIUS; y++) {
				for (int z = -MAXRADIUS; z <= MAXRADIUS; z++) {
					Location current = new Location(
						world,
						center.getBlockX() + x,
						center.getBlockY() + y,
						center.getBlockZ() + z);
					Block current_bloc  = current.getBlock();
					if(current_bloc.getType().equals(TREEROOT)) {
						current.setY(current.getBlockY() + 1);
						Block next_bloc = current.getBlock();
						if(next_bloc.getType().equals(Material.AIR)) {
							current.setY(current.getBlockY() - 1);
							growTree(current);
							return;
						}
					}
				}
			}
		}
		assert NOTFOUNDMSG != null;
		player.sendMessage(NOTFOUNDMSG);
	}

	public void growTree(Location current) {
		final int delay = 20;
		scheduler.runTaskLater(plugin, new Runnable() {
			private int i = 1;
			@Override
			public void run() {
				assert ENDMSG != null;
				Location new_log = new Location(
					world,
					current.getBlockX(),
					current.getBlockY() + i,
					current.getBlockZ());
				Block new_block = new_log.getBlock();
				if(!new_block.getType().equals(Material.AIR)) {
					player.sendMessage(ENDMSG);
					return;
				}
				
				new_block.setType(TREEROOT);
				
				i++;
				if(i < TREEHEIGHT) scheduler.runTaskLater(plugin, this, delay);
				else {
					player.sendMessage(ENDMSG);
				}
			}
		}, delay);
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
