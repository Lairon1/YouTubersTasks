package com.lairon.plugin.youtuberstasks.task.realis;

import com.lairon.plugin.youtuberstasks.task.RegisteredTask;
import com.lairon.plugin.youtuberstasks.task.YTTask;
import com.lairon.plugin.youtuberstasks.utils.CustomNPC;
import com.lairon.plugin.youtuberstasks.utils.EffectUtils;
import com.lairon.plugin.youtuberstasks.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Domer extends YTTask {

    private Random random = new Random();
    private CustomNPC npc;
    private ItemStack magnit = new ItemStack(Material.FLINT) {{
        ItemStackUtils.setDisplayName(this, "§aЧитерский магнит");
    }};
    private ItemStack ball = new ItemStack(Material.FLINT){{
        ItemStackUtils.setDisplayName(this, "ball");
    }};

    public Domer(Plugin plugin, RegisteredTask registeredTask) {
        super(plugin, registeredTask);

        Bukkit.getPluginCommand("DomerTaskStart").setExecutor(this::domerTaskStart);
    }

    private boolean domerTaskStart(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            player.sendMessage("§7[§cДомер§7]: §fНастало время тебе познакомиться с МОИМ ДРУГОМ..");
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                player.sendMessage("§eЧитер присоединился к игре");
                ArrayList<ItemStack> itemStacks = new ArrayList<>();

                for (ItemStack itemStack : player.getInventory()) {
                    if (itemStack == null) continue;
                    if (itemStack.getType().isAir()) continue;
                    itemStacks.add(itemStack);
                }
                AtomicBoolean isInv = new AtomicBoolean(true);
                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {

                    isInv.set(false);

                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 9));
                    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 4));
                        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 400, 9));
                            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                                CustomNPC hacker = new CustomNPC(player.getLocation().clone(), "Читер");
                                hacker.skin("ReemOr1234").protection(false).spawn();
                                npc = hacker;

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (hacker == null || hacker.getEntity() == null || hacker.getEntity().isDead()) {
                                            this.cancel();
                                            return;
                                        }
                                        player.getWorld().strikeLightning(player.getLocation().clone());
                                    }
                                }.runTaskTimer(getPlugin(), 100, 100);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (hacker == null || hacker.getEntity() == null || hacker.getEntity().isDead()) {
                                            ItemStackUtils.giveItem(player, magnit);
                                            this.cancel();
                                            return;
                                        }
                                        hacker.getNavigator().setTarget(player, true);
                                    }
                                }.runTaskTimer(getPlugin(), 0, 0);
                            }, 600);
                        }, 600);
                    }, 600);
                }, 1200);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!isInv.get()) {
                            this.cancel();
                            return;
                        }
                        ArrayList<ItemStack> itemStacks = new ArrayList<>();
                        for (ItemStack itemStack : player.getInventory().getStorageContents()) {
                            if (itemStack == null) continue;
                            if (itemStack.getType().isAir()) continue;
                            itemStacks.add(itemStack);
                        }
                        ItemStack item = itemStacks.get(random.nextInt(itemStacks.size()));
                        player.getInventory().remove(item);
                        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.getInventory().addItem(item), 200);
                    }
                }.runTaskTimer(getPlugin(), 0, 50);
            }, 100);
        }, 100);
        return false;
    }

    private Material[] materials = new Material[]{
            Material.IRON_BLOCK,
            Material.GOLD_BLOCK,
            Material.DIAMOND_BLOCK,
            Material.NETHERITE_BLOCK,
    };

    @EventHandler
    public void onMagnitUse(PlayerInteractEvent e) {
        if (!ItemStackUtils.equalsItemsByName(e.getItem(), magnit)) return;
        if(e.getClickedBlock() == null) return;
        ArrayList<Item> items = new ArrayList<>();
        Item ballItem = e.getPlayer().getWorld().dropItem(e.getClickedBlock().getLocation().clone().add(0.5f,1,0.5f), ball);
        items.add(ballItem);
        ballItem.setGravity(false);
        ballItem.setVelocity(new Vector(0,0.3,0));
        final int[] await = {0};
        Bukkit.getScheduler().runTaskLater(getPlugin(), ()->{
            for (int yaw = -180; yaw < 180; yaw+= 30) {
                Location loc = ballItem.getLocation().clone();
                loc.setYaw(yaw);
                loc.setPitch(0);
                ballItem.setVelocity(new Vector(0,0,0));
                Bukkit.getScheduler().runTaskLater(getPlugin(), ()->{
                    Location itemLocation = loc.clone().add(loc.getDirection().normalize().multiply(4));
                    Item item = itemLocation.getWorld().dropItem(itemLocation, new ItemStack(materials[random.nextInt(materials.length)]));
                    items.add(item);
                    item.setGravity(false);
                    item.setVelocity(new Vector(0,0,0));
                    Bukkit.getScheduler().runTaskLater(getPlugin(), ()->{
                        EffectUtils.drawParticleLine(ballItem.getLocation().clone(), item.getLocation().clone(), 1, Particle.FLAME);
                    }, 10);


                }, await[0]);
                await[0] += 10;
            }
            await[0] += 20;
            Bukkit.getScheduler().runTaskLater(getPlugin(), ()->{
                items.forEach(item -> {
                    item.getLocation().getWorld().createExplosion(item.getLocation().clone(), 3, false, false, e.getPlayer());
                    item.remove();
                });
                ballItem.getLocation().clone().getWorld().dropItem(ballItem.getLocation().clone(), new ItemStack(Material.IRON_BLOCK, 32));
                ballItem.getLocation().clone().getWorld().dropItem(ballItem.getLocation().clone(), new ItemStack(Material.GOLD_BLOCK, 24));
                ballItem.getLocation().clone().getWorld().dropItem(ballItem.getLocation().clone(), new ItemStack(Material.DIAMOND_BLOCK, 15));
                ballItem.getLocation().clone().getWorld().dropItem(ballItem.getLocation().clone(), new ItemStack(Material.NETHERITE_BLOCK, 10));

            }, await[0]);
        }, 50);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (npc == null) return;
        if (npc.getEntity() == null) return;
        if (npc.getEntity().isDead()) return;
        if (e.getDamager() != npc.getEntity()) return;
        if (!(e.getEntity() instanceof LivingEntity)) return;
        ((LivingEntity) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
    }


}
