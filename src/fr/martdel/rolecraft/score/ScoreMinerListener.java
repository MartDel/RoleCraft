package fr.martdel.rolecraft.score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.martdel.rolecraft.player.CustomPlayer;
import fr.martdel.rolecraft.RoleCraft;

public class ScoreMinerListener implements Listener {
	
	private final RoleCraft plugin;
	
	private final Map<Material, Integer> cook = RoleCraft.getMaterialMap("score.miner.cook");
	private final Map<Material, Integer> broke = RoleCraft.getMaterialMap("score.miner.break");
	private final Map<Material, Integer> craft = RoleCraft.getMaterialMap("score.miner.craft");

	private final Map<Material, Integer> spe_cook = RoleCraft.getMaterialMap("score.miner.spe_cook");
	private final Map<Material, Integer> spe_broke = RoleCraft.getMaterialMap("score.miner.spe_break");
	private final Map<Material, Integer> spe_craft = RoleCraft.getMaterialMap("score.miner.spe_craft");

	public ScoreMinerListener(RoleCraft rolecraft) { this.plugin = rolecraft; }
	
	@EventHandler
	public void onCook(FurnaceExtractEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		int score = customPlayer.getScore();
		Material itemtype = event.getItemType();
		int nb = event.getItemAmount();
		if(customPlayer.getJob() != 1) return;
		
		/*
		 * A GUNSMITH COOK SOMETHING
		 */
		if(customPlayer.hasSpe()) {
			if(spe_cook.containsKey(itemtype) && !player.isOp()) {
				int add = spe_cook.get(itemtype) * nb;
				customPlayer.setScore(score + add);				
			}
			return;
		}
		/*
		 * A MINER COOK SOMETHING
		 */
		if(cook.containsKey(itemtype) && !player.isOp()) {
			int add = cook.get(itemtype) * nb;
			customPlayer.setScore(score + add);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		int score = customPlayer.getScore();
		Material itemtype = event.getBlock().getType();
		if(customPlayer.getJob() != 1) return;
		/*
		 * A GUNSMITH BREAK A BLOCK
		 */
		if(customPlayer.hasSpe()) {
			if(spe_broke.containsKey(itemtype) && !player.isOp()) {
				int add = spe_broke.get(itemtype);
				customPlayer.setScore(score + add);
			}
			return;
		}
		/*
		 * A MINER BREAK A BLOCK
		 */
		if(broke.containsKey(itemtype) && !player.isOp()) {
			int add = broke.get(itemtype);
			customPlayer.setScore(score + add);
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		Player player = (Player) event.getWhoClicked();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		int score = customPlayer.getScore();
		ItemStack item = event.getCurrentItem();
		assert item != null;
		Material itemtype = item.getType();
		int nb = item.getAmount();
		int nbFillStack = nbFillStack(event.getInventory());
		ClickType click = event.getClick();
		
		int nb_crafted = 0;
		int add = 0;
		
		if(customPlayer.getJob() != 1) return;
		/*
		 * A MINER CRAFT AN ITEM
		 */
		if((!customPlayer.hasSpe() && !player.isOp() && craft.containsKey(itemtype)) || (customPlayer.hasSpe() && !player.isOp() && spe_craft.containsKey(itemtype))) {
			// Control item quantity to count how many items are crafting
			event.setCancelled(true);
			
			if(click.equals(ClickType.SHIFT_LEFT) || click.equals(ClickType.SHIFT_RIGHT)) {
				while(nbFillStack(event.getInventory()) == nbFillStack) {
					nb_crafted += nb;
					player.getInventory().addItem(new ItemStack(itemtype, nb));
					int it = 0;
					for(int i = 0; i < event.getInventory().getStorageContents().length; i++) {
						ItemStack craftingItem = event.getInventory().getStorageContents()[i];
						if(craftingItem != null && !craftingItem.getType().equals(Material.AIR) && it < 9) {
							craftingItem.setAmount(craftingItem.getAmount() - 1);
							event.getInventory().setItem(i, craftingItem);
						}
						it++;
					}
				}
				if(!customPlayer.hasSpe()) {
					add = craft.get(itemtype) * nb_crafted;
				} else {
					add = spe_craft.get(itemtype) * nb_crafted;
				}
			}
			
			if(click.equals(ClickType.LEFT) || click.equals(ClickType.RIGHT)) {
				if(!customPlayer.hasSpe()) {
					add = craft.get(itemtype) * nb;
				} else {
					add = spe_craft.get(itemtype) * nb;
				}
				player.getInventory().addItem(new ItemStack(itemtype, nb));
				int it = 0;
				for(int i = 0; i < event.getInventory().getStorageContents().length; i++) {
					ItemStack craftingItem = event.getInventory().getStorageContents()[i];
					if(craftingItem != null && !craftingItem.getType().equals(Material.AIR) && it < 9) {
						craftingItem.setAmount(craftingItem.getAmount() - 1);
						event.getInventory().setItem(i, craftingItem);
					}
					it++;
				}
			}
			
			customPlayer.setScore(score + add);
		}
	}
	
	/**
	 * Count how many stacks are filled by an item in an inventory
	 * @param inv Inventory
	 * @return Number of filled stacks
	 */
	public int nbFillStack(Inventory inv) {
		int nb = 0;
		int it = 0;
		for(ItemStack i : inv.getStorageContents()) {
			it++;
			if(i != null && !i.getType().equals(Material.AIR) && it < 9) nb++;
		}
		return nb;
	}

}
