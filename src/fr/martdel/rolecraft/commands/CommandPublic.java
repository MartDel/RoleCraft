package fr.martdel.rolecraft.commands;

import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
					for(int i = 0; i < args.length; i++) {
						message.append(args[i] + " ");
					}

					List<Map<?, ?>> jobs_list = RoleCraft.config.getMapList("jobs");
					@SuppressWarnings("unchecked")
					Map<String, String> job = (Map<String, String>) jobs_list.get(i);
					String fr = job.get("fr") + "s";
					String player_color = customPlayer.getTeam().getColor();
					
					TeamManager to_team = new TeamManager(plugin, RoleCraft.firstLetterToUpperCase(job.get("fr")));
					
					for(OfflinePlayer p : plugin.getServer().getOfflinePlayers()) {
						if(p instanceof Player) {
							Player addressees = (Player) p;
							
							if(to_team.isIn(addressees)) {
								addressees.sendMessage("(A tous les " + fr + ") [§" + player_color + player.getDisplayName() + "§r] " + message);
							}
						}
					}
					
					player.sendMessage("(A tous les " + fr + ") [§" + player_color + player.getDisplayName() + "§r] " + message);
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
				if(args.length > 1) {
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
				} else {
					player.sendMessage("§4Il manque des arguments");
				}
			} else if(cmd.getName().equalsIgnoreCase("farmer")){
				/*
				 * SEND A MESSAGE TO ALL OF PLAYERS IN THE FARMER TEAM
				 */
				if(args.length < 1) {
					player.sendMessage("§4Il manque un argument.");
					return false;
				}

				StringBuilder message = new StringBuilder();
				for(int i = 0; i < args.length; i++) {
					message.append(args[i] + " ");
				}
				teamMsg(0, message.toString(), player);
			} else if(cmd.getName().equalsIgnoreCase("miner")){
				/*
				 * SEND A MESSAGE TO ALL OF PLAYERS IN THE MINER TEAM
				 */
				if(args.length < 1) {
					player.sendMessage("§4Il manque un argument.");
					return false;
				}

				StringBuilder message = new StringBuilder();
				for(int i = 0; i < args.length; i++) {
					message.append(args[i] + " ");
				}
				teamMsg(1, message.toString(), player);
			} else if(cmd.getName().equalsIgnoreCase("explorer")){
				/*
				 * SEND A MESSAGE TO ALL OF PLAYERS IN THE EXPLORER TEAM
				 */
				if(args.length < 1) {
					player.sendMessage("§4Il manque un argument.");
					return false;
				}

				StringBuilder message = new StringBuilder();
				for(int i = 0; i < args.length; i++) {
					message.append(args[i] + " ");
				}
				teamMsg(2, message.toString(), player);
			} else if(cmd.getName().equalsIgnoreCase("builder")){
				/*
				 * SEND A MESSAGE TO ALL OF PLAYERS IN THE BUILDER TEAM
				 */
				if(args.length < 1) {
					player.sendMessage("§4Il manque un argument.");
					return false;
				}

				StringBuilder message = new StringBuilder();
				for(int i = 0; i < args.length; i++) {
					message.append(args[i] + " ");
				}
				teamMsg(3, message.toString(), player);
			} else if(cmd.getName().equalsIgnoreCase("admin")){
				/*
				 * SEND A MESSAGE TO ALL OF PLAYERS IN THE ADMIN TEAM
				 */
				if(args.length < 1) {
					player.sendMessage("§4Il manque un argument.");
					return false;
				}
				
				int player_job = main.getJobs().getScore(player);
				String player_color = main.getConfig().getString("jobs." + player_job + ".color");

				StringBuilder message = new StringBuilder();
				for(int i = 0; i < args.length; i++) {
					message.append(args[i] + " ");
				}
				for(OfflinePlayer p : main.getServer().getOfflinePlayers()) {
					if((p instanceof Player) && p.isOnline()) {
						Player c_player = (Player) p;
						if(main.getSbAdmins().getScore(c_player) == 1) c_player.sendMessage("(A tous les admins) [§" + player_color + player.getDisplayName() + "§r] " + message.toString());
					}
				}
				player.sendMessage("(A tous les admins) [§" + player_color + player.getDisplayName() + "§r] " + message.toString());
			} else if(cmd.getName().equalsIgnoreCase("hide")) {
				/*
				 * HIDE/PRINT BROADCAST MESSAGES
				 */
				int hide = main.getHide().getScore(player);
				if(hide == 0) {
					main.getHide().setScore(player, 1);
					player.sendMessage("L'affichage des annonces est désactivé");
				} else {
					main.getHide().setScore(player, 0);
					player.sendMessage("L'affichage des annonces est activé");
				}
			} else if(cmd.getName().equalsIgnoreCase("switch")) {
				/*
				 * SWITCH AN ADMIN TO RP OR A RP ADMIN TO ADMIN
				 */
				if(main.getSbAdmins().getScore(player) == 1) {
					if(player.isOp()) {
						player.setOp(false);
						player.setGameMode(GameMode.SURVIVAL);
						player.sendMessage("Vous n'êtes plus OP");
						if(main.getSpe().getScore(player) == 0) {
							String job = main.getConfig().getString("jobs." + main.getJobs().getScore(player) + ".en");
							main.getAdmins().move(player, main.getTeam(job));
						} else {
							String spe = main.getConfig().getString("spe." + main.getJobs().getScore(player) + ".en");
							main.getAdmins().move(player, main.getTeam(spe));
						}
					} else {
						player.setOp(true);
						player.setGameMode(GameMode.CREATIVE);
						player.sendMessage("Vous êtes OP");
						main.removeOfAllTeams(player);
						main.getAdmins().add(player);
					}
				} else {
					player.sendMessage("§4Vous ne pouvez pas exécuter cette commande :");
					player.sendMessage("§6Vous n'avez pas les droits administrateurs de ce serveur.");
				}
			} else if(cmd.getName().equalsIgnoreCase("level")) player.sendMessage("Votre niveau actuel s'élève à §a" + main.getLVL().getScore(player)); // PRINT PLAYER'S LVL
			else if(cmd.getName().equalsIgnoreCase("invite")) {
				/*
				 * INVITE A PLAYER TO THE DISCORD SERVER
				 */
				if(args.length != 1) {
					player.sendMessage("§4La commande n'est pas valide");
					return false;
				}
				
				Player addressees = null;
				String addressees_str = args[0];
				
				for(OfflinePlayer a : main.getServer().getOfflinePlayers()) {
					if(a instanceof Player) {
						Player p = (Player) a;
						if(p.getDisplayName().equalsIgnoreCase(addressees_str)) addressees = p;
					}
				}
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
			} else if(cmd.getName().equalsIgnoreCase("delegate")) {
				/*
				 * SELL A GROUND TO AN OTHER PLAYER
				 */
				if(args.length == 3) {
					String type = args[0];
					String type_fr = null;
					String addressees_str = args[1];
					Player addressees = null;
					String price_str = args[2];
					Integer price = null;
					
					if(type.equalsIgnoreCase("shop")) {
						type_fr = "Magasin";
					} else if(type.equalsIgnoreCase("ground")){
						type_fr = "Maison";
					} else if(type.equalsIgnoreCase("farm")){
						type_fr = "Ferme";
					} else if(type.equalsIgnoreCase("build")){
						type_fr = "Construction";
					} else {
						player.sendMessage("§4Type de transaction invalide.");
						return false;
					}
					
					
					for(OfflinePlayer a : main.getServer().getOfflinePlayers()) {
						if(a instanceof Player) {
							Player p = (Player) a;
							if(p.getDisplayName().equalsIgnoreCase(addressees_str)) addressees = p;
						}
					}
					if(addressees == null) {
						player.sendMessage("§4Nom du joueur non valide.");
						return false;
					}
					if(addressees.equals(player)) {
						player.sendMessage("§4Vous ne pouvez pas délégué un terrain à vous-même.");
						return false;
					}
					if(type.equalsIgnoreCase("farm") && main.getJobs().getScore(addressees) != 0) {
						player.sendMessage("§4Vous ne pouvez pas vendre une ferme à un joueur qui n'est pas fermier.");
						return false;
					}
					if(type.equalsIgnoreCase("build") && main.getJobs().getScore(addressees) != 3 && !addressees.isOp()) {
						player.sendMessage("§4Vous ne pouvez pas vendre un terrain de construction à un joueur qui n'est pas builder.");
						return false;
					}
					if(!addressees.isOnline()) {
						player.sendMessage("§4Le joueur n'est pas en ligne pour le moment.");
						player.sendMessage("§4Attendez qu'il se reconnecte avant de demander la transaction.");
						return false;
					}
					
					try {
						price = Integer.parseInt(price_str);
					} catch (Exception e) {
						player.sendMessage("§4Le prix indiqué n'est pas valide.");
						return false;
					}
					
					// Give confirmation book
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + addressees.getDisplayName() + " written_book{pages:['[\"\",{\"text\":\"Demande de \"},{\"text\":\"CONFIRMATION\",\"color\":\"red\"},{\"text\":\" :\\\\n\\\\nExpéditeur : \",\"color\":\"reset\"},{\"selector\":\"" + player.getDisplayName() + "\"},{\"text\":\"\\\\n\\\\nType : \"},{\"text\":\"" + type_fr + "\",\"italic\":true},{\"text\":\"\\\\n\\\\nMontant : \",\"color\":\"reset\"},{\"text\":\"" + price + "\",\"italic\":true,\"color\":\"green\"},{\"text\":\" rubys\",\"color\":\"green\"},{\"text\":\"\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"Cliquez ICI\",\"underlined\":true,\"color\":\"light_purple\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/confirm " + type + " " + player.getDisplayName() + " " + price + " TM6j6W\"}},{\"text\":\" pour confirmer!\",\"color\":\"reset\"}]'],title:\"Demande de confirmation\",author:MartDel,display:{Lore:[\"Détails de la transaction et confirmation.\"]}}");
					/*if(!player.isOp()) {
						System.out.println("give non op");
						player.setOp(true);
						player.performCommand("give " + addressees.getDisplayName() + " written_book{pages:['[\"\",{\"text\":\"Demande de \"},{\"text\":\"CONFIRMATION\",\"color\":\"red\"},{\"text\":\" :\\\\n\\\\nExpéditeur : \",\"color\":\"reset\"},{\"selector\":\"" + player.getDisplayName() + "\"},{\"text\":\"\\\\n\\\\nType : \"},{\"text\":\"" + type_fr + "\",\"italic\":true},{\"text\":\"\\\\n\\\\nMontant : \",\"color\":\"reset\"},{\"text\":\"" + price + "\",\"italic\":true,\"color\":\"green\"},{\"text\":\" rubys\",\"color\":\"green\"},{\"text\":\"\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"Cliquez ICI\",\"underlined\":true,\"color\":\"light_purple\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/confirm " + type + " " + player.getDisplayName() + " " + price + " TM6j6W\"}},{\"text\":\" pour confirmer!\",\"color\":\"reset\"}]'],title:\"Demande de confirmation\",author:MartDel,display:{Lore:[\"Détails de la transaction et confirmation.\"]}}");
						player.setOp(false);
					} else {
						player.performCommand("give " + addressees.getDisplayName() + " written_book{pages:['[\"\",{\"text\":\"Demande de \"},{\"text\":\"CONFIRMATION\",\"color\":\"red\"},{\"text\":\" :\\\\n\\\\nExpéditeur : \",\"color\":\"reset\"},{\"selector\":\"" + player.getDisplayName() + "\"},{\"text\":\"\\\\n\\\\nType : \"},{\"text\":\"" + type_fr + "\",\"italic\":true},{\"text\":\"\\\\n\\\\nMontant : \",\"color\":\"reset\"},{\"text\":\"" + price + "\",\"italic\":true,\"color\":\"green\"},{\"text\":\" rubys\",\"color\":\"green\"},{\"text\":\"\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"Cliquez ICI\",\"underlined\":true,\"color\":\"light_purple\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/confirm " + type + " " + player.getDisplayName() + " " + price + " TM6j6W\"}},{\"text\":\" pour confirmer!\",\"color\":\"reset\"}]'],title:\"Demande de confirmation\",author:MartDel,display:{Lore:[\"Détails de la transaction et confirmation.\"]}}");
						System.out.println("give op");
					}*/
					
					addressees.sendMessage("§a" + player.getDisplayName() + "§6 veut te vendre une propriété.");
					addressees.sendMessage("§6Un livre t'a été donné pour que tu puisses confirmer la transaction.");
					addressees.sendMessage("§6Il attend ta confirmation!");
					player.sendMessage("§6La demande de confirmation a été envoyé à §a" + addressees.getDisplayName());
					player.sendMessage("§6Nous attendons sa réponse avec impatience!");
				} else {
					player.sendMessage("§4Il manque un argument.");
				}
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
	 * Send a message to all of team players
	 * @param team ID of the job -> get team name
	 * @param msg Message to send
	 * @param sender Player who send the message
	 */
	public void teamMsg(int team, String msg, Player sender) {
		String fr = main.getConfig().getString("jobs." + team + ".fr") + "s";
		
		int player_job = main.getJobs().getScore(sender);
		String player_color = main.getConfig().getString("jobs." + player_job + ".color");
		TeamManager player_team = main.getPlayerTeam(sender);
		
		if(player_team.getName().equalsIgnoreCase("Admin")) {
			player_color = "9";
		} else if(player_team.getName().equalsIgnoreCase("Nouveau") || player_team.getName().equalsIgnoreCase("Petit nouveau")) {
			player_color = "b";
		}
		
		for(OfflinePlayer p : main.getServer().getOfflinePlayers()) {
			if(p instanceof Player) {
				Player addressees = (Player) p;
				if(main.getJobs().getScore(addressees) == team) {
					addressees.sendMessage("(A tous les " + fr + ") [§" + player_color + sender.getDisplayName() + "§r] " + msg);
				}
			}
		}
		
		sender.sendMessage("(A tous les " + fr + ") [§" + player_color + sender.getDisplayName() + "§r] " + msg);
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
		
		return false;
	}

}
