package com.github.krockode.slideshow.slides;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class Slides implements Iterable<Location> {

    private List<Location> locations = new ArrayList<Location>();

    public Slides(List<String> locationStrings, Server server) {
        for (String locString : locationStrings) {
            locations.add(toLocation(locString, server));
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
        buf.append(",");
        buf.append(loc.getPitch()).append(",").append(loc.getYaw());
        buf.append(",").append(loc.getWorld().getName());
        return buf.toString();
    }

    private static Location toLocation(String locString, Server server) {
        String[] l = locString.split(",");
        World world = server.getWorld(l[5]);
        return new Location(world, Double.valueOf(l[0]), Double.valueOf(l[1]), Double.valueOf(l[2]),
                Float.valueOf(l[3]), Float.valueOf(l[4]));

    }
    public static Map<String, Slides> loadFromConfiguration() {
        return null;
    }

    public Iterator<Location> iterator() {
        return new SlideshowIterator();
    }

    private class SlideshowIterator implements Iterable<Location>, Iterator<Location> {

        private int index;

        public Iterator<Location> iterator() {
            return this;
        }

        public boolean hasNext() {
            return true;
        }

        public Location next() {
            return locations.get(index++ % locations.size());
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
