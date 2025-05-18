package net.ddns.vcccd;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.md_5.bungee.api.ChatColor;

public class PropertyGreifPrevention implements Listener {
	
	private final Main main;
	
	public PropertyGreifPrevention(Main main) {
		this.main = main;
	}
	
	private boolean blockInProperty(Block block, propertyJSON playerProperty) {
		Location blockLocation = block.getLocation();
		boolean inRangeX = !(blockLocation.getX() < playerProperty.xRange.get(0) || blockLocation.getX() > playerProperty.xRange.get(1));
        boolean inRangeY = !(blockLocation.getY() < playerProperty.yRange.get(0) || blockLocation.getY() > playerProperty.yRange.get(1));
        boolean inRangeZ = !(blockLocation.getZ() < playerProperty.zRange.get(0) || blockLocation.getZ() > playerProperty.zRange.get(1));
		return(inRangeX && inRangeY && inRangeZ);
	}
	
	@EventHandler
	public void blockBreakOnProperty(BlockBreakEvent event) {
		
		List<propertyJSON> playerProperties = main.getPlayerPropertiesFile().loadJson(propertyJSON.class);
		Block blockBroken = event.getBlock();
		Player player = event.getPlayer();
		for(propertyJSON property: playerProperties) {
			if(blockInProperty(blockBroken, property)) {
				event.setCancelled(true);
				player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "You cannot break blocks inside of a property...");
			}
		}
		
	}
}
