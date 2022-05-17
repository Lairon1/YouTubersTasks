package com.lairon.plugin.youtuberstasks.task.realis;

import com.lairon.plugin.youtuberstasks.task.RegisteredTask;
import com.lairon.plugin.youtuberstasks.task.YTTask;
import com.lairon.plugin.youtuberstasks.utils.ItemStackUtils;
import com.lairon.plugin.youtuberstasks.utils.LocationUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Rhyme extends YTTask {

    private Location parkourLoc = new Location(Bukkit.getWorld("YTЖелания"), 298, 184, 1135);

    private ItemStack dirtApple = new ItemStack(Material.GOLDEN_APPLE){{
        ItemStackUtils.setDisplayName(this, "§6Земляное яблоко");
    }};
    private HashMap<Player, Location> playerLocations = new HashMap<>();

    public Rhyme(Plugin plugin, RegisteredTask registeredTask) {
        super(plugin, registeredTask);

        Bukkit.getPluginCommand("RhymeTaskStart").setExecutor(this::rhymeTaskStart);
        Bukkit.getPluginCommand("RhymeTaskEnd").setExecutor(this::rhymeTaskEnd);
    }

    @EventHandler
    public void onDirtAppleUse(PlayerItemConsumeEvent e){
        if(!ItemStackUtils.equalsItemsByName(e.getItem(), dirtApple)) return;
        e.setCancelled(true);
        e.getItem().setAmount(e.getItem().getAmount() - 1);
        e.getPlayer().getWorld().createExplosion(e.getPlayer().getLocation(), 4, false, false, e.getPlayer());
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            Vector vector = new Vector(random.nextInt(100) - 50, 0.5f + random.nextDouble(), random.nextInt( 100) - 50).normalize();
            FallingBlock fallingBlock = e.getPlayer().getWorld().spawnFallingBlock(e.getPlayer().getLocation().clone().add(0,1,0), Bukkit.createBlockData(Material.DIRT));
            fallingBlock.setVelocity(vector);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(fallingBlock.isDead()){
                        this.cancel();
                        return;
                    }

                    LocationUtils.blockRadiusSphere(fallingBlock.getLocation().clone(), 3).forEach(b -> {
                        if(b.getType() != Material.AIR && b.getType() != Material.BEDROCK) b.setType(Material.AIR);
                    });
                }
            }.runTaskTimer(getPlugin(), 1, 1);
            
        }
        
        
    }

    private boolean rhymeTaskEnd(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        Bukkit.getScheduler().runTaskLater(getPlugin(), ()->{
            if(playerLocations.containsKey(player)){
                player.teleport(playerLocations.get(player));
                playerLocations.remove(player);
            }
            ArrayList<Location> locations = (ArrayList<Location>) LocationUtils.blockRadiusEmptyHSphere(player.getLocation().clone(), 10, 20);
            for (int i = 0; i < locations.size(); i++) {
                Location loc = locations.get(i);
                Bukkit.getScheduler().runTaskLater(getPlugin(), ()->{
                    Firework firework = loc.getWorld().spawn(loc, Firework.class);
                    FireworkMeta meta = firework.getFireworkMeta();

                    meta.addEffect(FireworkEffect.builder().withColor(Color.BLUE).flicker(true).build());
                    firework.setFireworkMeta(meta);
                }, i * 5);
            }
            player.sendTitle("§aПобеда!", "");
            ItemStackUtils.giveItem(player, dirtApple);
        }, 100);

        return false;
    }

    private boolean rhymeTaskStart(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        playerLocations.put(player, player.getLocation().clone());

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
            isPart.set(false);
            player.teleport(parkourLoc);
            player.sendMessage("§7[§0Райм§7]: §fНа первый взгляд паркур выглядит сложным, да и на второй тоже.. УДАЧИ!");
        }, 300);
        return false;
    }


}
