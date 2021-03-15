package fr.martdel.rolecraft.deathroom;

import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.RoleCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;

public class DeathListener implements Listener {

	private final RoleCraft plugin;
	private final BukkitScheduler scheduler;
	private static final Location WAITING_ROOM = RoleCraft.getConfigLocation((MemorySection) RoleCraft.config.get("waiting_room.spawn"), false);

	public DeathListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
		this.scheduler = rolecraft.getServer().getScheduler();
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		customPlayer.setDeathLocation(player.getLocation());
		customPlayer.save();
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
	public void onClickInventory(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		InventoryView view = event.getView();
		String title = view.getTitle();
		if(item == null || !title.contains("§7Room")) return;
		event.setCancelled(true);
		ItemMeta itemmeta = item.getItemMeta();
		if(itemmeta != null && !itemmeta.hasCustomModelData()) return;
		// Check if clicked item is a key
		if(!DeathKey.isKey(item)) return;

		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		int room_id = Integer.parseInt(title.substring(7,8));
		DeathRoom room = DeathRoom.getRoomById(room_id, plugin);
		int key_id = itemmeta.getCustomModelData();
		room.spawnPlayer(customPlayer, plugin, key_id == 8 ? null : DeathKey.getKeyById(key_id));
		view.setCursor(item);
		player.closeInventory();
	}

	@EventHandler
	public void onCloseInventory(InventoryCloseEvent event){
		Player player = (Player) event.getPlayer();
		InventoryView view = event.getView();
		ItemStack cursor = view.getCursor();
		String title = view.getTitle();
		if(!title.contains("§7Room")) return;
		if(!cursor.getType().equals(Material.AIR)){
			view.setCursor(new ItemStack(Material.AIR));
			return;
		}
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		int room_id = Integer.parseInt(title.substring(7,8));
		DeathRoom room = DeathRoom.getRoomById(room_id, plugin);
		room.spawnPlayer(customPlayer, plugin, null); // Player didn't choose a key
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
				player.teleport(player.getBedSpawnLocation());
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
							DeathRoom room = getFreeRespawnRoom();
							if(room != null) room.spawnPlayer(customP, plugin);
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
		for(DeathRoom room : DeathRoom.getAllRooms(plugin)){
			if(!room.isCurrentlyUsed()) return room;
		}
		return null;
	}

}
