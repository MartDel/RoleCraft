package fr.martdel.rolecraft.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.powers.BuilderShockWave;

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
			// Shock wave for builders
			if(iMeta.getDisplayName().equalsIgnoreCase("Onde de choc (Builder)")) { // TODO Add lore check
				event.setCancelled(true);
				Location center = player.getLocation();
				BuilderShockWave circle = new BuilderShockWave(plugin, center);
				circle.setStartCircle();
			}
		}
	}

}
