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

	// Error messages sent when a player try to do an unauthorized action (craft an item, get an item, use a block)
	private static final String ERROR_CRAFT = RoleCraft.config.getString("error_msg.craft_item");
	private static final String ERROR_GET = RoleCraft.config.getString("error_msg.get_item");
	private static final String ERROR_USE = RoleCraft.config.getString("error_msg.use_block");

	// Unauthorized items for crafting and getting
	private static final List<Material> FARMER_GET_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.farmer_get_craft");
	private static final List<Material> BREEDER_GET_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.breeder_get_craft");
	private static final List<Material> MINER_GET_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.miner_get_craft");
	private static final List<Material> GUNSMITH_GET_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.gunsmith_get_craft");
	private static final List<Material> BUILDER_GET_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.builder_get_craft");
	private static final List<Material> ENGINEER_GET_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.engineer_get_craft");
	private static final List<Material> EXPLORER_GET_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.explorer_get_craft");
	private static final List<Material> GUARDIAN_GET_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.guardian_get_craft");

	// Unauthorized items for only crafting
	private static final List<Material> FARMER_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.farmer_craft");
	private static final List<Material> BREEDER_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.breeder_craft");
	private static final List<Material> MINER_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.miner_craft");
	private static final List<Material> GUNSMITH_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.gunsmith_craft");
	private static final List<Material> BUILDER_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.builder_craft");
	private static final List<Material> ENGINEER_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.engineer_craft");
	private static final List<Material> EXPLORER_CRAFT = RoleCraft.getConfigMaterialList("controlled_items.explorer_craft");

	// Unauthorized block for using
	private static final List<Material> USE1 = RoleCraft.getConfigMaterialList("controlled_items.use1");
	private static final List<Material> FARMER_USE = RoleCraft.getConfigMaterialList("controlled_items.farmer_use");
	private static final List<Material> USE2 = RoleCraft.getConfigMaterialList("controlled_items.use2");
	private static final List<Material> USE3 = RoleCraft.getConfigMaterialList("controlled_items.use3");

	public CraftListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		Action action = event.getAction();
		if(customPlayer.isNew()) return;

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
