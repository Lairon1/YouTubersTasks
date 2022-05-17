package com.lairon.plugin.youtuberstasks.task.realis;

import com.lairon.plugin.youtuberstasks.task.RegisteredTask;
import com.lairon.plugin.youtuberstasks.task.YTTask;
import com.lairon.plugin.youtuberstasks.utils.EffectUtils;
import com.lairon.plugin.youtuberstasks.utils.ItemStackUtils;
import com.lairon.plugin.youtuberstasks.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Uni extends YTTask {


    // /execute in minecraft:tasksworld run tp @s -2056.47 63.00 12169.37 -492.10 49.12

    private final Location spawnLoc = new Location(Bukkit.getWorld("tasksWorld"), -2056.47, 63.00, 12169.37);
    private ArrayList<Player> taskPlayer = new ArrayList<>();
    private ArrayList<Material> killMaterials = new ArrayList<Material>(){{
        add(Material.GRASS_BLOCK);
        add(Material.OAK_LEAVES);
        add(Material.DARK_OAK_LEAVES);
    }};
    private ItemStack stick = new ItemStack(Material.STICK){{
        ItemStackUtils.setDisplayName(this, "§fКроличий посох");
    }};

    public Uni(Plugin plugin, RegisteredTask registeredTask) {
        super(plugin, registeredTask);
        spawnLoc.getWorld().setSpawnLocation(spawnLoc);
        Bukkit.getPluginCommand("UniTaskStart").setExecutor(this::uniTaskStart);
    }

    private boolean uniTaskStart(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        Bukkit.getScheduler().runTaskLater(getPlugin(), ()-> {
            player.teleport(spawnLoc);
            taskPlayer.add(player);
        }, 100);
        Bukkit.getScheduler().runTaskLater(getPlugin(), ()-> player.sendMessage("§7[§dЮни§7]: §fДоберись до деревни для начала следующего испытания!"), 200);
        return false;
    }

    @EventHandler
    public void onStickUse(PlayerInteractEvent e){
        if(!ItemStackUtils.equalsItemsByName(e.getItem(), stick)) return;
        ArrayList<Entity> targetEntity = LocationUtils.getTargetEntity(e.getPlayer(), 100);
        if(targetEntity.size() == 0) return;
        Entity entity = targetEntity.get(0);
        EffectUtils.drawParticleLine(
                entity.getLocation().clone().add(0,2,0),
                e.getPlayer().getLocation().clone().add(0,1.7f,0),
                1,
                Particle.CLOUD);
        entity.remove();
        Wolf wolf = entity.getWorld().spawn(entity.getLocation(), Wolf.class);
        wolf.setOwner(e.getPlayer());
        wolf.setSilent(true);
        wolf.setInvisible(true);

        Rabbit rabbit = entity.getWorld().spawn(entity.getLocation(), Rabbit.class);
        rabbit.setCustomName("Кролик убийца");

        new BukkitRunnable() {
            @Override
            public void run() {
                if(rabbit.isDead() || wolf.isDead()){
                    rabbit.remove();
                    wolf.remove();
                    this.cancel();
                }
                rabbit.teleport(wolf.getLocation());
            }
        }.runTaskTimer(getPlugin(), 1 , 1);

    }


    @EventHandler
    public void onDeathInTask(PlayerRespawnEvent e){
        if(!taskPlayer.contains(e.getPlayer())) return;
        e.setRespawnLocation(spawnLoc);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(!taskPlayer.contains(e.getPlayer())) return;
        if(killMaterials.contains(e.getTo().clone().subtract(0,1,0).getBlock().getType())) e.getPlayer().setHealth(0);
        else if(e.getTo().clone().subtract(0,1,0).getBlock().getType() == Material.GRASS_PATH){
            ItemStackUtils.giveItem(e.getPlayer(), stick);
            taskPlayer.remove(e.getPlayer());
            e.getPlayer().sendTitle("§aИспытание", "§aпройдено");
        }
    }



}
