package me.appl3.partycontrol;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.naming.Name;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

public class PartyControl extends JavaPlugin implements Listener {
    private ConfigManager config;

    private final Material[] armourPiece = { Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS };
    private final String[] armourName = { "Helmet", "Chestplate", "Leggings", "Boots" };

    private double colourCycle = 0;
    private String backpackText = "&c&lBackpack &7(Right-click to Open)";
    private String idText = "&7&oID:&6&o ";
    private String backpackMenuText = "&0Backpack - ID:&6 ";

    public void onEnable() {
        loadConfigManager();

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7> &aPartyControl has been Enabled!"));
        Bukkit.getPluginManager().registerEvents(this, this);

        for (int i = 0; i < armourPiece.length; i++) {
            Bukkit.removeRecipe(new NamespacedKey(this, armourName[i]));
        }
        Bukkit.removeRecipe(new NamespacedKey(this, "Backpack"));

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Color c = Color.fromRGB((int)getRedValue(), (int)getGreenValue(), (int)getBlueValue());

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    for (int i = 0; i < armourPiece.length; i++) {
                        ItemStack item = null;

                        switch(armourName[i]) {
                            case "Helmet": item = player.getInventory().getHelmet(); break;
                            case "Chestplate": item = player.getInventory().getChestplate(); break;
                            case "Leggings": item = player.getInventory().getLeggings(); break;
                            case "Boots": item = player.getInventory().getBoots(); break;
                        }

                        if (item != null && item.getType() == armourPiece[i] && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&b&lParty " + armourName[i] + " &7(Right-click to Wear)"))) {
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b&lParty " + armourName[i] + " &7(Right-click to Wear)"));
                            item.setItemMeta(meta);

                            switch(armourName[i]) {
                                case "Helmet": player.getInventory().setHelmet(getColorArmor(item, c)); break;
                                case "Chestplate": player.getInventory().setChestplate(getColorArmor(item, c)); break;
                                case "Leggings": player.getInventory().setLeggings(getColorArmor(item, c)); break;
                                case "Boots": player.getInventory().setBoots(getColorArmor(item, c)); break;
                            }
                        }

                        //if (item != null) {

                        //}
                    }
                }
            }
        }, 0, 1);

        for (int i = 0; i < armourPiece.length; i++) {
            ArrayList<String> lore = new ArrayList<>();
            ItemStack item = new ItemStack(armourPiece[i], 1);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b&lParty " + armourName[i] + " &7(Right-click to Wear)"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&fTurn your world into a disco."));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&c6-Year Anniversary Special"));
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);

            ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, armourName[i]), item);
            recipe.addIngredient(armourPiece[i]);
            recipe.addIngredient(Material.APPLE);

            this.getServer().addRecipe(recipe);
        }

        // Backpack Recipes
        ItemStack backpack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) backpack.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA4N2M2NWQ3YmRlNjY1YjZlMTk1ZThkY2ZjMjFmNGFkZGNmOTJhOTA3MTQwYzM3ZDQ3NGMxMmFjY2Y3YWIifX19"));
        Field field;

        try {
            field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, profile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', backpackText));
        backpack.setItemMeta(meta);

        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, "Backpack"), backpack);
        recipe.addIngredient(Material.CHEST);
        recipe.addIngredient(8, Material.LEATHER);

        this.getServer().addRecipe(recipe);
    }

    public void loadConfigManager() {
        config = new ConfigManager();
        config.setup();
        config.saveBackpacks();
        config.reloadBackpacks();
    }

    private ItemStack getColorArmor(ItemStack m, Color c) {
        LeatherArmorMeta meta = (LeatherArmorMeta) m.getItemMeta();
        meta.setColor(c);
        m.setItemMeta(meta);
        return m;
    }

    public double getRedValue() {
        if (colourCycle == 360)
            colourCycle = 0;
        else
            colourCycle += 0.1;
        return 127 * (Math.cos(colourCycle) + 1);
    }

    public double getGreenValue() {
        return 127 * (Math.sin(2 * colourCycle - (Math.PI / 2)) + 1);
    }

    public double getBlueValue() {
        return 127 * (-Math.cos(colourCycle) + 1);
    }

    public void spawnFirework(Player player) {
        Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.withTrail();
        builder.withFlicker();
        builder.withColor(Color.RED);
        builder.with(FireworkEffect.Type.BURST);
        meta.addEffects(new FireworkEffect[] { builder.build() });
        meta.setPower(1);
        firework.setFireworkMeta(meta);
    }

    // Converts ampersands into symbols used for color coding.
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void openBackpack(Player player, ItemStack backpack, int id) {
        int itemPosition = 0;
        Inventory inventory = getServer().createInventory(null, 36, ChatColor.translateAlternateColorCodes('&', backpackMenuText + id));

        if (config.doesExist(id)) {
            for (String index : config.getBackpacks().getConfigurationSection("backpacks." + id).getKeys(false)) {
                ItemStack item = config.getBackpacks().getItemStack("backpacks." + id + "." + index);
                inventory.setItem(itemPosition, item);
                itemPosition++;
            }
        } else {
            ItemMeta meta = backpack.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', idText + id));
            meta.setLore(lore);
            backpack.setItemMeta(meta);
        }
        player.openInventory(inventory);
    }

    public void closeBackpack(Player player, Inventory inventory, int id) {
        ItemStack[] items = inventory.getContents();

        if (config.doesExist(id))
            config.getBackpacks().set("backpacks." + id, null);

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                config.addToBackpack(items[i], i, id);
                config.saveBackpacks();
            }
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        int partyAmourCount = 0;
        Player player = event.getPlayer();

        if (player.isSneaking()) {
            for (int i = 0; i < armourPiece.length; i++) {
                ItemStack item = null;

                switch (armourName[i]) {
                    case "Helmet": item = player.getInventory().getHelmet(); break;
                    case "Chestplate": item = player.getInventory().getChestplate(); break;
                    case "Leggings": item = player.getInventory().getLeggings(); break;
                    case "Boots": item = player.getInventory().getBoots(); break;
                }

                if (item != null && item.getType() == armourPiece[i] && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&b&lParty " + armourName[i] + " &7(Right-click to Wear)")))
                    partyAmourCount++;
            }

            if (partyAmourCount == armourPiece.length) {
                // Spawn fireworks
                spawnFirework(player);
                String message = ChatColor.translateAlternateColorCodes('&', "&c&lHAPPY 6 YEARS!");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            }
        }
    }

    // TODO: Create backpack inventory in config.
    // TODO: When closing inventory, update contents of inventory in config.
    // TODO: When opening inventory, get contents of inventory in config.

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity player = event.getPlayer();
        InventoryView inventory = event.getView();

        // Interact with the inventory as normal if item has no meta data or does not exist.
        if (inventory == null) { return; }

        if (inventory.getTitle().contains(ChatColor.translateAlternateColorCodes('&', backpackMenuText))) {
            int id = Integer.parseInt(inventory.getTitle().replace(ChatColor.translateAlternateColorCodes('&', backpackMenuText), ""));
            closeBackpack((Player) player, inventory.getTopInventory(), id);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;

        if (item.getType() == Material.PLAYER_HEAD) {
            if (item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', backpackText))) {
                event.setCancelled(true);

                ArrayList<String> lore = new ArrayList<>();
                lore = (ArrayList<String>) item.getItemMeta().getLore();

                if (lore != null) {
                    for (int i = 0; i < lore.size(); i++) {
                        if (lore.get(i).contains(ChatColor.translateAlternateColorCodes('&', idText))) {
                            String id = lore.get(i).replace(ChatColor.translateAlternateColorCodes('&', idText), "");
                            //for (int j = 0; j < config.getNumberOfBackpacks(); j++) {
                            //    if (Integer.parseInt(id) == j);
                                    openBackpack(player, item, Integer.parseInt(id));
                            //}
                        }
                    }
                } else {
                    // Create new backpack inventory.
                    openBackpack(player, item, config.getNumberOfBackpacks() + 1);
                }
            }
        }
    }
}
