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

import fr.martdel.rolecraft.powers.Bunker;
import fr.martdel.rolecraft.powers.ShockWave;

public class CommandPower implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(cmd.getName().equalsIgnoreCase("power")) {
				if(args.length != 0) {
					if(args[0].equalsIgnoreCase("summoner")) {
						ItemStack summoner = new ItemStack(Material.BOW);
						ItemMeta smeta = summoner.getItemMeta();
						smeta.setDisplayName("§dInvocateur§r");
						smeta.setLore(Arrays.asList("Tirez sur un ennemi pour", "§dinvoquer un allié§r"));
						smeta.addEnchant(Enchantment.DAMAGE_ALL, 200, true);
						smeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						summoner.setItemMeta(smeta);
						player.getInventory().addItem(summoner);
					} else if(args[0].equalsIgnoreCase("shockwave")) {
						ItemStack shockwave = ShockWave.getItemStack();
						player.getInventory().addItem(shockwave);
					} else if(args[0].equalsIgnoreCase("bunker")) {
						ItemStack bunker = Bunker.getItemStack();
						player.getInventory().addItem(bunker);
					}
				}
			}
		}
		
		return false;
	}

}
