package fr.martdel.rolecraft;

import org.bukkit.plugin.java.JavaPlugin;

public class RoleCraft extends JavaPlugin {
	
	@Override
	public void onEnable() {
		System.out.println("§b[RoleCraft]§r Server ON !");
	}
	
	@Override
	public void onDisable() {
		System.out.println("§b[RoleCraft]§r Server OFF...");
	}

}
