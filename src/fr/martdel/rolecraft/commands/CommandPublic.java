package fr.martdel.rolecraft.commands;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.TeamManager;

public class CommandPublic implements CommandExecutor {
	
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
			}
		}
		
		return false;
	}

}
