package enderchestsaver;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EnderChestListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		if (!(EnderChestManager.enderChestLoaded(p))) {
			new Thread() {
				@Override
				public void run() {
					EnderChestManager.loadEnderChest(p);			
				}
			}.start();
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if ((e.getAction() == Action.RIGHT_CLICK_BLOCK) && (e.getClickedBlock().getType() == Material.ENDER_CHEST)) {
			Player p = e.getPlayer();
			
			if (EnderChestManager.loadingEnderChest(p) || !(EnderChestManager.enderChestLoaded(p))) {
				e.setCancelled(true);
				String msg = EnderChestSaver.getPlugin().getConfig().getString("carregando");
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
				
			}
		}
	}

}
