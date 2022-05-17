package com.lairon.plugin.youtuberstasks.task.realis;

import com.lairon.plugin.youtuberstasks.task.RegisteredTask;
import com.lairon.plugin.youtuberstasks.task.YTTask;
import com.lairon.plugin.youtuberstasks.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Consumer;

public class Shaggy extends YTTask {

    private ItemStack armorItem = new ItemStack(Material.FLINT){{
        //Радужная броня
        ItemStackUtils.setDisplayName(this, "§dРадужная броня");
    }};

    private ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET){{
        //Радужный шлем
        ItemStackUtils.setDisplayName(this, "§dРадужный шлем");
    }};
    private ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE){{
        //Радужный нагрудник
        ItemStackUtils.setDisplayName(this, "§dРадужный нагрудник");
    }};
    private ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS){{
        //Радужные поножи
        ItemStackUtils.setDisplayName(this, "§dРадужные поножи");
    }};
    private ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS){{
        //Радужные ботинки
        ItemStackUtils.setDisplayName(this, "§dРадужные ботинки");
    }};


    public Shaggy(Plugin plugin, RegisteredTask registeredTask) {
        super(plugin, registeredTask);

        Bukkit.getPluginCommand("ShaggyTaskStart").setExecutor(this::shaggyTaskStart);
        Bukkit.getPluginCommand("Rename").setExecutor(this::rename);
        Bukkit.getPluginCommand("ShaggyGiveArmor").setExecutor(this::shaggyGiveArmor);

    }

    private Random random = new Random();

    @EventHandler
    public void onArmorItemUse(PlayerInteractEvent e){
        if(!ItemStackUtils.equalsItemsByName(e.getItem(), armorItem)) return;
        ArmorUtils.setArmor(e.getPlayer(), helmet, chestplate, leggings, boots);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!ArmorUtils.isArmor(e.getPlayer(), helmet, chestplate, leggings, boots)){
                    this.cancel();
                    return;
                }
                EffectUtils.startGroundLinesRGBEffect(e.getPlayer(), 20);
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 1));
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20, 2));
            }
        }.runTaskTimer(getPlugin(), 1, 20);
        CustomNPC sha = new CustomNPC(e.getPlayer().getLocation().clone().add(1,0,1), "iShaggyStarZz");
        sha.spawn();
        sha.skin("iShaggyStarZz").protection(false).setOnClickListener(event -> {
            sha.protection(true);
            sha.getEntity().setVelocity(new Vector(0,3,0));
            Bukkit.getScheduler().runTaskLater(getPlugin(), ()->{
                sha.getEntity().setVelocity(new Vector(0,-3,0));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(sha.getEntity() == null){
                            this.cancel();
                            return;
                        }
                        if(!sha.getEntity().isOnGround()) return;
                        sha.getEntity().getWorld().createExplosion(sha.getEntity().getLocation().clone(), 5, false, false, event.getPlayer());
                        for (Block block : LocationUtils.blockRadiusSphere(sha.getEntity().getLocation().clone(), 5)) {
                            if(block.getType().isAir()) continue;
                            if(block.getType() == Material.BEDROCK) continue;
                            BlockData blockData = Bukkit.createBlockData(block.getType());
                            block.breakNaturally();
                            Vector vector = new Vector(random.nextInt(100) - 50, 4, random.nextInt( 100) - 50).normalize().multiply(3);
                            FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), blockData);
                            fallingBlock.setVelocity(vector);
                        }
                        Bukkit.getScheduler().runTaskLater(getPlugin(), ()-> sha.protection(false), 10);
                        this.cancel();
                    }
                }.runTaskTimer(getPlugin(), 1, 1);


            }, 30);


        });;

        new BukkitRunnable() {
            @Override
            public void run() {
                if(sha == null){
                    this.cancel();
                    return;
                }
                if(sha.getEntity() == null){
                    this.cancel();
                    return;
                }
                if(sha.getEntity().isDead()){
                    this.cancel();
                    return;
                }
                sha.getNavigator().setTarget(e.getPlayer(), false);
            }
        }.runTaskTimer(getPlugin(), 1, 1);

    }

    private boolean shaggyGiveArmor(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        ItemStackUtils.giveItem(player, armorItem);
        return false;
    }


    private boolean rename(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack == null){
            player.sendMessage("Нельзя переименовать воздух");
            return false;
        }

        if(itemStack.getType() == Material.AIR){
            player.sendMessage("Нельзя переименовать воздух");
            return false;
        }

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', String.join(" ", strings)));
        itemStack.setItemMeta(meta);
        player.updateInventory();
        return false;
    }

    private boolean shaggyTaskStart(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        Bukkit.getScheduler().runTaskLater(getPlugin(), ()->{
            player.sendMessage("§7[§dШегги§7]: §fБудь осторожен со своими желаниями.. ведь у тебя ОДНА ПОПЫТКА..");
        },100);
        return false;
    }


}
