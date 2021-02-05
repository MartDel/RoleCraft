package fr.martdel.rolecraft.deathroom;

import com.mysql.fabric.xmlrpc.base.Array;
import fr.martdel.rolecraft.CustomItems;
import fr.martdel.rolecraft.RoleCraft;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public enum DeathKeys {

    A1(CustomItems.DEATHKEYA1),
    A2(CustomItems.DEATHKEYA2),
    B1(CustomItems.DEATHKEYB1),
    B2(CustomItems.DEATHKEYB2),
    C1(CustomItems.DEATHKEYC1);

    private CustomItems customitem;
    private String config_path;

    DeathKeys(CustomItems item){
        this.config_path = "deathkeys." + this.toString();
        this.customitem = item;
    }

    public int getId(){
        return RoleCraft.config.getInt(config_path + ".id");
    }

    public int getLost(){
        return RoleCraft.config.getInt(config_path + ".lost");
    }

    public int getRoomDrop(){
        return RoleCraft.config.getInt(config_path + ".room");
    }

    public int getDrop(){
        return RoleCraft.config.getInt(config_path + ".drop");
    }

    public int getPrice(){
        return RoleCraft.config.getInt(config_path + ".price");
    }

    public ItemStack getItem(){
        ItemStack key = customitem.getItem();
        ItemMeta keymeta = key.getItemMeta();
        keymeta.setLore(Arrays.asList(
            "§fTaux de pertes: §4" + getLost(),
            "§fTaux de drops dans la salle: §a" + getRoomDrop(),
            "§fTaux de drops sur place: §2" + getDrop()
        ));
        key.setItemMeta(keymeta);
        return key;
    }

    public static DeathKeys getKeyById(int id){
        for(DeathKeys current : values()){
            if(current.getId() == id) return current;
        }
        return null;
    }
}
