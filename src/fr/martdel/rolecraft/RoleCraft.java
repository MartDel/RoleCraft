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
import fr.martdel.rolecraft.listeners.ClickListener;
import fr.martdel.rolecraft.listeners.CraftListener;
import fr.martdel.rolecraft.listeners.PluginListener;
import fr.martdel.rolecraft.listeners.SellListener;
import fr.martdel.rolecraft.player.Score;
import fr.martdel.rolecraft.powers.PowerListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RoleCraft extends JavaPlugin {
	
	public static FileConfiguration config;
	public static final World OVERWORLD = Bukkit.getWorld("world");
	
	private static final String[] PUBLICCOMMANDS = {"switch", "mp", "farmer", "miner", "explorer", "builder", "admin", "invite", "ginfo", "sell"};
	private static final String[] ADMINCOMMANDS = {"delimiter", "path", "spawn", "rubis", "cinematic", "mob"};
	private static final String[] TESTCOMMANDS = {"test", "power"};
	
	private DatabaseManager db;
	private Score jobSB;
	private Score isNewSB;
	private Score scoreSB;
	private Score lvlSB;
	private Score hasSpeSB;
	private Score deathroomSB;
	private Score isRecordingSB;

	private Map<String, Cinematic> cinematic_list = new HashMap<>();

	public Map<String, List<Player>> channels;

	@Override
	public void onEnable() {
		saveDefaultConfig();		
		System.out.println("[RoleCraft] Server ON !");
		RoleCraft.config = this.getConfig();

		this.db = new DatabaseManager();
		this.jobSB = new Score(this, "job");
		this.isNewSB = new Score(this, "isNew");
		this.scoreSB = new Score(this, "score");
		this.lvlSB = new Score(this, "level");
		this.hasSpeSB = new Score(this, "hasSpe");
		this.deathroomSB = new Score(this, "deathroom");

		// Admin scoreboard
		this.isRecordingSB = new Score(this, "isRecording");

		// Channels
		this.channels = new HashMap<>();
		this.channels.put("global", new ArrayList<>());
		this.channels.put("dungeon1", new ArrayList<>());

		// Load cinematic files
		String[] names = new File(Cinematic.PATH).list();
		assert names != null;
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
			PluginCommand cmd = getCommand(command);
			assert cmd != null;
			cmd.setExecutor(new CommandPublic(this));
		}
		for (String command : ADMINCOMMANDS) {
			PluginCommand cmd = getCommand(command);
			assert cmd != null;
			cmd.setExecutor(new CommandAdmin(this));
		}
		for (String command : TESTCOMMANDS) {
			PluginCommand cmd = getCommand(command);
			assert cmd != null;
			cmd.setExecutor(new CommandTest(this));
		}
		
		// Listeners
		getServer().getPluginManager().registerEvents(new PluginListener(this), this);
		getServer().getPluginManager().registerEvents(new ClickListener(this), this);
		getServer().getPluginManager().registerEvents(new DeathListener(this), this);
//		getServer().getPluginManager().registerEvents(new MapProtectListener(this), this);
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
		System.out.println("[RoleCraft] Server OFF...");
	}

	
	/**
	 * Capitalize the first letter of a string
	 * @param str The string
	 * @return String
	 */
	public static String firstLetterToUpperCase(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	// Config functions //////////

	/**
	 * Get a location from the config file
	 * @param data The config file raw
	 * @param orientation If the raw contains orientations data
	 * @return Location The found location
	 */
	public static Location getConfigLocation(MemorySection data, boolean orientation){
		String x_str = data.getString("x"); assert x_str != null;
		String y_str = data.getString("y"); assert y_str != null;
		String z_str = data.getString("z"); assert z_str != null;

		double x = Double.parseDouble(x_str);
		double y = Double.parseDouble(y_str);
		double z = Double.parseDouble(z_str);
		if(orientation){
			String yaw_str = data.getString("yaw"); assert yaw_str != null;
			String pitch_str = data.getString("pitch"); assert pitch_str != null;
			float yaw = Float.parseFloat(yaw_str);
			float pitch = Float.parseFloat(pitch_str);
			return new Location(OVERWORLD, x, y, z, yaw, pitch);
		} else return new Location(OVERWORLD, x, y, z);
	}
	public static Location getConfigLocation(Object config_object, boolean orientation) {
		@SuppressWarnings("unchecked")
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

	/**
	 * Get a Material List from a configuration path
	 * @param path The configuration path to read
	 * @return The found Material list, can be void if nothing was found
	 */
	public static List<Material> getConfigMaterialList(String path){
		List<Material> result = new ArrayList<>();
		List<String> list = config.getStringList(path);
		for (String name : list) {
			result.add(Material.getMaterial(name));
		}
		return result;
	}

	/**
	 * Get a Material from configuration file
	 * @param path The Material configuration path
	 * @return The found Material
	 */
	public static Material getConfigMaterial(String path){
		String name = config.getString(path);
		assert name != null;
		return Material.getMaterial(name);
	}

	/**
	 * Get a Map which contains Materials and Strings for score listeners from configuration file
	 * @param path The configuration path
	 * @return The found Map
	 */
	public static Map<Material, Integer> getMaterialMap(String path){
		Map<Material, Integer> res = new HashMap<>();
		List<Map<?, ?>> config_list = config.getMapList(path);
		for (Map<?, ?> el : config_list) {
			@SuppressWarnings("unchecked")
			Map<String, ?> config_el = (Map<String, ?>) el;
			Material type = Material.getMaterial((String) config_el.get("type"));
			Integer score = (Integer) config_el.get("score");
			res.put(type, score);
		}
		return res;
	}

	/**
	 * Get a Map which contains EntityTypes and Strings for score listeners from configuration file
	 * @param path The configuration path
	 * @return The found Map
	 */
	public static Map<EntityType, Integer> getEntityMap(String path){
		Map<EntityType, Integer> res = new HashMap<>();
		List<Map<?, ?>> config_list = config.getMapList(path);
		for (Map<?, ?> el : config_list) {
			@SuppressWarnings("unchecked")
			Map<String, ?> config_el = (Map<String, ?>) el;
			EntityType type = EntityType.valueOf((String) config_el.get("entity"));
			Integer score = (Integer) config_el.get("score");
			res.put(type, score);
		}
		return res;
	}

	///////////////////

	public DatabaseManager getDB() { return db; }
	public Score getLvlSB() { return lvlSB; }
	public Score getDeathroomSB() { return deathroomSB; }
	public Score getRecordingSB() { return isRecordingSB; }

	public Map<String, Cinematic> getCinematicList() { return cinematic_list; }
	public void setCinematicList(Map<String, Cinematic> cinematic_list) { this.cinematic_list = cinematic_list; }
}
