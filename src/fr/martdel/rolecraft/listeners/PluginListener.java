package fr.martdel.rolecraft.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.powers.Bomb;
import fr.martdel.rolecraft.powers.Bunker;
import fr.martdel.rolecraft.powers.PowerLoader;
import fr.martdel.rolecraft.powers.ShockWave;
import fr.martdel.rolecraft.powers.SummonMob;

public class PluginListener implements Listener {
	
	private RoleCraft plugin;

	public PluginListener(RoleCraft roleCraft) {
		this.plugin = roleCraft;
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack item = event.getItem();
		
		// Using an item
		if(item != null && item.hasItemMeta()) {
			ItemMeta iMeta = item.getItemMeta();
			if(iMeta.hasDisplayName()) {
				
				if(action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
					if(iMeta.getDisplayName().equalsIgnoreCase(ShockWave.ITEMNAME)) {
						// Shockwave power
						event.setCancelled(true);
						Location center = player.getLocation();
						ShockWave wave = new ShockWave(plugin, center);
						if(!wave.checkWaveEnvironment()) {
							player.sendMessage("Il faut un espace dégagé pour lancer une §5onde de choc§r !");
							return;
						}
						wave.launch();
						wave.makeDamages(player);
						PowerLoader loader = new PowerLoader(plugin, player, ShockWave.getItemStack());
						loader.startLoading(ShockWave.COOLDOWN);
					}
				}
				
				if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
					if(iMeta.getDisplayName().equalsIgnoreCase(Bunker.ITEMNAME)) {
						// Bunker power
						event.setCancelled(true);
						Location center = player.getLocation();
						Bunker bunker = new Bunker(plugin, center);
						if(!bunker.checkBunkerEnvironment()) {
							player.sendMessage("Il faut un espace dégagé pour poser un §5bunker§r !");
							return;
						}
						bunker.build();
						PowerLoader loader = new PowerLoader(plugin, player, Bunker.getItemStack());
						loader.startLoading(Bunker.COOLDOWN);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onArrowTouch(EntityDamageByEntityEvent event) {
		DamageCause cause = event.getCause();
		Entity damager = event.getDamager();
		Entity v = event.getEntity();
		if(damager.getType().equals(EntityType.ARROW) && cause.equals(DamageCause.PROJECTILE) && v instanceof LivingEntity) {
			Arrow arrow = (Arrow) damager;
			Player shooter = (Player) arrow.getShooter();
			LivingEntity victim = (LivingEntity) v;
			@SuppressWarnings("deprecation")
			ItemStack weapon = shooter.getItemInHand();
			if(weapon.getType().equals(SummonMob.ITEMTYPE) && weapon.getItemMeta().getDisplayName().equalsIgnoreCase(SummonMob.ITEMNAME)) {
				// Summoner power
				event.setDamage(0);
				Location spawner = shooter.getLocation();
				SummonMob mob = new SummonMob(plugin, spawner);
				LivingEntity summon = mob.spawn(EntityType.BLAZE);
				summon.setAI(false);
				summon.setInvulnerable(true);
				mob.setEntity(summon);
				mob.attack(victim);
				PowerLoader loader = new PowerLoader(plugin, shooter, SummonMob.getItemStack());
				loader.startLoading(SummonMob.COOLDOWN);
			}
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		Player player = event.getPlayer();
		ItemStack itemstack = item.getItemStack();
		
		if(itemstack.equals(Bomb.getItemStack())) {
			plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					Bomb bomb = new Bomb(item.getLocation());
					bomb.explode();
					item.remove();
					player.getInventory().addItem(Bomb.getItemStack());
				}
			}, 40);
		}
	}
	
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		// TODO check bomb pickup
	}

}
