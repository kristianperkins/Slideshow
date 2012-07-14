package com.github.krockode.slideshow;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Slideshow extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");

    public void onDisable() {
        log.info("Slideshow has been disabled");
    }

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        getCommand("slideshow").setExecutor(new SlideshowCommandExecutor(this));
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }
}
