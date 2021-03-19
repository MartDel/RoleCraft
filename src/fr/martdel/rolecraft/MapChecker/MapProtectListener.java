package fr.martdel.rolecraft.MapChecker;

import fr.martdel.rolecraft.RoleCraft;
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

import java.util.List;

public class MapProtectListener implements Listener {

	private static final List<Material> FORBIDDEN_GROUNDS = RoleCraft.getConfigMaterialList("controlled_items.grounds"); // Forbidden interactions on grounds
	private static final List<Material> FORBIDDEN_CITY = RoleCraft.getConfigMaterialList("controlled_items.city"); // Forbidden interactions in the city
	
	private RoleCraft plugin;

	public MapProtectListener(RoleCraft rolecraft) { this.plugin = rolecraft; }
	
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
				if(new LocationChecker(player, plugin).isFree()){
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
				if(LocationChecker.isInProtectedMap(coo)) {	// Is in protected map

					if(FORBIDDEN_CITY.contains(type)) {
						// Forbidden some items in protected map
						event.setCancelled(true);
						return;
					}

					List<LocationInMap> bloc_place = new LocationChecker(coo, player, plugin).getType();
					if(bloc_place.contains(LocationInMap.SHOP)) {
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
