package com.github.krockode.slideshow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.krockode.slideshow.slides.SlideDeck;

public class SlideshowCommandExecutor implements CommandExecutor {

    private static final Logger log = Logger.getLogger("Minecraft");

    // 20 server ticks per second
    private static final int ONE_SECOND_PERIOD = 20;
    private static final int ONE_MINUTE_PERIOD = ONE_SECOND_PERIOD * 60;
    private Plugin plugin;
    private Map<String, SlideDeck> decks = new HashMap<String, SlideDeck>();
    private SlideDeck editingSlides;

    SlideshowCommandExecutor(Plugin plugin) {
        this.plugin = plugin;
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("slides");
        for (String slideName : config.getKeys(false)) {
            ConfigurationSection slideConfig = config.getConfigurationSection(slideName);
            SlideDeck slide = new SlideDeck(slideConfig.getStringList("locations"), plugin.getServer());
            decks.put(slideName, slide);
            log.info("added slideshow " + slideName + " with " + decks.size() + "  slides");
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player) && (args.length > 0)) {
            Player player = (Player) sender;
            if ("create".equals(args[0])) {
                create(player);
            } else if ("add".equals(args[0])) {
               add(player);
            } else if ("save".equals(args[0])) {
                save(player, args[1]);
            } else if ("run".equals(args[0])) {
                run(player, args[1]);
            } else {
                list(sender);
            }
        } else {
            list(sender);
        }
        return true;
    }

    private void create(Player player) {
        if (editingSlides != null) {
            player.sendMessage(ChatColor.RED + "There is already a slideshow being edited");
        } else {
            editingSlides = new SlideDeck();
            player.sendMessage(ChatColor.YELLOW + "add your current location using /slides add");
        }
    }

    private void add(Player player) {
        editingSlides.add(player.getLocation());
    }

    private void save(Player player, String slideDeck) {
        Configuration config = plugin.getConfig();
        if (config.contains(slideDeck)) {
            player.sendMessage(ChatColor.RED + "Slideshow" + slideDeck + " already exists.");
        } else if (editingSlides == null) {
            player.sendMessage(ChatColor.RED + slideDeck + " no slideshow being edited.");
        } else {
            List<String> deckString = editingSlides.toStringList();
            ConfigurationSection slidesConfig = plugin.getConfig().getConfigurationSection("slides");
            ConfigurationSection deckConfig = slidesConfig.createSection(slideDeck);
            deckConfig.set("locations", deckString);
            decks.put(slideDeck, editingSlides);
            editingSlides = null;
            plugin.saveConfig();
        }
    }

    private void run(Player player, String slideDeck) {
        SlideDeck slide = decks.get(slideDeck);
        if (slide != null || slide.size() > 0) {
            SlideshowRunner task = new SlideshowRunner(player, slide.iterator());
            player.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, task, ONE_SECOND_PERIOD);
        } else {
            player.sendMessage(ChatColor.RED + "Cannot run slideshow " + slideDeck);
        }
    }

    private void list(CommandSender sender) {
        sender.sendMessage("Slideshows (" + decks.size() + "): " + ChatColor.GREEN +
                StringUtils.join(decks.keySet(), ChatColor.WHITE + ", " + ChatColor.GREEN));
    }

    private class SlideshowRunner implements Runnable {

        private Player player;
        private Iterator<Location> slides;
        private Location previous;
        public SlideshowRunner(Player player, Iterator<Location> slides) {
            this.player = player;
            this.slides = slides;
            this.previous = player.getLocation();
        }

        public void run() {
            log.finest("isOnline: " + player.isOnline());
            log.finest("flight: " + player.getAllowFlight());
            if (player.isOnline() && player.getAllowFlight() && !hasMoved(player)) {
                player.setFlying(true);
                Location next = slides.next();
                log.finest("player at: " + player.getLocation());
                log.finest("teleporting to: " + next);
                player.teleport(next);
                previous = player.getLocation();
                player.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, this, ONE_MINUTE_PERIOD / 6);
            } else {
                player.sendMessage(ChatColor.YELLOW + "Slideshow cancelled.");
                return;
            }
        }

        private boolean hasMoved(Player player) {
            return previous.distance(player.getLocation()) > 1;
        }
    }
}
