package com.github.krockode.slideshow.slides;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;

public class Slide {

    private Location location;
    private String message;
    private Integer duration;

    public Slide(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location.clone();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("Slide: ");
        buf.append(location.getWorld().getName()).append(",");
        buf.append(location.getX()).append(",");
        buf.append(location.getY()).append(",");
        buf.append(location.getZ()).append(",");
        buf.append(location.getYaw()).append(",");
        buf.append(location.getPitch());
        return buf.toString();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("world", location.getWorld().getName());
        m.put("x", location.getX());
        m.put("y", location.getY());
        m.put("z", location.getZ());
        m.put("yaw", location.getYaw());
        m.put("pitch", location.getPitch());
        if (message != null) {
            m.put("message", message);
        }
        if (duration != null) {
            m.put("seconds", duration);
        }
        return m;
    }
}
