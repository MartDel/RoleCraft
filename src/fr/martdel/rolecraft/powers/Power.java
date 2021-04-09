package fr.martdel.rolecraft.powers;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Power {
    
    private final String itemname;
    private final Material itemtype;

    protected Power(String itemname, Material itemtype) {
        this.itemname = itemname;
        this.itemtype = itemtype;
    }

    public abstract void start();
    
    /**
     * Get item to start the power
     * @return The ItemStack to give to the player
     */
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(itemtype);
        ItemMeta itemmeta = item.getItemMeta();
        assert itemmeta != null;
        itemmeta.setDisplayName(itemname);
        itemmeta.addEnchant(Enchantment.DURABILITY, 200, true);
        itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemmeta);
        return item;
    }
    
}
