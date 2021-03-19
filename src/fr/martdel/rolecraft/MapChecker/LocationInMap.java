package fr.martdel.rolecraft.MapChecker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import fr.martdel.rolecraft.RoleCraft;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.martdel.rolecraft.database.DatabaseManager;

public enum LocationInMap {
	
	OWNED("votre terrain"),
	HOUSE("la maison d'un joueur"),
	FARM("la ferme d'un fermier"),
	SHOP("le magasin d'un joueur"),
	BUILD("le terrain de construction d'un builder"),
	PROTECTED_MAP("le village ou dans la périphérie"),
	FREE_PLACE("une zone libre");

	private String description;

	LocationInMap(String desc) {
		this.description = desc;
	}
	
	public String getDescription() {
		return this.description;
	}
}
