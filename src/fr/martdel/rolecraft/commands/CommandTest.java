package fr.martdel.rolecraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTest implements CommandExecutor {

//	private RoleCraft plugin;
//
//	public CommandTest(RoleCraft roleCraft) {
//		this.plugin = roleCraft;
//	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

		if(sender instanceof Player) {
//			CustomPlayer player = new CustomPlayer((Player) sender, plugin);
			
			if(cmd.getName().equalsIgnoreCase("test")) {
				
			}
		}
		
		return false;
	}

}
