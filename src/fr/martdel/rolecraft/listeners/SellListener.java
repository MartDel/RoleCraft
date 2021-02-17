package fr.martdel.rolecraft.listeners;

import fr.martdel.rolecraft.*;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
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
	public void onCloseInventory(InventoryCloseEvent event){
		Player player = (Player) event.getPlayer();
		InventoryView view = event.getView();
		String title = view.getTitle();
		if(title.contains("§9")){
			try{
				int step = Integer.parseInt(title.substring(2,3));
				if(step == 5) return;
				Map<Integer, ItemStack> paper_info = getPaper(player.getInventory());
				ItemStack paper = (ItemStack) paper_info.values().toArray()[0];
				if(paper == null) return;
				List<String> lore = paper.getItemMeta().getLore();
				if(step != lore.size()){
					removePaper(player);
				}
			} catch (NumberFormatException ignored) {}
		}
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		ItemStack item = event.getCurrentItem();
		InventoryView view = event.getView();
		String title = view.getTitle();
		int slot = event.getRawSlot();
		if(item == null) return;

		// STEP 1
		if(title.equalsIgnoreCase(GUI.SELL_STEP1_NAME)) {
			try {
				if(!isCorrectItem(item, slot, GUI.SELL_STEP1_SIZE)){
					event.setCancelled(true);
					return;
				}
				ItemMeta iMeta = item.getItemMeta();
				if (iMeta.hasCustomModelData()) {
					int data = iMeta.getCustomModelData();

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
							if(customPlayer.getHouse() == null){
								player.sendMessage("§4Vous n'avez actuellement pas de maison.");
								return;
							}
							if(plugin.getServer().getOnlinePlayers().size() == 1){
								player.sendMessage("§4Aucun joueur n'est en ligne.");
								return;
							}
							if (!addDataToPaper(player, "house")) {
								player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
								return;
							}
							player.openInventory(GUI.createSellStep3(plugin, player));
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
							player.openInventory(GUI.createSellStep3(plugin, player));
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

		// STEP 2 (Admin)
		if(title.equalsIgnoreCase(GUI.SELL_STEP2_ADMINNAME)) {
			try {
				if(!isCorrectItem(item, slot, GUI.SELL_STEP2_ADMINSIZE)){
					event.setCancelled(true);
					return;
				}
				ItemMeta iMeta = item.getItemMeta();
				if(iMeta.hasDisplayName() && iMeta.hasCustomModelData()){

					String to_save = "house";
					Integer required_job = null;
					switch (iMeta.getCustomModelData()){
						case 1: to_save = "house"; break;
						case 2: to_save = "shop"; break;
						case 3: to_save = "farm"; required_job = 0; break;
						case 4: to_save = "build"; required_job = 3; break;
					}

					if(!addDataToPaper(player, to_save)) {
						player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
						return;
					}
					if(required_job != null){
						try{ player.openInventory(GUI.createSellStep3Job(plugin, required_job)); }
						catch (Exception e){ player.sendMessage("§4Aucun joueur pouvant recevoir ce terrain n'est en ligne."); }
					} else {
						if(plugin.getServer().getOnlinePlayers().size() == 1){
							player.sendMessage("§4Aucun joueur n'est en ligne.");
							return;
						}
						player.openInventory(GUI.createSellStep3(plugin, player));
					}

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
				if(!isCorrectItem(item, slot, GUI.SELL_STEP3_SIZE)){
					event.setCancelled(true);
					return;
				}
				ItemMeta iMeta = item.getItemMeta();
				if(iMeta.hasDisplayName()){

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
				if(!isCorrectItem(item, slot, GUI.SELL_STEP4_SIZE)){
					event.setCancelled(true);
					return;
				}
				ItemMeta iMeta = item.getItemMeta();
				if(iMeta.hasDisplayName()){
					if(!iMeta.equals(CustomItems.RUBIS.getItemMeta())){
						// Validate the price
						if(iMeta.getDisplayName().equalsIgnoreCase("§2Valider le prix")){
							int nb_rubis = Wallet.count(event.getInventory());
							System.out.println(nb_rubis);
							if(!addDataToPaper(player, Integer.toString(nb_rubis))) {
								player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
								return;
							}
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
							if(Wallet.count(event.getInventory()) + rubis.getAmount() <= 2304){
								event.getInventory().addItem(rubis);
							}
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
				if(!isCorrectItem(item, slot, GUI.SELL_STEP5_SIZE)){
					event.setCancelled(true);
					return;
				}
				ItemMeta iMeta = item.getItemMeta();
				if(iMeta.hasDisplayName()){
					if(item.getType().equals(CustomItems.SELL_PAPER.getType())){
						Map<Integer, ItemStack> paper_info = getPaper(player.getInventory());
						ItemStack paper = (ItemStack) paper_info.values().toArray()[0];
						List<String> lore = paper.getItemMeta().getLore();

						try{
							sell(player, lore);
						} catch (Exception e){
							player.sendMessage(e.getMessage());
						}
						try{
							player.closeInventory();
						} catch (Exception ignored) {}
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

	private boolean isCorrectItem(ItemStack item, int slot, int max_size) {
		return item.hasItemMeta() && slot < max_size;
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

	private void sell(Player sender, List<String> infos) throws Exception {
		// Sell a ground
		CustomPlayer customSender = new CustomPlayer(sender, plugin).loadData();
		String sell_type = infos.get(0);
		Player to = null;
		CustomPlayer customTo = null;
		Integer price = null;
		Map<String, Integer> ground = null;

		switch (sell_type){
			case "admin":
				String type_name = infos.get(1);
				ground = customSender.getAdmin_ground();
				to = plugin.getServer().getPlayer(infos.get(2));
				if(to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin).loadData();
				price = Integer.parseInt(infos.get(3));

				// Set addressees ground
				switch (type_name){
					case "house": customTo.setHouse(ground); break;
					case "shop": customTo.setShop(ground); break;
					case "farm": customTo.addFarm(sender.getDisplayName() + UUID.randomUUID().toString().substring(0, 5), ground); break;
					case "build": customTo.addBuild(sender.getDisplayName() + UUID.randomUUID().toString().substring(0, 5), ground); break;
				}
				break;
			case "buy_deco":
				String name = infos.get(1);
				if(name.equalsIgnoreCase("house")){
					ground = customSender.getHouse();
				} else if(name.equalsIgnoreCase("shop")){
					ground = customSender.getShop();
				} else if(name.contains("f:")){
					ground = customSender.getFarms().get(name.substring(2, name.length()));
				} else if(name.contains("g:")){
					ground = customSender.getBuilds().get(name.substring(2, name.length()));
				}
				to = plugin.getServer().getPlayer(infos.get(2));
				if(to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin).loadData();
				price = Integer.parseInt(infos.get(3));
				customTo.addBuild("Terrain de " + sender.getDisplayName(), ground);
				break;
			case "house":
				ground = customSender.getHouse();
				to = plugin.getServer().getPlayer(infos.get(1));
				if(to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin).loadData();
				price = Integer.parseInt(infos.get(2));
				customTo.setHouse(ground);
				break;
			case "shop":
				ground = customSender.getShop();
				to = plugin.getServer().getPlayer(infos.get(1));
				if(to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin).loadData();
				price = Integer.parseInt(infos.get(2));
				customTo.setShop(ground);
				break;
			case "farm":
				ground = customSender.getFarms().get(infos.get(1));
				to = plugin.getServer().getPlayer(infos.get(2));
				if(to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin).loadData();
				price = Integer.parseInt(infos.get(3));
				customTo.addFarm(to.getDisplayName() + UUID.randomUUID().toString().substring(0, 5), ground);
				break;
			case "build":
				ground = customSender.getBuilds().get(infos.get(1));
				to = plugin.getServer().getPlayer(infos.get(2));
				if(to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin).loadData();
				price = Integer.parseInt(infos.get(3));
				customTo.addBuild(to.getDisplayName() + UUID.randomUUID().toString().substring(0, 5), ground);
				break;
			case "sell_deco":
				to = plugin.getServer().getPlayer(infos.get(2));
				if(to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin);
				customSender.removeBuild(infos.get(1));
				price = Integer.parseInt(infos.get(3));
				break;
		}

		// Transaction
		Wallet senderWallet = customSender.getWallet();
		Wallet toWallet = customTo.getWallet();
		if(!to.isOp()){
			if(!toWallet.has(price)) throw new Exception("§4Le destinataire n'a pas assez de rubis");
			toWallet.remove(price);
		}
		if(!sender.isOp()) senderWallet.give(price);

		// Remove paper
		removePaper(sender);
		customTo.save();
	}

	public void removePaper(Player player){
		for(ItemStack stack : player.getInventory().getStorageContents()) {
			if(stack != null) {
				ItemMeta meta = stack.getItemMeta();
				String name = meta.getDisplayName();
				if(name.equalsIgnoreCase(CustomItems.SELL_PAPER.getName())) {
					player.getInventory().remove(stack);
				}
			}
		}
	}

}
