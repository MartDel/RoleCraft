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

	public static final Material ITEMTYPE = Material.BOW;
	public static final String ITEMNAME = RoleCraft.config.getString("powers.summoner.item_name");
	public static final int COOLDOWN = RoleCraft.config.getInt("powers.summoner.cooldown");
	
	private Location location;
	private World world;
	private int ticklife;
	private LivingEntity mob;
	private LivingEntity victim;
	
	private RoleCraft plugin;
	private BukkitScheduler scheduler;

	public SummonMob(RoleCraft plugin, Location spawner) {
		this.location = spawner;
		this.world = spawner.getWorld();
		this.ticklife = LIFE;
		
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
				double distance2D = Math.sqrt(Math.pow(xdif, 2) + Math.pow(zdif, 2));

				// Get yaw to target victim
				double yaw = 1;
				if(xdif == 0) {
					if(zdif < 0) yaw = 179;
					else yaw = 1;
				} else {
					yaw = Math.atan(Math.abs(zdif) / Math.abs(xdif));
					yaw = Math.toDegrees(yaw);
					if(xdif > 0 && zdif > 0) yaw = -90 + yaw;
					else if(xdif < 0 && zdif > 0) yaw = 90 - yaw;
					else if(xdif < 0 && zdif < 0) yaw = 90 + yaw;
					else if(xdif > 0 && zdif < 0) yaw = -90 - yaw;
				}
				
				// Get pitch to target victim
				double pitch = Math.atan(Math.abs(ydif) / Math.abs(distance2D));
				pitch = Math.toDegrees(pitch);
				if(ydif > 0) pitch = -pitch;
				
				mob.setRotation((float) yaw, (float) pitch);
				
				SmallFireball fireball = mob.launchProjectile(SmallFireball.class);
				fireball.setIsIncendiary(false);
				
				life += delay;
				if(life < ticklife && !victim.isDead()) scheduler.runTaskLater(plugin, this, delay);
				else mob.remove();
			}
		}, delay);
	}
	
	public void setEntity(LivingEntity mob) {
		this.mob = mob;
	}
	
	public void setTickLife(int life) {
		this.ticklife = life;
	}
	
	public static ItemStack getItemStack() {
		ItemStack item = new ItemStack(ITEMTYPE);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(ITEMNAME);
		itemmeta.addEnchant(Enchantment.DAMAGE_ALL, 200, true);
		itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemmeta);
		return item;
	}

}