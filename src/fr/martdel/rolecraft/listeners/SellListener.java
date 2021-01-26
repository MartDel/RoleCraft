package fr.martdel.rolecraft.listeners;

import fr.martdel.rolecraft.*;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SellListener implements Listener {

	private RoleCraft plugin;

	public SellListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
	}
	
	@EventHandler
	public void onGUIStart(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack item = event.getItem();
		CustomItems sell_paper = CustomItems.SELL_PAPER;
		if(!(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))) return;
		if(item == null) return;
		ItemMeta meta = item.getItemMeta();
		
		if(meta.equals(sell_paper.getItemMeta())) {
			/*
			 * PLAYER USES 'SELL PAPER'
			 */
			Inventory step1 = GUI.createSellStep1();
			player.openInventory(step1);
		}
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		ItemStack item = event.getCurrentItem();
		InventoryView view = event.getView();
		String title = view.getTitle();
		if(item == null) return;

		// STEP 1
		if(title.equalsIgnoreCase(GUI.SELL_STEP1_NAME)) {
			try {
				ItemMeta iMeta = item.getItemMeta();
				if (iMeta.hasCustomModelData()) {
					int data = iMeta.getCustomModelData();
					player.closeInventory();
					switch (data) {
						case 0:    // Sell an admin ground
							if (!player.isOp()) {
								player.sendMessage("§4Vous devez être admin pour accéder à ce type de vente.");
								return;
							}
							if (!addDataToPaper(player, "admin")) {
								player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
								return;
							}
							player.openInventory(GUI.createSellStep2Admin());
							return;
						case 1:    // Delegate his ground to a builder for a decoration
							customPlayer.loadData();
							if (customPlayer.getHouse() == null && customPlayer.getShop() == null) {
								if ((customPlayer.getJob() == 0 && customPlayer.getFarms().size() == 0) || (customPlayer.getJob() == 3 && customPlayer.getBuilds().size() == 0)) {
									player.sendMessage("§4Vous n'avez actuellement aucun terrain pour cette vente.");
									return;
								}
							}
							if (!addDataToPaper(player, "buy_deco")) {
								player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
								return;
							}
							player.openInventory(GUI.createSellStep2(customPlayer, item.getType(), "all"));
							return;
						case 2:    // Sell a house
							customPlayer.loadData();
//						if(customPlayer.getHouse() == null){
//							player.sendMessage("§4Vous n'avez actuellement pas de maison.");
//							return;
//						}
//						if(plugin.getServer().getOnlinePlayers().size() == 1){
//							player.sendMessage("§4Aucun joueur n'est en ligne.");
//							return;
//						}
							if (!addDataToPaper(player, "house")) {
								player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
								return;
							}
							player.openInventory(GUI.createSellStep3(plugin));
							return;
						case 3:    // Sell a shop
							customPlayer.loadData();
							if (customPlayer.getShop() == null) {
								player.sendMessage("§4Vous n'avez actuellement pas de magasin.");
								return;
							}
							if (plugin.getServer().getOnlinePlayers().size() == 1) {
								player.sendMessage("§4Aucun joueur n'est en ligne.");
								return;
							}
							if (!addDataToPaper(player, "shop")) {
								player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
								return;
							}
							player.openInventory(GUI.createSellStep3(plugin));
							return;
						case 4:    // Sell a farm
							customPlayer.loadData();
							if (customPlayer.getJob() != 0) {
								player.sendMessage("§4Vous devez être fermier pour accéder à cette vente.");
								return;
							}
							if (customPlayer.getFarms().size() == 0) {
								player.sendMessage("§4Vous n'avez actuellement aucune ferme.");
								return;
							}
							if (!addDataToPaper(player, "farm")) {
								player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
								return;
							}
							player.openInventory(GUI.createSellStep2(customPlayer, item.getType(), "farm"));
							return;
						case 5:
						case 6:    // Sell a build ground
							customPlayer.loadData();
							if (customPlayer.getJob() != 3) {
								player.sendMessage("§4Vous devez être builder pour accéder à cette vente.");
								return;
							}
							if (customPlayer.getBuilds().size() == 0) {
								player.sendMessage("§4Vous n'avez actuellement aucun terrain de construction.");
								return;
							}
							if (!addDataToPaper(player, data == 5 ? "build" : "sell_deco")) {
								player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
								return;
							}
							player.openInventory(GUI.createSellStep2(customPlayer, item.getType(), "build"));
							return;
					}
				}
				event.setCancelled(true);
				return;
			} catch (Exception e) {
				// Player clicks on his inventory
			}
		}

		// STEP 2
		if(title.equalsIgnoreCase(GUI.SELL_STEP2_NAME)) {
			try {
				ItemMeta iMeta = item.getItemMeta();
				if(iMeta.hasDisplayName() && iMeta.hasLore()){
					player.closeInventory();
					customPlayer.loadData();

					String key = iMeta.getDisplayName();
					String to_save = "ground";
					if(key.equalsIgnoreCase("§3Maison")) {
						to_save = "house:" + customPlayer.getHouseId();
					} else if (key.equalsIgnoreCase("§6Magasin")) {
						to_save = "shop:" + customPlayer.getShopId();
					} else if(key.contains("§2")) {
						to_save = "farm:" + key;
					} else if (key.contains("§5")){
						to_save = "build:" + key;
					}
					if(addDataToPaper(player, to_save)) {
						if(getDataFromPaper(player).get(0).equalsIgnoreCase("build")){
							try{
								player.openInventory(GUI.createSellStep3Admin(plugin));
							} catch(Exception e){
								player.sendMessage(e.getMessage());
								return;
							}
						} else if(getDataFromPaper(player).get(0).equalsIgnoreCase("farm")){
							try{
								player.openInventory(GUI.createSellStep3Farmer(plugin));
							} catch(Exception e){
								player.sendMessage(e.getMessage());
								return;
							}
						} else {
							if(plugin.getServer().getOnlinePlayers().size() == 1){
								player.sendMessage("§4Aucun joueur n'est en ligne.");
								return;
							}
							player.openInventory(GUI.createSellStep3(plugin));
						}
					} else {
						player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
						return;
					}

					event.setCancelled(true);
					return;
				}
				return;
			} catch (Exception e) {
				// Player clicks on his inventory
			}
		}

		// STEP 2 (Admin)
		if(title.equalsIgnoreCase(GUI.SELL_STEP2_ADMINNAME)) {
			try {
				ItemMeta iMeta = item.getItemMeta();
				if(iMeta.hasDisplayName() && iMeta.hasCustomModelData()){
					player.closeInventory();

					String to_save = "house";
					switch (iMeta.getCustomModelData()){
						case 1: to_save = "house"; break;
						case 2: to_save = "shop"; break;
						case 3: to_save = "farm"; break;
						case 4: to_save = "build"; break;
					}

					if(plugin.getServer().getOnlinePlayers().size() == 1){
						player.sendMessage("§4Aucun joueur n'est en ligne.");
						return;
					}
					if(!addDataToPaper(player, to_save)) {
						player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
						return;
					}
					player.openInventory(GUI.createSellStep3(plugin));

					event.setCancelled(true);
					return;
				}
				return;
			} catch (Exception e) {
				// Player clicks on his inventory
			}
		}

		// STEP 3
		if(title.equalsIgnoreCase(GUI.SELL_STEP3_NAME)) {
			try {
				ItemMeta iMeta = item.getItemMeta();
				if(iMeta.hasDisplayName()){
					player.closeInventory();

					SkullMeta headmeta = (SkullMeta) item.getItemMeta();
					OfflinePlayer clicked_player = headmeta.getOwningPlayer();
					if(!clicked_player.isOnline()){
						player.sendMessage("§4Le joueur sélectionné n'est plus en ligne.");
						return;
					}
					if(!addDataToPaper(player, clicked_player.getName())) {
						player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
						return;
					}
					player.openInventory(GUI.createSellStep4());

					event.setCancelled(true);
					return;
				}
				return;
			} catch (Exception e) {
				// Player clicks on his inventory
			}
		}

		// STEP 4
		if(title.equalsIgnoreCase(GUI.SELL_STEP4_NAME)) {
			try{
				ItemMeta iMeta = item.getItemMeta();
				if(iMeta.hasDisplayName()){
					if(!iMeta.equals(CustomItems.RUBIS.getItemMeta())){
						// Validate the price
						if(iMeta.getDisplayName().equalsIgnoreCase("§2Valider le prix")){
							Integer nb_rubis = Wallet.count(event.getInventory());
							if(!addDataToPaper(player, nb_rubis.toString())) {
								player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
								return;
							}
							player.closeInventory();
							Map<Integer, ItemStack> paper_info = getPaper(player.getInventory());
							ItemStack paper = (ItemStack) paper_info.values().toArray()[0];
							player.openInventory(GUI.createSellStep5(paper.getItemMeta()));
							return;
						}

						// Change nb of Rubis
						ItemStack rubis = new ItemStack(CustomItems.RUBIS.getType(), iMeta.getCustomModelData());
						rubis.setItemMeta(CustomItems.RUBIS.getItemMeta());
						if(iMeta.getDisplayName().contains("-")){
							Wallet.remove(event.getInventory(), rubis.getAmount());
						} else {
							event.getInventory().addItem(rubis);
						}
					}
					event.setCancelled(true);
					return;
				}
				return;
			} catch (Exception e) {
				// Player clicks on his inventory
//				e.printStackTrace();
			}
		}

		// STEP 5
		if(title.equalsIgnoreCase(GUI.SELL_STEP5_NAME)) {
			try{
				ItemMeta iMeta = item.getItemMeta();
				if(iMeta.hasDisplayName()){
					if(item.getType().equals(CustomItems.SELL_PAPER.getType())){
						player.closeInventory();
						Map<Integer, ItemStack> paper_info = getPaper(player.getInventory());
						ItemStack paper = (ItemStack) paper_info.values().toArray()[0];
						List<String> lore = paper.getItemMeta().getLore();

						// Sell a ground
						customPlayer.loadData();
						String sell_type = lore.get(0);
						Player to;
						Integer price;
						Map<String, Integer> ground;

						if(sell_type.equalsIgnoreCase("admin")){
							String type_name = lore.get(1);
							ground = customPlayer.getAdmin_ground();
							to = plugin.getServer().getPlayer(lore.get(2));
							if(to == null){
								player.sendMessage("§4Le joueur sélectionné n'est plus en ligne.");
								return;
							}
							CustomPlayer customTo = new CustomPlayer(to, plugin).loadData();
							price = Integer.parseInt(lore.get(3));
							switch (type_name){
								case "house": customTo.setHouse(ground); break;
								case "shop": customTo.setShop(ground); break;
								case "farm": customTo.addFarm(player.getDisplayName() + UUID.randomUUID().toString().substring(0, 5), ground); break;
								case "build": customTo.addBuild(player.getDisplayName() + UUID.randomUUID().toString().substring(0, 5), ground); break;
							}
						} else if(sell_type.equalsIgnoreCase("buy_deco")){
							String name = lore.get(1);
//							switch (name){
//								case ""
//							}
							to = plugin.getServer().getPlayer(lore.get(2));
							if(to == null){
								player.sendMessage("§4Le joueur sélectionné n'est plus en ligne.");
								return;
							}
							CustomPlayer customTo = new CustomPlayer(to, plugin).loadData();
							price = Integer.parseInt(lore.get(3));
//							customTo.addBuild("Terrain de " + player.getDisplayName(), ground);
						} else if(sell_type.equalsIgnoreCase("house")){
							String name = lore.get(1);
							ground = customPlayer.getAdmin_ground();
							to = plugin.getServer().getPlayer(lore.get(2));
							if(to == null){
								player.sendMessage("§4Le joueur sélectionné n'est plus en ligne.");
								return;
							}
							CustomPlayer customTo = new CustomPlayer(to, plugin).loadData();
							price = Integer.parseInt(lore.get(3));
							customTo.setHouse(ground);
						} else if(sell_type.equalsIgnoreCase("shop")){
							String name = lore.get(1);
							ground = customPlayer.getAdmin_ground();
							to = plugin.getServer().getPlayer(lore.get(2));
							if(to == null){
								player.sendMessage("§4Le joueur sélectionné n'est plus en ligne.");
								return;
							}
							CustomPlayer customTo = new CustomPlayer(to, plugin).loadData();
							price = Integer.parseInt(lore.get(3));
							customTo.setHouse(ground);
						}

						// Transaction

						// Remove paper
					}
					event.setCancelled(true);
					return;
				}
				return;
			} catch (Exception e) {
				// Player clicks on his inventory
			}
		}
	}

	/**
	 * Search the sell paper in the player's inventory
	 * Add data in its lore
	 * Set its lore with data if the lore is equals to the default lore
	 * @param player The player to check
	 * @param data Data to set
	 * @return boolean If everything is ok
	 */
	private boolean addDataToPaper(Player player, String data) {
		Map<Integer, ItemStack> paper_info = getPaper(player.getInventory());
		int paper_slot = (int) paper_info.keySet().toArray()[0];
		ItemStack paper = paper_info.get(paper_slot);

		ItemMeta paperMeta = paper.getItemMeta();
		List<String> lore = paperMeta.getLore();
		if(lore.equals(CustomItems.SELL_PAPER.getLore())) lore.clear();
		lore.add(data);
		paperMeta.setLore(lore);
		paper.setItemMeta(paperMeta);
		player.getInventory().setItem(paper_slot, paper);
		return true;
	}

	/**
	 * Get the paper lore
	 * @param player The player to check
	 * @return List<String> The paper lore
	 */
	private List<String> getDataFromPaper(Player player) {
		Map<Integer, ItemStack> paper_info = getPaper(player.getInventory());
		ItemStack paper = (ItemStack) paper_info.values().toArray()[0];
		ItemMeta paperMeta = paper.getItemMeta();
		List<String> lore = paperMeta.getLore();
		return lore;
	}

	private Map<Integer, ItemStack> getPaper(Inventory inv){
		// Find paper
		ItemStack paper = null;
		Integer paper_slot = null;
		ItemStack[] content = inv.getContents();
		CustomItems template_paper = CustomItems.SELL_PAPER;
		for(int i = 0; i < content.length; i++) {
			if(content[i] != null) {
				ItemStack stack = content[i];
				if(stack.hasItemMeta()) {
					Material type = stack.getType();
					ItemMeta stackMeta = stack.getItemMeta();
					if(type.equals(template_paper.getType()) && stackMeta.hasLore()) {
						paper = stack;
						paper_slot = i;
					}
				}
			}
		}
		if(paper == null && paper_slot == null) return null;

		Map<Integer, ItemStack> result = new HashMap<>();
		result.put(paper_slot, paper);
		return result;
	}

}
