package fr.martdel.rolecraft.listeners;

import fr.martdel.rolecraft.CustomItems;
import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.player.CustomPlayer;
import fr.martdel.rolecraft.player.MainScoreboard;
import fr.martdel.rolecraft.player.TeamManager;
import fr.martdel.rolecraft.player.Wallet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import javax.management.relation.Role;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class PluginListener implements Listener {
	
	private static final List<Material> FORBIDDEN_WEAPONS= RoleCraft.getConfigMaterialList("controled_items.weapons");
	
	private final RoleCraft plugin;

	public PluginListener(RoleCraft roleCraft) { this.plugin = roleCraft; }

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();

		if(customPlayer.isRegistered()) {
			TeamManager team = customPlayer.getTeam();
			String color = team.getColor();
			String prefix = team.getTeam().getPrefix();
			
			event.setJoinMessage("(§a+§r) §" + color + prefix + "§r" + player.getDisplayName());

			customPlayer.updateScoreboard();
			
			System.out.println(player.getDisplayName() + " vient de rejoindre le serveur.");
			System.out.println("Il a choisi le metier de " + customPlayer.getStringJob("fr"));
			System.out.println("Il possede un score de " + customPlayer.getScore());
		} else {
			/*
			 * NEW PLAYER
			 */
			event.setJoinMessage("(§a+§r) Un nouveau joueur, §a" + player.getDisplayName() + "§r, vient d'arriver sur le serveur!");
			System.out.println(player.getDisplayName() + " est nouveau sur ce serveur!");
			
			player.setGameMode(GameMode.SURVIVAL);
			player.setOp(false);
			
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
			ItemStack compass = CustomItems.COMPASS.getItem();
			player.getInventory().clear();
			player.getInventory().addItem(compass);
		}

		// Update Ip
		InetSocketAddress address = player.getAddress();
		assert address != null;
		String ip = address.getAddress().toString().substring(1);
		customPlayer.setIp(ip);
		customPlayer.save();

		// Manage channels
		plugin.channels.get("global").add(player);

		// Update scoreboards
		/*for (Player p : Bukkit.getOnlinePlayers()){
			new CustomPlayer(p,  plugin).loadData().updateScoreboard();
		}*/
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

		// Manage channels
		plugin.channels.get("global").remove(player);
	}
	
	@EventHandler
	public void onMessage(PlayerChatEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		TeamManager team = customPlayer.getTeam();
		String msg = event.getMessage();
		String username = player.getDisplayName();
		String color = team.getColor();
		String prefix = team.getTeam().getPrefix();
		event.setCancelled(true);

		// Manage channels
		for(Player p : plugin.channels.get("global")){
			p.sendMessage("§" + color +  prefix + "§r " + (p.equals(player) ? "Vous" : username) + "> " + msg);
		}
	}
	
	@EventHandler
	public void onDie(PlayerDeathEvent event) {
		// A player die
		Player player = event.getEntity();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		TeamManager team = customPlayer.getTeam();
		TeamManager team_new = new TeamManager(plugin, "Nouveau");
		if(team.getName().equals(team_new.getName())) event.setKeepInventory(true);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		// Remove emerald craft
		ItemStack item = event.getCurrentItem();
		assert item != null;
		if(item.getType().equals(Material.EMERALD)) event.setCancelled(true);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		ItemStack item = event.getItemDrop().getItemStack();
		ItemMeta itemMeta = item.getItemMeta();
		if(item.getType().equals(Material.WRITTEN_BOOK)) {
			// Player can't drop some useful books
			BookMeta bookMeta = (BookMeta) itemMeta;
			assert bookMeta != null;
			String title = bookMeta.getTitle();
			String author = bookMeta.getAuthor();
			List<String> protected_titles = Arrays.asList("Mission de départ", "Demande de confirmation");
			assert author != null;
			if(author.equalsIgnoreCase("MartDel") && protected_titles.contains(title)) {
				event.setCancelled(true);
			}
		} else if(item.getType().equals(Material.COMPASS)) {
			// Player can't drop the start compass
			customPlayer.loadData();
			if(customPlayer.isNew()) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity victim = event.getEntity();
		DamageCause cause = event.getCause();

		// A new player can't be damaged
		if (victim instanceof Player) {
			Player v = (Player) victim;
			CustomPlayer customVictim = new CustomPlayer(v, plugin).loadData();
			if (customVictim.isNew()) {
				event.setCancelled(true);
				return;
			}
		}

		// Tools make low damages
		if (damager instanceof Player) {
			Player player = (Player) damager;
			Material weapon = player.getItemInHand().getType();
			if (cause.equals(DamageCause.ENTITY_ATTACK) && FORBIDDEN_WEAPONS.contains(weapon)) {
				event.setDamage(0.5);
			}
		}
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		ItemStack item = event.getCurrentItem();
		InventoryView view = event.getView();
		String title = view.getTitle();
		Inventory inv = event.getClickedInventory();
		if(item == null) return;

		// Choose a job
		if(title.equalsIgnoreCase("§9Choisir son métier")) {
			customPlayer.loadData();
			ItemMeta meta = item.getItemMeta();
			assert meta != null;
			int selected_job = meta.getCustomModelData();
			customPlayer.setJob(selected_job);
			
			String jobname = RoleCraft.firstLetterToUpperCase(customPlayer.getStringJob("fr"));
			customPlayer.getTeam().move(player, jobname);
			
			double x = RoleCraft.config.getDouble("spawn.x");
			int y = RoleCraft.config.getInt("spawn.y");
			double z = RoleCraft.config.getDouble("spawn.z");
			Location spawn = new Location(player.getWorld(), x, y, z);
			
			player.teleport(spawn);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawnpoint " + player.getDisplayName() + " " + spawn.getBlockX() + " " + spawn.getBlockY() + " " + spawn.getBlockZ());
			player.getInventory().clear();
			
			String jobColor = customPlayer.getTeam().getColor();
			player.sendMessage("Vous avez choisi le métier de §" + jobColor + jobname);
			
			// Give some stuffs
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + player.getDisplayName() + " written_book{pages:['[\"\",{\"text\":\"Bienvenu sur ce serveur \"},{\"text\":\"" + player.getDisplayName() + "\",\"color\":\"dark_aqua\"},{\"text\":\"!\\\\n\\\\nVous avez choisi le métier §" + jobColor + jobname + "§r. Très bon choix!\\\\n\\\\nEnfin, ne perdons pas plus de temps avec les politesses. Il faut vous rendre à la \",\"color\":\"reset\"},{\"text\":\"banque\",\"color\":\"red\"},{\"text\":\", elle se situe \",\"color\":\"reset\"},{\"text\":\"à l\\'Est du village\",\"color\":\"red\"},{\"text\":\".\",\"color\":\"reset\"}]','[\"\",{\"text\":\"Vous y trouverez un banquier. Parlez avec lui et dites lui que vous êtes nouveau dans le coin, il vous donnera la possibilité d\\'\"},{\"text\":\"acheter une maison\",\"color\":\"red\"},{\"text\":\". Parce que c\\'est vrai qu\\'être SDF n\\'est pas la meilleur situation dans ce monde.\",\"color\":\"reset\"}]','[\"\",{\"text\":\"Enfin bref vous trouverez un \"},{\"text\":\"capital de départ\",\"color\":\"red\"},{\"text\":\" dans votre inventaire, faites en bonne usage! N\\'oubliez pas que sans maison, vous ne pourrez rien faire dans ce monde malheureusement.\",\"color\":\"reset\"}]','[\"\",{\"text\":\"Ah... J\\'ai failli oublier... Si vous ne voyez personne dans la banque, \"},{\"text\":\"contactez les banquiers présents sur le serveur en cliquant sur la pancarte\",\"color\":\"red\"},{\"text\":\". Vous ne pouvez pas la manquer!\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"Bon courage.\",\"color\":\"dark_green\"}]'],title:\"Mission de départ\",author:MartDel,display:{Lore:[\"Ouvrez ce livre pour découvrir\",\"votre première mission !\"]}}");
			// Get starter pack
			Material food_type = RoleCraft.getConfigMaterial("starter_pack.food.type");
			int food_amount = RoleCraft.config.getInt("starter_pack.food.amount");
			int rubis_builder = RoleCraft.config.getInt("starter_pack.rubis.builder");
			int rubis_else = RoleCraft.config.getInt("starter_pack.rubis.else");
			// Give starter pack
			ItemStack food = new ItemStack(food_type, food_amount);
			player.getInventory().addItem(food);
			Wallet wallet = customPlayer.getWallet();
			try {
				if(selected_job == 3) wallet.give(rubis_builder);
				else wallet.give(rubis_else);
			} catch (Exception e){ player.sendMessage(e.getMessage()); }

			// Set scoreboard
			MainScoreboard sb = new MainScoreboard();
			sb.setObjective(customPlayer);
			player.setScoreboard(sb.getScoreboard());
			
			customPlayer.save();
			event.setCancelled(true);
		} else if(title.contains(CustomItems.LETTERBOX.getName())) {
			if(event.getSlot() == 26) {
				// Get letterbox
				Location compass_target = player.getCompassTarget();
				Location chest_location = new Location(compass_target.getWorld(), compass_target.getBlockX(), compass_target.getBlockY(), compass_target.getBlockZ());
				Chest letterbox_chest = (Chest) player.getWorld().getBlockAt(chest_location).getState();
				
				// Send items in the letterbox
				Inventory letterbox_inv = letterbox_chest.getInventory();
				if(inv == null) return;
				for(int s = 0; s < inv.getSize() - 1; s++) {
					if(inv.getItem(s) != null) {
						ItemStack i = inv.getItem(s);
						letterbox_inv.addItem(i);
					}
				}
				
				player.closeInventory();
				event.setCancelled(true);
			}
		}
	}
	
}
