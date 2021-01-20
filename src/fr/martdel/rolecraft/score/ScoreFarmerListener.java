package fr.martdel.rolecraft.score;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.martdel.serverrp.ServerRP;

public class ScoreFarmerListener implements Listener {

	private ServerRP main;
	
	private Map<Material, Integer> use;
	private Map<Material, Integer> broke;
	private Map<Material, Integer> craft;
	private Map<Material, Integer> cook;

	private Map<Material, Integer> spe_use;
	private Map<EntityType, Integer> spe_kill;
	private Map<Material, Integer> spe_cook;
	
	public ScoreFarmerListener(ServerRP serverRP) {
		main = serverRP;
		
		// Use XP
		use = new HashMap<>();
		use.put(Material.STONE_HOE, 5);
		use.put(Material.IRON_HOE, 10);
		use.put(Material.DIAMOND_HOE, 15);
		use.put(Material.STONE_AXE, 5);
		use.put(Material.IRON_AXE, 10);
		use.put(Material.DIAMOND_AXE, 15);
		
		// Spe use XP
		spe_use = new HashMap<>();
		spe_use.put(Material.STONE_SWORD, 4);
		spe_use.put(Material.IRON_SWORD, 8);
		spe_use.put(Material.GOLDEN_SWORD, 6);
		spe_use.put(Material.DIAMOND_SWORD, 12);

		// Break XP
		broke = new HashMap<>();
		broke.put(Material.MELON, 2);
		broke.put(Material.BROWN_MUSHROOM, 3);
		broke.put(Material.RED_MUSHROOM, 3);

		// Craft XP
		craft = new HashMap<>();
		craft.put(Material.ACACIA_PLANKS, 1);
		craft.put(Material.BIRCH_PLANKS, 1);
		craft.put(Material.DARK_OAK_PLANKS, 1);
		craft.put(Material.JUNGLE_PLANKS, 1);
		craft.put(Material.OAK_PLANKS, 1);
		craft.put(Material.SPRUCE_PLANKS, 1);
		craft.put(Material.MUSHROOM_STEW, 3);
		craft.put(Material.CAKE, 3);
		craft.put(Material.COOKIE, 3);
		craft.put(Material.PUMPKIN_PIE, 3);
		craft.put(Material.RABBIT_STEW, 3);
		craft.put(Material.BEETROOT_SOUP, 3);

		// Cook XP
		cook = new HashMap<>();
		cook.put(Material.BAKED_POTATO, 1);

		// Spe cook XP
		spe_cook = new HashMap<>();
		spe_cook.put(Material.COOKED_PORKCHOP, 1);
		spe_cook.put(Material.COOKED_COD, 1);
		spe_cook.put(Material.COOKED_SALMON, 1);
		spe_cook.put(Material.COOKED_RABBIT, 1);
		spe_cook.put(Material.COOKED_MUTTON, 1);
		spe_cook.put(Material.COOKED_BEEF, 1);
		spe_cook.put(Material.COOKED_CHICKEN, 1);
		spe_cook.put(Material.BAKED_POTATO, 1);
		
		// Spe kill XP
		spe_kill = new HashMap<>();
		spe_kill.put(EntityType.CHICKEN, 1);
		spe_kill.put(EntityType.COD, 1);
		spe_kill.put(EntityType.COW, 1);
		spe_kill.put(EntityType.DOLPHIN, 3);
		spe_kill.put(EntityType.DONKEY, 2);
		spe_kill.put(EntityType.PANDA, 4);
		spe_kill.put(EntityType.PARROT, 3);
		spe_kill.put(EntityType.FOX, 4);
		spe_kill.put(EntityType.HORSE, 2);
		spe_kill.put(EntityType.LLAMA, 3);
		spe_kill.put(EntityType.MUSHROOM_COW, 3);
		spe_kill.put(EntityType.MULE, 2);
		spe_kill.put(EntityType.PIG, 1);
		spe_kill.put(EntityType.POLAR_BEAR, 5);
		spe_kill.put(EntityType.PUFFERFISH, 3);
		spe_kill.put(EntityType.RABBIT, 2);
		spe_kill.put(EntityType.SALMON, 1);
		spe_kill.put(EntityType.SHEEP, 1);
		spe_kill.put(EntityType.TURTLE, 3);
		spe_kill.put(EntityType.TROPICAL_FISH, 1);
	}
	
	@EventHandler
	public void onUsed(PlayerItemBreakEvent event) {
		Player player = event.getPlayer();
		int score = main.getScore().getScore(player);
		Material itemtype = event.getBrokenItem().getType();
		if(main.getJobs().getScore(player) != 0) return;
		
		/*
		 * A BREEDER BREAK A TOOL
		 */
		if(main.getSpe().getScore(player) == 1) {
			if(spe_use.containsKey(itemtype) && !player.isOp()) {
				main.getScore().setScore(player, score + spe_use.get(itemtype));				
			}
			return;
		}
		
		/*
		 * A FARMER BREAK A TOOL
		 */
		if(use.containsKey(itemtype) && !player.isOp()) {
			main.getScore().setScore(player, score + use.get(itemtype));
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		int score = main.getScore().getScore(player);
		Material blocktype = event.getBlock().getType();
		if(main.getJobs().getScore(player) != 0) return;
		
		/*
		 * A FERMER BREAK A BLOCK
		 */
		if(blocktype.equals(Material.WHEAT)) {
			ItemStack wheat = new ItemStack(Material.WHEAT);
			if(event.getBlock().getDrops().contains(wheat)) {
				main.getScore().setScore(player, score + 1);
			}
			return;
		}
		
		if(blocktype.equals(Material.CARROTS) || blocktype.equals(Material.POTATOES)) {
			if(event.getBlock().getDrops().size() > 1) {
				main.getScore().setScore(player, score + 1);
			}
			return;
		}
		
		if(blocktype.equals(Material.BEETROOTS)) {
			ItemStack root = new ItemStack(Material.BEETROOT);
			if(event.getBlock().getDrops().contains(root)) {
				main.getScore().setScore(player, score + 1);
			}
			return;
		}
		
		if(blocktype.equals(Material.COCOA)) {
			ItemStack beans = new ItemStack(Material.COCOA_BEANS, 3);
			if(event.getBlock().getDrops().contains(beans)) {
				main.getScore().setScore(player, score + 5);
			}
			return;
		}
		
		if(blocktype.equals(Material.NETHER_WART)) {
			ItemStack warts = new ItemStack(Material.NETHER_WART, 2);
			if(event.getBlock().getDrops().contains(warts)) {
				main.getScore().setScore(player, score + 3);
			}
			return;
		}
		
		if(broke.containsKey(blocktype) && !player.isOp()) {
			main.getScore().setScore(player, score + broke.get(blocktype));
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
		
		if(main.getJobs().getScore(player) != 0) return;
		
		/*
		 * A FARMER CRAFT AN ITEM
		 */
		if(!player.isOp() && craft.containsKey(itemtype)) {
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
				add = craft.get(itemtype) * nb_crafted;
			}
			
			if(click.equals(ClickType.LEFT) || click.equals(ClickType.RIGHT)) {
				nb_crafted = nb;
				add = craft.get(itemtype) * nb;
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
	
	@EventHandler
	public void onCook(FurnaceExtractEvent event) {
		Player player = event.getPlayer();
		int score = main.getScore().getScore(player);
		Material itemtype = event.getItemType();
		int nb = event.getItemAmount();
		if(main.getJobs().getScore(player) != 0) return;
		
		/*
		 * A BREEDER COOK AN ITEM
		 */
		if(main.getSpe().getScore(player) == 1) {
			if(!player.isOp() && spe_cook.containsKey(itemtype)) {
				int add = spe_cook.get(itemtype) * nb;
				main.getScore().setScore(player, score + add);
			}
			return;
		}
		
		/*
		 * A FARMER COOK SOMETHING
		 */
		if(cook.containsKey(itemtype) && !player.isOp()) {
			int add = cook.get(itemtype) * nb;
			main.getScore().setScore(player, score + add);
		}
	}
	
	@EventHandler
	public void onKill(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();
		
		if((damager instanceof Player) && (entity instanceof Damageable)) {
			Player player = (Player) damager;
			int score = main.getScore().getScore(player);
			Damageable victim = (Damageable) entity;
			EntityType entitytype = entity.getType();
			if(main.getJobs().getScore(player) != 0) return;
			
			/*
			 * A BREEDER KILL AN ENTITY
			 */
			if(main.getSpe().getScore(player) == 1 && !player.isOp() && spe_kill.containsKey(entitytype) && (victim.getHealth() - event.getDamage()) <= 0) {
				main.getScore().setScore(player, score + spe_kill.get(entitytype));
			}
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
