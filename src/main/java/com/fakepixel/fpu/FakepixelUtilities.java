package com.fakepixel.fpu;

import com.fakepixel.fpu.commands.DungeonRoomCommand;
import com.fakepixel.fpu.core.AutoRoom;
import com.fakepixel.fpu.core.FPUConfig;
import com.fakepixel.fpu.core.Waypoints;
import com.fakepixel.fpu.gui.LinkGUI;
import com.fakepixel.fpu.gui.WaypointsGUI;
import com.fakepixel.fpu.handlers.ConfigHandler;
import com.fakepixel.fpu.handlers.OpenLink;
import com.fakepixel.fpu.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = FakepixelUtilities.MODID, version = FakepixelUtilities.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class FakepixelUtilities {
    public static final String MODID = "fpu";
    public static final String VERSION = "1.0.4";
    private static final String API_KEY_B64 = "TXlTdXBlclNlY3JldEZQVUtleTIwMjY=";
    private static final String API_URL_B64 = "aHR0cDovLzUxLjc1LjExOC4xNDk6MjAwODE=";
    public static JsonObject roomsJson;
    public static JsonObject waypointsJson;
    public static final Logger logger = LogManager.getLogger("FPU");
    public static boolean inSkyblock = false;
    public static boolean isShowTooltip = true;
    public static boolean isDebugMode = false;
    public static boolean isMinionOverlayEnabled = true;
    public static boolean isMinionUpgradeAdviceEnabled = true;
    public static boolean isSendInfoEnabled = false;
    public static boolean isSafeModeEnabled = false;
    public static boolean isSmoothTPEnabled = true;
    public static boolean usingSBPSecrets = false;
    public static String guiToOpen = null;
    public static KeyBinding[] keyBindings = new KeyBinding[2];
    public static String hotkeyOpen = "gui";
    public static List<String> motd = new ArrayList();
    static int tickAmount = 1;
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean hasSentWelcomeMessage = false;

    public static String getApiKey() {
        return new String(Base64.getDecoder().decode(API_KEY_B64));
    }

    public static String getApiUrl() {
        return new String(Base64.getDecoder().decode(API_URL_B64));
    }

    public static boolean isDeveloper() {
        return Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.getName().equalsIgnoreCase("Devans_h");
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new DungeonRoomCommand());
        FPUConfig.init(event.getSuggestedConfigurationFile());
        logger.info("FPU PreInit");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new TooltipListener());
        MinecraftForge.EVENT_BUS.register(new MinionOverlayListener());
        MinecraftForge.EVENT_BUS.register(new ChatListener());
        MinecraftForge.EVENT_BUS.register(new InventoryScraperListener());
        MinecraftForge.EVENT_BUS.register(new FPUSafeModeListener());
        MinecraftForge.EVENT_BUS.register(new AutoRoom());
        MinecraftForge.EVENT_BUS.register(new Waypoints());
        MinecraftForge.EVENT_BUS.register(new SmoothTPListener());
        MinecraftForge.EVENT_BUS.register(new PlayerIconListener());
        ClientCommandHandler.instance.registerCommand(new FpuCommand());
        ClientCommandHandler.instance.registerCommand(new FpuSafeCommand());
        ConfigHandler.reloadConfig();
        try {
            ResourceLocation roomsLoc = new ResourceLocation("dungeonrooms", "dungeonrooms.json");
            InputStream roomsIn = Minecraft.getMinecraft().getResourceManager().getResource(roomsLoc).getInputStream();
            BufferedReader roomsReader = new BufferedReader(new InputStreamReader(roomsIn));
            ResourceLocation waypointsLoc = new ResourceLocation("dungeonrooms", "secretlocations.json");
            InputStream waypointsIn = Minecraft.getMinecraft().getResourceManager().getResource(waypointsLoc).getInputStream();
            BufferedReader waypointsReader = new BufferedReader(new InputStreamReader(waypointsIn));
            Gson gson = new Gson();
            roomsJson = (JsonObject) gson.fromJson(roomsReader, JsonObject.class);
            logger.info("Loaded dungeonrooms.json");
            waypointsJson = (JsonObject) gson.fromJson(waypointsReader, JsonObject.class);
            logger.info("Loaded secretlocations.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        keyBindings[0] = new KeyBinding("Open Room Images in DSG/SBP", 24, "FakepixelUtilities Mod");
        keyBindings[1] = new KeyBinding("Open Waypoint Menu", 25, "FakepixelUtilities Mod");
        for (KeyBinding keyBinding : keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        usingSBPSecrets = Loader.isModLoaded("sbp");
        logger.info("[FPU]: SBP Dungeon Secrets detection: " + usingSBPSecrets);
    }

    public static String getSkyblockId(ItemStack item) {
        if (item == null) {
            return "AIR";
        }
        String cleanName = EnumChatFormatting.getTextWithoutFormattingCodes(item.getDisplayName()).toUpperCase().trim().replace(" ", "_");
        if (item.hasTagCompound() && item.getTagCompound().hasKey("ExtraAttributes")) {
            NBTTagCompound ea = item.getTagCompound().getCompoundTag("ExtraAttributes");
            if (ea.hasKey("id")) {
                String baseId = ea.getString("id").toUpperCase().trim();
                if (baseId.equals("PET") && ea.hasKey("petInfo")) {
                    String petInfo = ea.getString("petInfo");
                    String type = "UNKNOWN";
                    String tier = "COMMON";
                    Matcher mType = Pattern.compile("\"type\":\"([^\"]+)\"").matcher(petInfo);
                    if (mType.find()) {
                        type = mType.group(1);
                    }
                    Matcher mTier = Pattern.compile("\"tier\":\"([^\"]+)\"").matcher(petInfo);
                    if (mTier.find()) {
                        tier = mTier.group(1);
                    }
                    return "PET_" + type + "_" + tier;
                }
                if (baseId.contains(";")) {
                    String[] parts = baseId.split(";");
                    if (parts.length >= 2) {
                        String namePart = parts[0];
                        String tierNum = parts[1];
                        String tierSuffix = "_COMMON";
                        if (tierNum.equals("5")) {
                            tierSuffix = "_MYTHIC";
                        } else if (tierNum.equals("4")) {
                            tierSuffix = "_LEGENDARY";
                        } else if (tierNum.equals("3")) {
                            tierSuffix = "_EPIC";
                        } else if (tierNum.equals("2")) {
                            tierSuffix = "_RARE";
                        } else if (tierNum.equals("1")) {
                            tierSuffix = "_UNCOMMON";
                        } else if (tierNum.equals("0")) {
                            tierSuffix = "_COMMON";
                        }
                        if (!namePart.startsWith("PET_")) {
                            namePart = "PET_" + namePart;
                        }
                        return namePart + tierSuffix;
                    }
                }
                if (baseId.equals("ENCHANTED_BOOK") && ea.hasKey("enchantments")) {
                    NBTTagCompound enchants = ea.getCompoundTag("enchantments");
                    if (!enchants.hasNoTags()) {
                        String firstEnchant = (String) enchants.getKeySet().iterator().next();
                        int level = enchants.getInteger(firstEnchant);
                        return "ENCHANTMENT_" + firstEnchant.toUpperCase() + "_" + level;
                    }
                }
                if ((baseId.contains("POTION") || ea.hasKey("potion")) && (cleanName.contains("POTION") || cleanName.contains("SPLASH") || cleanName.contains("BOTTLE"))) {
                    return cleanName;
                }
                return baseId;
            }
        }
        return cleanName;
    }

    @SubscribeEvent
    public void renderPlayerInfo(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL && Utils.inDungeons) {
            if (AutoRoom.guiToggled) {
                AutoRoom.renderText();
            }
            if (AutoRoom.coordToggled) {
                AutoRoom.renderCoord();
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        WorldClient worldClient = this.mc.theWorld;
        EntityPlayerSP player = this.mc.thePlayer;
        if (player == null) {
            this.hasSentWelcomeMessage = false;
        } else if (!this.hasSentWelcomeMessage) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "[FPU] Mod loaded successfully! " + EnumChatFormatting.YELLOW + "You are using v" + VERSION));
            this.hasSentWelcomeMessage = true;
        }
        tickAmount++;
        if (tickAmount % 20 == 0 && player != null) {
            Utils.checkForSkyblock();
            Utils.checkForDungeons();
            tickAmount = 0;
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (keyBindings[0].isPressed()) {
            if (!Utils.inDungeons) {
                if (player != null) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: Use this hotkey in dungeons!"));
                    return;
                }
                return;
            }
            switch (hotkeyOpen) {
                case "gui":
                    OpenLink.checkForLink("gui");
                    break;
                case "dsg":
                    OpenLink.checkForLink("dsg");
                    break;
                case "sbp":
                    OpenLink.checkForLink("sbp");
                    break;
                default:
                    if (player != null) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: hotkeyOpen config value improperly set"));
                        break;
                    }
                    break;
            }
        }
        if (keyBindings[1].isPressed()) {
            guiToOpen = "waypoints";
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (guiToOpen != null) {
            switch (guiToOpen) {
                case "link":
                    this.mc.displayGuiScreen(new LinkGUI());
                    break;
                case "waypoints":
                    this.mc.displayGuiScreen(new WaypointsGUI());
                    break;
            }
            guiToOpen = null;
        }
    }
}
