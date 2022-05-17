package com.lairon.plugin.youtuberstasks.task.realis;

import com.lairon.plugin.youtuberstasks.task.RegisteredTask;
import com.lairon.plugin.youtuberstasks.task.YTTask;
import com.lairon.plugin.youtuberstasks.utils.ItemStackUtils;
import com.lairon.plugin.youtuberstasks.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Felix extends YTTask {

    private Location spawnLoc = new Location(Bukkit.getWorld("YTЖелания"), 3000000, 81, 3000030);

    private HashMap<Player, Location> playerLocations = new HashMap<>();
    private ItemStack bow = new ItemStack(Material.BOW){{
        ItemStackUtils.setDisplayName(this, "§cЛук Феликса");
    }};

    public Felix(Plugin plugin, RegisteredTask registeredTask) {
        super(plugin, registeredTask);
        Bukkit.getPluginCommand("FelixTaskStart").setExecutor(this::felixTaskStart);
        Bukkit.getPluginCommand("FelixTaskEnd").setExecutor(this::felixTaskEnd);
    }

    private boolean felixTaskEnd(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        Bukkit.getScheduler().runTaskLater(getPlugin(), ()->{
            if(playerLocations.containsKey(player)){
                player.teleport(playerLocations.get(player));
                playerLocations.remove(player);
            }
        }, 100);
        return false;
    }

    private boolean felixTaskStart(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        AtomicBoolean isPart = new AtomicBoolean(true);
        Bukkit.getScheduler().runTaskLater(getPlugin(), ()->player.sendMessage("§cНастало время испытания.."), 100);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location loc : LocationUtils.blockRadiusEmptySphere(player.getLocation().clone(), 3, 3))
                    loc.getWorld().spawnParticle(Particle.CLOUD, loc, 0);
                if(!isPart.get()){
                    this.cancel();
                    return;
                }
            }
        }.runTaskTimer(getPlugin(), 100, 10);

        Bukkit.getScheduler().runTaskLater(getPlugin(), ()->{
            player.sendMessage("§7[§bФеликс§7]: §fНастало время проверить.. насколько ты СИЛЕН..");
            ItemStackUtils.giveItem(player, bow);
            isPart.set(false);
            player.teleport(spawnLoc);
        }, 200);



        return false;
    }

    @EventHandler
    public void onBowUse(EntityShootBowEvent e){
        if(e.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player) e.getEntity();
        if(!ItemStackUtils.equalsItemsByName(e.getBow(), bow)) return;
        e.setCancelled(true);
        Location loc = player.getLocation().clone().add(0,1.7f,0);
        for (int pitch = -10; pitch < 11; pitch+=10) {
            Location loc2 = loc.clone();
            loc2.setPitch(loc.getPitch() + pitch);
            Arrow arrow = loc2.getWorld().spawnArrow(loc2, loc2.getDirection(), e.getForce() * 3, 0);
            arrow.setShooter(player);
            arrow.setFireTicks(9999999);
        }
    }

}
