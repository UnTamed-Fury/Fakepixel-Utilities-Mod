package com.fakepixel.fpu.core;

import com.fakepixel.fpu.FakepixelUtilities;
import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class FPUConfig {
    public static Configuration config;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        loadConfig();
    }

    public static void loadConfig() {
        try {
            try {
                config.load();
                FakepixelUtilities.isShowTooltip = config.getBoolean("isShowTooltip", "FPU_Settings", true, "Show Custom Tooltips");
                FakepixelUtilities.isMinionOverlayEnabled = config.getBoolean("isMinionOverlayEnabled", "FPU_Settings", true, "Show Minion Prices");
                FakepixelUtilities.isMinionUpgradeAdviceEnabled = config.getBoolean("isMinionUpgradeAdviceEnabled", "FPU_Settings", true, "Show Minion Upgrades");
                FakepixelUtilities.isSafeModeEnabled = config.getBoolean("isSafeModeEnabled", "FPU_Settings", false, "Enable or disable Safe Mode");
                FakepixelUtilities.isSmoothTPEnabled = config.getBoolean("isSmoothTPEnabled", "FPU_Settings", true, "Enable Smooth AOTE/AOTV");
                FakepixelUtilities.isSendInfoEnabled = config.getBoolean("isSendInfoEnabled", "FPU_Settings", false, "Enable Send Info");
                FakepixelUtilities.isDebugMode = config.getBoolean("isDebugMode", "FPU_Settings", false, "Enable Debug Logs");
                if (config.hasChanged()) {
                    config.save();
                }
            } catch (Exception e) {
                System.out.println("[FPU] Error while loading config!");
                if (config.hasChanged()) {
                    config.save();
                }
            }
        } catch (Throwable th) {
            if (config.hasChanged()) {
                config.save();
            }
            throw th;
        }
    }

    public static void saveConfig() {
        config.get("FPU_Settings", "isShowTooltip", true).set(FakepixelUtilities.isShowTooltip);
        config.get("FPU_Settings", "isMinionOverlayEnabled", true).set(FakepixelUtilities.isMinionOverlayEnabled);
        config.get("FPU_Settings", "isMinionUpgradeAdviceEnabled", true).set(FakepixelUtilities.isMinionUpgradeAdviceEnabled);
        config.get("FPU_Settings", "isSafeModeEnabled", false).set(FakepixelUtilities.isSafeModeEnabled);
        config.get("FPU_Settings", "isSmoothTPEnabled", true).set(FakepixelUtilities.isSmoothTPEnabled);
        config.get("FPU_Settings", "isSendInfoEnabled", false).set(FakepixelUtilities.isSendInfoEnabled);
        config.get("FPU_Settings", "isDebugMode", false).set(FakepixelUtilities.isDebugMode);
        config.save();
    }
}
