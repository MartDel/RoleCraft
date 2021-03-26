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

public class FlyingMob {

    public static final String ITEMNAME = RoleCraft.config.getString("powers.flymob.item_name");
    public static final int COOLDOWN = RoleCraft.config.getInt("powers.flymob.cooldown");

    private static final Material ITEMTYPE = RoleCraft.getConfigMaterial("powers.flymob.item_type");
    private static final int RADIUS = RoleCraft.config.getInt("powers.flymob.radius");
    private static final int DURATION = RoleCraft.config.getInt("powers.flymob.duration");

    private final Player player;
    private final RoleCraft plugin;
    private final BukkitScheduler scheduler;

    public FlyingMob(Player player, RoleCraft plugin) {
        this.player = player;
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    /**
     * Make close mobs flying
     */
    public void start(){
        // Found target entities
        List<Entity> around = player.getNearbyEntities(RADIUS, 4, RADIUS);
        List<LivingEntity> targets = new ArrayList<>();
        for (Entity e : around){
            if(e instanceof LivingEntity && !(e instanceof Player)){
                targets.add((LivingEntity) e);
            }
        }

        // Make entities flying
        World world = player.getWorld();
        List<Bat> bats = new ArrayList<>();
        for(LivingEntity target : targets){
            Bat bat = (Bat) world.spawnEntity(target.getLocation(), EntityType.BAT);
            bat.setAwake(true);
            bat.addPassenger(target);
            bats.add(bat);
        }

        // Make entities going back to the ground
        scheduler.runTaskLater(plugin, () -> {
            for(Bat bat : bats){
                bat.removePassenger(bat.getPassengers().get(0));
                bat.remove();
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
