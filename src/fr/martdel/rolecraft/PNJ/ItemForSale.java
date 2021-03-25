package fr.martdel.rolecraft.PNJ;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemForSale {

	private Material type;
	private int amount;
	private int price;
	private boolean toSell;
	
	public ItemForSale(Material type, int amount, int price) {
		this.type = type;
		this.amount = amount;
		this.price = price;
		this.toSell = true;
	}
	
	public ItemStack getItemStack() {
		ItemStack tradeItem = new ItemStack(type, amount);
		ItemMeta itemMeta = tradeItem.getItemMeta();
		assert itemMeta != null;
		itemMeta.setLore(Arrays.asList("§fPrix " + (toSell ? "de vente " : "") + ":", "§a" + price + " rubis."));
		itemMeta.setCustomModelData(price);
		tradeItem.setItemMeta(itemMeta);
		return tradeItem;
	}
	
	public Material getType() {
		return type;
	}
	public void setType(Material type) {
		this.type = type;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public boolean isToSell() {
		return toSell;
	}
	public void setToSell(boolean toSell) {
		this.toSell = toSell;
	}
	
}
