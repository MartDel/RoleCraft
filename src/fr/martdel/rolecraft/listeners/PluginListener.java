package fr.martdel.rolecraft.listeners;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.TeamManager;

public class PluginListener implements Listener {
	
	private RoleCraft plugin;

	public PluginListener(RoleCraft roleCraft) {
		this.plugin = roleCraft;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		
		if(customPlayer.isSaved()) {
			TeamManager team = customPlayer.getTeam();
			String color = team.getColor();
			
			event.setJoinMessage("(§a+§r) §" + color + "[" + team.getName() + "] §r" + player.getDisplayName());
			System.out.println(player.getDisplayName() + " vient de rejoindre le serveur.");
			System.out.println("Il a choisi le metier de " + customPlayer.getStringJob("fr"));
//			System.out.println("Il possede un score de " + main.getScore().getScore(player));
		} else {
			/*
			 * NEW PLAYER
			 */
			System.out.println(player.getDisplayName() + " est nouveau sur ce serveur!");
			event.setJoinMessage("(§a+§r) Un nouveau joueur, §a" + player.getDisplayName() + "§r, vient d'arriver sur le serveur!");
			
			// TP to World Spawn
			double x = RoleCraft.config.getDouble("world_spawn.x");
			int y = RoleCraft.config.getInt("world_spawn.y");
			double z = RoleCraft.config.getDouble("world_spawn.z");
			Location worldspawn = new Location(player.getWorld(), x, y, z);
			player.teleport(worldspawn);
			
			// Set data
			customPlayer.save();
			new TeamManager(plugin, "Nouveau").add(player);
			
			ItemStack compass = new ItemStack(Material.COMPASS);
			ItemMeta compassMeta = compass.getItemMeta();
			compassMeta.setDisplayName("§9Choisir son métier");
			compassMeta.setLore(Arrays.asList("Faites un clic droit pour choisir votre métier"));
			compassMeta.setCustomModelData(3);
			compass.setItemMeta(compassMeta);
			player.getInventory().addItem(compass);
		}
	}
	
}
