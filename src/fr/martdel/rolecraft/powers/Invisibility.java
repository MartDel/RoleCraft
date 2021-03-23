package fr.martdel.rolecraft.powers;

import fr.martdel.rolecraft.RoleCraft;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public class Invisibility {

    public static final String ITEMNAME = RoleCraft.config.getString("powers.invisibility.item_name");
    public static final int COOLDOWN = RoleCraft.config.getInt("powers.invisibility.cooldown");

    private static final Material ITEMTYPE = RoleCraft.getConfigMaterial("powers.invisibility.item_type");
    private static final List<String> STARTMSG = RoleCraft.config.getStringList("powers.invisibility.start_msg");
    private static final String STOPMSG = RoleCraft.config.getString("powers.invisibility.stop_msg");
    private static final int DURATION = RoleCraft.config.getInt("powers.invisibility.duration");

    private final Player player;
    private final RoleCraft plugin;
    private final BukkitScheduler scheduler;

    public Invisibility(RoleCraft rolecraft, Player player) {
        this.player = player;
        this.plugin = rolecraft;
        this.scheduler = plugin.getServer().getScheduler();
    }

    /**
     * Turn invisibility on for the player during DURATION ticks
     */
    public void start(){
        // Start invisibility
        player.setInvisible(true);
        for (String msg : STARTMSG){
            player.sendMessage(msg);
        }

        // Hide player extra items
        PlayerInventory inv = player.getInventory();
        inv.setItem(9, inv.getHelmet());
        inv.setItem(10, inv.getChestplate());
        inv.setItem(11, inv.getLeggings());
        inv.setItem(12, inv.getBoots());
        inv.setItem(13, inv.getItemInOffHand());
        inv.setArmorContents(new ItemStack[]{});
        inv.setItemInOffHand(new ItemStack(Material.AIR));
        player.updateInventory();

        scheduler.runTaskLater(plugin, () -> {
            // Stop invisibility
            player.setInvisible(false);
            assert STOPMSG != null;
            player.sendMessage(STOPMSG);

            // Show player extra items
            PlayerInventory inv2 = player.getInventory();
            inv2.setHelmet(inv2.getItem(9));
            inv2.setChestplate(inv2.getItem(10));
            inv2.setLeggings(inv2.getItem(11));
            inv2.setBoots(inv2.getItem(12));
            inv2.setItemInOffHand(inv2.getItem(13));
            int[] to_remove = {9, 10, 11, 12, 13};
            for (int slot: to_remove){
                inv2.setItem(slot, new ItemStack(Material.AIR));
            }
            player.updateInventory();
        }, DURATION);
    }

    /**
     * Get item to start the power
     * @return The ItemStack to give to the player
     */
    public static ItemStack getItemStack() {
        assert ITEMTYPE != null;
        ItemStack item = new ItemStack(ITEMTYPE);
        ItemMeta itemmeta = item.getItemMeta();
        assert itemmeta != null;
        itemmeta.setDisplayName(ITEMNAME);
        itemmeta.addEnchant(Enchantment.DURABILITY, 200, true);
        itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemmeta);
        return item;
    }

}
