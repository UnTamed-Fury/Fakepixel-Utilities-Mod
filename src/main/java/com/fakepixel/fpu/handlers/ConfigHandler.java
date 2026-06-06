package com.fakepixel.fpu.handlers;

import com.fakepixel.fpu.FakepixelUtilities;
import com.fakepixel.fpu.core.AutoRoom;
import com.fakepixel.fpu.core.Waypoints;
import java.io.File;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
    public static Configuration config;
    private static final String file = "config/DungeonRooms.cfg";

    public static void init() {
        config = new Configuration(new File(file));
        try {
            try {
                config.load();
                config.save();
            } catch (Exception ex) {
                ex.printStackTrace();
                config.save();
            }
        } catch (Throwable th) {
            config.save();
            throw th;
        }
    }

    public static int getInt(String category, String key) {
        config = new Configuration(new File(file));
        try {
            try {
                config.load();
                if (!config.getCategory(category).containsKey(key)) {
                    return 0;
                }
                int i = config.get(category, key, 0).getInt();
                config.save();
                return i;
            } catch (Exception ex) {
                ex.printStackTrace();
                config.save();
                return 0;
            }
        } finally {
            config.save();
        }
    }

    public static double getDouble(String category, String key) {
        config = new Configuration(new File(file));
        try {
            try {
                config.load();
                if (!config.getCategory(category).containsKey(key)) {
                    config.save();
                    return 0.0d;
                }
                double d = config.get(category, key, 0.0d).getDouble();
                config.save();
                return d;
            } catch (Exception ex) {
                ex.printStackTrace();
                config.save();
                return 0.0d;
            }
        } catch (Throwable th) {
            config.save();
            throw th;
        }
    }

    public static String getString(String category, String key) {
        config = new Configuration(new File(file));
        try {
            try {
                config.load();
                if (!config.getCategory(category).containsKey(key)) {
                    return "";
                }
                String string = config.get(category, key, "").getString();
                config.save();
                return string;
            } catch (Exception ex) {
                ex.printStackTrace();
                config.save();
                return "";
            }
        } finally {
            config.save();
        }
    }

    public static boolean getBoolean(String category, String key) {
        config = new Configuration(new File(file));
        try {
            try {
                config.load();
                if (!config.getCategory(category).containsKey(key)) {
                    return true;
                }
                boolean z = config.get(category, key, false).getBoolean();
                config.save();
                return z;
            } catch (Exception ex) {
                ex.printStackTrace();
                config.save();
                return true;
            }
        } finally {
            config.save();
        }
    }

    public static void writeIntConfig(String category, String key, int value) {
        config = new Configuration(new File(file));
        try {
            try {
                config.load();
                config.get(category, key, value).getInt();
                config.getCategory(category).get(key).set(value);
                config.save();
            } catch (Exception ex) {
                ex.printStackTrace();
                config.save();
            }
        } catch (Throwable th) {
            config.save();
            throw th;
        }
    }

    public static void writeDoubleConfig(String category, String key, double value) {
        config = new Configuration(new File(file));
        try {
            try {
                config.load();
                config.get(category, key, value).getDouble();
                config.getCategory(category).get(key).set(value);
                config.save();
            } catch (Exception ex) {
                ex.printStackTrace();
                config.save();
            }
        } catch (Throwable th) {
            config.save();
            throw th;
        }
    }

    public static void writeStringConfig(String category, String key, String value) {
        config = new Configuration(new File(file));
        try {
            try {
                config.load();
                config.get(category, key, value).getString();
                config.getCategory(category).get(key).set(value);
                config.save();
            } catch (Exception ex) {
                ex.printStackTrace();
                config.save();
            }
        } catch (Throwable th) {
            config.save();
            throw th;
        }
    }

    public static void writeBooleanConfig(String category, String key, boolean value) {
        config = new Configuration(new File(file));
        try {
            try {
                config.load();
                config.get(category, key, value).getBoolean();
                config.getCategory(category).get(key).set(value);
                config.save();
            } catch (Exception ex) {
                ex.printStackTrace();
                config.save();
            }
        } catch (Throwable th) {
            config.save();
            throw th;
        }
    }

    public static boolean hasKey(String category, String key) {
        config = new Configuration(new File(file));
        try {
            try {
                config.load();
                if (!config.hasCategory(category)) {
                    config.save();
                    return false;
                }
                boolean zContainsKey = config.getCategory(category).containsKey(key);
                config.save();
                return zContainsKey;
            } catch (Exception ex) {
                ex.printStackTrace();
                config.save();
                return false;
            }
        } catch (Throwable th) {
            config.save();
            throw th;
        }
    }

    public static void deleteCategory(String category) {
        config = new Configuration(new File(file));
        try {
            try {
                config.load();
                if (config.hasCategory(category)) {
                    config.removeCategory(new ConfigCategory(category));
                }
                config.save();
            } catch (Exception ex) {
                ex.printStackTrace();
                config.save();
            }
        } catch (Throwable th) {
            config.save();
            throw th;
        }
    }

    public static void reloadConfig() {
        if (!hasKey("toggles", "chatToggled")) {
            writeBooleanConfig("toggles", "chatToggled", false);
        }
        if (!hasKey("toggles", "guiToggled")) {
            writeBooleanConfig("toggles", "guiToggled", true);
        }
        if (!hasKey("toggles", "coordToggled")) {
            writeBooleanConfig("toggles", "coordToggled", false);
        }
        if (!hasKey("toggles", "waypointsToggled")) {
            writeBooleanConfig("toggles", "waypointsToggled", true);
        }
        if (!hasKey("waypoint", "showEntrance")) {
            writeBooleanConfig("waypoint", "showEntrance", true);
        }
        if (!hasKey("waypoint", "showSuperboom")) {
            writeBooleanConfig("waypoint", "showSuperboom", true);
        }
        if (!hasKey("waypoint", "showSecrets")) {
            writeBooleanConfig("waypoint", "showSecrets", true);
        }
        if (!hasKey("waypoint", "showFairySouls")) {
            writeBooleanConfig("waypoint", "showFairySouls", true);
        }
        if (!hasKey("waypoint", "sneakToDisable")) {
            writeBooleanConfig("waypoint", "sneakToDisable", true);
        }
        if (!hasKey("waypoint", "disableWhenAllFound")) {
            writeBooleanConfig("waypoint", "disableWhenAllFound", true);
        }
        if (!hasKey("waypoint", "showWaypointText")) {
            writeBooleanConfig("waypoint", "showWaypointText", true);
        }
        if (!hasKey("waypoint", "showBoundingBox")) {
            writeBooleanConfig("waypoint", "showBoundingBox", true);
        }
        if (!hasKey("waypoint", "showBeacon")) {
            writeBooleanConfig("waypoint", "showBeacon", true);
        }
        if (!hasKey("gui", "scaleX")) {
            writeIntConfig("gui", "scaleX", 50);
        }
        if (!hasKey("gui", "scaleY")) {
            writeIntConfig("gui", "scaleY", 5);
        }
        if (!hasKey("gui", "hotkeyOpen")) {
            writeStringConfig("gui", "hotkeyOpen", "gui");
        }
        AutoRoom.chatToggled = getBoolean("toggles", "chatToggled");
        AutoRoom.guiToggled = getBoolean("toggles", "guiToggled");
        AutoRoom.coordToggled = getBoolean("toggles", "coordToggled");
        Waypoints.enabled = getBoolean("toggles", "waypointsToggled");
        Waypoints.showEntrance = getBoolean("waypoint", "showEntrance");
        Waypoints.showSuperboom = getBoolean("waypoint", "showSuperboom");
        Waypoints.showSecrets = getBoolean("waypoint", "showSecrets");
        Waypoints.showFairySouls = getBoolean("waypoint", "showFairySouls");
        Waypoints.sneakToDisable = getBoolean("waypoint", "sneakToDisable");
        Waypoints.disableWhenAllFound = getBoolean("waypoint", "disableWhenAllFound");
        Waypoints.showWaypointText = getBoolean("waypoint", "showWaypointText");
        Waypoints.showBoundingBox = getBoolean("waypoint", "showBoundingBox");
        Waypoints.showBeacon = getBoolean("waypoint", "showBeacon");
        AutoRoom.scaleX = getInt("gui", "scaleX");
        AutoRoom.scaleY = getInt("gui", "scaleY");
        FakepixelUtilities.hotkeyOpen = getString("gui", "hotkeyOpen");
    }
}
