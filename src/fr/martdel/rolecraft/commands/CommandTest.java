package fr.martdel.rolecraft.commands;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandTest implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(cmd.getName().equalsIgnoreCase("test")) {
				ItemStack invoker = new ItemStack(Material.BOW);
				ItemMeta imeta = invoker.getItemMeta();
				imeta.setDisplayName("§dInvocateur§r");
				imeta.setLore(Arrays.asList("Tirez sur un ennemi pour", "§dinvoquer un allié§r"));
				imeta.addEnchant(Enchantment.LUCK, 200, true);
				imeta.addEnchant(Enchantment.ARROW_INFINITE, 200, true);
				imeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				invoker.setItemMeta(imeta);
				player.getInventory().addItem(invoker);
			}
		}
		
		return false;
	}

}
