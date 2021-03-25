package fr.martdel.rolecraft.player;

import fr.martdel.rolecraft.RoleCraft;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;

public class Score {
	
	private final Objective sb;

	public Score(RoleCraft main, String sb_str) {
		ScoreboardManager sbm = main.getServer().getScoreboardManager();
		assert sbm != null;
		this.sb = sbm.getMainScoreboard().getObjective(sb_str);
	}
	
	/**
	 * Get player's score
	 * @param p Player
	 * @return Player's score (int)
	 */
	@SuppressWarnings("deprecation")
	public Integer getScore(OfflinePlayer p) {
		return sb.getScore(p).getScore();
	}
	
	/**
	 * Set player's score
	 * @param p The player to update
	 * @param score The score to set
	 */
	@SuppressWarnings("deprecation")
	public void setScore(Player p, Integer score) {
		sb.getScore(p).setScore(score);
	}
	
	/**
	 * Get the score limit for a specific LVL
	 * @param LVL The LVL to work with
	 * @return The score limit (int)
	 */
	public static int getLVLScore(int LVL) {
		int c_lvlscore = 25;
		for(int i = 2; i <= LVL; i++) c_lvlscore *= 1.4;
		return c_lvlscore;
	}

}
