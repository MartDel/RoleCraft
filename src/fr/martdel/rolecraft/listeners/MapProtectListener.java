package fr.martdel.rolecraft.listeners;

import java.util.Arrays;
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
	
	private static final List<Material> FORBIDDEN_GROUNDS = Arrays.asList(Material.ACACIA_BUTTON, Material.BIRCH_BUTTON, Material.DARK_OAK_BUTTON, Material.JUNGLE_BUTTON, Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.STONE_BUTTON, Material.LEVER, Material.ACACIA_DOOR, Material.ACACIA_TRAPDOOR, Material.BIRCH_DOOR, Material.BIRCH_TRAPDOOR, Material.DARK_OAK_DOOR, Material.DARK_OAK_TRAPDOOR, Material.IRON_DOOR, Material.IRON_TRAPDOOR, Material.JUNGLE_DOOR, Material.JUNGLE_TRAPDOOR, Material.OAK_DOOR, Material.OAK_TRAPDOOR, Material.SPRUCE_DOOR, Material.SPRUCE_TRAPDOOR, Material.ACACIA_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE);
	private static final List<Material> FORBIDDEN_CITY = Arrays.asList(Material.CHEST, Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL, Material.ITEM_FRAME, Material.BARREL, Material.FURNACE, Material.SMOKER, Material.BLAST_FURNACE, Material.ARMOR_STAND, Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED, Material.CYAN_BED, Material.GRAY_BED, Material.GREEN_BED, Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED, Material.LIME_BED, Material.MAGENTA_BED, Material.ORANGE_BED, Material.PINK_BED, Material.PURPLE_BED, Material.RED_BED, Material.WHITE_BED, Material.YELLOW_BED);
	
	private RoleCraft plugin;

	public MapProtectListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
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
		if(item != null && !LocationInMap.getPlayerPlace(plugin, player).equals(LocationInMap.FREE_PLACE) && !player.isOp()) {
			Material itemtype = item.getType();
			if(itemtype.equals(Material.FLINT_AND_STEEL) || itemtype.equals(Material.ENDER_PEARL)) {
				event.setCancelled(true);
				return;
			}
		}
		/*
		 * MAP PROTECTION
		 */
		if(event.getClickedBlock() != null && action == Action.RIGHT_CLICK_BLOCK) {
			BlockState bs = bloc.getState();
			Material type = bloc.getType();
			Location coo = bloc.getLocation();
			LocationInMap bloc_place = LocationInMap.getBlocPlace(plugin, player, coo);
			
			if(!player.isOp() && !bloc_place.equals(LocationInMap.FREE_PLACE) && !(bs instanceof Entity)) {
				if(!bloc_place.equals(LocationInMap.OWNED)) {	// Is in protected map

					if(FORBIDDEN_CITY.contains(type)) {
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
		if(!player.isOp() && LocationInMap.isInProtectedPlace(plugin, player, coo) && !block.getType().equals(Material.WATER)) {
			event.setCancelled(true);
			System.out.println("Bucket: interdit");
			return;
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
		if(!player.isOp() && LocationInMap.isInProtectedPlace(plugin, player, coo)) {
			event.setCancelled(true);
			return;
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
		if(!player.isOp() && LocationInMap.isInProtectedPlace(plugin, player, location) && entity.getType().equals(EntityType.ITEM_FRAME)) {
			event.setCancelled(true);
			return;
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
			if(!player.isOp() && LocationInMap.isInProtectedPlace(plugin, player, location)
			&& (entity.getType().equals(EntityType.ITEM_FRAME) || entity.getType().equals(EntityType.ARMOR_STAND))) {
				event.setCancelled(true);
				return;
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
		if(LocationInMap.isInProtectedPlace(plugin, player, blocLoc) && !player.isOp()) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onEntityPlace(HangingPlaceEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getEntity();
		Location location = entity.getLocation();
		
		System.out.println(event.getEventName());
		/*
		 * PLAYER PLACES AN ENTITY
		 */
		if(!player.isOp() && LocationInMap.isInProtectedPlace(plugin, player, location)) {
			event.setCancelled(true);
			return;
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
		if(LocationInMap.isInProtectedPlace(plugin, player, location) && !player.isOp()) {
			event.setCancelled(true);
			return;
		}
	}

}
