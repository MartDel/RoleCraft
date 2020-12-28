package fr.martdel.rolecraft;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import fr.martdel.rolecraft.commands.CommandTest;
import fr.martdel.rolecraft.listeners.PluginListener;

public class RoleCraft extends JavaPlugin {
	
	public static RoleCraft main;
	
	@Override
	public void onEnable() {
		main = this;
		
		System.out.println("§b[RoleCraft]§r Server ON !");
		getCommand("test").setExecutor(new CommandTest());
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
