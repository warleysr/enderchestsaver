package enderchestsaver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.kill3rtaco.tacoserialization.InventorySerialization;

public class EnderChestManager {
	
	private static HashMap<String, Inventory> enderchests = new HashMap<>();
	private static List<String> loading = new ArrayList<>();
	
	public static void saveAllEnderChests() {
		try {
			PreparedStatement st = EnderChestSaver.getConnection().prepareStatement("INSERT INTO `enderchests` "
					+ "(`player`, `data`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `data` = ?;");
			
			Iterator<Entry<String, Inventory>> it = enderchests.entrySet().iterator();
			
			while (it.hasNext()) {
				Entry<String, Inventory> entry = it.next();
				
				String data = InventorySerialization.serializeInventoryAsString(entry.getValue());
				
				st.setString(1, entry.getKey());
				st.setString(2, data);
				st.setString(3, data);
				
				st.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadEnderChest(Player p) {
		loading.add(p.getName());
		
		if (hasEnderChestSaved(p)) {
			ItemStack[] enderchest = getSavedEnderChest(p);
			
			if (enderchest != null)
				p.getEnderChest().setContents(enderchest);
		}
		
		enderchests.put(p.getName().toLowerCase(), p.getEnderChest());
		
		loading.remove(p.getName());
	}
	
	public static boolean enderChestLoaded(Player p) {
		return enderchests.containsKey(p.getName().toLowerCase());
	}
	
	public static boolean loadingEnderChest(Player p) {
		return loading.contains(p.getName());
	}
	
	public static boolean hasEnderChestSaved(Player p) {
		try {
			PreparedStatement st = EnderChestSaver.getConnection().prepareStatement("SELECT `player` FROM `enderchests` "
					+ "WHERE `player` = ?;");
			st.setString(1, p.getName());
			
			ResultSet rs = st.executeQuery();
			
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static ItemStack[] getSavedEnderChest(Player p) {
		try {
			PreparedStatement st = EnderChestSaver.getConnection().prepareStatement("SELECT `data` FROM  `enderchests` "
					+ "WHERE `player` = ?;");
			st.setString(1, p.getName());
			
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				String data = rs.getString("data");
				
				ItemStack[] items = InventorySerialization.getInventory(data, 27);
				
				return items;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static ItemStack[] getUnsavedEnderChest(Player p) {
		return p.getEnderChest().getContents();
	}

}
