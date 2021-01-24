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
					switch(data) {
					case 0:
						player.closeInventory();
						if(!player.isOp()) {
							player.sendMessage("§4Vous devez être admin pour accéder à ce type de vente.");
							return;
						}
						addDataToPaper(player, "admin");
						player.openInventory(GUI.createSellStep3());
						return;
					case 1:
						player.closeInventory();
						addDataToPaper(player, "buy_deco");
						customPlayer.loadData();
						if(customPlayer.getHouse() == null && customPlayer.getShop() == null){
							if((customPlayer.getJob() == 0 && customPlayer.getFarms().size() == 0) || (customPlayer.getJob() == 3 && customPlayer.getBuilds().size() == 0)){
								player.sendMessage("§4Vous n'avez actuellement aucun terrain pour cette vente.");
								return;
							}
						}
						player.openInventory(GUI.createSellStep2(customPlayer, item.getType(), "all"));
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
					}
				}
			}
		}
		if(paper == null) return false;

		ItemMeta paperMeta = paper.getItemMeta();
		List<String> lore = paperMeta.getLore();
		if(lore.equals(template_paper.getLore())) lore.clear();
		lore.add(data);
		return true;
	}

}
