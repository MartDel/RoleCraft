package fr.martdel.rolecraft;

import fr.martdel.rolecraft.PNJ.PNJListener;
import fr.martdel.rolecraft.commands.CommandAdmin;
import fr.martdel.rolecraft.commands.CommandPublic;
import fr.martdel.rolecraft.commands.CommandTest;
import fr.martdel.rolecraft.database.DatabaseManager;
import fr.martdel.rolecraft.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RoleCraft extends JavaPlugin {
	
	public static FileConfiguration config;
	public static final World OVERWORLD = Bukkit.getWorld("world");
	
	private static final String[] PUBLICCOMMANDS = {"switch", "mp", "farmer", "miner", "explorer", "builder", "admin", "invite", "ginfo", "sell"};
	private static final String[] ADMINCOMMANDS = {"delimiter", "path", "spawn", "rubis"};
	private static final String[] TESTCOMMANDS = {"test", "power"};
	
	private DatabaseManager db;
	private Score lvl;
	private Score waiting;

	@Override
	public void onEnable() {
		saveDefaultConfig();		
		System.out.println("§b[RoleCraft]§r Server ON !");
		RoleCraft.config = this.getConfig();

		this.db = new DatabaseManager();
		this.lvl = new Score(this, "Niveau");
		this.waiting = new Score(this, "waiting");
		
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
		getServer().getPluginManager().registerEvents(new ClickListener(this), this);
		getServer().getPluginManager().registerEvents(new DeathListener(this), this);
		getServer().getPluginManager().registerEvents(new MapProtectListener(this), this);
		getServer().getPluginManager().registerEvents(new CraftListener(this), this);
		getServer().getPluginManager().registerEvents(new PNJListener(this), this);
		getServer().getPluginManager().registerEvents(new SellListener(this), this);
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

	@SuppressWarnings("unchecked")
	public static Location getConfigLocation(MemorySection data){
		return new Location(OVERWORLD, Double.parseDouble(data.getString("x")), Double.parseDouble(data.getString("y")), Double.parseDouble(data.getString("z")));
	}
	@SuppressWarnings("unchecked")
	public static Location getConfigLocation(Object config_object) {
		Map<String, ?> data = (Map<String, ?>) config_object;
		Double x = data.get("x") instanceof Double ? (Double) data.get("x") : ((Integer) data.get("x"));
		Double y = data.get("y") instanceof Double ? (Double) data.get("y") : ((Integer) data.get("y"));
		Double z = data.get("z") instanceof Double ? (Double) data.get("z") : ((Integer) data.get("z"));
		return new Location(OVERWORLD, x, y, z);
	}
	
	public static void printLocation(Location l) {
		System.out.println(
			"x=" + l.getBlockX() +
			" y=" + l.getBlockY() +
			" z=" + l.getBlockZ()
		);
	}

	public Score getLvl() { return lvl; }
	public Score getWaiting() { return waiting; }

}
