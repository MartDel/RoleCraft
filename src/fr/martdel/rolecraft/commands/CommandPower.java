package fr.martdel.rolecraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.martdel.rolecraft.powers.Bomb;
import fr.martdel.rolecraft.powers.Bunker;
import fr.martdel.rolecraft.powers.ShockWave;
import fr.martdel.rolecraft.powers.SummonMob;

public class CommandPower implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(cmd.getName().equalsIgnoreCase("power")) {
				if(args.length != 0) {
					if(args[0].equalsIgnoreCase("summoner")) {
						ItemStack summoner = SummonMob.getItemStack();
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
					}
				}
			}
		}
		
		return false;
	}

}
