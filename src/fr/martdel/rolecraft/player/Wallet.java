package fr.martdel.rolecraft.player;

import fr.martdel.rolecraft.CustomItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Wallet {
	
	private final Player player;
	private final Inventory inv;
		
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
				assert meta != null;
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
				assert meta != null;
				if(meta.equals(CustomItems.RUBIS.getItemMeta())) {
					nb += stack.getAmount();
				}
			}
		}
		return nb;
	}
	
	/**
	 * Give ruby to the player
	 * @param nb Number of ruby to give to the player
	 */
	public void give(int nb) throws Exception {
		ItemStack ruby = new ItemStack(CustomItems.RUBIS.getType());
		ruby.setItemMeta(CustomItems.RUBIS.getItemMeta());

		for(int i = 0; i < nb; i++){
			if(!canGet(inv)) {
				remove(i);
				throw new Exception("§cVous n'avez pas assez de place pour recevoir " + nb + " rubis!");
			}
			inv.addItem(ruby);
		}

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
				assert meta != null;
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
		player.updateInventory();
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
				assert meta != null;
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

	public static boolean canGet(Inventory inv){
		for (ItemStack slot: inv.getStorageContents()) {
			if(slot == null) return true;
			if(slot.getType().equals(CustomItems.RUBIS.getType()) && slot.hasItemMeta()){
				ItemMeta slotMeta = slot.getItemMeta();
				assert slotMeta != null;
				if(slotMeta.getDisplayName().equalsIgnoreCase(CustomItems.RUBIS.getName()) && slot.getAmount() < slot.getMaxStackSize()) return true;
			}
		}
		return false;
	}

}
