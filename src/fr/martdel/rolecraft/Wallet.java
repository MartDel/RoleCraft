package fr.martdel.rolecraft;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Wallet {
	
	private Player player;
	private Inventory inv;
		
	public Wallet(Player player) {
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
				if(meta.equals(CustomItems.RUBIS.getItemMeta())) {
					nb += stack.getAmount();
				}
			}
		}
		return nb;
	}

	/**
	 * Count how many ruby the inventory contains
	 * @return Number of ruby
	 */
	public static int count(Inventory inv) {
		int nb = 0;
		for(ItemStack stack : inv.getStorageContents()) {
			if(stack != null) {
				ItemMeta meta = stack.getItemMeta();
				if(meta.equals(CustomItems.RUBIS.getItemMeta())) {
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
		ItemStack ruby = new ItemStack(CustomItems.RUBIS.getType(), nb);
		ruby.setItemMeta(CustomItems.RUBIS.getItemMeta());
		player.getInventory().addItem(ruby);
		player.updateInventory();
	}
	
	/**
	 * Remove Rubis from the player's inventory
	 * @param nb The nb of Rubis to remove
	 */
	public void remove(int nb) {
		for(ItemStack stack : inv.getStorageContents()) {
			if(stack != null && nb != 0) {
				ItemMeta meta = stack.getItemMeta();
				if(meta.equals(CustomItems.RUBIS.getItemMeta())) {
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
	 * Remove Rubis from the given inventory
	 * @param inv The inventory to update
	 * @param nb The nb of Rubis to remove
	 */
	public static void remove(Inventory inv, int nb) {
		for(ItemStack stack : inv.getStorageContents()) {
			if(stack != null && nb != 0) {
				ItemMeta meta = stack.getItemMeta();
				if(meta.equals(CustomItems.RUBIS.getItemMeta())) {
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

}
