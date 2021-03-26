package fr.martdel.rolecraft.commands;

import fr.martdel.rolecraft.player.CustomPlayer;
import fr.martdel.rolecraft.deathroom.DeathRoom;
import fr.martdel.rolecraft.powers.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.martdel.rolecraft.RoleCraft;

import java.util.UUID;

public class CommandTest implements CommandExecutor {

	private final RoleCraft plugin;

	public CommandTest(RoleCraft roleCraft) {
		this.plugin = roleCraft;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

		if(sender instanceof Player) {
			Player player = (Player) sender;
			CustomPlayer customPlayer = new CustomPlayer(player, plugin);
			
			if(cmd.getName().equalsIgnoreCase("test")) {
				// Test command
				DeathRoom dr = DeathRoom.getRoomById(1, plugin);
				dr.setCurrentlyUsed(!dr.isCurrentlyUsed());
				player.sendMessage("La première deathroom est " + (dr.isCurrentlyUsed() ? "indisponible" : "disponible"));
			} else if(cmd.getName().equalsIgnoreCase("power")) {
				if(args.length != 0) {
					if(args[0].equalsIgnoreCase("summoner")) {
						ItemStack summoner = SummonMob.getSpawner();
						player.getInventory().addItem(summoner);
					} else if(args[0].equalsIgnoreCase("shockwave")) {
						ItemStack shockwave = ShockWave.getItemStack();
						player.getInventory().addItem(shockwave);
					} else if(args[0].equalsIgnoreCase("bunker")) {
						ItemStack bunker = Bunker.getItemStack();
						player.getInventory().addItem(bunker);
					} else if(args[0].equalsIgnoreCase("bomb")) {
						ItemStack bomb = Bomb.getItemStack();
						player.getInventory().addItem(bomb);
					} else if(args[0].equalsIgnoreCase("telekinesis")) {	
						ItemStack tk = Telekinesis.getItemStack();
						player.getInventory().addItem(tk);
					} else if(args[0].equalsIgnoreCase("fertility")) {	
						ItemStack fertility = Fertility.getItemStack();
						player.getInventory().addItem(fertility);
					} else if(args[0].equalsIgnoreCase("invisibility")) {
						ItemStack invisibility = Invisibility.getItemStack();
						player.getInventory().addItem(invisibility);
					}
				}
			}
		}
		
		return false;
	}

}
