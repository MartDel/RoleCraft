package fr.martdel.rolecraft;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class GUI {
	
	public static final String SELL_STEP1_NAME = RoleCraft.config.getString("sell.step1.name");
	public static final String SELL_STEP2_NAME = RoleCraft.config.getString("sell.step2.name");
	public static final String SELL_STEP2_ADMINNAME = RoleCraft.config.getString("sell.step2.admin_name");
	public static final String SELL_STEP3_NAME = RoleCraft.config.getString("sell.step3.name");
	public static final String SELL_STEP4_NAME = RoleCraft.config.getString("sell.step4.name");
	public static final String SELL_STEP5_NAME = RoleCraft.config.getString("sell.step5.name");

	public static final int SELL_STEP1_SIZE = RoleCraft.config.getInt("sell.step1.size");
	public static final int SELL_STEP2_SIZE = RoleCraft.config.getInt("sell.step2.size");
	public static final int SELL_STEP2_ADMINSIZE = RoleCraft.config.getInt("sell.step2.admin_size");
	public static final int SELL_STEP3_SIZE = RoleCraft.config.getInt("sell.step3.size");
	public static final int SELL_STEP4_SIZE = RoleCraft.config.getInt("sell.step4.size");
	public static final int SELL_STEP5_SIZE = RoleCraft.config.getInt("sell.step5.size");
	
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
		return size > 54 ? 54 : size;
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

	/****************************
	 		  SELL STEPS
	****************************/
	
	/**
	 * Create the GUI for the step 1 of sell command
	 * @return The inventory to show
	 */
	public static Inventory createSellStep1() {
		GUI step1 = new GUI(SELL_STEP1_NAME, SELL_STEP1_SIZE);
		step1.setRule("fill", "yes");
		step1.setRule("fill_type", "BLACK_STAINED_GLASS_PANE");
		
		ItemStack[] items = {
			createItem(Material.OAK_SIGN, "§aVendre un terrain §9(Admin)", Arrays.asList("Vendre un terrain de n'importe", "quel type à un joueur."), 0),
			createItem(Material.POPPY, "§aDemander une décoration", Arrays.asList("Donner accès à son terrain", "pour qu'un builder", "le décore."), 1),
			createItem(Material.OAK_DOOR, "§aVendre sa maison", Arrays.asList("Vendre sa maison", "à un admin", "ou à un joueur."), 2),
			createItem(Material.CHEST, "§aVendre son magasin", Arrays.asList("Vendre son magasin", "à un admin", "ou à un joueur."), 3),
			createItem(Material.HAY_BLOCK, "§aVendre son champ §2(Fermier)", Arrays.asList("Vendre son champ", "à un admin", "ou à un joueur."), 4),
			createItem(Material.WHITE_GLAZED_TERRACOTTA, "§aVendre une construction §5(Builder)", Arrays.asList("Vendre une construction", "à un admin"), 5),
			createItem(Material.PEONY, "§aVendre une décoration §5(Builder)", Arrays.asList("Vendre un terrain décoré", "à un autre joueur."), 6)
		};
		int[] stacks = {19, 21, 23, 25, 29, 31, 33};
		for (int i = 0; i < stacks.length; i++) {
			step1.setItem(stacks[i], items[i]);
		}
		
		return step1.getInventory();
	}

	/**
	 * Create the GUI for the step 2 of sell command
	 * @return The inventory to show
	 */
	public static Inventory createSellStep2(CustomPlayer player, Material itemtype, String type) {
		GUI step2 = new GUI(SELL_STEP2_NAME, SELL_STEP2_SIZE);

		ItemStack item = new ItemStack(itemtype);
		ItemMeta itemmeta = item.getItemMeta();

		// Get grounds
		Map<String, Map<String, Integer>> grounds = new HashMap<>();
		switch (type){
			case "farm": grounds = player.getFarms(); break;
			case "build": grounds = player.getBuilds(); break;
			default:
				if(player.getJob() == 0) grounds = player.getFarms();
				else if(player.getJob() == 3) grounds = player.getBuilds();
				grounds.put("Maison", player.getHouse());
				grounds.put("Magasin", player.getShop());
				break;
		}

		// Show grounds
		Set<String> entries = grounds.keySet();
		for (String key : entries) {
			Map<String, Integer> ground = grounds.get(key);

			// Get color
			String color;
			switch (key){
				case "Maison": color = "§3"; break;
				case "Magasin": color = "§6"; break;
				default:
					if(type.equalsIgnoreCase("farm")) color = "§2";
					else color = "§5";
					break;
			}

			itemmeta.setDisplayName(color + key);
			itemmeta.setLore(Arrays.asList(ground.get("x1") + ";" + ground.get("z1"), ground.get("x2") + ";" + ground.get("z2")));
			item.setItemMeta(itemmeta);
			step2.addItem(item);
		}

		return step2.getInventory();
	}

	/**
	 * Create the GUI for the step 2 (admin version) of sell command
	 * @return The inventory to show
	 */
	public static Inventory createSellStep2Admin() {
		GUI step2 = new GUI(SELL_STEP2_ADMINNAME, SELL_STEP2_ADMINSIZE);

		ItemStack[] items = {
				createItem(Material.OAK_DOOR, "§aMaison", new ArrayList<>(), 1),
				createItem(Material.CHEST, "§aMagasin", new ArrayList<>(), 2),
				createItem(Material.HAY_BLOCK, "§aChamp", new ArrayList<>(), 3),
				createItem(Material.WHITE_GLAZED_TERRACOTTA, "§aTerrain de construction", new ArrayList<>(), 4),
		};
		int[] stacks = {1, 3, 5, 7};
		for (int i = 0; i < stacks.length; i++) {
			step2.setItem(stacks[i], items[i]);
		}

		return step2.getInventory();
	}

	/**
	 * Create the GUI for the step 3 of sell command
	 * @param plugin The current plugin
	 * @return The inventory to show
	 */
	public static Inventory createSellStep3(RoleCraft plugin, Player sender) {
		GUI step3 = new GUI(SELL_STEP3_NAME, SELL_STEP3_SIZE);

		for(Player p: plugin.getServer().getOnlinePlayers()){
			if(!p.equals(sender)){
				CustomPlayer customP = new CustomPlayer(p, plugin);
				String color = customP.getTeam().getColor();
				// Create player's head (skull)
				ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) skull.getItemMeta();
				meta.setOwningPlayer(p);
				meta.setDisplayName("§" + color + p.getName());
				skull.setItemMeta(meta);
				step3.addItem(skull);
			}
		}

		return step3.getInventory();
	}

	/**
	 * Create the GUI for the step 3 (farmer version) of sell command
	 * @param plugin The current plugin
	 * @param searched_job The job which we are looking for
	 * @return The inventory to show
	 */
	public static Inventory createSellStep3Job(RoleCraft plugin, int searched_job) throws Exception {
		GUI step3 = new GUI(SELL_STEP3_NAME, SELL_STEP3_SIZE);

		int founds = 0;
		for(Player p: plugin.getServer().getOnlinePlayers()){
			CustomPlayer customP = new CustomPlayer(p, plugin).loadData();
			if(customP.getJob() == searched_job || customP.isAdmin()){
				String color = customP.getTeam().getColor();
				// Create player's head (skull)
				ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) skull.getItemMeta();
				meta.setOwningPlayer(p);
				meta.setDisplayName("§" + color + p.getName());
				skull.setItemMeta(meta);
				step3.addItem(skull);
				founds++;
			}
		}
		if(founds == 0) throw new Exception();

		return step3.getInventory();
	}

	/**
	 * Create the GUI for the step 4 of sell command
	 * @return The inventory to show
	 */
	public static Inventory createSellStep4() {
		GUI step4 = new GUI(SELL_STEP4_NAME, SELL_STEP4_SIZE);

		ItemStack rubis = new ItemStack(CustomItems.RUBIS.getType(), 30);
		rubis.setItemMeta(CustomItems.RUBIS.getItemMeta());
		step4.setItem(0, rubis);

		ItemStack[] items = {
				createItem(Material.REDSTONE, 64, "§c-64", new ArrayList<>(), 64),
				createItem(Material.REDSTONE, 10, "§c-10", new ArrayList<>(), 10),
				createItem(Material.REDSTONE, 1, "§c-1", new ArrayList<>(), 1),
				createItem(Material.LIME_STAINED_GLASS_PANE, "§2Valider le prix", new ArrayList<>(), 2),
				createItem(Material.EMERALD, 1, "§a+1", new ArrayList<>(), 1),
				createItem(Material.EMERALD, 10, "§a+10", new ArrayList<>(), 10),
				createItem(Material.EMERALD, 64, "§a+64", new ArrayList<>(), 64),
		};
		int[] stacks = {37,38,39,40,41,42,43};
		for (int i = 0; i < stacks.length; i++) {
			step4.setItem(stacks[i], items[i]);
		}

		return step4.getInventory();
	}

	/**
	 * Create the GUI for the step 5 of sell command
	 * @param papermeta The paper meta
	 * @return The inventory to show
	 */
	public static Inventory createSellStep5(ItemMeta papermeta) {
		GUI step5 = new GUI(SELL_STEP5_NAME, SELL_STEP5_SIZE);
		step5.setRule("fill", "yes");
		step5.setRule("fill_type", "BLACK_STAINED_GLASS_PANE");

		List<String> lore = papermeta.getLore();
		List<String> result = new ArrayList<>();
		int i = 0;

		switch (lore.get(i)){
			case "admin":
				result.add("Vous vendez votre terrain admin");
				i++;
				result.add("Type du terrain : " + lore.get(i));
				break;
			case "buy_deco":
				result.add("Vous donnez l'accés à votre terrain pour une déco");
				i++;
				result.add("Nom du terrain :" + lore.get(i));
				break;
			case "house": result.add("Vous vendez votre maison"); break;
			case "shop": result.add("Vous vendez votre magasin"); break;
			case "farm": result.add("Vous vendez votre ferme");
				i++;
				result.add("Nom du terrain :" + lore.get(i));
				break;
			case "build":
				result.add("Vous vendez votre terrain de construction");
				i++;
				result.add("Nom du terrain :" + lore.get(i));
				break;
			case "sell_deco":
				result.add("Vous vendez une décoration");
				i++;
				result.add("Nom du terrain :" + lore.get(i));
				break;
		}
		i++;

		result.add("Vous vendez à " + lore.get(i)); i++;
		result.add("au prix de " + lore.get(i) + " rubis.");

		ItemStack paper = new ItemStack(CustomItems.SELL_PAPER.getType());
		papermeta.setDisplayName("§2Valider la vente");
		papermeta.setLore(result);
		paper.setItemMeta(papermeta);
		step5.setItem(13, paper);

		return step5.getInventory();
	}
	
	/**
	 * Quick creating of an ItemStack
	 * @param mat The item type
	 * @param name The item name
	 * @param lore The item description
	 * @param data The item metadata
	 * @return ItemStack The created item
	 */
	public static ItemStack createItem(Material mat, int amount, String name, List<String> lore, int data) {
		ItemStack item = new ItemStack(mat, amount);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(lore);
		itemMeta.setCustomModelData(data);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemMeta);
		return item;
	}
	public static ItemStack createItem(Material mat, String name, List<String> lore, int data) {
		return createItem(mat, 1, name, lore, data);
	}

}
