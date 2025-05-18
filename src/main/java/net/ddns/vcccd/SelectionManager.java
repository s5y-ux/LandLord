package net.ddns.vcccd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class SelectionManager {
    private final Map<UUID, Integer> positionTracker = new HashMap<>();
    private final Map<UUID, List<Block>> blocks = new HashMap<>();
    private final Map<UUID, List<Material>> allBlocks = new HashMap<>();

    // Track the current selection position for a player
    public int getPosition(UUID playerId) {
        return positionTracker.getOrDefault(playerId, 0);
    }

    public void incrementPosition(UUID playerId) {
        positionTracker.put(playerId, getPosition(playerId) + 1);
    }

    public void resetPosition(UUID playerId) {
        positionTracker.put(playerId, 0);
    }

    // Manage block selections
    public void addBlock(UUID playerId, Block block) {
        blocks.computeIfAbsent(playerId, k -> new ArrayList<>()).add(block);
    }

    public List<Block> getBlocks(UUID playerId) {
        return blocks.getOrDefault(playerId, new ArrayList<>());
    }

    public void clearBlocks(UUID playerId) {
        List<Block> list = blocks.get(playerId);
        if (list != null) list.clear();
    }

    // Manage allBlocks (materials in selection)
    public void addMaterial(UUID playerId, Material material) {
        allBlocks.computeIfAbsent(playerId, k -> new ArrayList<>()).add(material);
    }

    public List<Material> getMaterials(UUID playerId) {
        return allBlocks.getOrDefault(playerId, new ArrayList<>());
    }

    public void clearMaterials(UUID playerId) {
        List<Material> list = allBlocks.get(playerId);
        if (list != null) list.clear();
    }

    // Utility: get selection size
    public int getSelectionSize(UUID playerId) {
        return getMaterials(playerId).size();
    }

    // Utility: get reference points as int[][]
    public int[][] getRefPoints(UUID playerId) {
        List<Block> playerBlocks = getBlocks(playerId);
        if (playerBlocks.size() < 2) return null;
        return new int[][] {
            {playerBlocks.get(0).getX(), playerBlocks.get(1).getX()},
            {playerBlocks.get(0).getY(), playerBlocks.get(1).getY()},
            {playerBlocks.get(0).getZ(), playerBlocks.get(1).getZ()}
        };
    }

    // Utility: min/max helpers
    public int lazyMin(int[] arr) {
        return Arrays.stream(arr).min().orElse(Integer.MIN_VALUE);
    }

    public int lazyMax(int[] arr) {
        return Arrays.stream(arr).max().orElse(Integer.MAX_VALUE);
    }

    // Clear all selection data for a player
    public void clearAll(UUID playerId) {
        clearBlocks(playerId);
        clearMaterials(playerId);
        resetPosition(playerId);
    }
}