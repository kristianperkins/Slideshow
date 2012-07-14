package com.github.krockode.slideshow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.krockode.slideshow.slides.Slides;

public class SlideshowCommandExecutor implements CommandExecutor {

    private static final Logger log = Logger.getLogger("Minecraft");

    // 20 server ticks per second
    private static final int ONE_SECOND_PERIOD = 20;
    private static final int ONE_MINUTE_PERIOD = ONE_SECOND_PERIOD * 60;
    private Plugin plugin;
    private Map<String, Slides> slides = new HashMap<String, Slides>();
    private Slides editingSlides;

    SlideshowCommandExecutor(Plugin plugin) {
        this.plugin = plugin;
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("slides");
        for (String slideName : config.getKeys(false)) {
            Slides slide = new Slides(config.getStringList(slideName), plugin.getServer());
            slides.put(slideName, slide);
            log.info("added slideshow " + slideName + "with " + slides.size() + " slides");
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player) && (args.length > 0)) {
            Player player = (Player) sender;
            sender.sendMessage("Player location: " + player.getLocation());

            if ("create".equals(args[0])) {
                if (editingSlides != null) {
                    player.sendMessage(ChatColor.RED + "There is already a slideshow being edited");
                }
            } else if ("add".equals(args[0])) {
                editingSlides.add(player.getLocation());
            } else if ("run".equals(args[0])) {
                Slides slide = slides.get(args[1]);
                if (slide != null || slide.size() == 0) {
                    player.sendMessage(ChatColor.RED + "Cannot run slideshow " + args[1]);
                } else {
                    SlideshowRunner task = new SlideshowRunner(player, slide.iterator());
                    sender.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, task, ONE_SECOND_PERIOD);
                }
            } else if ("save".equals(args[0])) {

                Configuration config = plugin.getConfig();
                if (config.contains(args[1])) {
                    sender.sendMessage(ChatColor.RED + args[1] + " already exists.");
                } else {
//                    ConfigurationSection slides = config.createSection(args[1]);
//                    List<String> locations = new ArrayList<String>();
//                    for (Location loc: savedLocations) {
//                        StringBuilder buf = new StringBuilder();
//                        buf.append(new Vector(loc.getX(), loc.getY(), loc.getZ()));
//                        buf.append(":");
//                        buf.append(loc.getPitch()).append(":").append(loc.getYaw());
//                        buf.append(":").append(loc.getWorld());
//                        locations.add(buf.toString());
//                    }
//                    slides.set("locations", locations);
                }
                plugin.saveConfig();
            } else if ("list".equals(args[0])) {
                player.sendMessage(ChatColor.GREEN + slides.toString());
            }
            return true;
        }
        return false;
    }

    private class SlideshowRunner implements Runnable {

        private Player player;
        private Iterator<Location> slides;
        private int index;
        private Location previous;
        public SlideshowRunner(Player player, Iterator<Location> slides) {
            this.player = player;
            this.slides = slides;
            this.previous = player.getLocation();
        }

        public void run() {
            player.sendMessage("isOnline: " + player.isOnline());
            player.sendMessage("flight: " + player.getAllowFlight());
            if (player.isOnline() && player.getAllowFlight() && !hasMoved(player)) {
                player.setFlying(true);
                slides.next();
                player.teleport(previous);
                previous = player.getLocation();
                player.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, this, ONE_MINUTE_PERIOD / 6);
            } else {
                player.sendMessage(ChatColor.YELLOW + "Slideshow cancelled.");
                return;
            }
        }

        private boolean hasMoved(Player player) {
            System.out.println("distance: " + previous.distance(player.getLocation()));
            return previous.distance(player.getLocation()) > 1;
        }
    }
}
