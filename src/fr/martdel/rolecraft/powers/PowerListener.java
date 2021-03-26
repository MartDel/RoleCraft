package fr.martdel.rolecraft.powers;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.player.CustomPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
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

import java.util.Arrays;
import java.util.List;

public class PowerListener implements Listener {

	private final RoleCraft plugin;

	public PowerListener(RoleCraft roleCraft) {
		this.plugin = roleCraft;
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack item = event.getItem();
		
		// Using a power item
		if(item != null && item.hasItemMeta()) {
			Material type = item.getType();
			ItemMeta iMeta = item.getItemMeta();
			assert iMeta != null;
			if(iMeta.hasDisplayName()) {
				String name = iMeta.getDisplayName();
				
				if(action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
					if(name.equalsIgnoreCase(ShockWave.ITEMNAME)) {
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
					
					if(action.equals(Action.RIGHT_CLICK_BLOCK)) {
						
						if(name.equalsIgnoreCase(SummonMob.ITEMNAME) && type.equals(SummonMob.SPAWNERTYPE)) {
							// Summoner power
							event.setCancelled(true);
							Block clicked = event.getClickedBlock();
							assert clicked != null;
							Location clicked_bloc = clicked.getLocation();
							ItemStack viewfinder = SummonMob.getViewFinder();
							ItemMeta viewfinderMeta = viewfinder.getItemMeta();
							assert viewfinderMeta != null;
							viewfinderMeta.setLore(Arrays.asList(
								"Invoquer un blaze sur le bloc :",
								Integer.toString(clicked_bloc.getBlockX()),
								Integer.toString(clicked_bloc.getBlockY() + 1),
								Integer.toString(clicked_bloc.getBlockZ())
							));
							viewfinder.setItemMeta(viewfinderMeta);
							int current_slot = player.getInventory().first(item);
							player.getInventory().setItem(current_slot, viewfinder);
						} else if(name.equalsIgnoreCase(Telekinesis.ITEMNAME)) {
							// Telekinesis power
							event.setCancelled(true);
							iMeta.setDisplayName(Telekinesis.USINGNAME);
							item.setItemMeta(iMeta);
							Block clicked = event.getClickedBlock();
							Telekinesis tk = new Telekinesis(plugin, player, clicked);
							tk.setItemSlot(player.getInventory().first(item));
							tk.moveBloc();
						} else if(name.equalsIgnoreCase(Telekinesis.USINGNAME)) {
							// Stop telekinesis power
							event.setCancelled(true);
							iMeta.setDisplayName(Telekinesis.ITEMNAME);
							item.setItemMeta(iMeta);
						}
					}

					if(name.equalsIgnoreCase(Bunker.ITEMNAME)) {
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
					} else if(name.equalsIgnoreCase(Fertility.ITEMNAME)) {
						// Fertility power
						event.setCancelled(true);
						Fertility fertility = new Fertility(plugin, player);
						fertility.run();
					} else if(name.equalsIgnoreCase(Invisibility.ITEMNAME)){
						// Invisibility power
						event.setCancelled(true);
						Invisibility invisibility = new Invisibility(plugin, player);
						invisibility.start();
						PowerLoader loader = new PowerLoader(plugin, player, Invisibility.getItemStack());
						loader.startLoading(Invisibility.COOLDOWN);
					} else if(name.equalsIgnoreCase(FlyingMob.ITEMNAME)){
						// Flying mobs power
						event.setCancelled(true);
						FlyingMob flyingMob = new FlyingMob(player, plugin);
						flyingMob.start();
						PowerLoader loader = new PowerLoader(plugin, player, FlyingMob.getItemStack());
						loader.startLoading(FlyingMob.COOLDOWN);
					} else if(name.equalsIgnoreCase(Bug.ITEMNAME)){
						// Bug power
						event.setCancelled(true);
						Bug bug = new Bug(player, plugin);
						bug.start();
						PowerLoader loader = new PowerLoader(plugin, player, Bug.getItemStack());
						loader.startLoading(Bug.COOLDOWN);
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
			LivingEntity victim = (LivingEntity) v;
			
			if(arrow.getShooter() instanceof Player) {
				Player shooter = (Player) arrow.getShooter();
				ItemStack weapon = shooter.getInventory().getItemInMainHand();
				ItemMeta weaponmeta = weapon.getItemMeta();
				assert weaponmeta != null;

				if(weapon.getType().equals(SummonMob.VIEWFINDERTYPE) && weaponmeta.getDisplayName().equalsIgnoreCase(SummonMob.ITEMNAME)) {
					// Summoner power
					event.setDamage(0);
					List<String> weapon_lore = weapon.getItemMeta().getLore();
					Location spawn = shooter.getLocation();
					assert weapon_lore != null;
					spawn.setX(Integer.parseInt(weapon_lore.get(1)));
					spawn.setY(Integer.parseInt(weapon_lore.get(2)));
					spawn.setZ(Integer.parseInt(weapon_lore.get(3)));
					SummonMob mob = new SummonMob(plugin, spawn);
					LivingEntity summon = mob.spawn(EntityType.BLAZE);
					summon.setAI(false);
					summon.setInvulnerable(true);
					mob.setEntity(summon);
					mob.attack(victim);
					PowerLoader loader = new PowerLoader(plugin, shooter, weapon, SummonMob.getSpawner());
					loader.startLoading(SummonMob.COOLDOWN);
				}
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		Player player = event.getPlayer();
		ItemStack itemstack = item.getItemStack();

		if(itemstack.equals(Bomb.getItemStack())) {
			plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
				Bomb bomb = new Bomb(item.getLocation());
				bomb.explode();
				World world = item.getWorld();
				world.spawnParticle(Particle.SMOKE_NORMAL, item.getLocation(), 3);
				item.remove();
				player.getInventory().addItem(Bomb.getItemStack());
			}, 40);
		}
	}

	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		// TODO check bomb pickup
	}
	
}
