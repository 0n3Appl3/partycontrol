package me.appl3.partycontrol;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.naming.Name;
import java.util.ArrayList;

public class PartyControl extends JavaPlugin implements Listener {

    private final Material[] armourPiece = { Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS };
    private final String[] armourName = { "Helmet", "Chestplate", "Leggings", "Boots" };

    private double colourCycle = 0;

    public void onEnable() {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7> &aPartyControl has been Enabled!"));
        Bukkit.getPluginManager().registerEvents(this, this);

        for (int i = 0; i < armourPiece.length; i++) {
            Bukkit.removeRecipe(new NamespacedKey(this, armourName[i]));
        }

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
}
