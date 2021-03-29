package fr.martdel.rolecraft.powers;

import fr.martdel.rolecraft.RoleCraft;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class KillingSpree {

    public static final String ITEMNAME = RoleCraft.config.getString("powers.killingspree.item_name");
    public static final int COOLDOWN = RoleCraft.config.getInt("powers.killingspree.cooldown");

    private static final Material ITEMTYPE = RoleCraft.getConfigMaterial("powers.killingspree.item_type");
    private static final int RADIUS = RoleCraft.config.getInt("powers.killingspree.radius");
    private static final int DURATION = RoleCraft.config.getInt("powers.killingspree.duration");

    // Effects
    private static final int STRENGTH = RoleCraft.config.getInt("powers.killingspree.effects.strength");
    private static final int SPEED = RoleCraft.config.getInt("powers.killingspree.effects.speed");
    private static final int RESISTANCE = RoleCraft.config.getInt("powers.killingspree.effects.resistance");

    private final Player player;
    private final RoleCraft plugin;
    private final BukkitScheduler scheduler;

    public KillingSpree(Player player, RoleCraft plugin) {
        this.player = player;
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    /**
     * Remove close mobs AI
     */
    public void start(){
        // Give glowing to near mobs
        List<Entity> around = player.getNearbyEntities(RADIUS, 4, RADIUS);
        for (Entity e : around){
            if(e instanceof LivingEntity && !(e instanceof Player)){
                LivingEntity target = (LivingEntity) e;
                target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, DURATION, 1));
            }
        }
        // Apply potion effects
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, DURATION, STRENGTH));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, DURATION, SPEED));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, DURATION, RESISTANCE));
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
