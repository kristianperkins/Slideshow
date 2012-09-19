package com.github.krockode.slideshow;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Slideshow extends JavaPlugin {

    public void onDisable() {
        getLogger().info("Slideshow has been disabled");
    }

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        SlideshowCommandExecutor executor = new SlideshowCommandExecutor(this);
        getCommand("slides").setExecutor(executor);
        getServer().getPluginManager().registerEvents(executor, this);
        getLogger().info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }
}
