package fr.martdel.rolecraft.listeners;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.martdel.rolecraft.CustomItems;
import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.Wallet;
import fr.martdel.rolecraft.PNJ.PNJ;

public class PNJListener implements Listener {
	
	private RoleCraft plugin;
	
	public PNJListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
	}

	@EventHandler
	public void onPNJUse(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		Entity entity = event.getRightClicked();
		
		if(entity instanceof Villager) {
			/*
			 * WHEN A PLAYER INTERACT WITH A PNJ
			 */
			Villager pnj = (Villager) entity;
			String name = pnj.getName();
			Map<String, PNJ> registered_pnj = PNJ.getAllPNJ();
			
			if(registered_pnj.containsKey(name)) {
				event.setCancelled(true);
				PNJ custom_pnj = registered_pnj.get(name);
				if(customPlayer.loadData().getJob() != custom_pnj.getRequiredJob()) return;
				Inventory inv = custom_pnj.getInventory();
				player.openInventory(inv);
			}
		}
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		ItemStack item = event.getCurrentItem();
		InventoryView view = event.getView();

		if(item == null) return;
		
		if(view.getTitle().contains("Marchand - ")) {
			/*
			 * CLICK ON A CUSTOM PNJ INVENTORY
			 */
			try {
				event.setCancelled(true);
				int value = item.getItemMeta().getCustomModelData();
				Material itemtype = item.getType();
				int amount = item.getAmount();
				Wallet p_account = customPlayer.getWallet();
				if(item.getItemMeta().getLore().get(0).contains("vente")) {
					/*
					 * PLAYER SELL SOMETHING
					 */
					if(!itemtype.equals(Material.EMERALD)) {
						if(countItem(player, itemtype) < amount) return;
						
						int count = amount;
						for(ItemStack stack : player.getInventory().getStorageContents()) {
							if(stack != null && count != 0) {
								if(stack.getType().equals(itemtype)) {
									if(stack.getAmount() < count) {
										count -= stack.getAmount();
										stack.setAmount(0);
									} else {
										stack.setAmount(stack.getAmount() - count);
										count = 0;
									}
								}
							}
						}
						p_account.give(value);
					} else { // Emerald bug fixed
						int nb_emerald = 0;
						CustomItems rubis = CustomItems.RUBIS;
						for(ItemStack stack : player.getInventory().getStorageContents()) {
							if(stack != null) {
								ItemMeta meta = stack.getItemMeta();
								if(!meta.equals(rubis.getItemMeta()) && stack.getType().equals(rubis.getType())) {
									nb_emerald += stack.getAmount();
								}
							}
						}
						if(nb_emerald < amount) return;
						
						int count = amount;
						for(ItemStack stack : player.getInventory().getStorageContents()) {
							if(stack != null && count != 0) {
								ItemMeta meta = stack.getItemMeta();
								if(!meta.equals(rubis.getItemMeta()) && stack.getType().equals(rubis.getType())) {
									if(stack.getAmount() < count) {
										count -= stack.getAmount();
										stack.setAmount(0);
									} else {
										stack.setAmount(stack.getAmount() - count);
										count = 0;
									}
								}
							}
						}
						p_account.give(value);
					}
				} else if(!item.getItemMeta().getLore().get(0).contains("vente") && p_account.has(value)) {
					/*
					 * PLAYER BUY SOMETHING
					 */
					player.getInventory().addItem(new ItemStack(itemtype, amount));
					p_account.remove(value);
				}
			} catch (Exception e) {
				// Player clicks on his inventory
			}
			
		}
	}
	
	/**
	 * Count an occurrence of an item in the player's inventory
	 * @param player
	 * @param type Item type
	 * @return Occurrence number
	 */
	private int countItem(Player player, Material type) {
		int c = 0;
		for(ItemStack stack : player.getInventory().getContents()) {
			if(stack != null) {
				Material stacktype = stack.getType();
				if(stacktype.equals(type)) c += stack.getAmount();
			}
		}
		return c;
	}

}
