package fr.martdel.rolecraft.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.martdel.rolecraft.CustomItems;
import fr.martdel.rolecraft.CustomPlayer;
import fr.martdel.rolecraft.GUI;
import fr.martdel.rolecraft.LocationInMap;
import fr.martdel.rolecraft.RoleCraft;
import fr.martdel.rolecraft.Wallet;

public class ClickListener implements Listener {
	
	private static final int LETTERBOX_PRICE = RoleCraft.config.getInt("prices.letterbox");
	
	private RoleCraft plugin;

	public ClickListener(RoleCraft rolecraft) {
		this.plugin = rolecraft;
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CustomPlayer customPlayer = new CustomPlayer(player, plugin);
		Action action = event.getAction();
		ItemStack item = event.getItem();
		/*
		 * PLAYER USES START COMPASS
		 */
		if(item != null && (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR))) {
			ItemMeta itemMeta = item.getItemMeta();
			CustomItems compass = CustomItems.COMPASS;
			if(item.getType().equals(compass.getType()) && itemMeta.equals(compass.getItemMeta())) {
				Inventory inv = GUI.createCompassGUI();
				player.openInventory(inv);
				return;
			}
		}
		
		if(event.getClickedBlock() != null) {
			BlockState bs = event.getClickedBlock().getState();
			
			if(action == Action.RIGHT_CLICK_BLOCK) {				
				if(bs instanceof Chest) {
					/*
					 * LETTER BOX
					 */
					Chest letterbox = (Chest) bs;
					String name = letterbox.getCustomName();
					Map<String, OfflinePlayer> owners = LocationInMap.getPlayerOwner(plugin, player, letterbox.getLocation());
					if(owners.size() == 0 || !owners.containsKey("houses")) return;
					OfflinePlayer owner = owners.get("houses");

					if(name == null) return;
					if(name.equalsIgnoreCase("§8Boite aux lettres") && owner != null && !owner.equals(player) && !player.isOp()) {
						// Build letterbox inventory
						String color = new CustomPlayer(owner.getPlayer(), plugin).getTeam().getColor();
						Inventory show = Bukkit.createInventory(null, 27, "§8Boite aux lettres de §" + color + owner.getName());
						ItemStack validate = new ItemStack(Material.GREEN_CONCRETE);
						ItemMeta meta = validate.getItemMeta();
						meta.setDisplayName("§aEnvoyer");
						meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
						validate.setItemMeta(meta);
						show.setItem(26, validate);
						
						player.openInventory(show);
						player.setCompassTarget(letterbox.getLocation());
						
						event.setCancelled(true);
					}
				} else if(bs instanceof Sign) {
					Sign sign = (Sign) bs;
					if(item != null) {
						ItemMeta iMeta = item.getItemMeta();
						CustomItems delimiter = CustomItems.DELIMITER;
						if(player.isOp() && item.getType().equals(Material.STICK) && iMeta.getDisplayName().equalsIgnoreCase(delimiter.getItemMeta().getDisplayName()) && sign.getLine(0).equalsIgnoreCase("Banque")) {
							/*
							 * RIGHT CLICK ON THE GROUND SIGN BY AN ADMIN
							 */
							Map<String, Integer> ground = new HashMap<>();
							ground.put("x1", Integer.parseInt(sign.getLine(1).split(";")[0]));
							ground.put("z1", Integer.parseInt(sign.getLine(2).split(";")[0]));
							ground.put("x2", Integer.parseInt(sign.getLine(1).split(";")[1]));
							ground.put("z2", Integer.parseInt(sign.getLine(2).split(";")[1]));
							
							// Save the ground
							System.out.println(ground);
							customPlayer.loadData().setAdminGround(ground);
							customPlayer.save();
							
							player.sendMessage("Ce terrain vous appartient");
							player.getWorld().getBlockAt(sign.getLocation()).setType(Material.AIR);
							return;
						}
						
					}
					if(sign.getLine(0).equalsIgnoreCase("§dBoite aux lettres")) {
						/*
						 * PLAYER BUYS A LETTER BOX
						 */
						Wallet wallet = customPlayer.getWallet();
						if(wallet.has(LETTERBOX_PRICE)) {
							ItemStack letterbox = CustomItems.LETTERBOX.getItem();
							player.getInventory().addItem(letterbox);
							player.sendMessage("Vous venez d'obtenir une §aBoite aux lettres.");
							wallet.remove(LETTERBOX_PRICE);
						} else {
							player.sendMessage("§4Vous ne possédez pas le nombre de rubis nécessaire pour cet achat.");
						}
						return;
					} else if(sign.getLine(0).equalsIgnoreCase("§f*****§d Echange §f*****")) {
						/*
						 * PLAYER CONVERTS HIS SAPHIRS TO RUBYS
						 */
						Wallet wallet = customPlayer.getWallet();
						int nb_saphir = 0;
						
						for(ItemStack stack : player.getInventory().getStorageContents()) {
							if(stack != null) {
								ItemMeta meta = stack.getItemMeta();
								Material type = stack.getType();
								if(meta.equals(CustomItems.SAPHIR.getItemMeta()) && type.equals(CustomItems.SAPHIR.getType())) {
									nb_saphir += stack.getAmount();
									stack.setAmount(0);
								}
							}
						}
						
						if(nb_saphir > 0) {
							try{ wallet.give(nb_saphir * 20); }
							catch (Exception e){ player.sendMessage(e.getMessage()); }
							player.sendMessage("Vous venez d'échanger §a" + nb_saphir + "§r saphirs contre §a" + (nb_saphir * 20) + "§r rubis.");
						} else {
							player.sendMessage("§4Vous ne possédez pas de saphirs");
						}
						return;
					}
				}
				
				
			} else if(action == Action.LEFT_CLICK_BLOCK && item != null) {
				ItemMeta iMeta = item.getItemMeta();
				String name = iMeta.getDisplayName();
				if(!iMeta.hasCustomModelData()) return;
				int metadata = iMeta.getCustomModelData();
				Material type = item.getType();
				
				CustomItems delimiter = CustomItems.DELIMITER;
				
				if(player.isOp() && type.equals(delimiter.getType()) && name.equalsIgnoreCase(delimiter.getName()) && metadata == delimiter.getData()) {
					/*
					 * ADMIN DEMARCATES A GROUND
					 */
					if(iMeta.getLore().equals(delimiter.getLore())) {
						Location coo = bs.getLocation();
						iMeta.setLore(Arrays.asList("Premier point :", Integer.toString(coo.getBlockX()), Integer.toString(coo.getBlockZ()), "Cliquez sur un bloc du sol", "pour fixer le deuxième point."));
						item.setItemMeta(iMeta);
					} else if(iMeta.getLore().get(0).equalsIgnoreCase("Premier point :")) {
						Location coo2 = bs.getLocation();
						
						Integer x1 = Integer.parseInt(iMeta.getLore().get(1));
						Integer z1 = Integer.parseInt(iMeta.getLore().get(2));
						
						Integer x2 = coo2.getBlockX();
						Integer z2 = coo2.getBlockZ();

						Integer xMin = 0;
						Integer zMin = 0;
						Integer xLong = 0;
						Integer zLong = 0;
						
						if(x1 > x2) {xLong = x1 - x2; xMin = x2;}
						else {xLong = x2 - x1; xMin = x1;}
						if(z1 > z2) {zLong = z1 - z2; zMin = z2;}
						else {zLong = z2 - z1; zMin = z1;}
						
						player.getWorld().getBlockAt(x2, coo2.getBlockY() + 1, z2).setType(Material.OAK_SIGN);
						Sign sign = (Sign) player.getWorld().getBlockAt(x2, coo2.getBlockY() + 1, z2).getState();
						sign.setLine(0, "Banque");
						sign.setLine(1, xMin + ";" + (xMin + xLong));
						sign.setLine(2, zMin + ";" + (zMin + zLong));
						sign.update();
						
						player.sendMessage("Le terrain a été délimité pour la banque");
						iMeta.setLore(delimiter.getLore());
						item.setItemMeta(iMeta);
					}
					event.setCancelled(true);
					return;
				}
			}
		}
	}

}
