package fr.martdel.rolecraft.powers;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Scepter {
	
	private int level;

	public Scepter(int lvl) {
		this.level = lvl;
	}
	
	public void give(Player player) {
		ItemStack scepter = new ItemStack(Material.STICK);
		ItemMeta smeta = scepter.getItemMeta();
		smeta.setDisplayName("§dSceptre§r de niveau §4" + level + "§r");
		smeta.setLore(Arrays.asList("Clique droit pour lancer une §4boule de feu§r."));
		smeta.addEnchant(Enchantment.ARROW_DAMAGE, 200, true);
		smeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		scepter.setItemMeta(smeta);
		player.getInventory().addItem(scepter);
	}

}
