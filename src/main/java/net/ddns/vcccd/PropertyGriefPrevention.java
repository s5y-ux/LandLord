package net.ddns.vcccd;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import net.md_5.bungee.api.ChatColor;

public class PropertyGriefPrevention implements Listener {
	
	private final Main main;
	
	public PropertyGriefPrevention(Main main) {
		this.main = main;
		//Test
	}
	
	private boolean blockInProperty(Block block, propertyJSON playerProperty) {
		Location blockLocation = block.getLocation();
		boolean inRangeX = !(blockLocation.getX() < playerProperty.xRange.get(0) || blockLocation.getX() > playerProperty.xRange.get(1));
        boolean inRangeY = !(blockLocation.getY() < playerProperty.yRange.get(0) || blockLocation.getY() > playerProperty.yRange.get(1));
        boolean inRangeZ = !(blockLocation.getZ() < playerProperty.zRange.get(0) || blockLocation.getZ() > playerProperty.zRange.get(1));
		return(inRangeX && inRangeY && inRangeZ);
	}

	private boolean entityInProperty(Entity entity, propertyJSON playerProperty) {
		Location blockLocation = entity.getLocation();
		boolean inRangeX = !(blockLocation.getX() < playerProperty.xRange.get(0) || blockLocation.getX() > playerProperty.xRange.get(1));
        boolean inRangeY = !(blockLocation.getY() < playerProperty.yRange.get(0) || blockLocation.getY() > playerProperty.yRange.get(1));
        boolean inRangeZ = !(blockLocation.getZ() < playerProperty.zRange.get(0) || blockLocation.getZ() > playerProperty.zRange.get(1));
		return(inRangeX && inRangeY && inRangeZ);
	}
	
	@EventHandler
	public void blockBreakOnProperty(BlockBreakEvent event) {
		try {
		List<propertyJSON> playerProperties = main.getPlayerPropertiesFile().loadJson(propertyJSON.class);
		Block blockBroken = event.getBlock();
		Player player = event.getPlayer();
		for (propertyJSON property : playerProperties) {
			if (blockInProperty(blockBroken, property)) {
			event.setCancelled(true);
			player.sendMessage(main.getPluginPrefix() + ChatColor.RED
				+ "You cannot break blocks inside of a property...");
			}
		}

		} catch (NullPointerException e) {
			// Exception handled silently
		}
	}

	@EventHandler
	public void blockPlaceOnProperty(BlockPlaceEvent event) {
		try {
		List<propertyJSON> playerProperties = main.getPlayerPropertiesFile().loadJson(propertyJSON.class);
		Block blockPlaced = event.getBlock();
		Player player = event.getPlayer();
		for (propertyJSON property : playerProperties) {
			if (blockInProperty(blockPlaced, property)) {
			event.setCancelled(true);
			player.sendMessage(main.getPluginPrefix() + ChatColor.RED
				+ "You cannot place blocks inside of a property...");
			}
		}

		} catch (NullPointerException e) {
			// Exception handled silently
		}
	}

	@EventHandler
	public void onExplodeInProperty(ExplosionPrimeEvent event){
		try {
		List<propertyJSON> playerProperties = main.getPlayerPropertiesFile().loadJson(propertyJSON.class);
		Entity exploder = event.getEntity();
		for (propertyJSON property : playerProperties) {
			if (entityInProperty(exploder, property)) {
			event.setCancelled(true);
			exploder.remove();
			}
		}

		} catch (NullPointerException e) {
			// Exception handled silently
		}
	}

	@EventHandler
	public void lavaCast(BlockFormEvent event){
		try {
		List<propertyJSON> playerProperties = main.getPlayerPropertiesFile().loadJson(propertyJSON.class);
		Block blockPlaced = event.getBlock();
		for (propertyJSON property : playerProperties) {
			if (blockInProperty(blockPlaced, property)) {
			event.setCancelled(true);
			}
		}

		} catch (NullPointerException e) {
			// Exception handled silently
		}
	}
}
