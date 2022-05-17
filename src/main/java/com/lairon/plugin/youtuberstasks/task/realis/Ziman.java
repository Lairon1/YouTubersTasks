package com.lairon.plugin.youtuberstasks.task.realis;

import com.lairon.plugin.youtuberstasks.task.RegisteredTask;
import com.lairon.plugin.youtuberstasks.task.YTTask;
import com.lairon.plugin.youtuberstasks.utils.ItemStackUtils;
import com.lairon.plugin.youtuberstasks.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.type.TNT;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Ziman extends YTTask {

    private Random random = new Random();
    private Location galovolLoc = new Location(Bukkit.getWorld("YTЖелания"),  4000085.64, 122.00, 4000102.27);
    private ItemStack sword = new ItemStack(Material.NETHERITE_SWORD) {{
        ItemStackUtils.setDisplayName(this, "§cВозмездие Ютуберов");
    }};

    private String[] ytNicks = new String[]{
            "WorId_of_Uni",
            "SavvaRu",
            "Rimekekov",
            "iShaggyStarZz",
            "Graduss",
            "Domer",
    };

    public Ziman(Plugin plugin, RegisteredTask registeredTask) {
        super(plugin, registeredTask);

        Bukkit.getPluginCommand("ZimanTaskStart").setExecutor(this::zimanTaskStart);
    }

    private boolean zimanTaskStart(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        AtomicBoolean isPart = new AtomicBoolean(true);

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.sendMessage("§cНастало время испытания.."), 100);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location loc : LocationUtils.blockRadiusEmptySphere(player.getLocation().clone(), 3, 3))
                    loc.getWorld().spawnParticle(Particle.CLOUD, loc, 0);
                if (!isPart.get()) {
                    this.cancel();
                    return;
                }
            }
        }.runTaskTimer(getPlugin(), 100, 10);

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            isPart.set(false);
            player.teleport(galovolLoc);
            player.sendMessage("§7[§fЗиман§7]: §fЭто будет очень тяжело…поэтому возьми это оружие…");
            ItemStackUtils.giveItem(player, sword);
            BossBar bar = Bukkit.createBossBar("§cЗомби Апокалипсис: Фаза 1", BarColor.RED, BarStyle.SEGMENTED_20);

            final double[] maxZombie = {50};
            ArrayList<Entity> zombies = new ArrayList<>();

            bar.setProgress(1);
            bar.addPlayer(player);
            final boolean[] isBarCounter = {true};

            for (int i = 0; i < maxZombie[0]; i++) {
                Zombie zombie = player
                        .getWorld()
                        .spawn(
                                LocationUtils.toHigestLocation(
                                        player
                                                .getLocation()
                                                .clone()
                                                .add(random.nextInt(100) - 50, 0, random.nextInt(100) - 50)
                                )
                                , Zombie.class);
                zombie.setTarget(player);
                zombies.add(zombie);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (zombies.size() == 0) {
                        this.cancel();
                        maxZombie[0] = 70;
                        for (int i = 0; i < maxZombie[0]; i++) {
                            Zombie zombie = player
                                    .getWorld()
                                    .spawn(
                                            LocationUtils.toHigestLocation(
                                                    player
                                                            .getLocation()
                                                            .clone()
                                                            .add(random.nextInt(100) - 50, 0, random.nextInt(100) - 50)
                                            )
                                            , Zombie.class);
                            zombie.setTarget(player);
                            EntityEquipment equipment = zombie.getEquipment();
                            if (random.nextBoolean()) equipment.setHelmet(new ItemStack(Material.IRON_HELMET));
                            if (random.nextBoolean()) equipment.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                            if (random.nextBoolean()) equipment.setBoots(new ItemStack(Material.IRON_BOOTS));
                            if (random.nextBoolean()) equipment.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                            if (random.nextBoolean()) equipment.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                            if (random.nextBoolean()) equipment.setItemInMainHand(new ItemStack(Material.IRON_SHOVEL));
                            zombies.add(zombie);
                        }
                        bar.setTitle("§cЗомби Апокалипсис: Фаза 2");

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (zombies.size() == 0) {
                                    this.cancel();
                                    isBarCounter[0] = false;
                                    new GiantZombieEvent(player);
                                }
                            }
                        }.runTaskTimer(getPlugin(), 1, 1);
                    }
                }
            }.runTaskTimer(getPlugin(), 1, 1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    zombies.removeIf(z -> z.isDead());
                    if (!isBarCounter[0]) {
                        bar.removeAll();
                        this.cancel();
                        return;
                    }
                    double progress = ((double) zombies.size()) / maxZombie[0];
                    bar.setProgress(progress > 1 || progress < 0 ? 0 : progress);
                }
            }.runTaskTimer(getPlugin(), 1, 1);


        }, 300);
        return false;
    }

    @EventHandler
    public void onSwordUse(PlayerInteractEvent e) {
        if (!ItemStackUtils.equalsItemsByName(e.getItem(), sword)) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (!e.getPlayer().isSneaking()) return;

        Location playerLocH = e.getPlayer().getLocation().clone();
        playerLocH.setY(150);

        for (int i = 0; i < 50; i++) {
            Location randomLoc = playerLocH.clone();
            randomLoc.add(random.nextInt(100) - 50, 0, random.nextInt(100) - 50);

            ArmorStand stand = randomLoc.getWorld().spawn(randomLoc, ArmorStand.class);
            stand.setGravity(false);

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner(ytNicks[random.nextInt(ytNicks.length)]);
            skull.setItemMeta(skullMeta);
            stand.setInvisible(true);
            stand.setHelmet(skull);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (stand.getLocation().getY() < 0) {
                        stand.remove();
                        this.cancel();
                        return;
                    }
                    stand.teleport(stand.getLocation().clone().subtract(0, 0.4f, 0));
                    for (Entity ent : stand.getWorld().getNearbyEntities(stand.getLocation(), 1, 1, 1)) {
                        if (!(ent instanceof LivingEntity)) continue;
                        LivingEntity le = (LivingEntity) ent;
                        if (stand == le) continue;
                        if (le == e.getPlayer()) {
                            stand.remove();
                            this.cancel();
                            return;
                        }
                        le.damage(50);
                        stand.remove();
                        this.cancel();
                        return;
                    }

                }
            }.runTaskTimer(getPlugin(), 1, 1);

        }


    }


    private class GiantZombieEvent {

        private Player player;
        private BossBar bar;
        private Giant giant;


        public GiantZombieEvent(Player player) {
            this.player = player;
            Location spawnLoc = player.getLocation().clone().add(random.nextInt(50) - 25, 0, random.nextInt(50) - 25);
            giant = spawnLoc.getWorld().spawn(LocationUtils.toHigestLocation(spawnLoc), Giant.class);
            giant.setCustomName("Король Зомби");

            bar = Bukkit.createBossBar("§aКороль Зомби", BarColor.RED, BarStyle.SOLID);
            bar.setProgress(1);
            bar.addPlayer(player);

            Zombie zombie = spawnLoc.getWorld().spawn(giant.getLocation(), Zombie.class);
            zombie.setSilent(true);
            zombie.setInvisible(true);
            zombie.setRemoveWhenFarAway(false);
            zombie.setPersistent(false);
            zombie.setTarget(player);
            zombie.setMaxHealth(100);
            zombie.setHealth(100);
            zombie.setAdult();
            zombie.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));


            new BukkitRunnable() {
                @Override
                public void run() {
                    if (giant.isDead()) {
                        this.cancel();
                        return;
                    }
                    giant.teleport(zombie.getLocation());
                    giant.getWorld().getNearbyEntities(giant.getLocation(), 4, 4, 4).forEach(entity -> {
                        if (!(entity instanceof LivingEntity)) return;
                        LivingEntity l = (LivingEntity) entity;
                        if (l.getType() == EntityType.PLAYER)
                            l.damage(1, giant);
                    });

                }
            }.runTaskTimer(getPlugin(), 1, 1);


            new BukkitRunnable() {
                @Override
                public void run() {
                    if (giant.isDead()) {
                        this.cancel();
                        bar.removeAll();
                        return;
                    }
                    double progress = giant.getHealth() / giant.getMaxHealth();
                    bar.setProgress(progress > 1 || progress < 0 ? 0 : progress);
                }
            }.runTaskTimer(getPlugin(), 1, 1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (giant.isDead()) {
                        this.cancel();
                        return;
                    }
                    Collection<Location> locations1 = LocationUtils.blockRadiusEmptyHSphere(giant.getLocation(), 5, 8);
                    double i1 = 0;
                    for (Location location : locations1) {
                        i1 += 0.3f;
                        location.add(0, i1, 0);
                        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> location.getWorld().spawnParticle(Particle.FLAME, location, 0), (int) i1);
                    }

                    Collection<Location> locations2 = LocationUtils.blockRadiusEmptyHSphereR(giant.getLocation(), 5, 8);
                    double i2 = 0;
                    for (Location location : locations2) {
                        i2 += 0.3f;
                        location.add(0, i2, 0);
                        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> location.getWorld().spawnParticle(Particle.FLAME, location, 0), (int) i2);
                    }

                    Location add = giant.getLocation().clone().add(0, 11, 0);
                    TNTPrimed tntPrimed = add.getWorld().spawn(add, TNTPrimed.class);
                    tntPrimed.setVelocity(player.getLocation().subtract(tntPrimed.getLocation()).toVector().normalize().multiply(2));
                }
            }.runTaskTimer(getPlugin(), 600, 600);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (giant.isDead()) {
                        this.cancel();
                        return;
                    }

                    for (int i = 0; i < 5; i++) {
                        Zombie zombie = player
                                .getWorld()
                                .spawn(
                                        LocationUtils.toHigestLocation(
                                                player
                                                        .getLocation()
                                                        .clone()
                                                        .add(random.nextInt(100) - 50, 0, random.nextInt(100) - 50)
                                        )
                                        , Zombie.class);
                        zombie.setRemoveWhenFarAway(false);
                        zombie.setPersistent(false);
                        zombie.setTarget(player);
                        EntityEquipment equipment = zombie.getEquipment();
                        equipment.setHelmet(new ItemStack(Material.IRON_HELMET));
                        equipment.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                        equipment.setBoots(new ItemStack(Material.IRON_BOOTS));
                        equipment.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                        equipment.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                    }
                }
            }.runTaskTimer(getPlugin(), 900, 900);

        }


    }
}
