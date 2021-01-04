package fr.martdel.rolecraft;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.martdel.rolecraft.commands.CommandPower;
import fr.martdel.rolecraft.commands.CommandTest;
import fr.martdel.rolecraft.database.DatabaseManager;
import fr.martdel.rolecraft.listeners.PluginListener;

public class RoleCraft extends JavaPlugin {
	
	public static FileConfiguration config;
	
	private DatabaseManager db;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();		
		System.out.println("§b[RoleCraft]§r Server ON !");
		RoleCraft.config = this.getConfig();
		
		this.db = new DatabaseManager();
		
		List<String> announcements = config.getStringList("announcements");
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.broadcastMessage("§5[Info Serveur]§b " + announcements.get(new Random().nextInt(announcements.size())));
			}
		}.runTaskTimer(this, 0, 12000);
		
		getCommand("test").setExecutor(new CommandTest(this));
		getCommand("power").setExecutor(new CommandPower());
		getServer().getPluginManager().registerEvents(new PluginListener(this), this);
	}
	
	@Override
	public void onDisable() {
		System.out.println("§b[RoleCraft]§r Server OFF...");
	}
	
	public DatabaseManager getDB() { return db; }
	
	public static void printLocation(Location l) {
		System.out.println(
			"x=" + l.getBlockX() +
			" y=" + l.getBlockY() +
			" z=" + l.getBlockZ()
		);
	}

}
