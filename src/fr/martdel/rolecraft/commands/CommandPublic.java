package fr.martdel.rolecraft.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.martdel.rolecraft.*;
import fr.martdel.rolecraft.listeners.SellListener;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.martdel.rolecraft.database.DatabaseManager;
import org.bukkit.inventory.meta.ItemMeta;

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

					try {
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
					} catch (SQLException e) {
						DatabaseManager.error(e);
					}
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
						team.move(player, team_str);

						player.sendMessage("Vous n'êtes plus OP");
					} else {
						player.setOp(true);
						player.setGameMode(GameMode.CREATIVE);
						team.move(player, "Admin");
						player.sendMessage("Vous êtes OP");
					}
					customPlayer.updateScoreboard();
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
				
				try {
					PreparedStatement query = plugin.getDB().getConnection().prepareStatement("SELECT uuid FROM players WHERE admin=?");
					query.setByte(1, (byte) 1);
					ResultSet result = query.executeQuery();
					while(result.next()) {
						String uuid = result.getString("uuid");
						Player addressees = plugin.getServer().getPlayer(uuid);
						addressees.sendMessage("(A tous les admins) [§" + player_color + player.getDisplayName() + "§r] " + message.toString());
					}
					query.close();
				} catch (SQLException e) {
					DatabaseManager.error(e);
				}
				
				player.sendMessage("(A tous les admins) [§" + player_color + player.getDisplayName() + "§r] " + message.toString());
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
				LocationInMap ground = LocationInMap.getPlayerPlace(plugin, player);
				player.sendMessage("Vous êtes dans " + ground.getDescription() + ".");
			} else if(cmd.getName().equalsIgnoreCase("sell")) {
				/*
				 * SELL COMMAND
				 */
				if(player.getInventory().firstEmpty() == -1){
					player.sendMessage("§4Vous devez posséder un slot libre dans votre inventaire pour cette opération.");
					return false;
				}
				ItemStack paper = CustomItems.SELL_PAPER.getItem();
				player.getInventory().addItem(paper);
				player.openInventory(GUI.createSellStep1());
			} else if(cmd.getName().equalsIgnoreCase("confirm")) {
				/*
				 * CONFIRM A SELL BY AN OTHER PLAYER
				 */
				// /confirm type name pseudo value key
				if(args.length != 5 || !args[4].equalsIgnoreCase(Credentials.CONFIRM_KEY)) {
					player.sendMessage("§4La commande n'est pas valide... Vous n'avez peut être pas les droits de l'exécuter.");
					return false;
				}
				
				String type = args[0];
				String name = args[1];
				int value = Integer.parseInt(args[3]);
				
				// Get emitter
				Player emitter = plugin.getServer().getPlayer(args[2]);
				if(emitter == null) {
					player.sendMessage("§4L'émetteur de la demande de paiement n'a pas été trouvé...");
					return false;
				}
				if(!emitter.isOnline()) {
					player.sendMessage("§4L'émetteur n'est pas en ligne pour le moment.");
					player.sendMessage("§4Attendez qu'il se reconnecte avant de confirmer la transaction.");
					return false;
				}
				CustomPlayer customEmitter = new CustomPlayer(emitter, plugin).loadData();
				Wallet account1 = customEmitter.getWallet();
				
				// Get addressees
				Player addressees = player;
				CustomPlayer customAddressees = customPlayer;
				Wallet account2 = customAddressees.getWallet();
				if(!addressees.isOp() && !account2.has(value)) {
					addressees.sendMessage("§4Vous n'avez pas les rubis nécessaires pour accepter cette transaction...");
					addressees.sendMessage("§4Il vous faut §a" + value + " rubis§4.");
					return false;
				}				

				// Manage grounds
				Map<String, Integer> emitter_ground = null;
				if(emitter.isOp()) {
					emitter_ground = customEmitter.getAdmin_ground();
					customEmitter.setAdminGround(null);
				} else {
					switch (type) {
						case "house":
							emitter_ground = customEmitter.getHouse();
							customEmitter.setHouse(null);
							break;
						case "shop":
							emitter_ground = customEmitter.getShop();
							customEmitter.setShop(null);
							break;
						case "farm":
							Map<String, Map<String, Integer>> farms = customEmitter.getFarms();
							if(!farms.containsKey(name)) {
								addressees.sendMessage("§4Le terrain demandé n'éxiste plus.");
								return false;
							}
							emitter_ground = farms.get(name);
							customEmitter.removeFarm(name);
							break;
						case "build":
							Map<String, Map<String, Integer>> builds = customEmitter.getBuilds();
							if(!builds.containsKey(name)) {
								addressees.sendMessage("§4Le terrain demandé n'éxiste plus.");
								return false;
							}
							emitter_ground = builds.get(name);
							customEmitter.removeBuild(name);
							break;
						default:
							System.out.println("Le switch marche pas frère");
							break;
					}
				}
				if(emitter_ground == null) {
					emitter.sendMessage("§4Vous ne pouvez pas déléger un terrain vide.");
					addressees.sendMessage("§4Aucun terrain n'a été trouvé...");
					return false;
				}
				
				// Send the emitter ground to the addressees
				if(addressees.isOp()) customAddressees.setAdminGround(emitter_ground);
				else {
					switch (type) {
						case "house": customAddressees.setHouse(emitter_ground); break;
						case "shop": customAddressees.setShop(emitter_ground); break;
						case "farm": customAddressees.addFarm("Ferme sans nom", emitter_ground); break;
						case "build": customAddressees.addBuild("Terrain de build sans nom", emitter_ground); break;
						default:
							System.out.println("Le switch marche pas frère");
							break;
					}
				}

				// Sound effects
				addressees.playSound(addressees.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				addressees.playSound(addressees.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
				
				// Manage money
				account2.remove(value);
				account1.give(value);

				customAddressees.removeBook("Demande de confirmation");
				
				player.sendMessage("Votre terrain a été délégué §e" + addressees.getDisplayName());
				addressees.sendMessage("§e" + player.getDisplayName() + "§r vient de vous déléger son terrain");
				
				// Builder XP
				if(customAddressees.getJob() != 3 || player.isOp()) return false;
				int score = customAddressees.getScore();
				int add = 0;
				
				if(value <= 100) add = 100;
				else if(value > 100 && value <= 500) add = 200;
				else if(value > 500 && value <= 1000) add = 500;
				else if(value > 1000) add = 750;
				
				customAddressees.setScore(score + add);
				customAddressees.save();
			}
		}
		
		return false;
	}

}
