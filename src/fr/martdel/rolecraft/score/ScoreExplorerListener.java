package fr.martdel.rolecraft.score;

import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.player.CustomPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Map;

public class ScoreExplorerListener implements Listener {

	private final RoleCraft plugin;

	private final Map<EntityType, Integer> kill = RoleCraft.getEntityMap("score.explorer.kill");
	
	public ScoreExplorerListener(RoleCraft rolecraft) { this.plugin = rolecraft; }
	
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
