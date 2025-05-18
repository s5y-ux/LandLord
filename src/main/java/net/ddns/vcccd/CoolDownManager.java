package net.ddns.vcccd;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class CoolDownManager {

    private HashMap<UUID, Long> cooldowns = new HashMap<>();

    public void add(Player player, long cooldown) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        cooldowns.put(playerId, currentTime + cooldown);
    }

    public boolean isOnCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        return cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime;
    }
    
}
