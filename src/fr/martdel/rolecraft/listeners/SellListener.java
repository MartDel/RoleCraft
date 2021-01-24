package fr.martdel.rolecraft.listeners;

import org.bukkit.Material;
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

import fr.martdel.rolecraft.CustomItems;
import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.GUI;
import fr.martdel.rolecraft.RoleCraft;

import java.util.List;

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
		
		if(title.equalsIgnoreCase(GUI.SELL_STEP1_NAME)) {
			try {
				ItemMeta iMeta = item.getItemMeta();
				if(iMeta.hasCustomModelData()) {
					int data = iMeta.getCustomModelData();
					player.closeInventory();
					switch(data) {
					case 0:	// Sell an admin ground
						if(!player.isOp()) {
							player.sendMessage("§4Vous devez être admin pour accéder à ce type de vente.");
							return;
						}
						if(plugin.getServer().getOnlinePlayers().size() == 1){
							player.sendMessage("§4Aucun joueur n'est en ligne.");
							return;
						}
						if(!addDataToPaper(player, "admin")) {
							player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
							return;
						}
						player.openInventory(GUI.createSellStep3(plugin));
						return;
					case 1:	// Delegate his ground to a builder for a decoration
						customPlayer.loadData();
						if(customPlayer.getHouse() == null && customPlayer.getShop() == null){
							if((customPlayer.getJob() == 0 && customPlayer.getFarms().size() == 0) || (customPlayer.getJob() == 3 && customPlayer.getBuilds().size() == 0)){
								player.sendMessage("§4Vous n'avez actuellement aucun terrain pour cette vente.");
								return;
							}
						}
						if(!addDataToPaper(player, "buy_deco")) {
							player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
							return;
						}
						player.openInventory(GUI.createSellStep2(customPlayer, item.getType(), "all"));
						return;
					case 2:	// Sell a house
						customPlayer.loadData();
						if(customPlayer.getHouse() == null){
							player.sendMessage("§4Vous n'avez actuellement pas de maison.");
							return;
						}
						if(plugin.getServer().getOnlinePlayers().size() == 1){
							player.sendMessage("§4Aucun joueur n'est en ligne.");
							return;
						}
						if(!addDataToPaper(player, "house")) {
							player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
							return;
						}
						player.openInventory(GUI.createSellStep3(plugin));
						return;
					case 3:	// Sell a shop
						customPlayer.loadData();
						if(customPlayer.getShop() == null){
							player.sendMessage("§4Vous n'avez actuellement pas de magasin.");
							return;
						}
						if(plugin.getServer().getOnlinePlayers().size() == 1){
							player.sendMessage("§4Aucun joueur n'est en ligne.");
							return;
						}
						if(!addDataToPaper(player, "shop")) {
							player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
							return;
						}
						player.openInventory(GUI.createSellStep3(plugin));
						return;
					case 4:	// Sell a farm
						customPlayer.loadData();
						if(customPlayer.getJob() != 0){
							player.sendMessage("§4Vous devez être fermier pour accéder à cette vente.");
							return;
						}
						if(customPlayer.getFarms().size() == 0){
							player.sendMessage("§4Vous n'avez actuellement aucune ferme.");
							return;
						}
						if(!addDataToPaper(player, "farm")) {
							player.sendMessage("§4Veuillez garder le papier sur vous pendant la configuration de la vente.");
							return;
						}
						player.openInventory(GUI.createSellStep2(customPlayer, item.getType(), "farm"));
						return;
					case 5:
						case 6:
							customPlayer.loadData();
							if(customPlayer.getJob() != 3){
								player.sendMessage("§4Vous devez être builder pour accéder à cette vente.");
								return;
							}
							if(customPlayer.getBuilds().size() == 0){
								player.sendMessage("§4Vous n'avez actuellement aucun terrain de construction.");
								return;
							}
							if(!addDataToPaper(player, "build")) {
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
	}
	
	private boolean addDataToPaper(Player player, String data) {
		// Find paper
		ItemStack paper = null;
		Integer paper_slot = null;
		ItemStack[] content = player.getInventory().getContents();
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
		if(paper == null && paper_slot == null) return false;

		ItemMeta paperMeta = paper.getItemMeta();
		List<String> lore = paperMeta.getLore();
		if(lore.equals(template_paper.getLore())) lore.clear();
		lore.add(data);
		paperMeta.setLore(lore);
		paper.setItemMeta(paperMeta);
		player.getInventory().setItem(paper_slot, paper);
		return true;
	}

}
