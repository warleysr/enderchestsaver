package enderchestsaver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EnderChestSaver extends JavaPlugin {
	
	private static Plugin plugin;
	private static Connection conn;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		saveDefaultConfig();
		reloadConfig();
		
		String host = getConfig().getString("MySQL.host").trim(), user = getConfig().getString("MySQL.user").trim(),
			   pass = getConfig().getString("MySQL.pass"), db = getConfig().getString("MySQL.db").trim();
		int port = getConfig().getInt("MySQL.port");
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, pass);
			
			conn.prepareStatement("CREATE TABLE IF NOT EXISTS `enderchests` "
					+ "(`player` VARCHAR(16) PRIMARY KEY, `data` TEXT);").executeUpdate();
		} catch (SQLException e) {
			getLogger().severe("*** Nao foi possivel iniciar por causa do MySQL: \n" + e.getMessage());
			setEnabled(false);
			return;
		}
		
		int save = getConfig().getInt("tempo-salvar");
		
		new BukkitRunnable() {
			@Override
			public void run() {
				EnderChestManager.saveAllEnderChests();
			}
		}.runTaskTimerAsynchronously(plugin, save * 1200L, save * 1200L);
		
		Bukkit.getPluginManager().registerEvents(new EnderChestListener(), this);
	}
	
	@Override
	public void onDisable() {
		if (conn != null)
			EnderChestManager.saveAllEnderChests();
	}
	
	protected static Plugin getPlugin() {
		return plugin;
	}
	
	protected static Connection getConnection() {
		return conn;
	}

}
