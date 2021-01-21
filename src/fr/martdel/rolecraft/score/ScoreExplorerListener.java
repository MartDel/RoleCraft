package fr.martdel.rolecraft.score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.RoleCraft;

public class ScoreExplorerListener implements Listener {

	private RoleCraft plugin;
	
	private Map<EntityType, Integer> kill;
	
	public ScoreExplorerListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
		
		// Kill XP
		kill = new HashMap<>();
		List<Map<?, ?>> kills_config = RoleCraft.config.getMapList("score.explorer.kill");
		for (Map<?, ?> el : kills_config) {
			@SuppressWarnings("unchecked")
			Map<String, ?> kill_config = (Map<String, ?>) el;
			EntityType entity = EntityType.valueOf((String) kill_config.get("entity"));
			Integer score = (Integer) kill_config.get("score");
			kill.put(entity, score);
		}
		
	}
	
	@EventHandler
	public void onKill(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();
		
		if((damager instanceof Player) && (entity instanceof Damageable)) {
			Player player = (Player) damager;
			CustomPlayer customPlayer = new CustomPlayer(player, plugin).loadData();
			int score = customPlayer.getScore();
			Damageable victim = (Damageable) entity;
			EntityType entitytype = entity.getType();
			if(customPlayer.getJob() != 2) return;
			
			/*
			 * A EXPLORER KILL AN ENTITY
			 */
			if(!player.isOp() && kill.containsKey(entitytype) && (victim.getHealth() - event.getDamage()) <= 0) {
				customPlayer.setScore(score + kill.get(entitytype));
			}
		}
	}

}
