package fr.martdel.rolecraft.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.martdel.rolecraft.LocationInMap;
import fr.martdel.rolecraft.RoleCraft;

public class MapProtectListener implements Listener {
	
	private static final List<Material> FORBIDDEN_GROUNDS = new ArrayList<>();
	private static final List<Material> FORBIDDEN_CITY = new ArrayList<>();
	
	private RoleCraft plugin;

	public MapProtectListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
		
		List<String> list;
		// Get forbidden interactions on grounds
		list = RoleCraft.config.getStringList("controled_items.grounds");
		for (String name : list) {
			FORBIDDEN_GROUNDS.add(Material.getMaterial(name));
		}
		
		// Get forbidden interactions on grounds
		list = RoleCraft.config.getStringList("controled_items.city");
		for (String name : list) {
			FORBIDDEN_CITY.add(Material.getMaterial(name));
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		Block bloc = event.getClickedBlock();
		Action action = event.getAction();
		/*
		 * PLAYER USES "flint_and_steel" OR "ender_pearl"
		 */
		if(item != null && !player.isOp()) {
			Material itemtype = item.getType();
			if(itemtype.equals(Material.FLINT_AND_STEEL) || itemtype.equals(Material.ENDER_PEARL)) {
				if(!LocationInMap.getPlayerPlace(plugin, player).equals(LocationInMap.FREE_PLACE)){
					event.setCancelled(true);
					return;
				}
			}
		}
		/*
		 * MAP PROTECTION
		 */
		if(event.getClickedBlock() != null && action == Action.RIGHT_CLICK_BLOCK) {
			BlockState bs = bloc.getState();
			Material type = bloc.getType();
			Location coo = bloc.getLocation();

			if(!player.isOp() && !(bs instanceof Entity)) {
				LocationInMap bloc_place = LocationInMap.getBlocPlace(plugin, player, coo);
				if(!bloc_place.equals(LocationInMap.FREE_PLACE) && !bloc_place.equals(LocationInMap.OWNED)) {	// Is in protected map

					if(FORBIDDEN_CITY.contains(type)) {
						// Forbidden some items in protected map
						event.setCancelled(true);
						return;
					}
					
					if(!bloc_place.equals(LocationInMap.PROTECTED_MAP)) {	// Is in a player ground
						
						if(bloc_place.equals(LocationInMap.SHOP)) {
							// Is in a player shop
							if(FORBIDDEN_GROUNDS.contains(type) && type.equals(Material.CHEST)) {
								event.setCancelled(true);
								return;
							}
						} else {
							// Isn't in a player shop
							if(FORBIDDEN_GROUNDS.contains(type)) {
								event.setCancelled(true);
								System.out.println("Pas chez soi");
								return;
							}
						}
						
					}
					
				}
			}
		}
	}
	
	@EventHandler
	public void onBucketUse(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlockClicked();
		Location coo = block.getLocation();
		/*
		 * PLAYER USES A BUCKET
		 */
		if(!player.isOp() && !block.getType().equals(Material.WATER)) {
			if(LocationInMap.isInProtectedPlace(plugin, player, coo)){
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onArmorStandUse(PlayerArmorStandManipulateEvent event) {
		Player player = event.getPlayer();
		ArmorStand armorstand = event.getRightClicked();
		Location coo = armorstand.getLocation();
		/*
		 * PLAYER USES AN ARMOR STAND
		 */
		if(!player.isOp()) {
			if(LocationInMap.isInProtectedPlace(plugin, player, coo)){
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onEntityUse(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		Location location = entity.getLocation();
		/*
		 * PLAYER INTERACT WHITH UNAUTHORIZED "item_frame"
		 */
		if(!player.isOp() && entity.getType().equals(EntityType.ITEM_FRAME)) {
			if(LocationInMap.isInProtectedPlace(plugin, player, location)){
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Entity p = event.getDamager();
		Entity entity = event.getEntity();
		Location location = entity.getLocation();

		if(p instanceof Player) {
			Player player = (Player) p;
			/*
			 * PLAYER BREAK AN UNAUTHORIZED ENTITY ("item_frame", "armor_stand", ...)
			 */
			if(!player.isOp() && (entity.getType().equals(EntityType.ITEM_FRAME) || entity.getType().equals(EntityType.ARMOR_STAND))) {
				if(LocationInMap.isInProtectedPlace(plugin, player, location)){
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block bloc = event.getBlockPlaced();
		Location blocLoc = bloc.getLocation();
		/*
		 * PLAYER PLACES A BLOCK
		 */
		if(!player.isOp()) {
			if(LocationInMap.isInProtectedPlace(plugin, player, blocLoc)){
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onEntityPlace(HangingPlaceEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getEntity();
		Location location = entity.getLocation();
		/*
		 * PLAYER PLACES AN ENTITY
		 */
		if(!player.isOp()) {
			if(LocationInMap.isInProtectedPlace(plugin, player, location)){
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block bloc = event.getBlock();
		Location location = bloc.getLocation();
		/*
		 * PLAYER BREAKS A BLOCK
		 */
		if(!player.isOp()) {
			if(LocationInMap.isInProtectedPlace(plugin, player, location)){
				event.setCancelled(true);
				return;
			}
		}
	}

}
