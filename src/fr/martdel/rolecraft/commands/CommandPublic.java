package fr.martdel.rolecraft.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.TeamManager;

public class CommandPublic implements CommandExecutor {
	
	private final String[] TEAMCMD = {"farmer", "miner", "explorer", "builder"};
	
	private RoleCraft plugin;

	public CommandPublic(RoleCraft rolecraft) {
		this.plugin = rolecraft;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
			TeamManager team = customPlayer.getTeam();
			
			// Team messages
			for (int i = 0; i < TEAMCMD.length; i++) {
				String team_cmd = TEAMCMD[i];
				if(cmd.getName().equalsIgnoreCase(team_cmd)){
					/*
					 * SEND A MESSAGE TO ALL OF PLAYERS IN THE SPECIFIC TEAM
					 */
					if(args.length < 1) {
						player.sendMessage("§4Il manque un argument.");
						return false;
					}

					StringBuilder message = new StringBuilder();
					for(int j = 0; j < args.length; j++) {
						message.append(args[j] + " ");
					}

					PreparedStatement query = plugin.getDB().getConnection().prepareStatement("SELECT uuid FROM players WHERE job=?");
					query.setInt(1, i);
					ResultSet result = query.executeQuery();
					while(result.next()) {
						String uuid = result.getString("uuid");
						Player addressees = plugin.getServer().getPlayer(uuid);
						
						List<Map<?, ?>> jobs_list = RoleCraft.config.getMapList("jobs");
						@SuppressWarnings("unchecked")
						Map<String, String> job = (Map<String, String>) jobs_list.get(i);
						String fr = job.get("fr") + "s";
						String player_color = team.getColor();						
						
						addressees.sendMessage("(A tous les " + fr + ") [§" + player_color + player.getDisplayName() + "§r] " + message);
					}
					query.close();
					
					return true;
				}
			}
			
			if(cmd.getName().equalsIgnoreCase("switch")) {
				/*
				 * SWITCH AN ADMIN TO RP OR A RP ADMIN TO ADMIN
				 */
				if(customPlayer.isAdmin()) {
					if(player.isOp()) {
						player.setOp(false);
						player.setGameMode(GameMode.SURVIVAL);
						
						String team_str = customPlayer.getStringJob("fr");
						team_str = RoleCraft.firstLetterToUpperCase(team_str);
						System.out.println(team_str);
						team.move(player, team_str);

						player.sendMessage("Vous n'êtes plus OP");
					} else {
						player.setOp(true);
						player.setGameMode(GameMode.CREATIVE);

						System.out.println(new TeamManager(plugin, "Admin"));
						team.move(player, "Admin");
						
						player.sendMessage("Vous êtes OP");
					}
				} else {
					player.sendMessage("§4Vous ne pouvez pas exécuter cette commande :");
					player.sendMessage("§6Vous n'avez pas les droits administrateurs de ce serveur.");
				}
			} else if(cmd.getName().equalsIgnoreCase("mp")) {
				/*
				 * SEND A PRIVATE MESSAGE TO AN OTHER PLAYER
				 */
				if(args.length <= 1) {
					player.sendMessage("§4Il manque des arguments");
					return false;
				}
				
				String addressees_str = args[0];
				Player addressees = null;
				
				for(OfflinePlayer a : plugin.getServer().getOfflinePlayers()) {
					if(a instanceof Player) {
						Player p = (Player) a;
						if(p.getDisplayName().equalsIgnoreCase(addressees_str)) addressees = p;
					}
				}
				if(addressees == null) {
					player.sendMessage("§4Nom du joueur non valide");
					return false;
				}
				
				StringBuilder message = new StringBuilder();
				for(int i = 1; i < args.length; i++) {
					message.append(args[i] + " ");
				}
				
				String sender_color = customPlayer.getTeam().getColor();
				String addressees_color = new CustomPlayer(addressees, plugin).getTeam().getColor();
				
				player.sendMessage("[§" + sender_color + player.getDisplayName() + "§r] -> [§" + addressees_color + addressees.getDisplayName() + "§r] " + message);
				addressees.sendMessage("[§" + sender_color + player.getDisplayName() + "§r] " + message);
			} else if(cmd.getName().equalsIgnoreCase("admin")){
				/*
				 * SEND A MESSAGE TO ALL OF PLAYERS IN THE ADMIN TEAM
				 */
				if(args.length < 1) {
					player.sendMessage("§4Il manque un argument.");
					return false;
				}
				
				StringBuilder message = new StringBuilder();
				for(int i = 0; i < args.length; i++) {
					message.append(args[i] + " ");
				}

				String player_color = team.getColor();
				
				PreparedStatement query = plugin.getDB().getConnection().prepareStatement("SELECT uuid FROM players WHERE admin=?");
				query.setByte(1, (byte) 1);
				ResultSet result = query.executeQuery();
				while(result.next()) {
					String uuid = result.getString("uuid");
					Player addressees = plugin.getServer().getPlayer(uuid);
					addressees.sendMessage("(A tous les admins) [§" + player_color + player.getDisplayName() + "§r] " + message.toString());
				}
				query.close();
				
				player.sendMessage("(A tous les admins) [§" + player_color + player.getDisplayName() + "§r] " + message.toString());
			} else if(cmd.getName().equalsIgnoreCase("level")) {
				player.sendMessage("Votre niveau actuel s'élève à §a" + customPlayer.getLevel()); // PRINT PLAYER'S LVL
			} else if(cmd.getName().equalsIgnoreCase("invite")) {
				/*
				 * INVITE A PLAYER TO THE DISCORD SERVER
				 */
				if(args.length != 1) {
					player.sendMessage("§4La commande n'est pas valide");
					return false;
				}
				
				Player addressees = plugin.getServer().getPlayer(args[0]);
				if(addressees == null) {
					player.sendMessage("§4Nom du joueur non valide.");
					return false;
				}
				if(!addressees.isOnline()) {
					player.sendMessage("§4Le joueur n'est pas en ligne pour le moment.");
					player.sendMessage("§4Attendez qu'il se reconnecte avant de l'inviter à nouveau.");
					return false;
				}
				
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + addressees.getDisplayName() + " [\"\",{\"text\":\"" + player.getDisplayName() + "\",\"color\":\"green\"},{\"text\":\" vous a invité à rejoindre le Serveur Discord\\n\"},{\"text\":\"Cliquez ICI!\",\"underlined\":true,\"color\":\"dark_purple\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://discord.gg/mSGVzjJ\"}}]");
			} else if(cmd.getName().equalsIgnoreCase("ginfo")) {
				/*
				 * SHOW INFORMATION ABOUT GROUNDS
				 */
				Location coo = player.getLocation();
				if(inGround(player, coo)) {
					player.sendMessage("Vous êtes dans votre propriété.");
				} else if(getPlayerOwner(coo) != null) {
					player.sendMessage("Vous êtes chez §a" + getPlayerOwner(coo).getDisplayName());
				} else if(inMap(coo)) {
					player.sendMessage("Vous êtes dans la zone protégée.");
				} else {
					player.sendMessage("Vous êtes hors des zones protégées");
				}
			} else if(cmd.getName().equalsIgnoreCase("sell")) {
				ItemStack paper = new ItemStack(Material.PAPER);
				ItemMeta paper_meta = paper.getItemMeta();
				paper_meta.setDisplayName("§9Que voulez-vous faire?");
				paper_meta.setLore(Arrays.asList("Indiquez ce qui constitue", "votre vente."));
				paper_meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 10, true);
				paper_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				paper_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				paper.setItemMeta(paper_meta);
				player.getInventory().addItem(paper);
			} else if(cmd.getName().equalsIgnoreCase("confirm")) {
				/*
				 * CONFIRM A SELL BY AN OTHER PLAYER
				 */
				if(args.length != 4) {
					player.sendMessage("§4La commande n'est pas valide... Vous n'avez peut être pas les droits de l'exécuter.");
					return false;
				}
				if(!args[3].equalsIgnoreCase("TM6j6W")) {
					player.sendMessage("§4La commande n'est pas valide... Vous n'avez peut être pas les droits de l'exécuter.");
					return false;
				}
				
				String type = args[0];
				int value = Integer.parseInt(args[2]);
				
				Player addressees = player;
				Money add_account = new Money(addressees);
				player = main.getServer().getPlayer(args[1]);
				Money p_account = new Money(player);
				
				if(!player.isOnline()) {
					addressees.sendMessage("§4Le joueur n'est pas en ligne pour le moment.");
					addressees.sendMessage("§4Attendez qu'il se reconnecte avant de confirmer la transaction.");
					return false;
				}
				
				if(!addressees.isOp() && !add_account.has(value)) {
					addressees.sendMessage("§4Vous n'avez pas les rubis nécessaires pour accepter cette transaction...");
					addressees.sendMessage("§4Il vous faut §a" + value + " rubis§4.");
					return false;
				}

				// Get sender's ground
				Integer sender_x1 = null;
				Integer sender_y1 = null;
				Integer sender_z1 = null;
				Integer sender_x2 = null;
				Integer sender_y2 = null;
				Integer sender_z2 = null;
				if(player.isOp()) {
					sender_x1 = main.getAdmin_x1().getScore(player);
					sender_y1 = main.getAdmin_y1().getScore(player);
					sender_z1 = main.getAdmin_z1().getScore(player);
					sender_x2 = main.getAdmin_x2().getScore(player);
					sender_y2 = main.getAdmin_y2().getScore(player);
					sender_z2 = main.getAdmin_z2().getScore(player);
				} else {
					if(type.equalsIgnoreCase("ground")) {
						sender_x1 = main.getGround_x1().getScore(player);
						sender_y1 = main.getGround_y1().getScore(player);
						sender_z1 = main.getGround_z1().getScore(player);
						sender_x2 = main.getGround_x2().getScore(player);
						sender_y2 = main.getGround_y2().getScore(player);
						sender_z2 = main.getGround_z2().getScore(player);
					} else if(type.equalsIgnoreCase("shop")){
						sender_x1 = main.getShop_x1().getScore(player);
						sender_y1 = main.getShop_y1().getScore(player);
						sender_z1 = main.getShop_z1().getScore(player);
						sender_x2 = main.getShop_x2().getScore(player);
						sender_y2 = main.getShop_y2().getScore(player);
						sender_z2 = main.getShop_z2().getScore(player);
					} else if(type.equalsIgnoreCase("farm")){
						sender_x1 = main.getFarm_x1().getScore(player);
						sender_y1 = main.getFarm_y1().getScore(player);
						sender_z1 = main.getFarm_z1().getScore(player);
						sender_x2 = main.getFarm_x2().getScore(player);
						sender_y2 = main.getFarm_y2().getScore(player);
						sender_z2 = main.getFarm_z2().getScore(player);
					} else if(type.equalsIgnoreCase("build")){
						sender_x1 = main.getBuilder_x1().getScore(player);
						sender_y1 = main.getBuilder_y1().getScore(player);
						sender_z1 = main.getBuilder_z1().getScore(player);
						sender_x2 = main.getBuilder_x2().getScore(player);
						sender_y2 = main.getBuilder_y2().getScore(player);
						sender_z2 = main.getBuilder_z2().getScore(player);
					}
				}
				if(sender_x1 == 0 && sender_y1 == 0 && sender_z1 == 0 && sender_x2 == 0 && sender_y2 == 0 && sender_z2 == 0) {
					player.sendMessage("§4Vous ne pouvez pas déléger un terrain vide");
					return false;
				}
				
				// Give the sender's ground to addressees
				if(addressees.isOp()){
					main.getAdmin_x1().setScore(addressees, sender_x1);
					main.getAdmin_y1().setScore(addressees, sender_y1);
					main.getAdmin_z1().setScore(addressees, sender_z1);
					main.getAdmin_x2().setScore(addressees, sender_x2);
					main.getAdmin_y2().setScore(addressees, sender_y2);
					main.getAdmin_z2().setScore(addressees, sender_z2);
				} else {
					if(type.equalsIgnoreCase("ground")) {
						main.getGround_x1().setScore(addressees, sender_x1);
						main.getGround_y1().setScore(addressees, sender_y1);
						main.getGround_z1().setScore(addressees, sender_z1);
						main.getGround_x2().setScore(addressees, sender_x2);
						main.getGround_y2().setScore(addressees, sender_y2);
						main.getGround_z2().setScore(addressees, sender_z2);
					} else if(type.equalsIgnoreCase("shop")) {
						main.getShop_x1().setScore(addressees, sender_x1);
						main.getShop_y1().setScore(addressees, sender_y1);
						main.getShop_z1().setScore(addressees, sender_z1);
						main.getShop_x2().setScore(addressees, sender_x2);
						main.getShop_y2().setScore(addressees, sender_y2);
						main.getShop_z2().setScore(addressees, sender_z2);
					} else if(type.equalsIgnoreCase("farm")) {
						main.getFarm_x1().setScore(addressees, sender_x1);
						main.getFarm_y1().setScore(addressees, sender_y1);
						main.getFarm_z1().setScore(addressees, sender_z1);
						main.getFarm_x2().setScore(addressees, sender_x2);
						main.getFarm_y2().setScore(addressees, sender_y2);
						main.getFarm_z2().setScore(addressees, sender_z2);
					} else if(type.equalsIgnoreCase("build")) {
						main.getBuilder_x1().setScore(addressees, sender_x1);
						main.getBuilder_y1().setScore(addressees, sender_y1);
						main.getBuilder_z1().setScore(addressees, sender_z1);
						main.getBuilder_x2().setScore(addressees, sender_x2);
						main.getBuilder_y2().setScore(addressees, sender_y2);
						main.getBuilder_z2().setScore(addressees, sender_z2);
					}
				}
				
				// Remove the sender's ground
				if(player.isOp()){
					main.getAdmin_x1().setScore(player, 0);
					main.getAdmin_y1().setScore(player, 0);
					main.getAdmin_z1().setScore(player, 0);
					main.getAdmin_x2().setScore(player, 0);
					main.getAdmin_y2().setScore(player, 0);
					main.getAdmin_z2().setScore(player, 0);
				} else {
					if(type.equalsIgnoreCase("ground")) {
						main.getGround_x1().setScore(player, 0);
						main.getGround_y1().setScore(player, 0);
						main.getGround_z1().setScore(player, 0);
						main.getGround_x2().setScore(player, 0);
						main.getGround_y2().setScore(player, 0);
						main.getGround_z2().setScore(player, 0);
					} else if(type.equalsIgnoreCase("shop")) {
						main.getShop_x1().setScore(player, 0);
						main.getShop_y1().setScore(player, 0);
						main.getShop_z1().setScore(player, 0);
						main.getShop_x2().setScore(player, 0);
						main.getShop_y2().setScore(player, 0);
						main.getShop_z2().setScore(player, 0);
					} else if(type.equalsIgnoreCase("farm")) {
						main.getFarm_x1().setScore(player, 0);
						main.getFarm_y1().setScore(player, 0);
						main.getFarm_z1().setScore(player, 0);
						main.getFarm_x2().setScore(player, 0);
						main.getFarm_y2().setScore(player, 0);
						main.getFarm_z2().setScore(player, 0);
					} else if(type.equalsIgnoreCase("build")) {
						main.getBuilder_x1().setScore(player, 0);
						main.getBuilder_y1().setScore(player, 0);
						main.getBuilder_z1().setScore(player, 0);
						main.getBuilder_x2().setScore(player, 0);
						main.getBuilder_y2().setScore(player, 0);
						main.getBuilder_z2().setScore(player, 0);
					}
				}

				addressees.playSound(addressees.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				addressees.playSound(addressees.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
				
				add_account.remove(value);
				p_account.give(value);

				Mission.removeBook(addressees, "Demande de confirmation");
				
				player.sendMessage("Votre terrain a été délégué §e" + addressees.getDisplayName());
				addressees.sendMessage("§e" + player.getDisplayName() + "§r vient de vous déléger son terrain");
				
				boolean isBuilder = main.getJobs().getScore(player) == 3;
				// Builder mission ("Premier pas")
				if(isBuilder && !player.isOp()) {
					if(Mission.hasBook(player, "Premiers pas") && value <= 500) {
						Mission.removeBook(player, "Premiers pas");

						ItemStack[] rewards = {
								new ItemStack(Material.STONECUTTER),
								new ItemStack(Material.STRIPPED_OAK_LOG, 32),
								new ItemStack(Material.STRIPPED_SPRUCE_LOG, 32),
								new ItemStack(Material.STRIPPED_BIRCH_LOG, 32),
						};
						for(ItemStack reward : rewards) {
							player.getInventory().addItem(reward);
						}
						main.getMission().setScore(player, main.getMission().getScore(player) + 1);
						
						player.sendMessage("Bravo! Vous venez de réussir la mission \"§aPremiers pas§r\".");
						player.sendMessage("Pour vous récompenser, vous gagnez §aun stonecutter§r et §aun stock de bois§r.");
					}
				}
				
				// Builder XP
				if(!isBuilder || player.isOp()) return false;
				int score = main.getScore().getScore(player);
				int add = 0;
				
				if(value <= 100) add = 100;
				else if(value > 100 && value <= 500) add = 200;
				else if(value > 500 && value <= 1000) add = 500;
				else if(value > 1000) add = 750;
				
				main.getScore().setScore(player, score + add);
				player.sendMessage("Vous venez de vendre une proprité pour §b" + value + "§r : §a+" + add + "pts§r !");
				player.sendMessage("Votre score s'élève à présent à §d" + main.getScore().getScore(player));
			}
		}
		
		return false;
	}
	
	/**
	 * Return true if the coo are in the protected map
	 * @param coo Location to analyse
	 * @return boolean
	 */
	public boolean inMap(Location coo) {
		int x = coo.getBlockX();
		int y = coo.getBlockY();
		int z = coo.getBlockZ();
		Environment world = coo.getWorld().getEnvironment();
		
		if(world.equals(Environment.NORMAL)) {
			for(int i = 1; i <= main.getConfig().getInt("nb_maps"); i++) {
				main.getConfig().getString("map." + i + ".x1");
			}
			
			for(String map : main.getConfig().getConfigurationSection("map").getKeys(false)) {
				Integer x1 = main.getConfig().getConfigurationSection("map").getInt(map + ".x1");
				Integer z1 = main.getConfig().getConfigurationSection("map").getInt(map + ".z1");
				Integer x2 = main.getConfig().getConfigurationSection("map").getInt(map + ".x2");
				Integer z2 = main.getConfig().getConfigurationSection("map").getInt(map + ".z2");
				
				Integer xMin = null;
				Integer zMin = null;
				Integer xMax = null;
				Integer zMax = null;
				
				if(x1 > x2) {xMin = x2; xMax = x1;}
				else {xMin = x1; xMax = x2;}
				if(z1 > z2) {zMin = z2; zMax = z1;}
				else {zMin = z1; zMax = z2;}

				if(xMin <= x && x <= xMax && zMin <= z && z <= zMax && y > 50) return true;
			}
		} else if(world.equals(Environment.NETHER)) {
			main.getConfig().getString("nether.x1");
			Integer x1 = main.getConfig().getInt("nether.x1");
			Integer z1 = main.getConfig().getInt("nether.z1");
			Integer x2 = main.getConfig().getInt("nether.x2");
			Integer z2 = main.getConfig().getInt("nether.z2");
			
			Integer xMin = null;
			Integer zMin = null;
			Integer xMax = null;
			Integer zMax = null;
			
			if(x1 > x2) {xMin = x2; xMax = x1;}
			else {xMin = x1; xMax = x2;}
			if(z1 > z2) {zMin = z2; zMax = z1;}
			else {zMin = z1; zMax = z2;}

			if(xMin <= x && x <= xMax && zMin <= z && z <= zMax) return true;
		}
	}

}
