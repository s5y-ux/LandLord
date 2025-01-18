package net.ddns.vcccd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class FromScratch implements Listener {

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

	private void RenterEgg(Player player) {
		ItemStack renterEgg = new ItemStack(Material.EGG);
		ItemMeta renterMeta = renterEgg.getItemMeta();
		ArrayList<String> ItemLore = new ArrayList<String>();
		ItemLore.add(ChatColor.GRAY + "Throw at the ground in a property");
		ItemLore.add(ChatColor.GRAY + "to place your renter...");
		renterMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aRenter Spawn Egg"));
		renterMeta.setLore(ItemLore);
		renterEgg.setItemMeta(renterMeta);
		player.getInventory().addItem(renterEgg);
	}

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
    public FromScratch(Main main) {
        this.main = main;
    }

    // Actual event handler method for selecting property
    @EventHandler
    public void interactEvent(PlayerInteractEvent event) {
    	
    	// A whole bunch of variables for easy access in the program
        Player player = event.getPlayer();
        double playerBalance = main.getEconomy().getBalance(player);
        UUID playerId = player.getUniqueId();
        World world = player.getWorld();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Block selectedBlock = event.getClickedBlock();

        // Cool-down of 500ms
        if (cooldowns.containsKey(playerId) && (System.currentTimeMillis() - cooldowns.get(playerId)) < 500) {
            return;
        }
        cooldowns.put(playerId, System.currentTimeMillis());

        // On the condition that the player right clicks a block with the wand
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && heldItem.getType().equals(Material.STICK) && heldItem.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&aProperty Selector"))) {
        	if(!positionTracker.containsKey(playerId)) {
        		
        		// Determines position of current selection based on Hashmap keeping track of player selections
        		positionTracker.put(playerId, 1);
        	} else {
        		positionTracker.put(playerId, positionTracker.get(playerId) + 1);
        	}
        	
        	// Add the selected block to the Arraylist of current blocks
        	// TODO URGENT: You have to convert this into a Hashmap with the player UUID 
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
                    	main.getEconomy().withdrawPlayer(player, main.getConfig().getInt("HomeRegistrationFee"));
                    	player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Deed purchased sucessfully!");
                    	player.sendMessage(main.getPluginPrefix() + String.format(ChatColor.translateAlternateColorCodes('&', "Your balance is now: &f[&a$%.2f&f]"), main.getEconomy().getBalance(player)));
                    	particles(player, Particle.CLOUD);
                    	player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
                    	RenterEgg(player);
                    	player.sendTitle(ChatColor.GREEN + "Congratulations!", ChatColor.GRAY + "You now own a Home!", 5, 40, 5);
                    	player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    }
                } 
                
                // Code for handling when stipulations for what is considered a home is not met
                else {
                    player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Missing requirements to be considered a home...");
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

