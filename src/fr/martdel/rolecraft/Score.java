package fr.martdel.rolecraft;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

public class Score {
	
	private Objective sb;

	public Score(RoleCraft main, String sb_str) {
		this.sb = main.getServer().getScoreboardManager().getMainScoreboard().getObjective(sb_str);
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
	 * @param p Player
	 * @param score
	 */
	@SuppressWarnings("deprecation")
	public void setScore(Player p, Integer score) {
		sb.getScore(p).setScore(score);
	}
	
	/**
	 * Get the score limit for a specific LVL
	 * @param LVL
	 * @return The score limit (int)
	 */
	public static int getLVLScore(int LVL) {
		int c_lvlscore = 25;
		for(int i = 2; i <= LVL; i++) c_lvlscore *= 1.4;
		return (int) c_lvlscore;
	}

}
