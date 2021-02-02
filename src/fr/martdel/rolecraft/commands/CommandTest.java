package fr.martdel.rolecraft.commands;

import fr.martdel.rolecraft.CustomPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.powers.Bomb;
import fr.martdel.rolecraft.powers.Bunker;
import fr.martdel.rolecraft.powers.Fertility;
import fr.martdel.rolecraft.powers.ShockWave;
import fr.martdel.rolecraft.powers.SummonMob;
import fr.martdel.rolecraft.powers.Telekinesis;

public class CommandTest implements CommandExecutor {

	private RoleCraft plugin;

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
				World test = Bukkit.getWorld("world");
				System.out.println(test);
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
					}
				}
			}
		}
		
		return false;
	}

}
