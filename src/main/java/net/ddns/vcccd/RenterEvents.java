package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.entity.Villager;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityRemoveEvent;

public class RenterEvents implements Listener {
	
	@EventHandler
	public void onRenterInteract(EntityInteractEvent event) {
		try {
			if(event.getEntity().getCustomName().equals(ChatColor.translateAlternateColorCodes('&', "&a&lRenter")) && event.getEntity() instanceof Villager) {
				event.setCancelled(true);
			}
		} catch (Exception e) {
			return;
		}
	}

	@EventHandler
	public void renterDamage(EntityDamageEvent event) {
		try {
		if(event.getEntity().getCustomName().equals(ChatColor.translateAlternateColorCodes('&', "&a&lRenter")) && event.getEntity() instanceof Villager) {
			event.setCancelled(true);
		}
		} catch (Exception e) {
			return;
		}
	}
	
	@EventHandler
	public void renterDespawnEvent(EntityRemoveEvent event) {
		try {
		if(event.getEntity().getCustomName().equals(ChatColor.translateAlternateColorCodes('&', "&a&lRenter")) && event.getEntity() instanceof Villager) {
			((Cancellable) event).setCancelled(true);
		}
		} catch (Exception e) {
			return;
		}
	}
}
