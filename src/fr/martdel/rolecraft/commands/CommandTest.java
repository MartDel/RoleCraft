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
					switch (args[0]){
						case "summoner":
							ItemStack summoner = SummonMob.getSpawner();
							player.getInventory().addItem(summoner);
							break;
						case "shockwave":
							ItemStack shockwave = ShockWave.getItemStack();
							player.getInventory().addItem(shockwave);
							break;
						case "bunker":
							ItemStack bunker = Bunker.getItemStack();
							player.getInventory().addItem(bunker);
							break;
						case "bomb":
							ItemStack bomb = Bomb.getItemStack();
							player.getInventory().addItem(bomb);
							break;
						case "telekinesis":
							ItemStack tk = Telekinesis.getItemStack();
							player.getInventory().addItem(tk);
							break;
						case "fertility":
							ItemStack fertility = Fertility.getItemStack();
							player.getInventory().addItem(fertility);
							break;
						case "invisibility":
							ItemStack invisibility = Invisibility.getItemStack();
							player.getInventory().addItem(invisibility);
							break;
						case "flymob":
							ItemStack flyingmob = FlyingMob.getItemStack();
							player.getInventory().addItem(flyingmob);
							break;
						case "bug":
							ItemStack bug = Bug.getItemStack();
							player.getInventory().addItem(bug);
							break;
						case "killingspree":
							ItemStack ks = KillingSpree.getItemStack();
							player.getInventory().addItem(ks);
							break;
						default:
							player.sendMessage("Aucun pouvoir n'a été trouvé avec ce nom...");
							break;
					}
				}
			}
		}
		
		return false;
	}

}
