package fr.martdel.rolecraft;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Money {

	public static final String RUBYNAME = "Rubis";
	public static final Material RUBYTYPE = Material.EMERALD;
	
	public static final String SAPHIRNAME = "Saphir";
	public static final Material SAPHIRTYPE = Material.PRISMARINE_SHARD;
	
	private Player player;
	private Inventory inv;
		
	public Money(Player player) {
		this.player = player;
		this.inv = player.getInventory();
	}
	
	/**
	 * Count how many ruby the player gets in his inventory
	 * @return Number of ruby
	 */
	public int count() {
		int nb = 0;
		for(ItemStack stack : inv.getStorageContents()) {
			if(stack != null) {
				ItemMeta meta = stack.getItemMeta();
				if(meta.equals(getRubyMeta())) {
					nb += stack.getAmount();
				}
			}
		}
		return nb;
	}
	
	/**
	 * Give ruby to the player
	 * @param nb
	 */
	public void give(int nb) {		
		ItemStack ruby = new ItemStack(Material.EMERALD, nb);
		ruby.setItemMeta(getRubyMeta());
		player.getInventory().addItem(ruby);
		player.updateInventory();
	}
	
	/**
	 * Remove ruby from the player's inventory
	 * @param nb
	 */
	public void remove(int nb) {
		for(ItemStack stack : inv.getStorageContents()) {
			if(stack != null && nb != 0) {
				ItemMeta meta = stack.getItemMeta();
				if(meta.equals(getRubyMeta())) {
					if(stack.getAmount() < nb) {
						nb -= stack.getAmount();
						stack.setAmount(0);
					} else {
						stack.setAmount(stack.getAmount() - nb);
						nb = 0;
					}
				}
			}
		}
	}
	
	/**
	 * Return true if the player has more ruby on his inventory than the @param
	 * @param value Number of ruby
	 * @return boolean
	 */
	public boolean has(int value) {
		return count() >= value;
	}
	
	/**
	 * Get the ruby item meta
	 * @return ItemMeta
	 */
	public static ItemMeta getRubyMeta() {
		ItemStack ruby = new ItemStack(RUBYTYPE);
		ItemMeta rubymeta = ruby.getItemMeta();
		rubymeta.setDisplayName(RUBYNAME);
		rubymeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 10, true);
		return rubymeta;
	}
	
	/**
	 * Get the ruby item meta
	 * @return ItemMeta
	 */
	public static ItemMeta getSaphirMeta() {
		ItemStack saphir = new ItemStack(SAPHIRTYPE);
		ItemMeta saphirmeta = saphir.getItemMeta();
		saphirmeta.setDisplayName(SAPHIRNAME);
		saphirmeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 10, true);
		return saphirmeta;
	}

}
