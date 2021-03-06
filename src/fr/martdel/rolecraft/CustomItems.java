package fr.martdel.rolecraft;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum CustomItems {
	
	// Public items
	LETTERBOX("letterbox"),
	RUBIS("rubis"),
	SAPHIR("saphir"),
	COMPASS("compass"),
	SELL_PAPER("sell_paper"),
	DEATHKEYA1("deathkeyA1"),
	DEATHKEYA2("deathkeyA2"),
	DEATHKEYB1("deathkeyB1"),
	DEATHKEYB2("deathkeyB2"),
	DEATHKEYC1("deathkeyC1"),
	
	// Private items
	DELIMITER("delimiter");

	private final String config_name;

	CustomItems(String config_name) {
		this.config_name = "custom_items." + config_name;
	}

	public Material getType() { return RoleCraft.getConfigMaterial(config_name + ".type"); }

	public String getName() {
		return RoleCraft.config.getString(config_name + ".name");
	}

	public List<String> getLore() {
		return RoleCraft.config.getStringList(config_name + ".lore");
	}

	public int getData() {
		return RoleCraft.config.getInt(config_name + ".metadata");
	}
	
	public Map<Enchantment, Integer> getEnchants(){
		Map<Enchantment, Integer> r = new HashMap<>();
		List<Map<?, ?>> enchants_list = RoleCraft.config.getMapList(config_name + ".enchantments");
		for(Map<?, ?> el : enchants_list) {
			@SuppressWarnings("unchecked")
			Map<String, ?> enchant = (Map<String, ?>) el;
			@SuppressWarnings("deprecation")
			Enchantment name = Enchantment.getByName((String) enchant.get("name"));
			Integer lvl = (Integer) enchant.get("level");
			r.put(name, lvl);
		}
		return r;
	}
	
	public ItemStack getItem() {
		ItemStack item = new ItemStack(getType());
		item.setItemMeta(getItemMeta());
		return item;
	}
	
	public ItemMeta getItemMeta() {
		ItemStack item = new ItemStack(getType());
		ItemMeta iMeta = item.getItemMeta();
		assert iMeta != null;
		iMeta.setDisplayName(getName());
		iMeta.setLore(getLore());
		iMeta.setCustomModelData(getData());
		iMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

		// Customs
		if(this.equals(SELL_PAPER) || this.toString().contains("DEATHKEY")) {
			iMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		// Manage enchantments
		Set<Entry<Enchantment, Integer>> enchants = getEnchants().entrySet();
		for (Entry<Enchantment, Integer> enchant : enchants) {
			iMeta.addEnchant(enchant.getKey(), enchant.getValue(), true);
		}
		
		return iMeta;
	}

}
