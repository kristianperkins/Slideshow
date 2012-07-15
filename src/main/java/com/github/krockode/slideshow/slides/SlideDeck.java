package com.github.krockode.slideshow.slides;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class SlideDeck implements Iterable<Location> {

    private static final Logger log = Logger.getLogger("Minecraft");

    private List<Location> locations = new ArrayList<Location>();

    /**
     * Creates a new empty SlideDeck
     */
    public SlideDeck() {}

    /**
     * Loads a SlideDeck from a list of Strings
     */
    public SlideDeck(List<String> locationStrings, Server server) {
        log.finest("the locations: " + locationStrings);
        for (String locString : locationStrings) {
            Location location = toLocation(locString, server);
            locations.add(location);
            log.finest("adding location: " + location);
        }
    }

    public void add(Location location) {
        locations.add(location);
    }

    public int size() {
        return locations.size();
    }

    public List<String> toStringList() {
        ArrayList<String> list = new ArrayList<String>();
        for (Location loc : locations) {
            list.add(toString(loc));
        }
        return list;
    }

    private static String toString(Location loc) {
        StringBuilder buf = new StringBuilder();
        buf.append(loc.getWorld().getName()).append(",");
        buf.append(loc.getX()).append(",");
        buf.append(loc.getY()).append(",");
        buf.append(loc.getZ()).append(",");
        buf.append(loc.getYaw()).append(",");
        buf.append(loc.getPitch());
        return buf.toString();
    }

    private static Location toLocation(String locString, Server server) {
        String[] l = locString.split(",");
        World world = server.getWorld(l[0]);
        return new Location(world, Double.valueOf(l[1]), Double.valueOf(l[2]), Double.valueOf(l[3]),
                Float.valueOf(l[4]), Float.valueOf(l[5]));

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
