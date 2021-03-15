package fr.martdel.rolecraft.commands;

import fr.martdel.rolecraft.cinematics.Cinematic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import fr.martdel.rolecraft.CustomItems;
import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.Wallet;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.List;
import java.util.Map;

public class CommandAdmin implements CommandExecutor {
	
	private RoleCraft plugin;

	public CommandAdmin(RoleCraft roleCraft) {
		this.plugin = roleCraft;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			CustomPlayer customPlayer = new CustomPlayer(player, plugin);
			
			if(cmd.getName().equalsIgnoreCase("delimiter")) {
				/*
				 * GIVE THE GROUND DELIMITER STICK ("délimiteur de terrain")
				 */
				ItemStack delimiter = CustomItems.DELIMITER.getItem();
				player.getInventory().addItem(delimiter);
				player.sendMessage("Don d'un délimiteur de terrain");
			} else if(cmd.getName().equalsIgnoreCase("path")) {
				/*
				 * GIVE THE PATH GENERATOR ITEM (WorldEdit)
				 */
				if(player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
					ItemStack arrow = new ItemStack(Material.ARROW);
					player.getInventory().setItemInMainHand(arrow);
					player.performCommand("/brush sphere 54%grass_block,14%dead_bubble_coral_block,12%gravel,12%dead_brain_coral_block,8%andesite");
					player.performCommand("mask 2");
				} else {
					player.sendMessage("§4Vous ne devez rien tenir dans votre main pour obtenir cet item.");
				}
			} else if(cmd.getName().equalsIgnoreCase("spawn")) {
				/*
				 * TP THE PLAYER TO THE SPAWN POINT (CENTER OF THE VILLAGE)
				 */
				double x = RoleCraft.config.getDouble("spawn.x");
				int y = RoleCraft.config.getInt("spawn.y");
				double z = RoleCraft.config.getDouble("spawn.z");
				Location spawn = new Location(player.getWorld(), x, y, z);
				
				player.teleport(spawn);
				player.sendMessage("Vous avez été tp au village.");
			} else if(cmd.getName().equalsIgnoreCase("rubis")) {
				/*
				 * GIVE RUBY TO THE PLAYER
				 */
				Integer nb = 1;
				if(args.length == 1) {
					try {
						nb = Integer.parseInt(args[0]);
					} catch (Exception e) {
						nb = 1;
					}
				}
				Wallet playerWallet = customPlayer.getWallet();
				try{ playerWallet.give(nb); }
				catch (Exception e){ player.sendMessage(e.getMessage()); }
			} else if(cmd.getName().equalsIgnoreCase("cinematic")){
				/*
				 * MANAGE CINEMATIC
				 */
				if (args.length < 1) {
					player.sendMessage("§4Il manque un argument...");
					return false;
				}
				Map<String, Cinematic> cinematic_list = plugin.getCinematicList();
				switch (args[0]){
					case "rec":
						if(plugin.getRecording().getScore(player) == 1) return false;
						BukkitScheduler scheduler = plugin.getServer().getScheduler();
						scheduler.runTaskLater(plugin, new Runnable() {
							private int t = 3;
							@Override
							public void run() {
								player.sendMessage("L'enregistrement commence dans §a" + t);
								t--;
								if (t > -1) scheduler.runTaskLater(plugin, this, 20);
								else {
									player.sendMessage("L'enregistrement §aa commencé");
									Cinematic.record(player, plugin, null);
								}
							}
						}, 20);
						break;
					case "stop":
						if(plugin.getRecording().getScore(player) == 0) return false;
						plugin.getRecording().setScore(player, 0);
						player.sendMessage("L'enregistrement §ca été arrété");
						break;
					case "save":
						if(plugin.getRecording().getScore(player) == 1) return false;
						if(args.length <= 1){
							player.sendMessage("§4Veuillez préciser le nom de la cinématique !");
							return false;
						}
						// Play last cinematic
						if(cinematic_list.containsKey("last")){
							cinematic_list.get("last").save(args[1], plugin);
							player.sendMessage("La cinématique a été enregistré sous le nom : §a" + args[1]);
						} else {
							player.sendMessage("§4Aucune cinématique n'a été trouvée...");
						}
						break;
					case "play":
						if(plugin.getRecording().getScore(player) == 1) return false;
						String search;
						if(args.length == 1) search = "last";
						else search = args[1];
						// Play last cinematic
						if(cinematic_list.containsKey(search)){
							plugin.getCinematicList().get(search).play(player, plugin, null);
						} else {
							player.sendMessage("§4Aucune cinématique n'a été trouvée...");
						}
						break;
					case "crop":
						if(args.length != 3){
							player.sendMessage("§4Il manque un/des argument(s)...");
							return false;
						}
						if(cinematic_list.containsKey(args[1]) && checkInt(args[2])){
							Cinematic to_update = cinematic_list.get(args[1]);
							int time = Integer.parseInt(args[2]);
							List<Location> locations = to_update.getData();
							int size = locations.size();
							for (int t = size; t > size - time; t--){
								locations.remove(locations.size() - 1);
							}
							to_update.setData(locations);
							to_update.save(args[1], plugin);
							player.sendMessage("La cinématique a été §arognée de " + args[2] + " ticks§r à la fin");
						} else {
							player.sendMessage("§4Arguments invalides...");
						}
						break;
					case "remove":
						if(args.length != 2){
							player.sendMessage("§4Veuillez préciser le nom de la cinématique...");
							return false;
						}
						if(cinematic_list.containsKey(args[1])){
							cinematic_list.get(args[1]).delete(args[1], plugin);
							player.sendMessage("La cinématique " + args[1] + " §ca été supprimée");
						} else {
							player.sendMessage("§4Aucune cinématique n'a été trouvée...");
						}
						break;
					default:
						player.sendMessage("§4Argument incorrect...");
						break;
				}
			}
		}
		
		return false;
	}

	private boolean checkInt(String to_check){
		try {
			Integer.parseInt(to_check);
		} catch (Exception e){
			return false;
		}
		return true;
	}

}
