package net.ddns.vcccd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PropertyWandEvents implements Listener {

	// Used for keeping track of player selection positions via the wand 
    private HashMap<UUID, Integer> positionTracker = new HashMap<>();
    
    // Made to be two blocks long. One it the bottom corner of the property and one is the top
    private HashMap<UUID, ArrayList<Block>> blocks = new HashMap<>();
    
    // Enumerates via a O(n^3) algorithm to gather all of the blocks in that cubic region
    private HashMap<UUID, ArrayList<Material>> allBlocks = new HashMap<>();
    
    // Used to reference main program
    private final Main main;
    
    // Used for the event cool-down so players don't select one block as a piece of property
    private HashMap<UUID, Long> cooldowns = new HashMap<>();

    // Private method for creating a circle of particles
    private void particles(Player player, Particle particle) {
    	World world = player.getWorld();
    	for(int i = 0; i < 360; i+=20) {
    		Location refLocal = player.getLocation();
    		refLocal.setX(refLocal.getX() + Math.cos(Math.toRadians(i)));
    		refLocal.setY(refLocal.getY() + Math.sin(Math.toRadians(i)));
    		world.spawnParticle(particle, refLocal, 10);
    	}
    }
    
    // Constructor for class to include main from Main
    public PropertyWandEvents(Main main) {
        this.main = main;
    }
    
    // function used to find if the selection overlaps a property
    private boolean selectionOverlapsProperty(int refPoints[][]) {
    	List<propertyJSON> playerProperties = main.getPlayerPropertiesFile().loadJson(propertyJSON.class);
    	for(propertyJSON playerProperty: playerProperties) {
		boolean inRangeX = !(lazyMax(refPoints[0]) < playerProperty.xRange.get(0) || lazyMin(refPoints[0]) > playerProperty.xRange.get(1));
        boolean inRangeY = !(lazyMax(refPoints[1]) < playerProperty.yRange.get(0) || lazyMin(refPoints[1]) > playerProperty.yRange.get(1));
        boolean inRangeZ = !(lazyMax(refPoints[2]) < playerProperty.zRange.get(0) || lazyMin(refPoints[2]) > playerProperty.zRange.get(1));
		if(inRangeX && inRangeY && inRangeZ) {
			return true;
		 }
    	}
    	return false;
    }
    
    private boolean playerHasTooManyHomes(Player player) {
    	List<propertyJSON> playerProperties = main.getPlayerPropertiesFile().loadJson(propertyJSON.class);
    	if(playerProperties == null) {
    		return false;
    	}
    	int homeCount = 0;
    	for(propertyJSON property: playerProperties) {
    		if(property.playerUUID.equals(player.getUniqueId())) {
    			homeCount++;
    		}
    	}
    	return(homeCount >= 9);
    }
    
    //
    private void purchaseSuccess(Player player, int refPoints[][], World world, UUID playerId) {
    	main.getEconomy().withdrawPlayer(player, main.getConfig().getDouble("HomeRegistrationFee"));
    	player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Deed purchased sucessfully!");
    	player.sendMessage(main.getPluginPrefix() + String.format(ChatColor.translateAlternateColorCodes('&', "Your balance is now: &f[&a$%.2f&f]"), main.getEconomy().getBalance(player)));
    	particles(player, Particle.CLOUD);
    	player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
    	new Renter(main).RenterEgg(player);
    	player.sendTitle(ChatColor.GREEN + "Congratulations!", ChatColor.GRAY + "You now own a Home!", 5, 40, 5);
    	player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    	ArrayList<Integer> xRange = new ArrayList<Integer>(java.util.List.of(lazyMin(refPoints[0]), lazyMax(refPoints[0])));
    	ArrayList<Integer> yRange = new ArrayList<Integer>(java.util.List.of(lazyMin(refPoints[1]), lazyMax(refPoints[1])));
    	ArrayList<Integer> zRange = new ArrayList<Integer>(java.util.List.of(lazyMin(refPoints[2]), lazyMax(refPoints[2])));
    	this.main.getPlayerPropertiesFile().saveJson(new propertyJSON(world.getName(), playerId, xRange, yRange, zRange));
    }

    // Actual event handler method for selecting property
    @EventHandler
    public void useWandEvent(PlayerInteractEvent event) {
    	
    	// A whole bunch of variables for easy access in the program
        Player player = event.getPlayer();
        double playerBalance = main.getEconomy().getBalance(player);
        UUID playerId = player.getUniqueId();
        World world = player.getWorld();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Block selectedBlock = event.getClickedBlock();
        boolean playerRightClickedWithStick = event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && heldItem.getType().equals(Material.STICK) && heldItem.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&aProperty Selector"));
        

        // Cool-down of 500ms
        if (cooldowns.containsKey(playerId) && (System.currentTimeMillis() - cooldowns.get(playerId)) < 500) {
            return;
        }
        cooldowns.put(playerId, System.currentTimeMillis());

        // On the condition that the player right clicks a block with the wand
        if (playerRightClickedWithStick) {
        	if(!positionTracker.containsKey(playerId)) {
        		
        		// Determines position of current selection based on Hashmap keeping track of player selections
        		positionTracker.put(playerId, 1);
        	} else {
        		positionTracker.put(playerId, positionTracker.get(playerId) + 1);
        	}
        	
        	// Add the selected block to the Arraylist found in the Hashmap with the player UUID of current blocks selected
        	if(!blocks.containsKey(playerId)) {
        		ArrayList<Block> refBlockList = new ArrayList<>();
        		refBlockList.add(selectedBlock);
        		blocks.put(playerId, refBlockList);
        	} else {
        		ArrayList<Block> refBlockList = blocks.get(playerId);
        		refBlockList.add(selectedBlock);
        		blocks.put(playerId, refBlockList);
        	}
        	
        	if(!allBlocks.containsKey(playerId)){
        		allBlocks.put(playerId, new ArrayList<Material>());
        	}
            
            // Informs player of selection position
            player.sendMessage(String.format(ChatColor.translateAlternateColorCodes('&', main.getPluginPrefix() + "Position %d set"), positionTracker.get(playerId)));
            player.sendMessage(String.format(ChatColor.translateAlternateColorCodes('&', main.getPluginPrefix() + "Block - X: &f%d&7, Y: &f%d&7, Z: &f%d&7"), event.getClickedBlock().getX(), event.getClickedBlock().getY(), event.getClickedBlock().getZ()));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            
            // If we have made all the selections
            if (blocks.get(playerId).size() >= 2) {
            	
            	// Convert the reference points (two selections) into a multidimensional integer array of coordinates
                int refPoints[][] = {
                    {blocks.get(playerId).get(0).getX(), blocks.get(playerId).get(1).getX()},
                    {blocks.get(playerId).get(0).getY(), blocks.get(playerId).get(1).getY()},
                    {blocks.get(playerId).get(0).getZ(), blocks.get(playerId).get(1).getZ()}
                };
                
                // Iterate from the minimum coordinate to maximum coordinate to capture all blocks in cubic area
                for (int Z = lazyMin(refPoints[2]); Z <= lazyMax(refPoints[2]); Z++) {
                    for (int Y = lazyMin(refPoints[1]); Y <= lazyMax(refPoints[1]); Y++) {
                        for (int X = lazyMin(refPoints[0]); X <= lazyMax(refPoints[0]); X++) {
                            allBlocks.get(playerId).add(world.getBlockAt(X, Y, Z).getType());
                        }
                    }
                }
                
                if(playerHasTooManyHomes(player)) {
                	player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "You have too many homes...");
                	return;
                }
                
                // Stipulations for acceptable property
                if (allBlocks.get(playerId).contains(Material.OAK_DOOR) && allBlocks.get(playerId).contains(Material.WHITE_BED) && allBlocks.get(playerId).contains(Material.CRAFTING_TABLE)) {
                	
                	// If the registration fee for a home is more than the player balance
                    if(main.getConfig().getInt("HomeRegistrationFee") > playerBalance) {
                    	
                    	// Handle the fact that they are broke
                    	player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Insufficient funds, must have at least: " + ChatColor.GREEN + "$" + String.valueOf(main.getConfig().getInt("HomeRegistrationFee")));
                    	particles(player, Particle.SMOKE_NORMAL);
                    	player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                    	
                    } else {
                    	
                    	// If they are not broke, allow them the opportunity to ponder their investment in the property
                    	try {
                    		if(selectionOverlapsProperty(refPoints)) {
                    			player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Selection overlaps another property...");;
                    		} else {
                    			purchaseSuccess(player, refPoints, world, playerId);
                    		}
                    	} catch (Exception e) {
                    		purchaseSuccess(player, refPoints, world, playerId);
                    	}
                    	
                    }
                } 
                
                // Code for handling when stipulations for what is considered a home is not met
                else {
                    player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Missing requirements to be considered a home...");
                    player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Items needed for a home: Oak door, White bed, Crafting table");
                }

                // Clear entire selection
                blocks.get(playerId).clear();
                allBlocks.get(playerId).clear();
                positionTracker.put(playerId, 0);
            }
        }
    }

    // Used to find Minimum element in an array
    private int lazyMin(int[] arr) {
        return (Arrays.stream(arr).min().getAsInt());
    }

    // Used to find Maximum element in an array
    private int lazyMax(int[] arr) {
        return (Arrays.stream(arr).max().getAsInt());
    }
}

