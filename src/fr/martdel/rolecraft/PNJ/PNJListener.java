package fr.martdel.rolecraft.PNJ;

import java.util.Arrays;
import java.util.Map;

import fr.martdel.rolecraft.*;
import fr.martdel.rolecraft.deathroom.DeathKey;
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

			// Deathkey PNJ
			if(name.equalsIgnoreCase(RoleCraft.config.getString("deathkeys.buy_GUI.PNJname"))){
				event.setCancelled(true);
				player.openInventory(GUI.createSellDeathkeys(customPlayer.loadData()));
				return;
			}

			Map<String, PNJ> registered_pnj = PNJ.getAllPNJ();
			if(registered_pnj.containsKey(name)) {
				event.setCancelled(true);
				PNJ custom_pnj = registered_pnj.get(name);
				Integer required_job = custom_pnj.getRequiredJob();
				if(required_job != null && !customPlayer.loadData().getJob().equals(required_job)) return;
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

		if(view.getTitle().equalsIgnoreCase(GUI.SELLDEATHKEYS_NAME)){
			// Player is buying a deathkey
			event.setCancelled(true);
			if(!item.hasItemMeta() && event.getRawSlot() >= GUI.SELLDEATHKEYS_SIZE) return;
			ItemMeta iMeta = item.getItemMeta();
			if(!iMeta.hasCustomModelData()) return;
			int id = iMeta.getCustomModelData();
			DeathKey key = DeathKey.getKeyById(id);
			customPlayer.loadData();
			if(customPlayer.hasKey(key)) return;
			int price = key.getPrice();
			Wallet p_account = customPlayer.getWallet();
			if(!p_account.has(price)) return;
			player.closeInventory();
			p_account.remove(price);

			// Database management for key buying
			customPlayer.addKey(key);
			customPlayer.save();

			iMeta.setLore(Arrays.asList("§8Vous possédez déja", "§8cette clé..."));

			player.sendMessage("§aVous venez d'acheter une deathkey de type §5" + key.toString());
		}
		
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
