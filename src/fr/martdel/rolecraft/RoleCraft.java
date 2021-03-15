package fr.martdel.rolecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.martdel.rolecraft.PNJ.PNJListener;
import fr.martdel.rolecraft.cinematics.Cinematic;
import fr.martdel.rolecraft.commands.CommandAdmin;
import fr.martdel.rolecraft.commands.CommandPublic;
import fr.martdel.rolecraft.commands.CommandTest;
import fr.martdel.rolecraft.database.DatabaseManager;
import fr.martdel.rolecraft.deathroom.DeathListener;
import fr.martdel.rolecraft.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RoleCraft extends JavaPlugin {
	
	public static FileConfiguration config;
	public static final World OVERWORLD = Bukkit.getWorld("world");
	
	private static final String[] PUBLICCOMMANDS = {"switch", "mp", "farmer", "miner", "explorer", "builder", "admin", "invite", "ginfo", "sell"};
	private static final String[] ADMINCOMMANDS = {"delimiter", "path", "spawn", "rubis", "cinematic"};
	private static final String[] TESTCOMMANDS = {"test", "power"};
	
	private DatabaseManager db;
	private Score lvl;
	private Score waiting;
	private Score recording;

	private Map<String, Cinematic> cinematic_list = new HashMap<>();

	@Override
	public void onEnable() {
		saveDefaultConfig();		
		System.out.println("§b[RoleCraft]§r Server ON !");
		RoleCraft.config = this.getConfig();

		this.db = new DatabaseManager();
		this.lvl = new Score(this, "Niveau");
		this.waiting = new Score(this, "waiting");

		// Admin scoreboard
		this.recording = new Score(this, "recording");

		// Load cinematic files
		String[] names = new File(Cinematic.PATH).list();
		try {
			for (String name : names){
				Scanner file = new Scanner(new File(Cinematic.PATH + "/" + name));
				name = name.split("\\.")[0];
				List<Location> locations = new ArrayList<>();
				while (file.hasNextLine()) {
					String data = file.nextLine();
					JsonObject json = new JsonParser().parse(data).getAsJsonObject();
					Location saved_loc = new Location(
						Bukkit.getWorld(json.get("world").getAsString()),
						json.get("x").getAsDouble(),
						json.get("y").getAsDouble(),
						json.get("z").getAsDouble(),
						json.get("yaw").getAsFloat(),
						json.get("pitch").getAsFloat()
					);
					locations.add(saved_loc);
				}
				file.close();
				cinematic_list.put(name, new Cinematic(locations));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

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

	
	/**
	 * Capitalize the first letter of a string
	 * @param str The string
	 * @return String
	 */
	public static String firstLetterToUpperCase(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * Get a location from the config file
	 * @param data The config file raw
	 * @param orientation If the raw contains orientations data
	 * @return Location The found location
	 */
	@SuppressWarnings("unchecked")
	public static Location getConfigLocation(MemorySection data, boolean orientation){
		double x = Double.parseDouble(data.getString("x"));
		double y = Double.parseDouble(data.getString("y"));
		double z = Double.parseDouble(data.getString("z"));
		if(orientation){
			float yaw = Float.parseFloat(data.getString("yaw"));
			float pitch = Float.parseFloat(data.getString("pitch"));
			return new Location(OVERWORLD, x, y, z, yaw, pitch);
		} else return new Location(OVERWORLD, x, y, z);
	}
	@SuppressWarnings("unchecked")
	public static Location getConfigLocation(Object config_object, boolean orientation) {
		Map<String, ?> data = (Map<String, ?>) config_object;
		double x = data.get("x") instanceof Double ? (Double) data.get("x") : (Integer) data.get("x");
		double y = data.get("y") instanceof Double ? (Double) data.get("y") : (Integer) data.get("y");
		double z = data.get("z") instanceof Double ? (Double) data.get("z") : (Integer) data.get("z");
		if(orientation){
			float yaw = data.get("yaw") instanceof Float ? (Float) data.get("yaw") : (Integer) data.get("yaw");
			float pitch = data.get("pitch") instanceof Float ? (Float) data.get("pitch") : (Integer) data.get("pitch");
			return new Location(OVERWORLD, x, y, z, yaw, pitch);
		} else return new Location(OVERWORLD, x, y, z);
	}
	
	public static void printLocation(Location l) {
		System.out.println(
			"x=" + l.getBlockX() +
			" y=" + l.getBlockY() +
			" z=" + l.getBlockZ()
		);
	}

	public DatabaseManager getDB() { return db; }
	public Score getLvl() { return lvl; }
	public Score getWaiting() { return waiting; }
	public Score getRecording() { return recording; }

	public Map<String, Cinematic> getCinematicList() { return cinematic_list; }
	public void setCinematicList(Map<String, Cinematic> cinematic_list) { this.cinematic_list = cinematic_list; }
}
