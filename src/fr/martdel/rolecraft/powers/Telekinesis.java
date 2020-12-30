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

import fr.martdel.rolecraft.ConditionBuilder;
import fr.martdel.rolecraft.RoleCraft;

public class Telekinesis {
	
	private static final Material ITEMTYPE = Material.getMaterial(RoleCraft.config.getString("powers.telekinesis.item_type"));
	private static final int MAXDISTANCE = RoleCraft.config.getInt("powers.telekinesis.max_distance");
	private static final String STARTMSG = RoleCraft.config.getString("powers.telekinesis.start_msg");
	private static final String STOPMSG = RoleCraft.config.getString("powers.telekinesis.stop_msg");
	
	public static final String ITEMNAME = RoleCraft.config.getString("powers.telekinesis.item_name");
	public static final String USINGNAME = RoleCraft.config.getString("powers.telekinesis.using_name");

	private Block bloc;
	private Player player;
	private RoleCraft plugin;
	private BukkitScheduler scheduler;
	private int slot;
	
	public Telekinesis(RoleCraft rolecraft, Player player, Block bloc) {
		this.player = player;
		this.bloc = bloc;
		this.plugin = rolecraft;
		this.scheduler = plugin.getServer().getScheduler();
	}
	
	public void moveBloc() {
		player.sendMessage(STARTMSG);
		scheduler.runTaskLater(plugin, new Runnable() {
			private Location player_loc = null;
			@Override
			public void run() {
				ItemStack tk_item = player.getInventory().getItemInMainHand();
				Block target_bloc = player.getTargetBlockExact(MAXDISTANCE);
				if(tk_item == null || !tk_item.hasItemMeta() || !tk_item.getItemMeta().getDisplayName().equalsIgnoreCase(USINGNAME) || target_bloc == null) {
					player.sendMessage(STOPMSG);
					player.getInventory().setItem(slot, getItemStack());
					return;
				}
				
				if(player_loc == null) player_loc = player.getLocation();
				
				if(bloc.equals(target_bloc)) {
					Location new_player_loc = player.getLocation();
					if(!locationEquals(player_loc, new_player_loc)) {
						int xdif = new_player_loc.getBlockX() - player_loc.getBlockX();
						int zdif = new_player_loc.getBlockZ() - player_loc.getBlockZ();
						Location bloc_loc = bloc.getLocation();
						bloc_loc.setX(bloc_loc.getBlockX() + xdif);
						bloc_loc.setZ(bloc_loc.getBlockZ() + zdif);
						moveTo(bloc_loc);
						player_loc = new_player_loc;
					}
					scheduler.runTaskLater(plugin, this, 1);
					return;
				}
				
				Block target = getEmptyBlocAround(target_bloc);
				if(target == null) {
					player.sendMessage(STOPMSG);
					player.getInventory().setItem(slot, getItemStack());
					return;
				}
				moveTo(target);
				
				scheduler.runTaskLater(plugin, this, 1);
			}
		}, 1);
	}
	
	public void moveTo(Block target) {
		Material type = bloc.getType();
		bloc.setType(Material.AIR);
		target.setType(type);
		bloc = target;
	}
	public void moveTo(Location target) {
		Block target_bloc = target.getWorld().getBlockAt(target);
		moveTo(target_bloc);
	}
	
	private boolean locationEquals(Location loc1, Location loc2) {
		ConditionBuilder condition = new ConditionBuilder(loc1.getBlockX() == loc2.getBlockX());
		condition.and(loc1.getBlockZ() == loc2.getBlockZ());
		return condition.result();
	}

	private Block getEmptyBlocAround(Block center_bloc) {
		Location center = center_bloc.getLocation();
		World world = center.getWorld(); int x = center.getBlockX();
		int y = center.getBlockY(); int z = center.getBlockZ();
		
		Location[] to_check = {
			new Location(world, x-1, y, z),
			new Location(world, x+1, y, z),
			new Location(world, x, y-1, z),
			new Location(world, x, y+1, z),
			new Location(world, x, y, z-1),
			new Location(world, x, y, z+1)
		};
		
		for (Location check : to_check) {
			Block current_bloc = world.getBlockAt(check);
			Material type = current_bloc.getType();
			if(type.equals(Material.AIR)) return current_bloc;
		}
		
		return null;
	}

	public static ItemStack getItemStack() {
		ItemStack item = new ItemStack(ITEMTYPE);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(ITEMNAME);
		itemmeta.addEnchant(Enchantment.DURABILITY, 200, true);
		itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemmeta);
		return item;
	}

	public void setItemSlot(int slot) {
		this.slot = slot;
	}

}
