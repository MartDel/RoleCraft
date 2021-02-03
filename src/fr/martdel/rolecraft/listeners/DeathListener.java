package fr.martdel.rolecraft.listeners;

import fr.martdel.rolecraft.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
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

import javax.management.relation.Role;
import java.util.*;

public class DeathListener implements Listener {

	private RoleCraft plugin;
	private BukkitScheduler scheduler;
	private static final List<DeathRoom> DEATH_ROOMS = DeathRoom.getAllRooms();
	private static final Location WAITING_ROOM = RoleCraft.getConfigLocation((MemorySection) RoleCraft.config.get("waiting_room.spawn"));

	public DeathListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
		this.scheduler = rolecraft.getServer().getScheduler();
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);

		DeathRoom room = getFreeRespawnRoom();
		if(room == null) {
			customPlayer.setWaiting(1);
			event.setRespawnLocation(WAITING_ROOM);
		} else room.spawnPlayer(customPlayer, event, plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		Block bloc = event.getClickedBlock();

		if(bloc != null) {
			BlockState bs = bloc.getState();
			// Respawn btn is pressed
			if(bloc.getType().equals(Material.STONE_BUTTON) && plugin.getWaiting().getScore(player) == 2) {
//				player.teleport(player.getBedSpawnLocation());
				customPlayer.setWaiting(0);

				// Clear all of entities in the room (Items and Mobs)
				Collection<Entity> entities = bloc.getWorld().getNearbyEntities(bloc.getLocation(), 10, 3, 10);
				for(Entity e : entities){
					if(!(e instanceof Player)) e.remove();
				}

				// Tp a waiting player (if he exists)
				scheduler.runTaskLater(plugin, () -> {
					for(Player p : Bukkit.getOnlinePlayers()){
						CustomPlayer customP = new CustomPlayer(p, plugin);
						if(customP.getWaiting() == 1){
							getFreeRespawnRoom().spawnPlayer(customP, plugin);
							return;
						}
					}
				}, 60);
				return;
			}
			if(bs instanceof Sign){
				Sign sign = (Sign) bs;
				// Respawn sign is clicked
				if(sign.getLine(0).equalsIgnoreCase("respawn")){
					player.teleport(player.getBedSpawnLocation());
					customPlayer.setWaiting(0);
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
