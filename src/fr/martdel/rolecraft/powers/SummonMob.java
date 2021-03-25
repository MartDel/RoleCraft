package fr.martdel.rolecraft.powers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SmallFireball;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import fr.martdel.rolecraft.RoleCraft;

public class SummonMob {

	private static final int LIFE = RoleCraft.config.getInt("powers.summoner.life");
	private static final int DELAY = RoleCraft.config.getInt("powers.summoner.attack_delay");

	public static final String ITEMNAME = RoleCraft.config.getString("powers.summoner.item_name");
	public static final Material SPAWNERTYPE = RoleCraft.getConfigMaterial("powers.summoner.spawner_type");
	public static final Material VIEWFINDERTYPE = RoleCraft.getConfigMaterial("powers.summoner.viewfinder_type");
	public static final int COOLDOWN = RoleCraft.config.getInt("powers.summoner.cooldown");
	
	private final Location location;
	private final World world;
	private LivingEntity mob;
	private LivingEntity victim;
	
	private final RoleCraft plugin;
	private final BukkitScheduler scheduler;

	public SummonMob(RoleCraft plugin, Location spawner) {
		this.location = spawner;
		this.world = spawner.getWorld();

		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
	}
	
	public LivingEntity spawn(EntityType mob) {
		Entity entity = world.spawnEntity(location, mob);
		if(entity instanceof LivingEntity) return (LivingEntity) entity;
		else return null;
	}
	
	public void attack(LivingEntity v) {
		this.attack(v, DELAY);
	}
	
	public void attack(LivingEntity v, int delay) {
		this.victim = v;
		scheduler.runTaskLater(plugin, new Runnable() {
			private int life = 0;
			@Override
			public void run() {
				Location target = victim.getLocation();
				Location position = mob.getLocation();
				double xdif = target.getBlockX() - position.getBlockX();
				double ydif = target.getBlockY() - position.getBlockY();
				double zdif = target.getBlockZ() - position.getBlockZ();
				
				Trigonometry targetinfo = new Trigonometry();
				targetinfo.setX(Math.abs(xdif));
				targetinfo.setY(Math.abs(ydif));
				targetinfo.setZ(Math.abs(zdif));
				targetinfo.calculWithCoordinates();

				// Get yaw to target victim
				double yaw = 1;
				if(xdif == 0) {
					if(zdif < 0) yaw = 179;
				} else {
					yaw = targetinfo.getYaw();
					if(xdif > 0 && zdif > 0) yaw = -90 + yaw;
					else if(xdif < 0 && zdif > 0) yaw = 90 - yaw;
					else if(xdif < 0 && zdif < 0) yaw = 90 + yaw;
					else if(xdif > 0 && zdif < 0) yaw = -90 - yaw;
				}
				
				// Get pitch to target victim
				double pitch = targetinfo.getPitch();
				if(ydif > 0) pitch = -pitch;
				
				mob.setRotation((float) yaw, (float) pitch);
				
				SmallFireball fireball = mob.launchProjectile(SmallFireball.class);
				fireball.setIsIncendiary(false);
				
				life += delay;
				if(life < LIFE && !victim.isDead()) scheduler.runTaskLater(plugin, this, delay);
				else mob.remove();
			}
		}, delay);
	}
	
	public void setEntity(LivingEntity mob) {
		this.mob = mob;
	}
	
	public static ItemStack getSpawner() {
		ItemStack item = new ItemStack(SPAWNERTYPE);
		ItemMeta itemmeta = item.getItemMeta();
		assert itemmeta != null;
		itemmeta.setDisplayName(ITEMNAME);
		itemmeta.addEnchant(Enchantment.DURABILITY, 200, true);
		itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemmeta);
		return item;
	}
	
	public static ItemStack getViewFinder() {
		ItemStack item = new ItemStack(VIEWFINDERTYPE);
		ItemMeta itemmeta = item.getItemMeta();
		assert itemmeta != null;
		itemmeta.setDisplayName(ITEMNAME);
		itemmeta.addEnchant(Enchantment.DURABILITY, 200, true);
		itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemmeta);
		return item;
	}

}