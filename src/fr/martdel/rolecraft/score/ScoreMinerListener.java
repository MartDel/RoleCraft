package fr.martdel.rolecraft.score;

import java.util.HashMap;
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

import fr.martdel.serverrp.ServerRP;

public class ScoreMinerListener implements Listener {
	
	private ServerRP main;
	
	private Map<Material, Integer> cook;
	private Map<Material, Integer> broke;
	private Map<Material, Integer> craft;

	private Map<Material, Integer> spe_cook;
	private Map<Material, Integer> spe_broke;
	private Map<Material, Integer> spe_craft;

	public ScoreMinerListener(ServerRP serverRP) {
		main = serverRP;
		
		// Cook XP
		cook = new HashMap<>();
		cook.put(Material.GOLD_INGOT, 3);

		// Spe cook XP
		spe_cook = new HashMap<>();
		spe_cook.put(Material.GOLD_INGOT, 2);
		
		// Break XP
		broke = new HashMap<>();
		broke.put(Material.LAPIS_ORE, 2);
		broke.put(Material.DIAMOND_ORE, 5);
		broke.put(Material.EMERALD_ORE, 7);

		// Spe break XP
		spe_broke = new HashMap<>();
		spe_broke.put(Material.REDSTONE_ORE, 1);
		spe_broke.put(Material.LAPIS_ORE, 2);
		spe_broke.put(Material.DIAMOND_ORE, 4);
		spe_broke.put(Material.EMERALD_ORE, 6);
		
		// Craft XP
		craft = new HashMap<>();
		craft.put(Material.IRON_PICKAXE, 8);
		craft.put(Material.DIAMOND_PICKAXE, 20);
		craft.put(Material.IRON_SHOVEL, 4);
		
		// Spe craft XP
		spe_craft = new HashMap<>();
		spe_craft.put(Material.DIAMOND_PICKAXE, 12);
		spe_craft.put(Material.DIAMOND_SHOVEL, 6);
		spe_craft.put(Material.IRON_SWORD, 1);
		spe_craft.put(Material.DIAMOND_SWORD, 8);
		spe_craft.put(Material.IRON_HELMET, 3);
		spe_craft.put(Material.IRON_CHESTPLATE, 5);
		spe_craft.put(Material.IRON_LEGGINGS, 4);
		spe_craft.put(Material.IRON_BOOTS, 2);
		spe_craft.put(Material.DIAMOND_HELMET, 6);
		spe_craft.put(Material.DIAMOND_CHESTPLATE, 10);
		spe_craft.put(Material.DIAMOND_LEGGINGS, 8);
		spe_craft.put(Material.DIAMOND_BOOTS, 5);
		spe_craft.put(Material.TURTLE_HELMET, 10);
		spe_craft.put(Material.BOW, 2);
		spe_craft.put(Material.CROSSBOW, 3);
		spe_craft.put(Material.SHIELD, 3);
	}
	
	@EventHandler
	public void onCook(FurnaceExtractEvent event) {
		Player player = event.getPlayer();
		int score = main.getScore().getScore(player);
		Material itemtype = event.getItemType();
		int nb = event.getItemAmount();
		if(main.getJobs().getScore(player) != 1) return;
		
		/*
		 * A GUNSMITH COOK SOMETHING
		 */
		if(main.getSpe().getScore(player) == 1) {
			if(spe_cook.containsKey(itemtype) && !player.isOp()) {
				int add = spe_cook.get(itemtype) * nb;
				main.getScore().setScore(player, score + add);				
			}
			return;
		}
		/*
		 * A MINER COOK SOMETHING
		 */
		if(cook.containsKey(itemtype) && !player.isOp()) {
			int add = cook.get(itemtype) * nb;
			main.getScore().setScore(player, score + add);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		int score = main.getScore().getScore(player);
		Material itemtype = event.getBlock().getType();
		if(main.getJobs().getScore(player) != 1) return;
		/*
		 * A GUNSMITH BREAK A BLOCK
		 */
		if(main.getSpe().getScore(player) == 1) {
			if(spe_broke.containsKey(itemtype) && !player.isOp()) {
				int add = spe_broke.get(itemtype);
				main.getScore().setScore(player, score + add);
			}
			return;
		}
		/*
		 * A MINER BREAK A BLOCK
		 */
		if(broke.containsKey(itemtype) && !player.isOp()) {
			int add = broke.get(itemtype);
			main.getScore().setScore(player, score + add);
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		Player player = (Player) event.getWhoClicked();
		int score = main.getScore().getScore(player);
		ItemStack item = event.getCurrentItem();
		Material itemtype = item.getType();
		int nb = item.getAmount();
		int nbFillStack = nbFillStack(event.getInventory());
		ClickType click = event.getClick();
		
		int nb_crafted = 0;
		int add = 0;
		
		if(main.getJobs().getScore(player) != 1) return;
		/*
		 * A MINER CRAFT AN ITEM
		 */
		if((main.getSpe().getScore(player) == 0 && !player.isOp() && craft.containsKey(itemtype)) || (main.getSpe().getScore(player) == 1 && !player.isOp() && spe_craft.containsKey(itemtype))) {
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
				if(main.getSpe().getScore(player) == 0) {
					add = craft.get(itemtype) * nb_crafted;
				} else {
					add = spe_craft.get(itemtype) * nb_crafted;
				}
			}
			
			if(click.equals(ClickType.LEFT) || click.equals(ClickType.RIGHT)) {
				nb_crafted = nb;
				if(main.getSpe().getScore(player) == 0) {
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
			
			main.getScore().setScore(player, score + add);
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
