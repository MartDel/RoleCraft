package fr.martdel.rolecraft;

import org.bukkit.plugin.java.JavaPlugin;

import fr.martdel.rolecraft.commands.CommandTest;
import fr.martdel.rolecraft.listeners.PluginListener;

public class RoleCraft extends JavaPlugin {
	
	@Override
	public void onEnable() {
		System.out.println("§b[RoleCraft]§r Server ON !");
		getCommand("test").setExecutor(new CommandTest());
		getServer().getPluginManager().registerEvents(new PluginListener(), this);
	}
	
	@Override
	public void onDisable() {
		System.out.println("§b[RoleCraft]§r Server OFF...");
	}

}
