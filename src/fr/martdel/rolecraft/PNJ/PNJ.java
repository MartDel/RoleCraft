package fr.martdel.rolecraft.PNJ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import fr.martdel.rolecraft.GUI;
import fr.martdel.rolecraft.RoleCraft;

public class PNJ {
	
	private String name;
	private String inventory_name;
	private int required_job;
	private boolean sell;
	private List<ItemForSale> items;
	
	public PNJ(String name, String inventory_name, int required_job, boolean sell, List<ItemForSale> items) {
		this.name = name;
		this.inventory_name = inventory_name;
		this.required_job = required_job;
		this.sell = sell;
		this.items = items;
	}
	
	public Inventory getInventory() {
		GUI inv = new GUI(inventory_name, items.size());
		for (ItemForSale item : items) {
			inv.addItem(item.getItemStack());
		}
		inv.setRule("fill", "yes");
		inv.setRule("fill_type", "BARRIER");
		return inv.getInventory();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, PNJ> getAllPNJ(){
		Map<String, PNJ> result = new HashMap<>();
		List<Map<?, ?>> pnj_list = RoleCraft.config.getMapList("PNJ");
		for(Map<?, ?> el : pnj_list) {
			Map<String, ?> pnj_config = (Map<String, ?>) el;
			
			// Get PNJ infos
			String name = (String) pnj_config.get("PNJ_name");
			String inventory_name = (String) pnj_config.get("inventory_name");
			Integer required_job = (Integer) pnj_config.get("required_job");
			Boolean sell = (Boolean) pnj_config.get("sell");
			
			// Get items
			List<Map<?, ?>> items = (List<Map<?, ?>>) pnj_config.get("items");
			List<ItemForSale> item_list = new ArrayList<>();
			for (Map<?, ?> el2 : items) {
				Map<String, ?> item = (Map<String, ?>) el2;
				Material type = Material.getMaterial((String) item.get("type"));
				Integer amount = (Integer) item.get("amount");
				Integer price = (Integer) item.get("price");
				ItemForSale itemforsale = new ItemForSale(type, amount, price);
				itemforsale.setToSell(!sell);
				item_list.add(itemforsale);
			}
			
			result.put(name, new PNJ(name, inventory_name, required_job, sell, item_list));
		}
		return result;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInventory_name() {
		return inventory_name;
	}

	public void setInventory_name(String inventory_name) {
		this.inventory_name = inventory_name;
	}

	public int getRequiredJob() {
		return required_job;
	}

	public void setRequiredJob(int required_job) {
		this.required_job = required_job;
	}

	public boolean isSeller() {
		return sell;
	}

	public void setSell(boolean sell) {
		this.sell = sell;
	}

	public List<ItemForSale> getItems() {
		return items;
	}

	public void setItems(List<ItemForSale> items) {
		this.items = items;
	}

}
