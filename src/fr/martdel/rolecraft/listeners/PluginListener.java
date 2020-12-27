package fr.martdel.rolecraft.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.powers.Bunker;
import fr.martdel.rolecraft.powers.ShockWave;

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
		if(item != null && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) && item.hasItemMeta()) {
			ItemMeta iMeta = item.getItemMeta();
			if(iMeta.hasDisplayName()) {				
				// Bunker power
				if(iMeta.getDisplayName().equalsIgnoreCase("Bunker")) { // TODO Add lore check
					event.setCancelled(true);
					Location center = player.getLocation();
					Bunker bunker = new Bunker(plugin, center);
					bunker.build();
				} else if(iMeta.getDisplayName().equalsIgnoreCase("Onde de choc")) { // TODO Add lore check
					event.setCancelled(true);
					Location center = player.getLocation();
					ShockWave wave = new ShockWave(plugin, center);
					if(!wave.checkEnvironment()) {
						player.sendMessage("Il faut un espace dégagé pour lancer une §5onde de choc§r !");
						return;
					}
					wave.launch();
					wave.makeDamages(player);
				} else if(iMeta.getDisplayName().contains("§dSceptre§r")) { // TODO Add lore check
					event.setCancelled(true);
					Fireball fireball = player.launchProjectile(Fireball.class);
					fireball.setIsIncendiary(false);
				}
			}
		}
	}

}
