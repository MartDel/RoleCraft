package fr.martdel.rolecraft.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.martdel.rolecraft.*;
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

@SuppressWarnings("deprecation")
public class PluginListener implements Listener {
	
	private static final List<Material> FORBIDDEN_WEAPONS= new ArrayList<>();
	
	private RoleCraft plugin;

	public PluginListener(RoleCraft roleCraft) {
		this.plugin = roleCraft;
		
		// Get forbidden weapons
		List<String> list = RoleCraft.config.getStringList("controled_items.weapons");
		for (String weapon_name : list) {
			FORBIDDEN_WEAPONS.add(Material.getMaterial(weapon_name));
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
		
		if(customPlayer.isRegistered()) {
			TeamManager team = customPlayer.getTeam();
			String color = team.getColor();
			String prefix = team.getTeam().getPrefix();
			
			event.setJoinMessage("(§a+§r) §" + color + prefix + "§r" + player.getDisplayName());

			// Set scoreboard
//			customPlayer.
			
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
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		ItemStack item = event.getItemDrop().getItemStack();
		ItemMeta itemMeta = item.getItemMeta();
		if(item.getType().equals(Material.WRITTEN_BOOK)) {
			/*
			 * PLAYER DROPS AN IMPORTANT BOOK
			 */
			BookMeta bookMeta = (BookMeta) itemMeta;
			String title = bookMeta.getTitle();
			String author = bookMeta.getAuthor();
			List<String> protected_titles = Arrays.asList("Mission de départ", "Demande de confirmation");
			if(author.equalsIgnoreCase("MartDel") && protected_titles.contains(title)) {
				event.setCancelled(true);
				return;
			}
		} else if(item.getType().equals(Material.COMPASS)) {
			/*
			 * PLAYER DROPS THE START COMPASS
			 */
			customPlayer.loadData();
			System.out.println(customPlayer.getJob());
			if(customPlayer.isNew()) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Entity p = event.getDamager();
		Entity entity = event.getEntity();
		DamageCause cause = event.getCause();
		/*
		 * NEW PLAYER IS DAMAGED
		 */
		if(entity instanceof Player) {
			Player victim = (Player) entity;
			CustomPlayer customVictim = new CustomPlayer(victim, plugin).loadData();
			if(customVictim.isNew()) {
				event.setCancelled(true);
				return;
			}
		}

		if(p instanceof Player) {
			Player player = (Player) p;
			/*
			 * PLAYER MAKES DAMAGE WITH A TOOL
			 */
			Material weapon = player.getItemInHand().getType();			
			if(cause.equals(DamageCause.ENTITY_ATTACK) && FORBIDDEN_WEAPONS.contains(weapon)) {
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
		/*
		 * PLAYER CHOOSES HIS JOB
		 */
		if(title.equalsIgnoreCase("§9Choisir son métier")) {
			customPlayer.loadData();
			int selected_job = item.getItemMeta().getCustomModelData();
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
			Material food_type = Material.getMaterial(RoleCraft.config.getString("starter_pack.food.type"));
			int food_amount = RoleCraft.config.getInt("starter_pack.food.amount");
			int rubis_builder = RoleCraft.config.getInt("starter_pack.rubis.builder");
			int rubis_else = RoleCraft.config.getInt("starter_pack.rubis.else");
			// Give starter pack
			ItemStack food = new ItemStack(food_type, food_amount);
			player.getInventory().addItem(food);
			Wallet wallet = customPlayer.getWallet();
			if(selected_job == 3) wallet.give(rubis_builder);
			else wallet.give(rubis_else);

			// Set scoreboard
			MainScoreboard sb = new MainScoreboard();
			sb.setObjective(customPlayer);
			player.setScoreboard(sb.getScoreboard());
			
			customPlayer.save();
			event.setCancelled(true);
			return;
		} else if(title.contains(CustomItems.LETTERBOX.getName())) {
			/*
			 * SEND ITEMS IN A LETTERBOX
			 */
			if(event.getSlot() == 26) {
				// Get letterbox
				Location compass_target = player.getCompassTarget();
				Location chest_location = new Location(compass_target.getWorld(), compass_target.getBlockX(), compass_target.getBlockY(), compass_target.getBlockZ());
				Chest letterbox_chest = (Chest) player.getWorld().getBlockAt(chest_location).getState();
				
				// Send items in the letterbox
				Inventory letterbox_inv = letterbox_chest.getInventory();
				for(int s = 0; s < inv.getSize() - 1; s++) {
					if(inv.getItem(s) != null) {
						ItemStack i = inv.getItem(s);
						letterbox_inv.addItem(i);
					}
				}
				
				player.closeInventory();
				event.setCancelled(true);
				return;
			}
		}
	}
	
}
