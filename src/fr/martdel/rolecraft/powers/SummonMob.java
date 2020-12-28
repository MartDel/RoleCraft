package fr.martdel.rolecraft.powers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SmallFireball;
import org.bukkit.scheduler.BukkitScheduler;

import fr.martdel.rolecraft.RoleCraft;

public class SummonMob {

	private Location location;
	private World world;
	private LivingEntity mob;
	private LivingEntity victim;
	
	private RoleCraft plugin;
	private BukkitScheduler scheduler;

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
	
	public void attack(LivingEntity v, int delay) {
		this.victim = v;
		scheduler.runTaskLater(plugin, new Runnable() {
			private int i = 0;
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
				fireball.setIsIncendiary(true);
				
				i++;
				if(i < 10 && !victim.isDead()) scheduler.runTaskLater(plugin, this, delay);
				else mob.remove();
			}
		}, delay);
	}
	
	public void setEntity(LivingEntity mob) {
		this.mob = mob;
	}

}
