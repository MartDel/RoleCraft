package fr.martdel.rolecraft.powers;

import fr.martdel.rolecraft.RoleCraft;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

public class Invisibility {

    public static final String ITEMNAME = RoleCraft.config.getString("powers.invisibility.item_name");
    public static final int COOLDOWN = RoleCraft.config.getInt("powers.invisibility.cooldown");

    private static final Material ITEMTYPE = RoleCraft.getConfigMaterial("powers.invisibility.item_type");
    private static final String STARTMSG = RoleCraft.config.getString("powers.invisibility.start_msg");
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

    public void start(){
        player.setInvisible(true);
        assert STARTMSG != null;
        player.sendMessage(STARTMSG);
        scheduler.runTaskLater(plugin, () -> {
            player.setInvisible(false);
            assert STOPMSG != null;
            player.sendMessage(STOPMSG);
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