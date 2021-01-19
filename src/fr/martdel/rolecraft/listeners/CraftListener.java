package fr.martdel.rolecraft.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.RoleCraft;

public class CraftListener implements Listener {
	
	private RoleCraft plugin;
	
	private static final String ERROR_CRAFT = "§4Votre métier ne vous donne pas le savoir nécessaire pour crafter cet objet";
	private static final String ERROR_GET = "§4Votre métier ne vous donne pas le savoir nécessaire pour obtenir cet objet";
	private static final String ERROR_USE = "§4Votre métier ne vous donne pas le savoir nécessaire pour utiliser cet objet";

	private static final List<Material> FARMER_GET_CRAFT = Arrays.asList(Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.IRON_SWORD, Material.IRON_PICKAXE, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.GOLDEN_SWORD, Material.GOLDEN_PICKAXE, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.STONE_PICKAXE, Material.WOODEN_PICKAXE, Material.CROSSBOW, Material.SHIELD, Material.TURTLE_HELMET, Material.ENCHANTED_BOOK, Material.BREWING_STAND, Material.CAULDRON, Material.ANVIL, Material.GLOWSTONE, Material.ENCHANTING_TABLE, Material.BLAST_FURNACE, Material.SMOKER, Material.SMITHING_TABLE, Material.FLETCHING_TABLE, Material.CARTOGRAPHY_TABLE, Material.QUARTZ_BLOCK, Material.SEA_LANTERN, Material.BRICKS, Material.SCAFFOLDING, Material.DISPENSER, Material.DROPPER, Material.PISTON, Material.STICKY_PISTON, Material.REDSTONE_TORCH, Material.TRIPWIRE_HOOK, Material.REDSTONE_LAMP, Material.HOPPER, Material.IRON_TRAPDOOR, Material.IRON_DOOR, Material.TRAPPED_CHEST, Material.REPEATER, Material.COMPARATOR, Material.BRICK, Material.GLASS);
	private static final List<Material> BREEDER_GET_CRAFT = Arrays.asList(Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.IRON_PICKAXE, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.GOLDEN_SWORD, Material.GOLDEN_PICKAXE, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.STONE_PICKAXE, Material.WOODEN_PICKAXE, Material.CROSSBOW, Material.SHIELD, Material.TURTLE_HELMET, Material.ENCHANTED_BOOK, Material.BREWING_STAND, Material.CAULDRON, Material.ENCHANTING_TABLE, Material.ANVIL, Material.GLOWSTONE, Material.BLAST_FURNACE, Material.SMITHING_TABLE, Material.FLETCHING_TABLE, Material.CARTOGRAPHY_TABLE, Material.QUARTZ_BLOCK, Material.SEA_LANTERN, Material.BRICKS, Material.SCAFFOLDING, Material.DISPENSER, Material.DROPPER, Material.PISTON, Material.STICKY_PISTON, Material.REDSTONE_TORCH, Material.TRIPWIRE_HOOK, Material.REDSTONE_LAMP, Material.HOPPER, Material.IRON_TRAPDOOR, Material.IRON_DOOR, Material.TRAPPED_CHEST, Material.REPEATER, Material.COMPARATOR, Material.BRICK, Material.GLASS);
	private static final List<Material> MINER_GET_CRAFT = Arrays.asList(Material.IRON_CHESTPLATE, Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.IRON_AXE, Material.IRON_HOE, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.STONE_AXE, Material.STONE_HOE, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.SHEARS, Material.CROSSBOW, Material.SHIELD, Material.TURTLE_HELMET, Material.ENCHANTED_BOOK, Material.BREWING_STAND, Material.CAULDRON, Material.ENCHANTING_TABLE, Material.GLOWSTONE, Material.SMOKER, Material.SMITHING_TABLE, Material.FLETCHING_TABLE, Material.COMPOSTER, Material.GLASS, Material.CARTOGRAPHY_TABLE, Material.QUARTZ_BLOCK, Material.SEA_LANTERN, Material.BRICKS, Material.SCAFFOLDING, Material.DISPENSER, Material.DROPPER, Material.PISTON, Material.STICKY_PISTON, Material.REDSTONE_TORCH, Material.TRIPWIRE_HOOK, Material.REDSTONE_LAMP, Material.HOPPER, Material.IRON_TRAPDOOR, Material.IRON_DOOR, Material.TRAPPED_CHEST, Material.REPEATER, Material.COMPARATOR, Material.BRICK);
	private static final List<Material> GUNSMITH_GET_CRAFT = Arrays.asList(Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.IRON_AXE, Material.IRON_HOE, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.STONE_AXE, Material.STONE_HOE, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.SHEARS, Material.GLOWSTONE, Material.SMOKER, Material.FLETCHING_TABLE, Material.COMPOSTER, Material.GLASS, Material.CARTOGRAPHY_TABLE, Material.QUARTZ_BLOCK, Material.SEA_LANTERN, Material.BRICKS, Material.SCAFFOLDING, Material.DISPENSER, Material.DROPPER, Material.PISTON, Material.STICKY_PISTON, Material.REDSTONE_TORCH, Material.TRIPWIRE_HOOK, Material.REDSTONE_LAMP, Material.HOPPER, Material.IRON_TRAPDOOR, Material.IRON_DOOR, Material.TRAPPED_CHEST, Material.REPEATER, Material.COMPARATOR, Material.BRICK);
	private static final List<Material> BUILDER_GET_CRAFT = Arrays.asList(Material.DIAMOND_SWORD, Material.DIAMOND_HOE, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.IRON_SWORD, Material.IRON_HOE, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.GOLDEN_SWORD, Material.GOLDEN_HOE, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.STONE_SWORD, Material.STONE_HOE, Material.WOODEN_HOE, Material.SHEARS, Material.CROSSBOW, Material.SHIELD, Material.TURTLE_HELMET, Material.BOW, Material.ENCHANTED_BOOK, Material.BREWING_STAND, Material.ENCHANTING_TABLE, Material.BUCKET, Material.BLAST_FURNACE, Material.SMOKER, Material.COMPOSTER, Material.DISPENSER, Material.DROPPER, Material.PISTON, Material.STICKY_PISTON, Material.REDSTONE_TORCH, Material.TRIPWIRE_HOOK, Material.REDSTONE_LAMP, Material.HOPPER, Material.IRON_TRAPDOOR, Material.IRON_DOOR, Material.TRAPPED_CHEST, Material.REPEATER, Material.COMPARATOR);
	private static final List<Material> ENGINEER_GET_CRAFT = Arrays.asList(Material.DIAMOND_SWORD, Material.DIAMOND_HOE, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.IRON_SWORD, Material.IRON_SHOVEL, Material.IRON_HOE, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.GOLDEN_SWORD, Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.STONE_SWORD, Material.STONE_SHOVEL, Material.STONE_HOE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE, Material.SHEARS, Material.CROSSBOW, Material.BOW, Material.SHIELD, Material.TURTLE_HELMET, Material.ENCHANTED_BOOK, Material.BREWING_STAND, Material.CAULDRON, Material.ENCHANTING_TABLE, Material.BUCKET, Material.BLAST_FURNACE, Material.SMOKER, Material.COMPOSTER);
	private static final List<Material> EXPLORER_GET_CRAFT = Arrays.asList(Material.DIAMOND_CHESTPLATE, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.STONE_HOE, Material.WOODEN_HOE, Material.SHEARS, Material.BREWING_STAND, Material.CAULDRON, Material.ENCHANTING_TABLE, Material.ANVIL, Material.BUCKET, Material.FURNACE, Material.GLOWSTONE, Material.BLAST_FURNACE, Material.SMOKER, Material.SMITHING_TABLE, Material.FLETCHING_TABLE, Material.COMPOSTER, Material.GLASS, Material.CARTOGRAPHY_TABLE, Material.QUARTZ_BLOCK, Material.BRICKS, Material.SCAFFOLDING, Material.DISPENSER, Material.DROPPER, Material.PISTON, Material.STICKY_PISTON, Material.REDSTONE_TORCH, Material.TRIPWIRE_HOOK, Material.REDSTONE_LAMP, Material.HOPPER, Material.IRON_TRAPDOOR, Material.IRON_DOOR, Material.TRAPPED_CHEST, Material.REPEATER, Material.COMPARATOR, Material.BRICK);
	private static final List<Material> GUARDIAN_GET_CRAFT = Arrays.asList(Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.STONE_HOE, Material.WOODEN_HOE, Material.SHEARS, Material.BREWING_STAND, Material.CAULDRON, Material.ENCHANTING_TABLE, Material.ANVIL, Material.BUCKET, Material.FURNACE, Material.GLOWSTONE, Material.BLAST_FURNACE, Material.SMOKER, Material.SMITHING_TABLE, Material.FLETCHING_TABLE, Material.COMPOSTER, Material.GLASS, Material.CARTOGRAPHY_TABLE, Material.QUARTZ_BLOCK, Material.BRICKS, Material.SCAFFOLDING, Material.DISPENSER, Material.DROPPER, Material.PISTON, Material.STICKY_PISTON, Material.REDSTONE_TORCH, Material.TRIPWIRE_HOOK, Material.REDSTONE_LAMP, Material.HOPPER, Material.IRON_TRAPDOOR, Material.IRON_DOOR, Material.TRAPPED_CHEST, Material.REPEATER, Material.COMPARATOR, Material.BRICK);
	
	private static final List<Material> FARMER_CRAFT = Arrays.asList(Material.STONE_SWORD, Material.GLISTERING_MELON_SLICE, Material.GOLDEN_CARROT, Material.SHULKER_BOX, Material.BOOKSHELF, Material.JUKEBOX, Material.LANTERN, Material.LECTERN, Material.NOTE_BLOCK, Material.OAK_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.STONE_PRESSURE_PLATE, Material.OAK_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE, Material.ACACIA_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.GOLDEN_APPLE, Material.FLOWER_POT, Material.LEVER, Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.BIRCH_BUTTON, Material.ACACIA_BUTTON, Material.JUNGLE_BUTTON, Material.DARK_OAK_BUTTON, Material.STONE_BUTTON, Material.OAK_FENCE, Material.SPRUCE_FENCE, Material.BIRCH_FENCE, Material.ACACIA_FENCE, Material.JUNGLE_FENCE, Material.DARK_OAK_FENCE, Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL, Material.BRICK_WALL, Material.GRANITE_WALL, Material.DIORITE_WALL, Material.ANDESITE_WALL, Material.PRISMARINE_WALL, Material.STONE_BRICK_WALL, Material.MOSSY_STONE_BRICK_WALL, Material.END_STONE_BRICK_WALL, Material.NETHER_BRICK_WALL, Material.RED_NETHER_BRICK_WALL, Material.SANDSTONE_WALL, Material.RED_SANDSTONE_WALL);
	private static final List<Material> BREEDER_CRAFT = Arrays.asList(Material.GLISTERING_MELON_SLICE, Material.GOLDEN_CARROT, Material.SHULKER_BOX, Material.BOOKSHELF, Material.JUKEBOX, Material.LANTERN, Material.LECTERN, Material.NOTE_BLOCK, Material.OAK_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.STONE_PRESSURE_PLATE, Material.OAK_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE, Material.ACACIA_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.GOLDEN_APPLE, Material.FLOWER_POT, Material.LEVER, Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.BIRCH_BUTTON, Material.ACACIA_BUTTON, Material.JUNGLE_BUTTON, Material.DARK_OAK_BUTTON, Material.STONE_BUTTON);
	private static final List<Material> MINER_CRAFT = Arrays.asList(Material.IRON_HELMET, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.IRON_SWORD, Material.GLISTERING_MELON_SLICE, Material.GOLDEN_CARROT, Material.SHULKER_BOX, Material.BARREL, Material.BOOKSHELF, Material.JUKEBOX, Material.LANTERN, Material.LECTERN, Material.NOTE_BLOCK, Material.OAK_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.STONE_PRESSURE_PLATE, Material.OAK_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE, Material.ACACIA_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.GOLDEN_APPLE, Material.BREAD, Material.MUSHROOM_STEW, Material.BEETROOT_SOUP, Material.FLOWER_POT, Material.LEVER, Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.BIRCH_BUTTON, Material.ACACIA_BUTTON, Material.JUNGLE_BUTTON, Material.DARK_OAK_BUTTON, Material.STONE_BUTTON, Material.OAK_FENCE, Material.SPRUCE_FENCE, Material.BIRCH_FENCE, Material.ACACIA_FENCE, Material.JUNGLE_FENCE, Material.DARK_OAK_FENCE, Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL, Material.BRICK_WALL, Material.GRANITE_WALL, Material.DIORITE_WALL, Material.ANDESITE_WALL, Material.PRISMARINE_WALL, Material.STONE_BRICK_WALL, Material.MOSSY_STONE_BRICK_WALL, Material.END_STONE_BRICK_WALL, Material.NETHER_BRICK_WALL, Material.RED_NETHER_BRICK_WALL, Material.SANDSTONE_WALL, Material.RED_SANDSTONE_WALL);
	private static final List<Material> GUNSMITH_CRAFT = Arrays.asList(Material.SHULKER_BOX, Material.BARREL, Material.JUKEBOX, Material.LANTERN, Material.LECTERN, Material.NOTE_BLOCK, Material.OAK_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.STONE_PRESSURE_PLATE, Material.OAK_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE, Material.ACACIA_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.BREAD, Material.MUSHROOM_STEW, Material.BEETROOT_SOUP, Material.FLOWER_POT, Material.LEVER, Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.BIRCH_BUTTON, Material.ACACIA_BUTTON, Material.JUNGLE_BUTTON, Material.DARK_OAK_BUTTON, Material.STONE_BUTTON, Material.OAK_FENCE, Material.SPRUCE_FENCE, Material.BIRCH_FENCE, Material.ACACIA_FENCE, Material.JUNGLE_FENCE, Material.DARK_OAK_FENCE, Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL, Material.BRICK_WALL, Material.GRANITE_WALL, Material.DIORITE_WALL, Material.ANDESITE_WALL, Material.PRISMARINE_WALL, Material.STONE_BRICK_WALL, Material.MOSSY_STONE_BRICK_WALL, Material.END_STONE_BRICK_WALL, Material.NETHER_BRICK_WALL, Material.RED_NETHER_BRICK_WALL, Material.SANDSTONE_WALL, Material.RED_SANDSTONE_WALL);
	private static final List<Material> BUILDER_CRAFT = Arrays.asList(Material.GLISTERING_MELON_SLICE, Material.GOLDEN_CARROT, Material.JUKEBOX, Material.ANVIL, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.GOLDEN_APPLE, Material.BREAD, Material.MUSHROOM_STEW, Material.BEETROOT_SOUP);
	private static final List<Material> ENGINEER_CRAFT = Arrays.asList(Material.GLISTERING_MELON_SLICE, Material.GOLDEN_CARROT, Material.ANVIL, Material.GOLDEN_APPLE, Material.BREAD, Material.MUSHROOM_STEW, Material.BEETROOT_SOUP);
	private static final List<Material> EXPLORER_CRAFT = Arrays.asList(Material.DIAMOND_SWORD, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.CROSSBOW, Material.TURTLE_HELMET, Material.ENCHANTED_BOOK, Material.GLISTERING_MELON_SLICE, Material.GOLDEN_CARROT, Material.SHULKER_BOX, Material.BARREL, Material.BOOKSHELF, Material.JUKEBOX, Material.LANTERN, Material.LECTERN, Material.NOTE_BLOCK, Material.OAK_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.STONE_PRESSURE_PLATE, Material.OAK_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE, Material.ACACIA_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.GOLDEN_APPLE, Material.BREAD, Material.MUSHROOM_STEW, Material.BEETROOT_SOUP, Material.SEA_LANTERN, Material.FLOWER_POT, Material.LEVER, Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.BIRCH_BUTTON, Material.ACACIA_BUTTON, Material.JUNGLE_BUTTON, Material.DARK_OAK_BUTTON, Material.STONE_BUTTON, Material.OAK_FENCE, Material.SPRUCE_FENCE, Material.BIRCH_FENCE, Material.ACACIA_FENCE, Material.JUNGLE_FENCE, Material.DARK_OAK_FENCE, Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL, Material.BRICK_WALL, Material.GRANITE_WALL, Material.DIORITE_WALL, Material.ANDESITE_WALL, Material.PRISMARINE_WALL, Material.STONE_BRICK_WALL, Material.MOSSY_STONE_BRICK_WALL, Material.END_STONE_BRICK_WALL, Material.NETHER_BRICK_WALL, Material.RED_NETHER_BRICK_WALL, Material.SANDSTONE_WALL, Material.RED_SANDSTONE_WALL);

	private static final List<Material> USE1 = Arrays.asList(Material.ENCHANTING_TABLE, Material.STONECUTTER, Material.GRINDSTONE, Material.LOOM);
	private static final List<Material> FARMER_USE = Arrays.asList(Material.ENCHANTING_TABLE, Material.STONECUTTER, Material.GRINDSTONE, Material.LOOM, Material.SMOKER);
	private static final List<Material> USE2 = Arrays.asList(Material.ENCHANTING_TABLE, Material.GRINDSTONE);
	private static final List<Material> USE3 = Arrays.asList(Material.STONECUTTER, Material.LOOM);

	public CraftListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		Action action = event.getAction();
		
		if(!player.isOp() && action == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
			Material itemtype = event.getClickedBlock().getType();
			/*
			 * PLAYER USES
			 */
			switch(customPlayer.getJob()) {
			case 0:
				if((!customPlayer.hasSpe() && FARMER_USE.contains(itemtype)) || (customPlayer.hasSpe() && USE1.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_USE);
				}
				break;
			case 1:
				if((!customPlayer.hasSpe() && USE1.contains(itemtype)) || (customPlayer.hasSpe() && USE3.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_USE);
				}
				break;
			case 2:
				if(USE1.contains(itemtype)) {
					event.setCancelled(true);
					player.sendMessage(ERROR_USE);
				}
				break;
			case 3:
				if(USE2.contains(itemtype)) {
					event.setCancelled(true);
					player.sendMessage(ERROR_USE);
				}
				break;
			}
		}
	}

	@EventHandler
	public void onCraft(CraftItemEvent event) {
		Player player = (Player) event.getWhoClicked();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		ItemStack item = event.getCurrentItem();
		Material itemtype = item.getType();

		if(!player.isOp()) {
			/*
			 * PLAYER CRAFTS ITEM (OR GETS) 
			 */
			switch(customPlayer.getJob()) {
			case 0:
				if((!customPlayer.hasSpe() && FARMER_GET_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && BREEDER_GET_CRAFT.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_CRAFT);
				}
				break;
			case 1:
				if((!customPlayer.hasSpe() && MINER_GET_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && GUNSMITH_GET_CRAFT.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_CRAFT);
				}
				break;
			case 2:
				if((!customPlayer.hasSpe() && EXPLORER_GET_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && GUARDIAN_GET_CRAFT.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_CRAFT);
				}
				break;
			case 3:
				if((!customPlayer.hasSpe() && BUILDER_GET_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && ENGINEER_GET_CRAFT.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_CRAFT);
				}
				break;
			}
			/*
			 * PLAYER CRAFTS ITEM
			 */
			switch(customPlayer.getJob()) {
			case 0:
				if((!customPlayer.hasSpe() && FARMER_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && BREEDER_CRAFT.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_CRAFT);
				}
				break;
			case 1:
				if((!customPlayer.hasSpe() && MINER_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && GUNSMITH_CRAFT.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_CRAFT);
				}
				break;
			case 2:
				if(EXPLORER_CRAFT.contains(itemtype)) {
					event.setCancelled(true);
					player.sendMessage(ERROR_CRAFT);
				}
				break;
			case 3:
				if((!customPlayer.hasSpe() && BUILDER_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && ENGINEER_CRAFT.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_CRAFT);
				}
				break;
			}
		}
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		ItemStack item = event.getCurrentItem();
		InventoryView view = event.getView();

		if(item == null) return;
		Material itemtype = item.getType();
		/*
		 * PLAYER GETS ITEM IN INVENTORY
		 */
		if(!view.getTitle().equalsIgnoreCase("Crafting") && !customPlayer.isNew() && !player.isOp()) {
			switch(customPlayer.getJob()) {
			case 0:
				if((!customPlayer.hasSpe() && FARMER_GET_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && BREEDER_GET_CRAFT.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_GET);
				}
				break;
			case 1:
				if((!customPlayer.hasSpe() && MINER_GET_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && GUNSMITH_GET_CRAFT.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_GET);
				}
				break;
			case 2:
				if((!customPlayer.hasSpe() && EXPLORER_GET_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && GUARDIAN_GET_CRAFT.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_GET);
				}
				break;
			case 3:
				if((!customPlayer.hasSpe() && BUILDER_GET_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && ENGINEER_GET_CRAFT.contains(itemtype))) {
					event.setCancelled(true);
					player.sendMessage(ERROR_GET);
				}
				break;
			}
			return;
		}
	}
	
	@EventHandler
	public void onPlayerPickup(EntityPickupItemEvent event) {
		LivingEntity entity = event.getEntity();
		Item item = event.getItem();
		
		if(entity instanceof Player) {
			Player player = (Player) entity;
			CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
			Material itemtype = item.getItemStack().getType();
			
			if(!player.isOp()) {
				/*
				 * PLAYER PICKUPS ITEM
				 */
				switch(customPlayer.getJob()) {
				case 0:
					if((!customPlayer.hasSpe() && FARMER_GET_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && BREEDER_GET_CRAFT.contains(itemtype))) {
						event.setCancelled(true);
					}
					break;
				case 1:
					if((!customPlayer.hasSpe() && MINER_GET_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && GUNSMITH_GET_CRAFT.contains(itemtype))) {
						event.setCancelled(true);
					}
					break;
				case 2:
					if((!customPlayer.hasSpe() && EXPLORER_GET_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && GUARDIAN_GET_CRAFT.contains(itemtype))) {
						event.setCancelled(true);
					}
					break;
				case 3:
					if((!customPlayer.hasSpe() && BUILDER_GET_CRAFT.contains(itemtype)) || (customPlayer.hasSpe() && ENGINEER_GET_CRAFT.contains(itemtype))) {
						event.setCancelled(true);
					}
					break;
				}
			}
			
		}
	}

}
