package fr.martdel.rolecraft.powers;

import java.util.ArrayList;
import java.util.List;

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

public class Telekinesis {
	
	private static final Material ITEMTYPE = RoleCraft.getConfigMaterial("powers.telekinesis.item_type");
	private static final int MAXDISTANCE = RoleCraft.config.getInt("powers.telekinesis.max_distance");
	private static final String STARTMSG = RoleCraft.config.getString("powers.telekinesis.start_msg");
	private static final String STOPMSG = RoleCraft.config.getString("powers.telekinesis.stop_msg");
	
	public static final String ITEMNAME = RoleCraft.config.getString("powers.telekinesis.item_name");
	public static final String USINGNAME = RoleCraft.config.getString("powers.telekinesis.using_name");

	private Block bloc;
	private final Player player;
	private final RoleCraft plugin;
	private final BukkitScheduler scheduler;
	private int slot;
	
	public Telekinesis(RoleCraft rolecraft, Player player, Block bloc) {
		this.player = player;
		this.bloc = bloc;
		this.plugin = rolecraft;
		this.scheduler = plugin.getServer().getScheduler();
	}
	
	public void moveBloc() {
		assert STARTMSG != null;
		player.sendMessage(STARTMSG);
		int delay = 2;
		scheduler.runTaskLater(plugin, new Runnable() {
			private Location player_loc = null;
			@Override
			public void run() {
				ItemStack tk_item = player.getInventory().getItemInMainHand();
				ItemMeta tk_meta = tk_item.getItemMeta();
				assert tk_meta != null;
				if(!tk_item.hasItemMeta() || !tk_meta.getDisplayName().equalsIgnoreCase(USINGNAME) || player.isSneaking()) {
					assert STOPMSG != null;
					player.sendMessage(STOPMSG);
					player.getInventory().setItem(slot, getItemStack());
					return;
				}
				
				if(player_loc == null) player_loc = player.getLocation();

				Block target_bloc = player.getTargetBlockExact(MAXDISTANCE);
				if(target_bloc == null) {
					scheduler.runTaskLater(plugin, this, delay);
					return;
				}
				
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
					scheduler.runTaskLater(plugin, this, delay);
					return;
				}
				
				List<Block> targets = getEmptyBlocsAround(target_bloc);
				if(targets.isEmpty()) {
					scheduler.runTaskLater(plugin, this, delay);
					return;
				}
				for (Block target : targets) {
					moveTo(target);
					Block player_target = player.getTargetBlockExact(MAXDISTANCE);
					assert player_target != null;
					if(player_target.equals(target)) {
						scheduler.runTaskLater(plugin, this, delay);
						return;
					}
				}
				
				scheduler.runTaskLater(plugin, this, delay);
			}
		}, delay);
	}
	
	public void moveTo(Block target) {
		Material type = bloc.getType();
		if(target.getType().equals(Material.AIR)) {
			bloc.setType(Material.AIR);
			target.setType(type);
			bloc = target;
		}
	}
	public void moveTo(Location target) {
		World target_world = target.getWorld();
		assert target_world != null;
		Block target_bloc = target_world.getBlockAt(target);
		moveTo(target_bloc);
	}
	
	private boolean locationEquals(Location loc1, Location loc2) {
		boolean condition = loc1.getBlockX() == loc2.getBlockX();
		condition = condition && loc1.getBlockZ() == loc2.getBlockZ();
		return condition;
	}

	private List<Block> getEmptyBlocsAround(Block center_bloc) {
		Location center = center_bloc.getLocation();
		World world = center.getWorld(); int x = center.getBlockX();
		int y = center.getBlockY(); int z = center.getBlockZ();
		assert world != null;

		Location[] to_check = {
			new Location(world, x-1, y, z),
			new Location(world, x+1, y, z),
			new Location(world, x, y-1, z),
			new Location(world, x, y+1, z),
			new Location(world, x, y, z-1),
			new Location(world, x, y, z+1)
		};

		List<Block> found = new ArrayList<>();
		for (Location location : to_check) {
			Block current_bloc = world.getBlockAt(location);
			Material type = current_bloc.getType();
			if (type.equals(Material.AIR)) found.add(current_bloc);
		}
		
		return found;
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

	public void setItemSlot(int slot) {
		this.slot = slot;
	}

}
