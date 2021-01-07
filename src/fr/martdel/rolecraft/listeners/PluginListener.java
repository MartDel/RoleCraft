package fr.martdel.rolecraft.listeners;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.TeamManager;

@SuppressWarnings("deprecation")
public class PluginListener implements Listener {
	
	private RoleCraft plugin;

	public PluginListener(RoleCraft roleCraft) {
		this.plugin = roleCraft;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		
		if(customPlayer.isRegistered()) {
			TeamManager team = customPlayer.getTeam();
			String color = team.getColor();
			String prefix = team.getTeam().getPrefix();
			event.setJoinMessage("(§a+§r) §" + color + prefix + "§r" + player.getDisplayName());
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
			customPlayer.register();
			new TeamManager(plugin, "Nouveau").add(player);
			
			// Give the start compass to choose his job
			ItemStack compass = new ItemStack(Material.COMPASS);
			ItemMeta compassMeta = compass.getItemMeta();
			compassMeta.setDisplayName("§9Choisir son métier");
			compassMeta.setLore(Arrays.asList("Faites un clic droit pour choisir votre métier"));
			compassMeta.setCustomModelData(3);
			compass.setItemMeta(compassMeta);
			player.getInventory().addItem(compass);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		
		try {
			TeamManager team = customPlayer.getTeam();
			String color = team.getColor();
			String prefix = team.getTeam().getPrefix();
			event.setQuitMessage("(§4-§r) §" + color + prefix + "§r" + player.getDisplayName());
		} catch (Exception e) {
			event.setQuitMessage("(§4-§r) " + player.getDisplayName());
		}
	}
	
	@EventHandler
	public void onMessage(PlayerChatEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		TeamManager team = customPlayer.getTeam();
		String color = team.getColor();
		String prefix = team.getTeam().getPrefix();
		
		// <%1$s> %2$s
		if(team != null) {
			event.setFormat("§" + color +  prefix + "§r %1$s> %2$s");
		}
	}
	
	@EventHandler
	public void onDie(PlayerDeathEvent event) {
		Player player = event.getEntity();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		TeamManager team = customPlayer.getTeam();
		/*
		 * NEW PLAYER DIES
		 */
		TeamManager team_new = new TeamManager(plugin, "Nouveau");
		if(team.getName().equals(team_new.getName())) event.setKeepInventory(true);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		ItemStack item = event.getCurrentItem();
		/*
		 * REMOVE EMERALD CRAFT
		 */
		if(item.getType().equals(Material.EMERALD)) event.setCancelled(true);
	}
	
}
