package fr.martdel.rolecraft;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUI {
	
	private String name;
	private int size;
	private Inventory inventory;
	private Map<String, String> rules;

	public GUI(String name, int size) {
		this.name = name;
		this.size = roundSize(size);
		this.inventory = Bukkit.createInventory(null, this.size, name);
		
		this.rules = new HashMap<>();
		rules.put("fill", "no");
		rules.put("fill_type", "BARRIER");
	}
	
	public String getName() {
		return name;
	}
	
	public int getSize() {
		return size;
	}
	
	public String getRule(String name) {
		return rules.get(name);
	}
	
	public void setRule(String name, String value) {
		rules.replace(name, value);
	}
	
	public void addItem(ItemStack item) {
		inventory.addItem(item);
	}
	
	public void setItem(int i, ItemStack item) {
		inventory.setItem(i, item);
	}

	public Inventory getInventory() {
		if(rules.get("fill").equalsIgnoreCase("yes")) {
			fillInventory(Material.getMaterial(rules.get("fill_type")));
		}
		return inventory;
	}
	
	/**
	 * Fill empty stacks with a given item
	 * @param type The item type
	 */
	private void fillInventory(Material type) {
		int size = inventory.getSize();
		for (int i = 0; i < size; i++) {
			ItemStack stack = inventory.getItem(i);
			if(stack == null) inventory.setItem(i, new ItemStack(type));
		}
	}
	
	private int roundSize(int size) {
		if(size % 9 == 0) return size;
		size += 9 - (size % 9);
		return size;
	}
	
	/**
	 * Create the GUI for the job selector (compass)
	 * @return Inventory The GUI to show
	 */
	public static Inventory createCompassGUI() {
		Inventory inv = Bukkit.createInventory(null, 27, "§9Choisir son métier");
		
		ItemStack farmer = createItem(Material.IRON_HOE, "§2Fermier", Arrays.asList("§3Cultive la terre,", "§3coupe les arbres", "§3pour les vendres.", "", "§fSpécialisation : §aEleveur", "§7Elève des animaux,", "§7pêche pour", "§7plus de ressources."), 0);
		inv.setItem(3, farmer);
		
		ItemStack explorer = createItem(Material.FILLED_MAP, "§6Aventurier", Arrays.asList("§3Explore le monde,", "§3prend tout ce qu'il trouve", "§3et le revend.", "", "§fSpécialisation : §eGarde", "§7Surveille le village,", "§7protège des mobs", "§7et règle des comptes."), 2);
		inv.setItem(6, explorer);
		
		ItemStack builder = createItem(Material.GREEN_GLAZED_TERRACOTTA, "§5Builder", Arrays.asList("§3Construit des maisons,", "§3décore les intérieurs", "§3en échange de ressources.", "", "§fSpécialisation : §dIngénieur", "§7Débloque la redstone", "§7pour créer des systèmes", "§7plus évolués."), 3);
		inv.setItem(20, builder);
		
		ItemStack miner = createItem(Material.IRON_PICKAXE, "§4Mineur", Arrays.asList("§3Mine sous terre,", "§3recolte les ressources", "§3pour les vendres.", "", "§fSpécialisation : §cArmurier", "§7Débloque les armures,", "§7les armes, les potions", "§7et les enchantements."), 1);
		inv.setItem(23, miner);
		
		return inv;
	}
	
	/**
	 * Quick creating of an ItemStack
	 * @param mat The item type
	 * @param name The item name
	 * @param let The item description
	 * @param data The item metadata
	 * @return ItemStack The created item
	 */
	public static ItemStack createItem(Material mat, String name, List<String> let, int data) {
		ItemStack item = new ItemStack(mat);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(let);
		itemMeta.setCustomModelData(data);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemMeta);
		return item;
	}

}
