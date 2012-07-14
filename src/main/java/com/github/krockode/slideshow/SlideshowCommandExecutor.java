package com.github.krockode.slideshow;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class SlideshowCommandExecutor implements CommandExecutor {

    // 20 server ticks per second
    private static final int ONE_SECOND_PERIOD = 20;
    private static final int ONE_MINUTE_PERIOD = ONE_SECOND_PERIOD * 60;
    private Plugin plugin;
    private List<Location> savedLocations = new ArrayList<Location>();
    private int taskId;

    SlideshowCommandExecutor(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player) && (args.length > 0)) {
            Player player = (Player) sender;
            sender.sendMessage("Player location: " + player.getLocation());

            if ("add".equals(args[0])) {
                savedLocations.add(player.getLocation());
            } else if ("run".equals(args[0])) {
                SlideshowRunner task = new SlideshowRunner(player, savedLocations);
                taskId = sender.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, task, ONE_SECOND_PERIOD, ONE_MINUTE_PERIOD / 6);
            } else if ("stop".equals(args[0])) {
                sender.getServer().getScheduler().cancelTask(taskId);
            } else if ("save".equals(args[0])) {

                Configuration config = plugin.getConfig();
                if (config.contains(args[1])) {
                    sender.sendMessage(ChatColor.RED + args[1] + " already exists.");
                } else {
                    ConfigurationSection slides = config.createSection(args[1]);
                    List<String> locations = new ArrayList<String>();
                    for (Location loc: savedLocations) {
                        StringBuilder buf = new StringBuilder();
                        buf.append(new Vector(loc.getX(), loc.getY(), loc.getZ()));
                        buf.append(":");
                        buf.append(loc.getPitch()).append(":").append(loc.getYaw());
                        buf.append(":").append(loc.getWorld());
                        locations.add(buf.toString());
                    }
                    slides.set("locations", locations);
                }
                plugin.saveConfig();
            }
            return true;
        }
        return false;
    }

    private static class SlideshowRunner implements Runnable {

        private Player player;
        private List<Location> locations;
        private int index;

        public SlideshowRunner(Player player, List<Location> locations) {
            this.player = player;
            this.locations = locations;
        }

        public void run() {
            if (player.isOnline() & player.getAllowFlight()) {
                player.setFlying(true);
                player.teleport(locations.get(index++ % locations.size()));
            }
        }
    }
}
