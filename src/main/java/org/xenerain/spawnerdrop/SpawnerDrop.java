package org.xenerain.spawnerdrop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnerDrop extends JavaPlugin implements Listener {

    private Material harvestItem;
    private Enchantment harvestEnchantment;
    private int enchantmentLevel;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("SpawnerDrop включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SpawnerDrop выключен!");
    }

    private void loadConfig() {
        harvestItem = Material.getMaterial(getConfig().getString("item", "DIAMOND_PICKAXE"));
        String enchantmentType = getConfig().getString("enchantment.type", "SILK_TOUCH");
        harvestEnchantment = Enchantment.getByName(enchantmentType);
        enchantmentLevel = getConfig().getInt("enchantment.level", 1);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (block.getType() == Material.SPAWNER) {
            if (itemInHand.getType() == harvestItem && itemInHand.containsEnchantment(harvestEnchantment) && itemInHand.getEnchantmentLevel(harvestEnchantment) >= enchantmentLevel) {
                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                ItemStack item = new ItemStack(Material.SPAWNER);
                BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
                meta.setBlockState(spawner);
                item.setItemMeta(meta);
                block.getWorld().dropItemNaturally(block.getLocation(), item);
                block.setType(Material.AIR);  // Remove the spawner block after breaking
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() == Material.SPAWNER) {
            Block block = event.getBlock();
            if (block.getState() instanceof CreatureSpawner) {
                BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                spawner.setSpawnedType(((CreatureSpawner) meta.getBlockState()).getSpawnedType());
                spawner.update();
            }
        }
    }
}
