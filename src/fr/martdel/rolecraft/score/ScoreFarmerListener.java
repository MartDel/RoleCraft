package fr.martdel.rolecraft.score;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

import fr.martdel.rolecraft.player.CustomPlayer;
import fr.martdel.rolecraft.RoleCraft;

public class ScoreFarmerListener implements Listener {

	private RoleCraft plugin;
	
	private Map<Material, Integer> use;
	private Map<Material, Integer> broke;
	private Map<Material, Integer> craft;
	private Map<Material, Integer> cook;

	private Map<Material, Integer> spe_use;
	private Map<EntityType, Integer> spe_kill;
	private Map<Material, Integer> spe_cook;
	
	public ScoreFarmerListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
		use = getMaterialConfigData("score.farmer.use", "type", "score");
		broke = getMaterialConfigData("score.farmer.broke", "type", "score");		
		craft = getMaterialConfigData("score.farmer.craft", "type", "score");
		cook = getMaterialConfigData("score.farmer.cook", "type", "score");
		spe_use = getMaterialConfigData("score.farmer.spe_use", "type", "score");
		spe_cook = getMaterialConfigData("score.farmer.spe_cook", "type", "score");
		spe_kill = getEntityConfigData("score.farmer.spe_kill", "entity", "score");
	}
	
	@EventHandler
	public void onUsed(PlayerItemBreakEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		int score = customPlayer.getScore();
		Material itemtype = event.getBrokenItem().getType();
		if(customPlayer.getJob() != 0) return;
		/*
		 * A BREEDER BREAK A TOOL
		 */
		if(customPlayer.hasSpe()) {
			if(spe_use.containsKey(itemtype) && !player.isOp()) {
				customPlayer.setScore(score + spe_use.get(itemtype));
			}
			return;
		}
		/*
		 * A FARMER BREAK A TOOL
		 */
		if(use.containsKey(itemtype) && !player.isOp()) {
			customPlayer.setScore(score + use.get(itemtype));
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		int score = customPlayer.getScore();
		Material blocktype = event.getBlock().getType();
		Collection<ItemStack> drops = event.getBlock().getDrops();
		if(customPlayer.getJob() != 0) return;
		
		/*
		 * A FERMER BREAK A BLOCK
		 */
		if(blocktype.equals(Material.WHEAT)) {
			ItemStack wheat = new ItemStack(Material.WHEAT);
			if(drops.contains(wheat)) customPlayer.setScore(score + 1);
			return;
		}
		
		if(blocktype.equals(Material.CARROTS) || blocktype.equals(Material.POTATOES)) {
			if(drops.size() > 1) customPlayer.setScore(score + 1);
			return;
		}
		
		if(blocktype.equals(Material.BEETROOTS)) {
			ItemStack root = new ItemStack(Material.BEETROOT);
			if(drops.contains(root)) customPlayer.setScore(score + 1);
			return;
		}
		
		if(blocktype.equals(Material.COCOA)) {
			ItemStack beans = new ItemStack(Material.COCOA_BEANS, 3);
			if(drops.contains(beans)) customPlayer.setScore(score + 5);
			return;
		}
		
		if(blocktype.equals(Material.NETHER_WART)) {
			ItemStack warts = new ItemStack(Material.NETHER_WART, 2);
			if(drops.contains(warts)) customPlayer.setScore(score + 3);
			return;
		}
		
		if(broke.containsKey(blocktype) && !player.isOp()) {
			customPlayer.setScore(score + broke.get(blocktype));
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		Player player = (Player) event.getWhoClicked();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		int score = customPlayer.getScore();
		ItemStack item = event.getCurrentItem();
		Material itemtype = item.getType();
		int nb = item.getAmount();
		int nbFillStack = nbFillStack(event.getInventory());
		ClickType click = event.getClick();
		
		int nb_crafted = 0;
		int add = 0;

		if(customPlayer.getJob() != 0) return;
		
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
			
			customPlayer.setScore(score + add);
		}
	}
	
	@EventHandler
	public void onCook(FurnaceExtractEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		int score = customPlayer.getScore();
		Material itemtype = event.getItemType();
		int nb = event.getItemAmount();
		if(customPlayer.getJob() != 0) return;
		
		/*
		 * A BREEDER COOK AN ITEM
		 */
		if(customPlayer.hasSpe()) {
			if(!player.isOp() && spe_cook.containsKey(itemtype)) {
				int add = spe_cook.get(itemtype) * nb;
				customPlayer.setScore(score + add);
			}
			return;
		}
		
		/*
		 * A FARMER COOK SOMETHING
		 */
		if(cook.containsKey(itemtype) && !player.isOp()) {
			int add = cook.get(itemtype) * nb;
			customPlayer.setScore(score + add);
		}
	}
	
	@EventHandler
	public void onKill(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();
		
		if((damager instanceof Player) && (entity instanceof Damageable)) {
			Player player = (Player) damager;
			CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
			int score = customPlayer.getScore();
			Damageable victim = (Damageable) entity;
			EntityType entitytype = entity.getType();
			if(customPlayer.getJob() != 0) return;
			
			/*
			 * A BREEDER KILL AN ENTITY
			 */
			if(customPlayer.hasSpe() && !player.isOp() && spe_kill.containsKey(entitytype) && (victim.getHealth() - event.getDamage()) <= 0) {
				customPlayer.setScore(score + spe_kill.get(entitytype));
			}
		}
	}
	
	/**
	 * Count how many stacks are filled by an item in an inventory
	 * @param inv Inventory
	 * @return Number of filled stacks
	 */
	private int nbFillStack(Inventory inv) {
		int nb = 0;
		int it = 0;
		for(ItemStack i : inv.getStorageContents()) {
			it++;
			if(i != null && !i.getType().equals(Material.AIR) && it < 9) nb++;
		}
		return nb;
	}
	
	private Map<String, Integer> getConfigData(String path, String key_name, String value_name){
		Map<String, Integer> result = new HashMap<>();
		List<Map<?, ?>> config_list = RoleCraft.config.getMapList(path);
		for (Map<?, ?> el : config_list) {
			@SuppressWarnings("unchecked")
			Map<String, ?> current_config = (Map<String, ?>) el;
			String key = (String) current_config.get(key_name);
			Integer value = Integer.getInteger((String) current_config.get(value_name));
			result.put(key, value);
		}
		return result;
	}
	private Map<Material, Integer> getMaterialConfigData(String path, String key_name, String value_name){
		Map<Material, Integer> result = new HashMap<>();
		Map<String, Integer> start_map = getConfigData(path, key_name, value_name);
		for (int i = 0; i < start_map.size(); i++) {
			String key_start = (String) start_map.keySet().toArray()[i];
			result.put(Material.getMaterial(key_start), start_map.get(key_start));
		}
		return result;
	}
	private Map<EntityType, Integer> getEntityConfigData(String path, String key_name, String value_name){
		Map<EntityType, Integer> result = new HashMap<>();
		Map<String, Integer> start_map = getConfigData(path, key_name, value_name);
		for (int i = 0; i < start_map.size(); i++) {
			String key_start = (String) start_map.keySet().toArray()[i];
			result.put(EntityType.valueOf(key_start), start_map.get(key_start));
		}
		return result;
	}

}
