package fr.martdel.rolecraft.player;

import fr.martdel.rolecraft.RoleCraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class MainScoreboard {

    private final Scoreboard sb;
    private final Objective obj;

    public MainScoreboard(){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        this.sb = manager.getNewScoreboard();
        this.obj = sb.registerNewObjective("gui", "dummy");
    }

    public void setObjective(CustomPlayer player){
        RoleCraft plugin = (RoleCraft) player.getPlayer().getServer().getPluginManager().getPlugin("RoleCraft");
        String color = player.getTeam().getColor();
        int lvl = player.getLevel();

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§5RoleCraft");

        obj.getScore("Métier: §" + color + player.getStringJob("fr")).setScore(8);
        obj.getScore("LVL: §a" + lvl).setScore(7);
        obj.getScore("Avant LVL " + (lvl + 1) + ": §2" + getLVLProgress(player.getScore(), lvl) + "%").setScore(6);
        obj.getScore("§e").setScore(5);
        obj.getScore("En ligne: §a" + Bukkit.getOnlinePlayers().size()).setScore(4);
        assert plugin != null;
        obj.getScore("Admins: §9" + getNbAdmins(plugin)).setScore(3);
        obj.getScore("").setScore(2);
        obj.getScore("§5Rejoignez le Discord !").setScore(1);

        player.getPlayer().setScoreboard(sb);
    }

    public Scoreboard getScoreboard(){ return sb; }

    private int getNbAdmins(RoleCraft plugin){
        int nb_admins = 0;
        for(Player p : plugin.getServer().getOnlinePlayers()) {
            CustomPlayer customP = new CustomPlayer(p, plugin).loadData();
            if(customP.isAdmin()) nb_admins++;
        }
        return nb_admins;
    }

    /**
     * Get the progress percentage before get the next LVL
     * @param score the current player's score
     * @param c_lvl the current player's lvl
     * @return Progress percentage (int)
     */
    private int getLVLProgress(int score, int c_lvl) {
        if(score == 0) return 0;

        System.out.println(c_lvl);
        int cLVLScore = 0;
        if(c_lvl == 2) cLVLScore = 25;
        else if(c_lvl > 2) cLVLScore = Score.getLVLScore(c_lvl - 1);
        int nxtLVLScore = Score.getLVLScore(c_lvl);
        System.out.println(cLVLScore);
        System.out.println(nxtLVLScore);

        int total = nxtLVLScore - cLVLScore;

        System.out.println(score);
        System.out.println(total);

        float calcul1 = (float) (score - cLVLScore) / total;
        System.out.println(calcul1);
        float calcul2 = calcul1 * 100;
        System.out.println(calcul2);

        return (int) calcul2;
    }

}
