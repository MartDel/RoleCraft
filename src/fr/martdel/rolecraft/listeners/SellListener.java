package fr.martdel.rolecraft.listeners;

import fr.martdel.rolecraft.*;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
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
				Integer paperslot = getPaperSlot(player.getInventory());
				if(paperslot == null) return;
				ItemStack paper = player.getInventory().getItem(paperslot);
				String name = paper.getItemMeta().getDisplayName();
				int written_step = Integer.parseInt(name.substring(8));
				if(written_step == step) removePaper(player);
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
			event.setCancelled(true);
			if(!isCorrectItem(item, slot, GUI.SELL_STEP1_SIZE)) return;
			ItemMeta iMeta = item.getItemMeta();
			if (!iMeta.hasCustomModelData()) return;

			int data = iMeta.getCustomModelData();
			switch (data) {
				case 0:    // Sell an admin ground
					if (!player.isOp()) {
						GUI.error(player,"§4Vous devez être admin pour accéder à ce type de vente.");
						return;
					}
					if (!addDataToPaper(player, "admin", 2)) {
						GUI.error(player, "§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
						return;
					}
					player.openInventory(GUI.createSellStep2Admin());
					break;
				case 1:    // Delegate his ground to a builder for a decoration
					customPlayer.loadData();
					if (customPlayer.getHouse() == null && customPlayer.getShop() == null) {
						if ((customPlayer.getJob() == 0 && customPlayer.getFarms().size() == 0) || (customPlayer.getJob() == 3 && customPlayer.getBuilds().size() == 0)) {
							GUI.error(player, "§4Vous n'avez actuellement aucun terrain pour cette vente.");
							return;
						}
					}
					if (!addDataToPaper(player, "buy_deco", 2)) {
						GUI.error(player, "§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
						return;
					}
					player.openInventory(GUI.createSellStep2(customPlayer, item.getType(), "all"));
					break;
				case 2:    // Sell a house
					customPlayer.loadData();
					if(customPlayer.getHouse() == null){
						GUI.error(player, "§4Vous n'avez actuellement pas de maison.");
						return;
					}
					if(plugin.getServer().getOnlinePlayers().size() == 1){
						GUI.error(player, "§4Aucun joueur n'est en ligne.");
						return;
					}
					if (!addDataToPaper(player, "house", 3)) {
						GUI.error(player, "§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
						return;
					}
					player.openInventory(GUI.createSellStep3(plugin, player));
					break;
				case 3:    // Sell a shop
					customPlayer.loadData();
					if (customPlayer.getShop() == null) {
						GUI.error(player, "§4Vous n'avez actuellement pas de magasin.");
						return;
					}
					if (plugin.getServer().getOnlinePlayers().size() == 1) {
						GUI.error(player, "§4Aucun joueur n'est en ligne.");
						return;
					}
					if (!addDataToPaper(player, "shop", 3)) {
						GUI.error(player, "§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
						return;
					}
					player.openInventory(GUI.createSellStep3(plugin, player));
					break;
				case 4:    // Sell a farm
					customPlayer.loadData();
					if (customPlayer.getJob() != 0) {
						GUI.error(player, "§4Vous devez être fermier pour accéder à cette vente.");
						return;
					}
					if (customPlayer.getFarms().size() == 0) {
						GUI.error(player, "§4Vous n'avez actuellement aucune ferme.");
						return;
					}
					if (!addDataToPaper(player, "farm", 2)) {
						GUI.error(player, "§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
						return;
					}
					player.openInventory(GUI.createSellStep2(customPlayer, item.getType(), "farm"));
					break;
				case 5:
				case 6:    // Sell a build ground
					customPlayer.loadData();
					if (customPlayer.getJob() != 3) {
						GUI.error(player, "§4Vous devez être builder pour accéder à cette vente.");
						return;
					}
					if (customPlayer.getBuilds().size() == 0) {
						GUI.error(player, "§4Vous n'avez actuellement aucun terrain de construction.");
						return;
					}
					if (!addDataToPaper(player, data == 5 ? "build" : "sell_deco", 2)) {
						GUI.error(player, "§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
						return;
					}
					player.openInventory(GUI.createSellStep2(customPlayer, item.getType(), "build"));
					break;
			}
		}

		// STEP 2
		if(title.equalsIgnoreCase(GUI.SELL_STEP2_NAME)){
			event.setCancelled(true);
			if(!isCorrectItem(item, slot, GUI.SELL_STEP2_SIZE)) return;
			ItemMeta iMeta = item.getItemMeta();
			if(!iMeta.hasDisplayName()) return;

			// Get saving data
			String ground = iMeta.getDisplayName();
			String to_save;
			if(ground.contains("§6")) to_save = "shop";
			else if(ground.contains("§2") || ground.contains("§5")) to_save = ground.substring(2, ground.length());
			else to_save = "house";

			// Get required job
			List<String> lore = getDataFromPaper(player);
			String type = lore.get(0);
			int required_job;
			if(type.equalsIgnoreCase("farm")) required_job = 0;
			else required_job = 3;

			try{
				Inventory to_show = GUI.createSellStep3Job(plugin, required_job, player);
				if(!addDataToPaper(player, to_save, 3)) {
					GUI.error(player, "§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
					return;
				}
				player.openInventory(to_show);
			} catch (Exception e){ GUI.error(player, "§4Aucun joueur pouvant recevoir ce terrain n'est en ligne."); }
		}

		// STEP 2 (Admin)
		if(title.equalsIgnoreCase(GUI.SELL_STEP2_ADMINNAME)) {
			event.setCancelled(true);
			if(!isCorrectItem(item, slot, GUI.SELL_STEP2_ADMINSIZE)) return;
			ItemMeta iMeta = item.getItemMeta();
			if(!iMeta.hasDisplayName() || !iMeta.hasCustomModelData()) return;

			String to_save = "house";
			Integer required_job = null;
			switch (iMeta.getCustomModelData()){
				case 1: to_save = "house"; break;
				case 2: to_save = "shop"; break;
				case 3: to_save = "farm"; required_job = 0; break;
				case 4: to_save = "build"; required_job = 3; break;
			}

			if(required_job != null){
				try{
					Inventory to_show = GUI.createSellStep3Job(plugin, required_job, player);
					if(!addDataToPaper(player, to_save, 3)) {
						GUI.error(player, "§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
						return;
					}
					player.openInventory(to_show);
				} catch (Exception e){ GUI.error(player, "§4Aucun joueur pouvant recevoir ce terrain n'est en ligne."); }
			} else {
				if(plugin.getServer().getOnlinePlayers().size() == 1){
					GUI.error(player, "§4Aucun joueur n'est en ligne.");
					return;
				}
				if(!addDataToPaper(player, to_save, 3)) {
					GUI.error(player, "§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
					return;
				}
				player.openInventory(GUI.createSellStep3(plugin, player));
			}
		}

		// STEP 3
		if(title.equalsIgnoreCase(GUI.SELL_STEP3_NAME)) {
			event.setCancelled(true);
			if(!isCorrectItem(item, slot, GUI.SELL_STEP3_SIZE)) return;
			ItemMeta iMeta = item.getItemMeta();
			if(!iMeta.hasDisplayName()) return;

			SkullMeta headmeta = (SkullMeta) item.getItemMeta();
			OfflinePlayer clicked_player = headmeta.getOwningPlayer();
			if(!clicked_player.isOnline()){
				GUI.error(player, "§4Le joueur sélectionné n'est plus en ligne.");
				return;
			}

			if(getDataFromPaper(player).get(0).equalsIgnoreCase("buy_deco")){
				if(!addDataToPaper(player, clicked_player.getName(), 5)) {
					GUI.error(player, "§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
					return;
				}
				Integer paperslot = getPaperSlot(player.getInventory());
				ItemStack paper = player.getInventory().getItem(paperslot);
				player.openInventory(GUI.createSellStep5(paper.getItemMeta()));
			} else {
				if(!addDataToPaper(player, clicked_player.getName(), 4)) {
					GUI.error(player, "§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
					return;
				}
				player.openInventory(GUI.createSellStep4());
			}
		}

		// STEP 4
		if(title.equalsIgnoreCase(GUI.SELL_STEP4_NAME)) {
			event.setCancelled(true);
			if(!isCorrectItem(item, slot, GUI.SELL_STEP4_SIZE)) return;
			ItemMeta iMeta = item.getItemMeta();
			if(!iMeta.hasDisplayName() || iMeta.equals(CustomItems.RUBIS.getItemMeta())) return;

			// Change nb of Rubis
			ItemStack rubis = new ItemStack(CustomItems.RUBIS.getType(), iMeta.getCustomModelData());
			rubis.setItemMeta(CustomItems.RUBIS.getItemMeta());
			String name = iMeta.getDisplayName();
			if(name.contains("-")){
				Wallet.remove(event.getInventory(), rubis.getAmount());
			} else if(name.contains("+")) {
				if(Wallet.count(event.getInventory()) + rubis.getAmount() <= 2304){
					event.getInventory().addItem(rubis);
				}
			}

			// Update submit button
			final int btn_slot = 40;
			int nb_rubis = Wallet.count(event.getInventory());
			ItemStack submit = view.getItem(btn_slot);
			ItemMeta submitbtn = submit.getItemMeta();
			submitbtn.setLore(Collections.singletonList("§fTotal : §a" + nb_rubis));
			submit.setItemMeta(submitbtn);
			view.setItem(btn_slot, submit);

			// Validate the price
			if(iMeta.getDisplayName().equalsIgnoreCase("§2Valider le prix")){
				if(!addDataToPaper(player, Integer.toString(nb_rubis), 5)) {
					GUI.error(player, "§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
					return;
				}
				Integer paperslot = getPaperSlot(player.getInventory());
				ItemStack paper = player.getInventory().getItem(paperslot);
				player.openInventory(GUI.createSellStep5(paper.getItemMeta()));
				return;
			}
		}

		// STEP 5
		if(title.equalsIgnoreCase(GUI.SELL_STEP5_NAME)) {
			event.setCancelled(true);
			if(!isCorrectItem(item, slot, GUI.SELL_STEP1_SIZE)) return;
			ItemMeta iMeta = item.getItemMeta();
			if(!iMeta.hasDisplayName() || !item.getType().equals(CustomItems.SELL_PAPER.getType())) return;

			List<String> lore = getDataFromPaper(player);
			try{
				sell(player, lore);
			} catch (Exception e){
				player.sendMessage(e.getMessage());
			}
			try{
				player.closeInventory();
			} catch (Exception ignored) {}
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
	private boolean addDataToPaper(Player player, String data, int next_step) {
		Integer paperslot = getPaperSlot(player.getInventory());
		if(paperslot == null) return false;
		ItemStack paper = player.getInventory().getItem(paperslot);

		ItemMeta paperMeta = paper.getItemMeta();
		List<String> lore = paperMeta.getLore();
		if (lore.equals(CustomItems.SELL_PAPER.getLore())) lore.clear();
		lore.add(data);
		paperMeta.setLore(lore);
		paperMeta.setDisplayName("§8étape " + next_step);
		paper.setItemMeta(paperMeta);
		player.getInventory().setItem(paperslot, paper);
		return true;
	}

	/**
	 * Get the paper lore
	 * @param player The player to check
	 * @return List<String> The paper lore
	 */
	private List<String> getDataFromPaper(Player player) {
		Integer paperslot = getPaperSlot(player.getInventory());
		if(paperslot == null) return null;
		ItemStack paper = player.getInventory().getItem(paperslot);
		ItemMeta paperMeta = paper.getItemMeta();
		List<String> lore = paperMeta.getLore();
		return lore;
	}

	private void removePaper(Player player){
		Inventory playerinv = player.getInventory();
		Integer paperslot = getPaperSlot(playerinv);
		if(paperslot == null) return;
		ItemStack paper = playerinv.getItem(paperslot);
		playerinv.remove(paper);
		player.updateInventory();
	}

	private Integer getPaperSlot(Inventory inv){
		// Find paper
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
						paper_slot = i;
					}
				}
			}
		}
		return paper_slot;
	}

	private void sell(Player sender, List<String> infos) throws Exception {
		// Sell a ground
		CustomPlayer customSender = new CustomPlayer(sender, plugin).loadData();
		String sell_type = infos.get(0);
		Player to = null;
		CustomPlayer customTo = null;
		Integer price = null;
		Map<String, Integer> ground = null;

		switch (sell_type) {
			case "admin":
				String type_name = infos.get(1);
				ground = customSender.getAdmin_ground();
				to = plugin.getServer().getPlayer(infos.get(2));
				if (to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin).loadData();
				price = Integer.parseInt(infos.get(3));

				// Set addressees ground
				switch (type_name) {
					case "house":
						customTo.setHouse(ground);
						break;
					case "shop":
						customTo.setShop(ground);
						break;
					case "farm":
						customTo.addFarm(sender.getDisplayName() + UUID.randomUUID().toString().substring(0, 5), ground);
						break;
					case "build":
						customTo.addBuild(sender.getDisplayName() + UUID.randomUUID().toString().substring(0, 5), ground);
						break;
				}
				customSender.setAdminGround(null);
				break;
			case "buy_deco":
				String name = infos.get(1);
				if (name.equalsIgnoreCase("house")) {
					ground = customSender.getHouse();
				} else if (name.equalsIgnoreCase("shop")) {
					ground = customSender.getShop();
				} else if (name.contains("f:")) {
					ground = customSender.getFarms().get(name.substring(2));
				} else if (name.contains("g:")) {
					ground = customSender.getBuilds().get(name.substring(2));
				}
				to = plugin.getServer().getPlayer(infos.get(2));
				if (to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin).loadData();
				price = 0;
				customTo.addBuild("Terrain de " + sender.getDisplayName(), ground);
				break;
			case "house":
				ground = customSender.getHouse();
				to = plugin.getServer().getPlayer(infos.get(1));
				if (to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin).loadData();
				price = Integer.parseInt(infos.get(2));
				customTo.setHouse(ground);
				customSender.setHouse(null);
				break;
			case "shop":
				ground = customSender.getShop();
				to = plugin.getServer().getPlayer(infos.get(1));
				if (to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin).loadData();
				price = Integer.parseInt(infos.get(2));
				customTo.setShop(ground);
				customSender.setShop(null);
				break;
			case "farm":
				ground = customSender.getFarms().get(infos.get(1));
				to = plugin.getServer().getPlayer(infos.get(2));
				if (to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin).loadData();
				price = Integer.parseInt(infos.get(3));
				customTo.addFarm(to.getDisplayName() + UUID.randomUUID().toString().substring(0, 5), ground);
				customSender.removeFarm(infos.get(1));
				break;
			case "build":
				ground = customSender.getBuilds().get(infos.get(1));
				to = plugin.getServer().getPlayer(infos.get(2));
				if (to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin).loadData();
				price = Integer.parseInt(infos.get(3));
				customTo.addBuild(to.getDisplayName() + UUID.randomUUID().toString().substring(0, 5), ground);
				customSender.removeBuild(infos.get(1));
				break;
			case "sell_deco":
				to = plugin.getServer().getPlayer(infos.get(2));
				if (to == null) throw new Exception("§4Le joueur sélectionné n'est plus en ligne.");
				customTo = new CustomPlayer(to, plugin);
				customSender.removeBuild(infos.get(1));
				price = Integer.parseInt(infos.get(3));
				break;
		}

		// Transaction
		Wallet senderWallet = customSender.getWallet();
		Wallet toWallet = customTo.getWallet();
		if (!to.isOp()) {
			if (!toWallet.has(price)) throw new Exception("§4Le destinataire n'a pas assez de rubis");
			toWallet.remove(price);
		}
		if (!sender.isOp()) senderWallet.give(price);

		// Remove paper
		removePaper(sender);
		System.out.println(customTo.getBuilds());
		customTo.save();
		customSender.save();
	}

}
