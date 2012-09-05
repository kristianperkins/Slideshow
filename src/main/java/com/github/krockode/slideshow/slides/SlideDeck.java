package com.github.krockode.slideshow.slides;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class SlideDeck implements Iterable<Slide> {

    private List<Slide> slides = new ArrayList<Slide>();

    /**
     * Creates a new empty SlideDeck
     */
    public SlideDeck() {}

    /**
     * Loads a SlideDeck from a list of Strings
     */
    public SlideDeck(List<Map<?, ?>> deckConfig, Plugin plugin) {
        Logger log = plugin.getLogger();
        log.finest("the locations: " + deckConfig);
        for (Map<?, ?> config : deckConfig) {
            Slide slide = toSlide(config, plugin.getServer());
            slides.add(slide);
            log.finest("adding slide: " + slide);
        }
    }

    public void add(Slide slide) {
        slides.add(slide);
    }

    public int size() {
        return slides.size();
    }

    public List<Map<String, Object>> toListOfMaps() {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Slide slide : slides) {
            list.add(slide.toMap());
        }
        return list;
    }

    public static Map<String, SlideDeck> loadFromConfiguration() {
        return null;
    }

    public Iterator<Slide> iterator() {
        return new SlideDeckIterator();
    }

    private static Slide toSlide(Map<?, ?> config, Server server) {
        World world = server.getWorld((String)config.get("world"));
        Location loc = new Location(world, (Double)config.get("x"), (Double)config.get("y"), (Double)config.get("z"),
                ((Double)config.get("yaw")).floatValue(), ((Double)config.get("pitch")).floatValue());
        Slide s = new Slide(loc);
        if (config.containsKey("message")) {
            s.setMessage(config.get("message").toString());
        }
        if (config.containsKey("duration")) {
            s.setDuration((Integer)config.get("duration"));
        }
        return s;
    }

    private class SlideDeckIterator implements Iterator<Slide> {

        private int index;

        public boolean hasNext() {
            return true;
        }

        public Slide next() {
            return slides.get(index++ % slides.size());
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
