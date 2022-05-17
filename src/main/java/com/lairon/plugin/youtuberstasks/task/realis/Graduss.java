package com.lairon.plugin.youtuberstasks.task.realis;

import com.lairon.plugin.youtuberstasks.task.RegisteredTask;
import com.lairon.plugin.youtuberstasks.task.YTTask;
import com.lairon.plugin.youtuberstasks.utils.ItemStackUtils;
import com.lairon.plugin.youtuberstasks.utils.LocationUtils;
import org.bukkit.*;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Graduss extends YTTask {

    private HashMap<Player, Location> playerLocations = new HashMap<>();
    private Location galovolLoc = new Location(Bukkit.getWorld("YTЖелания"), 4999999.00, 135.00, 4999975.00);
    private ItemStack help = new ItemStack(Material.FLINT){{
        ItemStackUtils.setDisplayName(this, "§eУтиная помощь");
    }};

    private ItemStack[] airItemStacks = new ItemStack[]{
            new ItemStack(Material.DIAMOND_HELMET){{
                ItemStackUtils.addEnchantment(this, Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            }},
            new ItemStack(Material.DIAMOND_CHESTPLATE){{
                ItemStackUtils.addEnchantment(this, Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                ItemStackUtils.addEnchantment(this, Enchantment.MENDING, 2);
            }},
            new ItemStack(Material.DIAMOND_BOOTS){{
                ItemStackUtils.addEnchantment(this, Enchantment.PROTECTION_ENVIRONMENTAL, 3);
            }},
            new ItemStack(Material.DIAMOND_BOOTS){{
                ItemStackUtils.addEnchantment(this, Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                ItemStackUtils.addEnchantment(this, Enchantment.THORNS, 3);
            }},
            new ItemStack(Material.DIAMOND_SWORD){{
                ItemStackUtils.addEnchantment(this, Enchantment.DAMAGE_ALL, 3);
            }},
            new ItemStack(Material.DIAMOND_AXE),
            new ItemStack(Material.DIAMOND_PICKAXE),
            new ItemStack(Material.GOLDEN_APPLE, 5),
            new ItemStack(Material.TOTEM_OF_UNDYING, 1),
            new ItemStack(Material.GRASS, 13),
            new ItemStack(Material.DIRT, 2),
            new ItemStack(Material.COBWEB, 1),
            new ItemStack(Material.GOLDEN_CARROT, 13),
            new ItemStack(Material.NETHERITE_BLOCK, 2),
            new ItemStack(Material.NETHERITE_HELMET){{
                ItemStackUtils.addEnchantment(this, Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            }},
            new ItemStack(Material.NETHERITE_CHESTPLATE){{
                ItemStackUtils.addEnchantment(this, Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                ItemStackUtils.addEnchantment(this, Enchantment.MENDING, 2);
            }},
            new ItemStack(Material.NETHERITE_BOOTS){{
                ItemStackUtils.addEnchantment(this, Enchantment.PROTECTION_ENVIRONMENTAL, 3);
            }},
            new ItemStack(Material.NETHERITE_BOOTS){{
                ItemStackUtils.addEnchantment(this, Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                ItemStackUtils.addEnchantment(this, Enchantment.THORNS, 3);
            }},
            new ItemStack(Material.NETHERITE_SWORD){{
                ItemStackUtils.addEnchantment(this, Enchantment.DAMAGE_ALL, 3);
            }},
            new ItemStack(Material.GLASS, 4),
            new ItemStack(Material.ARROW, 37),
    };

    private Random random = new Random();

    public Graduss(Plugin plugin, RegisteredTask registeredTask) {
        super(plugin, registeredTask);
        Bukkit.getPluginCommand("GradussTaskStart").setExecutor(this::gradussTaskStart);
        Bukkit.getPluginCommand("GradussTaskEnd").setExecutor(this::gradussTaskEnd);
    }


    @EventHandler
    public void onAirDropUse(PlayerInteractEvent e){
        if(!ItemStackUtils.equalsItemsByName(e.getItem(), help)) return;

        ArrayList<Entity> chickens = new ArrayList<>();

        Location airSpawnLoc = e.getPlayer().getLocation().clone();
        airSpawnLoc.setY(airSpawnLoc.getY() + 70);

        FallingBlock fallingBlock = airSpawnLoc.getWorld().spawnFallingBlock(airSpawnLoc, Bukkit.createBlockData(Material.BARREL));

        fallingBlock.setGravity(false);
        fallingBlock.setVelocity(new Vector(0,-0.5f,0));

        for (int i = 0; i < 5; i++) {
            Location add = airSpawnLoc.clone().add(random.nextInt(4) - 2, 0, random.nextInt(4) - 2);
            Chicken chicken = add.getWorld().spawn(add, Chicken.class);
            chicken.setCustomName("Уточка");
            chickens.add(chicken);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if(fallingBlock.isDead()){
                    this.cancel();
                    fallingBlock.remove();
                    fallingBlock.getLocation().getBlock().setType(Material.BARREL);
                    Barrel barrel = (Barrel) fallingBlock.getLocation().getBlock().getState();

                    barrel.setCustomName("Аир-дроп");

                    for (int i = 0; i < barrel.getInventory().getSize(); i++) {
                        if(random.nextDouble() < 0.7f) continue;
                        barrel.getInventory().setItem(i, airItemStacks[random.nextInt(airItemStacks.length)]);
                    }

                    return;
                }
                fallingBlock.getWorld().spawnParticle(Particle.CLOUD, fallingBlock.getLocation().clone(), 0);
                fallingBlock.setVelocity(new Vector(0,-0.5f,0));
                chickens.forEach(c -> {
                    Location loc = c.getLocation();
                    loc.setY(fallingBlock.getLocation().getY());
                    c.teleport(loc);
                });
            }
        }.runTaskTimer(getPlugin(), 1, 1);


    }

    private boolean gradussTaskEnd(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
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
            player.sendTitle("§cУтки!", "§eПовержены!");
            ItemStackUtils.giveItem(player, help);
        }, 100);

        return false;
    }

    private boolean gradussTaskStart(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
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
            player.teleport(galovolLoc);
            player.sendMessage("§7[§cГрадус§7]: §fНастало время познать всю силу УТОЧЕК..");
        }, 300);
        return false;
    }


}
