package com.lairon.plugin.youtuberstasks;

import com.lairon.plugin.youtuberstasks.task.RegisteredTask;
import com.lairon.plugin.youtuberstasks.task.realis.*;
import com.lairon.plugin.youtuberstasks.utils.CustomNPC;
import com.lairon.plugin.youtuberstasks.utils.PluginOwnerHolder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class YouTubersTasks extends JavaPlugin {

    private RegisteredTask registeredTask;

    @Override
    public void onEnable() {
        World tasksWorld = Bukkit.getWorld("tasksWorld");
        if(tasksWorld == null){
            WorldCreator worldCreator = new WorldCreator("tasksWorld");
            worldCreator.seed(4812528429346118552l);
            Bukkit.createWorld(worldCreator);
        }



        PluginOwnerHolder.setOwner(this);
        registeredTask = new RegisteredTask(this);
        registeredTask.registerTask(Uni.class);
        registeredTask.registerTask(Rhyme.class);
        registeredTask.registerTask(Felix.class);
        registeredTask.registerTask(Shaggy.class);
        registeredTask.registerTask(Domer.class);
        registeredTask.registerTask(Graduss.class);
        registeredTask.registerTask(Vladus.class);
        registeredTask.registerTask(Ziman.class);
        Bukkit.getPluginManager().registerEvents(new Listener() {
           @EventHandler
            public void onNPC(PlayerInteractAtEntityEvent e){
               CustomNPC.onNPCClick(e);
           }

           @EventHandler
            public void onCommand(PlayerCommandPreprocessEvent e){
               if(e.getMessage().startsWith("/world")){
                   e.getPlayer().sendMessage(e.getPlayer().getLocation().getWorld().getName());
               }
           }
        }, this);



    }

    @Override
    public void onDisable() {
        CustomNPC.disable();
    }
}
