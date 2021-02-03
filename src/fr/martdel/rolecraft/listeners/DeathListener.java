package fr.martdel.rolecraft.listeners;

import fr.martdel.rolecraft.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class DeathListener implements Listener {

	private RoleCraft plugin;
	private BukkitScheduler scheduler;
	private static final List<DeathRoom> DEATH_ROOMS = DeathRoom.getAllRooms();

	public DeathListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
		this.scheduler = rolecraft.getServer().getScheduler();

	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		Location loc = player.getLocation();

		// Disable drops when a player dead
		scheduler.runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				Collection<Entity> drops = loc.getWorld().getNearbyEntities(loc, 3, 3, 3);
				System.out.println(drops);
				for(Entity e : drops){
					System.out.println(e.getName());
					if(e instanceof Item) e.remove();
				}
			}
		}, 5);

		event.setKeepInventory(true);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);

		DeathRoom room = getFreeRespawnRoom();
		if(room == null) {
			// Waiting room
			return;
		} else room.spawnPlayer(customPlayer, event, plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Block bloc = event.getClickedBlock();

		if(bloc != null) {
			BlockState bs = bloc.getState();
			// Respawn btn is pressed
			if(bloc.getType().equals(Material.STONE_BUTTON) && plugin.getWaiting().getScore(player) == 2) {
				player.teleport(player.getBedSpawnLocation());
				return;
			}
			if(bs instanceof Sign){
				Sign sign = (Sign) bs;
				// Respawn sign is clicked
				if(sign.getLine(0).equalsIgnoreCase("respawn")){
					player.teleport(player.getBedSpawnLocation());
					return;
				}
			}
		}
	}

	private DeathRoom getFreeRespawnRoom(){
		for(DeathRoom room : DEATH_ROOMS){
			if(!room.isCurrentlyUsed()) return room;
		}
		return null;
	}

}
