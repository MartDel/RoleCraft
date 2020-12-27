package fr.martdel.rolecraft.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandTest implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(cmd.getName().equalsIgnoreCase("test")) {
				ItemStack item = new ItemStack(Material.STICK);
				ItemMeta itemmeta = item.getItemMeta();
				itemmeta.setDisplayName("Onde de choc (Builder)");
				item.setItemMeta(itemmeta);
				player.getInventory().addItem(item);
			}
		}
		
		return false;
	}

}
