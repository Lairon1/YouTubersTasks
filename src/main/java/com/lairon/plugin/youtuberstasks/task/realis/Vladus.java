package com.lairon.plugin.youtuberstasks.task.realis;

import com.lairon.plugin.youtuberstasks.task.RegisteredTask;
import com.lairon.plugin.youtuberstasks.task.YTTask;
import com.lairon.plugin.youtuberstasks.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class Vladus extends YTTask {

    private ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET){{
        ItemStackUtils.setDisplayName(this, "1 Здоровье)");
        ItemMeta meta = getItemMeta();
        meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID().toString(), -18, AttributeModifier.Operation.ADD_NUMBER));
        setItemMeta(meta);
        ItemStackUtils.addEnchantment(this, Enchantment.BINDING_CURSE, 1);
    }};

    private ArrayList<Player> helmetPlayers = new ArrayList<>();

    public Vladus(Plugin plugin, RegisteredTask registeredTask) {
        super(plugin, registeredTask);
        Bukkit.getPluginCommand("VladusTaskStart").setExecutor(this::vladusTaskStart);
    }

    private boolean vladusTaskStart(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;


        Bukkit.getScheduler().runTaskLater(getPlugin(), ()->{
            player.sendMessage("§7[§bВладус§7]: §fНастало время настоящего ХАРДКОРА.. докажи, что ты достоин звания НАСТОЯЩЕГО МАЙНКРАФТЕРА..");
            player.getInventory().setHelmet(helmet);
            helmetPlayers.add(player);
        }, 100);

        return false;
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        if(!helmetPlayers.contains(e.getPlayer())) return;
        e.getPlayer().getInventory().setHelmet(helmet);
    }

}
