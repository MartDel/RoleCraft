package fr.martdel.rolecraft.powers;

import fr.martdel.rolecraft.RoleCraft;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class Bug {

    public static final String ITEMNAME = RoleCraft.config.getString("powers.bug.item_name");
    public static final int COOLDOWN = RoleCraft.config.getInt("powers.bug.cooldown");

    private static final Material ITEMTYPE = RoleCraft.getConfigMaterial("powers.bug.item_type");
    private static final int RADIUS = RoleCraft.config.getInt("powers.bug.radius");
    private static final int DURATION = RoleCraft.config.getInt("powers.bug.duration");

    private final Player player;
    private final RoleCraft plugin;
    private final BukkitScheduler scheduler;

    public Bug(Player player, RoleCraft plugin) {
        this.player = player;
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    /**
     * Remove close mobs AI
     */
    public void start(){
        // Remove mobs AI
        List<Entity> around = player.getNearbyEntities(RADIUS, 4, RADIUS);
        List<LivingEntity> targets = new ArrayList<>();
        for (Entity e : around){
            if(e instanceof LivingEntity && !(e instanceof Player)){
                LivingEntity target = (LivingEntity) e;
                targets.add(target);
                target.setAI(false);
            }
        }

        // Give back mobs AI
        scheduler.runTaskLater(plugin, () -> {
            for(LivingEntity target : targets){
                target.setAI(true);
            }
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
