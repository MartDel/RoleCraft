package fr.martdel.rolecraft;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.martdel.rolecraft.commands.CommandAdmin;
import fr.martdel.rolecraft.commands.CommandPublic;
import fr.martdel.rolecraft.commands.CommandTest;
import fr.martdel.rolecraft.database.DatabaseManager;
import fr.martdel.rolecraft.listeners.PluginListener;
import fr.martdel.rolecraft.listeners.PowerListener;

public class RoleCraft extends JavaPlugin {
	
	public static FileConfiguration config;
	
	private final String[] PUBLICCOMMANDS = {"switch", "mp", "farmer", "miner", "explorer", "builder", "admin", "level", "invite", "ginfo", "sell"};
	private final String[] ADMINCOMMANDS = {"delimiter", "path", "spawn", "rubis"};
	private final String[] TESTCOMMANDS = {"test", "power"};
	
	private DatabaseManager db;
	private Score lvl;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();		
		System.out.println("§b[RoleCraft]§r Server ON !");
		RoleCraft.config = this.getConfig();
		
		this.db = new DatabaseManager();
		this.lvl = new Score(this, "Niveau");
		
		// Commands
		for (String command : PUBLICCOMMANDS) {
			getCommand(command).setExecutor(new CommandPublic(this));
		}
		for (String command : ADMINCOMMANDS) {
			getCommand(command).setExecutor(new CommandAdmin(this));
		}
		for (String command : TESTCOMMANDS) {
			getCommand(command).setExecutor(new CommandTest(this));
		}
		
		// Listeners
		getServer().getPluginManager().registerEvents(new PluginListener(this), this);
		getServer().getPluginManager().registerEvents(new PowerListener(this), this);
		
		// Announcements
		List<String> announcements = config.getStringList("announcements");
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.broadcastMessage("§5[Info Serveur]§b " + announcements.get(new Random().nextInt(announcements.size())));
			}
		}.runTaskTimer(this, 0, 12000);
	}
	
	@Override
	public void onDisable() {
		System.out.println("§b[RoleCraft]§r Server OFF...");
	}
	
	public DatabaseManager getDB() { return db; }
	
	/**
	 * Capitalize the first letter of a string
	 * @param str The string
	 * @return String
	 */
	public static String firstLetterToUpperCase(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public static void printLocation(Location l) {
		System.out.println(
			"x=" + l.getBlockX() +
			" y=" + l.getBlockY() +
			" z=" + l.getBlockZ()
		);
	}

	public Score getLvl() { return lvl; }

}
