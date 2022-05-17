package com.lairon.plugin.youtuberstasks.task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class RegisteredTask {

    private HashMap<Class<YTTask>, YTTask> registeredTasks = new HashMap<>();
    private Plugin plugin;

    public RegisteredTask(Plugin plugin) {
        this.plugin = plugin;
    }

    public  <T extends YTTask> void registerTask(Class<T> taskClass){
        if(registeredTasks.containsKey(taskClass))
            throw new IllegalArgumentException("This YTTask is already registered.");
        try {
            YTTask ytTask = (YTTask) taskClass.getConstructors()[0].newInstance(plugin, this);
            Bukkit.getPluginManager().registerEvents(ytTask, plugin);
            registeredTasks.put((Class<YTTask>) taskClass, ytTask);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public <T extends YTTask> T getTaskInstance(Class<T> taskClass){
        if(registeredTasks.containsKey(taskClass))
            return (T) registeredTasks.get(taskClass);
        return null;
    }

}
