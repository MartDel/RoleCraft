package fr.martdel.rolecraft.commands;

import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.Wallet;

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
				ItemStack item1 = new ItemStack(Material.STICK);
				ItemMeta itemmeta1 = item1.getItemMeta();
				itemmeta1.setDisplayName("Délimiter un terrain");
				itemmeta1.setLore(Arrays.asList("Cliquez sur un bloc du sol pour fixer le premier point"));
				itemmeta1.setCustomModelData(1);
				item1.setItemMeta(itemmeta1);
				player.getInventory().addItem(item1);
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
				playerWallet.give(nb);
			}
		}
		
		return false;
	}

}
