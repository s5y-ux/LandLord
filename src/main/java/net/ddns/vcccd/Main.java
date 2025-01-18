package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin{
	
	
	private ConsoleCommandSender console = getServer().getConsoleSender();
	private String prefix = ChatColor.translateAlternateColorCodes('&', "&7[&aLandLord&7] - ");
	private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
	
	public ConsoleCommandSender getMainConsole() {
		return(console);
	}
	
	public Economy getEconomy() {
		return(this.econ);
	}
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            chat = rsp.getProvider();
            return chat != null;
        } else {
            getLogger().warning("Chat service provider is not available.");
            return false;
        }
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public String getPluginPrefix() {
    	return(prefix);
    }
	
	@Override
	public void onEnable() {
		FileConfiguration config = this.getConfig();
		config.addDefault("HomeRegistrationFee", 100);
		config.addDefault("DaysBeforeRent", 1);
		config.addDefault("RentMoney", 100);
		this.saveDefaultConfig();
		if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();
        getServer().getPluginManager().registerEvents(new FromScratch(this), this);
        getServer().getPluginManager().registerEvents(new Renter(this), this);
        this.getCommand("property").setExecutor(new PropertyWand(this));
		console.sendMessage(getPluginPrefix() + "The Landlord Plugin has beed Loaded...");
		console.sendMessage(getPluginPrefix() + "Please note that this does not mean all features will work");
		console.sendMessage(getPluginPrefix() + "If issues are found with the plugin, report the issue to:");
		console.sendMessage(getPluginPrefix() + ChatColor.GREEN + "");
		console.sendMessage(getPluginPrefix() + "Trust me, I WILL see it");
		
	}
	
	@Override
	public void onDisable() {
		
	}
}
