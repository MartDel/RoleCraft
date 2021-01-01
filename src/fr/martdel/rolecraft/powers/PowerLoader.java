package fr.martdel.rolecraft.powers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import fr.martdel.rolecraft.RoleCraft;

public class PowerLoader {
	
	private static final Material LOADERTYPE = Material.getMaterial(RoleCraft.config.getString("powers.loader.item_type"));

	private ItemStack item;
	private ItemStack give_item;
	private Player player;
	private RoleCraft plugin;
	private BukkitScheduler scheduler;

	public PowerLoader(RoleCraft rolecraft, Player player, ItemStack itemStack) {
		this.item = itemStack;
		this.give_item = null;
		this.player = player;
		this.plugin = rolecraft;
		this.scheduler = rolecraft.getServer().getScheduler();
	}
	public PowerLoader(RoleCraft rolecraft, Player player, ItemStack itemStack, ItemStack giveItem) {
		this.item = itemStack;
		this.give_item = giveItem;
		this.player = player;
		this.plugin = rolecraft;
		this.scheduler = rolecraft.getServer().getScheduler();
	}

	public void startLoading(int cooldown) {
		scheduler.runTaskLater(plugin, new Runnable() {
			private int t = 0;
			private int slot = 0;
			@Override
			public void run() {
				String power = item.getItemMeta().getDisplayName();
				double progress = (((double) t)/((double) cooldown)) * 100;
				
				ItemStack[] content = player.getInventory().getContents();
				ItemStack loader = getItemStack(power, (int) progress);
				ItemMeta loadermeta = loader.getItemMeta();
				
				Integer itemslot = null;
				Integer loaderslot = null;
				for (int i = 0; i < content.length; i++) {
					ItemStack current_item = content[i];
					if(current_item != null) {
						if(current_item.equals(item)) itemslot = i;
						else if(current_item.getType().equals(LOADERTYPE)) {
							ItemMeta current_meta = current_item.getItemMeta();
							ItemMeta loadermeta_less = getCustomItemMeta(loadermeta, power, ((int)progress) - 1);
							if(current_meta.equals(loadermeta) || current_meta.equals(loadermeta_less)) {
								loaderslot = i;
								slot = i;
							}
						}
					}
				}
				
				if(itemslot != null) {
					player.getInventory().clear(itemslot);
					player.getInventory().setItem(itemslot, loader);
				} else if(loaderslot != null) {
					player.getInventory().setItem(loaderslot, loader);
				}
				
				t++;
				if(t < cooldown) scheduler.runTaskLater(plugin, this, 1);
				else player.getInventory().setItem(slot, give_item != null ? give_item : item);
			}
		}, 1);
	}
	
	private ItemMeta getCustomItemMeta(ItemMeta meta, String power, int progress) {
		meta.setDisplayName(
			RoleCraft.config.getString("powers.loader.item_name1") + " " + 
			power + " " +
			RoleCraft.config.getString("powers.loader.item_name2") + " " +
			progress +
			RoleCraft.config.getString("powers.loader.item_name3"));
		return meta;
	}
	
	private ItemStack getItemStack(String power, int progress) {
		ItemStack loader = new ItemStack(LOADERTYPE);
		loader.setItemMeta(getCustomItemMeta(loader.getItemMeta(), power, progress));
		if(progress != 0) loader.setAmount(progress);
		return loader;
	}

}
