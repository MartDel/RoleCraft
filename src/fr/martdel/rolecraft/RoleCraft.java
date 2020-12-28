package fr.martdel.rolecraft;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import fr.martdel.rolecraft.commands.CommandPower;
import fr.martdel.rolecraft.listeners.PluginListener;

public class RoleCraft extends JavaPlugin {
	
	public static FileConfiguration config;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();		
		System.out.println("§b[RoleCraft]§r Server ON !");
		RoleCraft.config = this.getConfig();
		
		getCommand("power").setExecutor(new CommandPower());
		getServer().getPluginManager().registerEvents(new PluginListener(this), this);
	}
	
	@Override
	public void onDisable() {
		System.out.println("§b[RoleCraft]§r Server OFF...");
	}
	
	public static void printLocation(Location l) {
		System.out.println(
				"x=" + l.getBlockX() +
				" y=" + l.getBlockY() +
				" z=" + l.getBlockZ());
	}

}
