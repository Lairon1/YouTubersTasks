package com.lairon.plugin.youtuberstasks.task;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class YTTask implements Listener{

    private Plugin plugin;
    private RegisteredTask registeredTask;

    public YTTask(Plugin plugin, RegisteredTask registeredTask) {
        this.plugin = plugin;
        this.registeredTask = registeredTask;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
