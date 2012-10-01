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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import com.github.krockode.slideshow.slides.Slide;
import com.github.krockode.slideshow.slides.SlideDeck;

public class SlideshowCommandExecutor implements CommandExecutor, Listener {

    private final Logger log;

    // 20 server ticks per second
    private static final int ONE_SECOND_PERIOD = 20;
    private static final int ONE_MINUTE_PERIOD = ONE_SECOND_PERIOD * 60;
    private Plugin plugin;
    private Map<String, SlideDeck> decks = new HashMap<String, SlideDeck>();
    private SlideDeck editingSlides;
    private final Map<String, Location> slideUserLocations = new HashMap<String, Location>();
    private boolean disableMovement;
    private boolean overrideFlyingAllowed;

    SlideshowCommandExecutor(Plugin plugin) {
        this.plugin = plugin;
        log = plugin.getLogger();
        FileConfiguration config = plugin.getConfig();
        disableMovement = config.getBoolean("disable_movement");
        overrideFlyingAllowed = config.getBoolean("override_flying_allowed");
        ConfigurationSection slideshowConfig = config.getConfigurationSection("slideshows");
        for (String slideName : slideshowConfig.getKeys(false)) {
            ConfigurationSection slideConfig = slideshowConfig.getConfigurationSection(slideName);
            SlideDeck slide = new SlideDeck(slideConfig.getMapList("slides"), plugin);
            decks.put(slideName, slide);
            log.info("added slideshow " + slideName + " with " + decks.size() + "  slides");
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player) && (args.length > 0)) {
            Player player = (Player) sender;
            String type = args[0];
            String option = args.length > 1 ? args[1] : "";
            if ("create".equals(type)) {
                create(player);
            } else if ("add".equals(type)) {
               add(player);
            } else if ("save".equals(type)) {
                save(player, option);
            } else if (StringUtils.isNotBlank(type)) {
                run(player, type);
            } else {
                list(sender);
            }
        } else if (args.length > 0 && args[0].equals("move")) {
            disableMovement = !disableMovement;
            sender.sendMessage("movement is " + (disableMovement ? "disabled" : "enabled") + " for slideshows");
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
        if (editingSlides == null) {
            editingSlides = new SlideDeck();
            player.sendMessage(ChatColor.YELLOW + "Creating new slideshow");
        }
        editingSlides.add(new Slide(player.getLocation()));
        player.sendMessage(ChatColor.YELLOW + "added current location");
    }

    private void save(Player player, String slideDeck) {
        Configuration config = plugin.getConfig();
        if (config.contains(slideDeck)) {
            player.sendMessage(ChatColor.RED + "Slideshow" + slideDeck + " already exists.");
        } else if (editingSlides == null) {
            player.sendMessage(ChatColor.RED + slideDeck + " no slideshow being edited.");
        } else {
            List<Map<String, Object>> deckString = editingSlides.toListOfMaps();
            ConfigurationSection slidesConfig = plugin.getConfig().getConfigurationSection("slideshows");
            ConfigurationSection deckConfig = slidesConfig.createSection(slideDeck);
            deckConfig.set("slides", deckString);
            decks.put(slideDeck, editingSlides);
            editingSlides = null;
            plugin.saveConfig();
        }
    }

    private void run(Player player, String slideDeckName) {
        SlideDeck slide = decks.get(slideDeckName);
        if (slide != null && slide.size() > 0) {
            SlideshowRunner task = new SlideshowRunner(player, slide.iterator());
            player.sendMessage(ChatColor.YELLOW + "Starting slideshow " + slideDeckName);
            log.info("Player " + player.getDisplayName() + " started slideshow " + slideDeckName);
            player.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, ONE_SECOND_PERIOD);
            slideUserLocations.put(player.getName(), player.getLocation());
        } else {
            player.sendMessage(ChatColor.RED + "Cannot run slideshow " + slideDeckName);
        }
    }

    private void list(CommandSender sender) {
        sender.sendMessage("Slideshows (" + decks.size() + "): " + ChatColor.GREEN +
                StringUtils.join(decks.keySet(), ChatColor.RESET + ", " + ChatColor.GREEN));
    }

    private class SlideshowRunner implements Runnable {

        private Player player;
        private Iterator<Slide> slides;
        private Slide previous;
        private boolean disallowFlyingOnCancel;

        public SlideshowRunner(Player player, Iterator<Slide> slides) {
            this.player = player;
            this.slides = slides;
        }

        public void run() {
            log.finest("isOnline: " + player.isOnline());
            log.finest("flight: " + player.getAllowFlight());
            if (player.isOnline() && allowFlying(player) && !hasMoved(player)) {
                Slide next = slides.next();
                log.finest("player at: " + player.getLocation());
                log.finest("teleporting to: " + next);
                player.teleport(next.getLocation());
                // XXX: when tp between worlds in 1.3.1 need to turn flying back on
                player.setFlying(true);
                if (next.getMessage() != null) {
                    player.sendMessage(ChatColor.GOLD + next.getMessage());
                }
                slideUserLocations.put(player.getName(), next.getLocation());
                previous = next;
                player.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, ONE_MINUTE_PERIOD / 6);
            } else {
                player.sendMessage(ChatColor.YELLOW + "Slideshow cancelled.");
                slideUserLocations.remove(player.getName());
                if (disallowFlyingOnCancel) {
                    player.setAllowFlight(false);
                }
                return;
            }
        }

        private boolean allowFlying(Player player) {
            if (overrideFlyingAllowed && !player.getAllowFlight()) {
                player.setAllowFlight(true);
                disallowFlyingOnCancel = true;
            }
            return player.getAllowFlight();
        }

        private boolean hasMoved(Player player) {
            if (disableMovement) {
                return false;
            }
            return previous != null && previous.getLocation().distance(player.getLocation()) > 1;
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (disableMovement && slideUserLocations.containsKey(p.getName())) {
            Location requiredLocation = slideUserLocations.get(p.getName());
            if (requiredLocation.distance(event.getTo()) > 1) {
                p.teleport(requiredLocation);

            }
        }
    }
}
