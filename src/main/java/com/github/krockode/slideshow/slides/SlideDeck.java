package com.github.krockode.slideshow.slides;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class SlideDeck implements Iterable<Location> {

    private static final Logger log = Logger.getLogger("Minecraft");

    private List<Location> locations = new ArrayList<Location>();

    public SlideDeck(List<String> locationStrings, Server server) {
        log.info("the locations: " + locationStrings);
        for (String locString : locationStrings) {
            Location location = toLocation(locString, server);
            locations.add(location);
            log.info("adding location: " + location);
        }
    }

    public void add(Location location) {
        locations.add(location);
    }

    public int size() {
        return locations.size();
    }

    public void save(ConfigurationSection config) {

    }

    private static String toString(Location loc) {
        StringBuilder buf = new StringBuilder();
        buf.append(loc.getX()).append(",");
        buf.append(loc.getY()).append(",");
        buf.append(loc.getZ()).append(",");
        buf.append(loc.getPitch()).append(",");
        buf.append(loc.getYaw()).append(",");
        buf.append(loc.getWorld().getName());
        return buf.toString();
    }

    private static Location toLocation(String locString, Server server) {
        String[] l = locString.split(",");
        World world = server.getWorld(l[5]);
        return new Location(world, Double.valueOf(l[0]), Double.valueOf(l[1]), Double.valueOf(l[2]),
                Float.valueOf(l[4]), Float.valueOf(l[3]));

    }
    public static Map<String, SlideDeck> loadFromConfiguration() {
        return null;
    }

    public Iterator<Location> iterator() {
        return new SlideDeckIterator();
    }

    private class SlideDeckIterator implements Iterator<Location> {

        private int index;

        public boolean hasNext() {
            return true;
        }

        public Location next() {
            return locations.get(index++ % locations.size()).clone();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
