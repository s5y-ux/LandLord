package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class RenterRemoverWandEvents implements Listener {
	
	private final Main main;
	
	public RenterRemoverWandEvents(Main main) {
		this.main = main;
	}

	@EventHandler
	public void removeWand(PlayerInteractEntityEvent event) {
		try {
		String renterName = ChatColor.translateAlternateColorCodes('&', "&a&lRenter");
		Player player = event.getPlayer();
		if(event.getRightClicked() instanceof Villager) {
			Villager potentialRenter = (Villager) event.getRightClicked();
			Location removedLocation = potentialRenter.getLocation();
			if(potentialRenter.getCustomName().equals(renterName)) {
				potentialRenter.remove();
				player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Renter had been evicted!");
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
				player.getWorld().spawnParticle(Particle.CLOUD, removedLocation, 10);
			}
		}
		} catch (Exception e) {
			assert true;
		}
		
	}
}
