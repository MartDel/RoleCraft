package fr.martdel.rolecraft.deathroom;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.player.CustomPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;
import java.util.Objects;

public class DeathListener implements Listener {

	private final RoleCraft plugin;
	private final BukkitScheduler scheduler;
	private static final Location WAITING_ROOM = RoleCraft.getConfigLocation((MemorySection) Objects.requireNonNull(RoleCraft.config.get("waiting_room.spawn")), false);
	private static final String RESPAWN_ENTITY_NAME = RoleCraft.config.getString("respawn_entity_name");

	public DeathListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
		this.scheduler = rolecraft.getServer().getScheduler();
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		int current_deathroom = customPlayer.getCurrentDeathroom();
		if(current_deathroom == 0){
			// Save death location
			customPlayer.loadData();
			customPlayer.setDeathLocation(player.getLocation());
			customPlayer.save();
		} else {
			// Respawn the player to his respawn point (death in deathroom)
			respawnPlayer(customPlayer, player.getLocation());
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);

		DeathRoom room = getFreeRespawnRoom();
		if(room == null) {
			// Tp the player to the waiting room
			customPlayer.setCurrentDeathroom(-1);
			event.setRespawnLocation(WAITING_ROOM);
		} else room.spawnPlayer(customPlayer, event, plugin); // Tp the player to a free deathroom
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
		assert itemmeta != null;
		// Check if clicked item is a key
		if(!DeathKey.isKey(item)) return;

		// Manage clicked key and affect item drops
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		int room_id = customPlayer.getCurrentDeathroom();
		DeathRoom room = DeathRoom.getRoomById(room_id, plugin);
		int key_id = itemmeta.getCustomModelData();
		room.spawnPlayer(customPlayer, plugin, key_id != 8 ? DeathKey.getKeyById(key_id) : null);
		view.setCursor(item);
		player.closeInventory();
	}

	@EventHandler
	public void onCloseInventory(InventoryCloseEvent event){
		Player player = (Player) event.getPlayer();
		InventoryView view = event.getView();
		ItemStack cursor = view.getCursor();
		String title = view.getTitle();
		if(!title.contains("§7Room") || cursor == null) return;
		if(!cursor.getType().equals(Material.AIR)){
			view.setCursor(new ItemStack(Material.AIR));
			return;
		}
		// Player didn't choose a key
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		int room_id = Integer.parseInt(title.substring(7,8));
		DeathRoom room = DeathRoom.getRoomById(room_id, plugin);
		room.spawnPlayer(customPlayer, plugin, null);
	}

	@EventHandler
	public void onEntityUse(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		Entity entity = event.getRightClicked();
		String name = entity.getName();
		assert RESPAWN_ENTITY_NAME != null;

		// Check if player has clicked on a respawn entity
		int current_deathroom = customPlayer.getCurrentDeathroom();
		if(name.equalsIgnoreCase(RESPAWN_ENTITY_NAME)){
			// Respawn the player to his spawnpoint
			if(current_deathroom > 0){
				respawnPlayer(customPlayer, entity.getLocation());
			} else if (current_deathroom == -1){
				Location respawn = player.getBedSpawnLocation();
				assert respawn != null;
				player.teleport(respawn);
				player.sendMessage("§2Bon retour parmi nous !");
				customPlayer.setCurrentDeathroom(0);
			}
		}
	}

	/**
	 * Get the first free waiting room
	 * @return The found deathroom or null if nothing was found
	 */
	private DeathRoom getFreeRespawnRoom(){
		for(DeathRoom room : DeathRoom.getAllRooms(plugin)){
			if(!room.isCurrentlyUsed()) return room;
		}
		return null;
	}

	/**
	 * Respawn a player from a deathroom
	 * @param customPlayer Instance of CustomPlayer representing the player to respawn
	 * @param epicentre The epicentre where the entity clear will start
	 */
	private void respawnPlayer(CustomPlayer customPlayer, Location epicentre){
		Player player = customPlayer.getPlayer();
		Location respawn = player.getBedSpawnLocation();
		assert respawn != null;
		player.teleport(respawn);
		player.sendMessage("§2Bon retour parmi nous !");

		DeathRoom dr = DeathRoom.getRoomById(customPlayer.getCurrentDeathroom(), plugin);
		customPlayer.setCurrentDeathroom(0);
		dr.setCurrentlyUsed(false);

		// Clear all of entities in the room (Items and Mobs)
		World world = epicentre.getWorld();
		assert world != null;
		Collection<Entity> entities = world.getNearbyEntities(epicentre, 10, 3, 10);
		for(Entity e : entities){
			String e_name = e.getName();
			if(!(e instanceof Player) && !e_name.equalsIgnoreCase(RESPAWN_ENTITY_NAME)){
				e.remove();
			}
		}

		// Tp a waiting player (if he exists)
		scheduler.runTaskLater(plugin, () -> {
			for(Player p : Bukkit.getOnlinePlayers()){
				CustomPlayer customP = new CustomPlayer(p, plugin);
				if(customP.getCurrentDeathroom() == -1){
					DeathRoom room = getFreeRespawnRoom();
					if(room != null) room.spawnPlayer(customP, plugin);
					return;
				}
			}
		}, 60);
	}

}
