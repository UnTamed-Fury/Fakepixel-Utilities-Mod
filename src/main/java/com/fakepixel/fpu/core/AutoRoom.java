package com.fakepixel.fpu.core;

import com.fakepixel.fpu.FakepixelUtilities;
import com.fakepixel.fpu.handlers.TextRenderer;
import com.fakepixel.fpu.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoRoom {
    public static JsonObject lastRoomJson;
    static int tickAmount = 1;
    public static List<String> autoTextOutput = null;
    public static boolean chatToggled = false;
    public static boolean guiToggled = true;
    public static boolean coordToggled = false;
    public static String lastRoomHash = null;
    public static String lastRoomName = null;
    public static String currentRoomKey = null;
    public static String lastCoordKey = "0_0";
    private static boolean newRoom = false;
    public static int worldLoad = 0;
    public static int scaleX = 50;
    public static int scaleY = 5;
    Minecraft mc = Minecraft.getMinecraft();
    private final Executor executor = Executors.newFixedThreadPool(5);

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        WorldClient worldClient = this.mc.theWorld;
        EntityPlayerSP entityPlayerSP = this.mc.thePlayer;
        tickAmount++;
        if (worldLoad < 200) {
            worldLoad++;
        }
        if (tickAmount % 30 == 0 && Utils.inDungeons && worldLoad == 200) {
            this.executor.execute(() -> {
                List<String> autoText;
                if ((chatToggled || guiToggled || Waypoints.enabled) && (autoText = autoText()) != null) {
                    autoTextOutput = autoText;
                }
                if (chatToggled) {
                    toggledChat();
                }
            });
            tickAmount = 0;
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        Utils.inDungeons = false;
        Utils.originBlock = null;
        Utils.originCorner = null;
        worldLoad = 0;
        Waypoints.allSecretsMap.clear();
        Random random = new Random();
        List<String> output = new ArrayList<>();
        if (random.nextBoolean() && FakepixelUtilities.motd != null && !FakepixelUtilities.motd.isEmpty()) {
            output.addAll(FakepixelUtilities.motd);
        }
        if (output.isEmpty()) {
            output.add("[FPU]: " + EnumChatFormatting.GREEN + "Press the hotkey \"" + GameSettings.getKeyDisplayString(FakepixelUtilities.keyBindings[1].getKeyCode()) + "\" to configure");
            output.add(EnumChatFormatting.GREEN + "Secret Waypoints settings.");
            output.add(EnumChatFormatting.WHITE + "(You can change the keybinds in Minecraft controls menu)");
        }
        autoTextOutput = output;
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        Utils.inDungeons = false;
    }

    public static List<String> autoText() {
        List<String> output = new ArrayList<>();
        EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
        int x = (int) Math.floor(((EntityPlayer) entityPlayerSP).posX);
        int y = (int) Math.floor(((EntityPlayer) entityPlayerSP).posY);
        int z = (int) Math.floor(((EntityPlayer) entityPlayerSP).posZ);
        int top = Utils.dungeonTop(x, y, z);
        String blockFrequencies = Utils.blockFrequency(x, top, z, true);
        if (blockFrequencies == null) {
            return output;
        }
        String MD5 = Utils.getMD5(blockFrequencies);
        String floorFrequencies = Utils.floorFrequency(x, top, z);
        String floorHash = Utils.getMD5(floorFrequencies);
        String text = "[FPU]: You are in " + EnumChatFormatting.GREEN;
        String coordKey = Utils.originBlock != null ? Utils.originBlock.getX() + "_" + Utils.originBlock.getZ() : "0_0";
        if (MD5.equals("16370f79b2cad049096f881d5294aee6") && !floorHash.equals("94fb12c91c4b46bd0c254edadaa49a3d")) {
            floorHash = "e617eff1d7b77faf0f8dd53ec93a220f";
        }
        if (MD5.equals(lastRoomHash) && lastRoomJson != null && floorHash != null && coordKey.equals(lastCoordKey)) {
            if (lastRoomJson.get("floorhash") != null) {
                if (floorHash.equals(lastRoomJson.get("floorhash").getAsString())) {
                    newRoom = false;
                    return null;
                }
            } else {
                newRoom = false;
                return null;
            }
        }
        newRoom = true;
        lastRoomHash = MD5;
        lastCoordKey = coordKey;
        Waypoints.allFound = false;
        if (FakepixelUtilities.roomsJson.get(MD5) == null && Utils.getSize(x, top, z).equals("1x1")) {
            output.add(EnumChatFormatting.LIGHT_PURPLE + "[FPU]: If you see this message in game (and did not create ghost blocks), send a");
            output.add(EnumChatFormatting.LIGHT_PURPLE + "screenshot and the room name to #bug-report channel in the Discord");
            output.add(EnumChatFormatting.AQUA + MD5);
            output.add(EnumChatFormatting.AQUA + floorHash);
            output.add("[FPU]: You are probably in: ");
            output.add(EnumChatFormatting.GREEN + "Literally no idea, all the rooms should have been found");
            lastRoomJson = null;
            return output;
        }
        if (FakepixelUtilities.roomsJson.get(MD5) == null) {
            lastRoomJson = null;
            return output;
        }
        JsonArray MD5Array = FakepixelUtilities.roomsJson.get(MD5).getAsJsonArray();
        int arraySize = MD5Array.size();
        if (arraySize >= 2) {
            boolean floorHashFound = false;
            List<String> chatMessages = new ArrayList<>();
            for (int i = 0; i < arraySize; i++) {
                JsonObject roomObject = MD5Array.get(i).getAsJsonObject();
                JsonElement jsonFloorHash = roomObject.get("floorhash");
                if (floorHash != null && jsonFloorHash != null) {
                    if (floorHash.equals(jsonFloorHash.getAsString())) {
                        String name = roomObject.get("name").getAsString();
                        String category = roomObject.get("category").getAsString();
                        int secrets = roomObject.get("secrets").getAsInt();
                        String fairysoul = "";
                        if (roomObject.get("fairysoul") != null) {
                            fairysoul = EnumChatFormatting.WHITE + " - " + EnumChatFormatting.LIGHT_PURPLE + "Fairy Soul";
                        }
                        output.add(text + category + " - " + name + fairysoul);
                        JsonElement notes = roomObject.get("notes");
                        if (notes != null) {
                            output.add(EnumChatFormatting.GREEN + notes.getAsString());
                        }
                        if (FakepixelUtilities.waypointsJson.get(name) == null && secrets != 0 && Waypoints.enabled) {
                            output.add(EnumChatFormatting.RED + "No waypoints available");
                            output.add(EnumChatFormatting.RED + "Press \"" + GameSettings.getKeyDisplayString(FakepixelUtilities.keyBindings[0].getKeyCode()) + "\" to view images");
                        }
                        lastRoomJson = roomObject;
                        floorHashFound = true;
                    }
                } else {
                    String name2 = roomObject.get("name").getAsString();
                    String category2 = roomObject.get("category").getAsString();
                    int secrets2 = roomObject.get("secrets").getAsInt();
                    String fairysoul2 = "";
                    if (roomObject.get("fairysoul") != null) {
                        fairysoul2 = EnumChatFormatting.WHITE + " - " + EnumChatFormatting.LIGHT_PURPLE + "Fairy Soul";
                    }
                    chatMessages.add(EnumChatFormatting.GREEN + category2 + " - " + name2 + fairysoul2);
                    JsonElement notes2 = roomObject.get("notes");
                    if (notes2 != null) {
                        chatMessages.add(EnumChatFormatting.GREEN + notes2.getAsString());
                    }
                    if (FakepixelUtilities.waypointsJson.get(name2) == null && secrets2 != 0 && Waypoints.enabled) {
                        output.add(EnumChatFormatting.RED + "No waypoints available");
                        output.add(EnumChatFormatting.RED + "Press \"" + GameSettings.getKeyDisplayString(FakepixelUtilities.keyBindings[0].getKeyCode()) + "\" to view images");
                    }
                }
            }
            if (!floorHashFound) {
                output.add("[FPU]: You are probably in one of the following: ");
                output.add(EnumChatFormatting.AQUA + "(check # of secrets to narrow down rooms)");
                output.addAll(chatMessages);
                output.add(EnumChatFormatting.LIGHT_PURPLE + "[FPU]: If you see this message in game (and did not create ghost blocks), send a");
                output.add(EnumChatFormatting.LIGHT_PURPLE + "screenshot and the room name to #bug-report channel in the Discord");
                output.add(EnumChatFormatting.AQUA + MD5);
                output.add(EnumChatFormatting.AQUA + floorHash);
                lastRoomJson = null;
            }
        } else {
            JsonObject roomObject2 = MD5Array.get(0).getAsJsonObject();
            String name3 = roomObject2.get("name").getAsString();
            String category3 = roomObject2.get("category").getAsString();
            int secrets3 = roomObject2.get("secrets").getAsInt();
            String fairysoul3 = "";
            if (roomObject2.get("fairysoul") != null) {
                fairysoul3 = EnumChatFormatting.WHITE + " - " + EnumChatFormatting.LIGHT_PURPLE + "Fairy Soul";
            }
            output.add(text + category3 + " - " + name3 + fairysoul3);
            JsonElement notes3 = roomObject2.get("notes");
            if (notes3 != null) {
                output.add(EnumChatFormatting.GREEN + notes3.getAsString());
            }
            if (FakepixelUtilities.waypointsJson.get(name3) == null && secrets3 != 0 && Waypoints.enabled) {
                output.add(EnumChatFormatting.RED + "No waypoints available");
                output.add(EnumChatFormatting.RED + "Press \"" + GameSettings.getKeyDisplayString(FakepixelUtilities.keyBindings[0].getKeyCode()) + "\" to view images");
            }
            lastRoomJson = roomObject2;
        }
        if (lastRoomJson != null && lastRoomJson.get("name") != null) {
            lastRoomName = lastRoomJson.get("name").getAsString();
            int secretCount = lastRoomJson.get("secrets").getAsInt();
            int listSize = secretCount > 0 ? secretCount : 1;
            currentRoomKey = lastRoomName + "_" + coordKey;
            Waypoints.allSecretsMap.putIfAbsent(currentRoomKey, new ArrayList(Collections.nCopies(listSize, true)));
            Waypoints.secretsList = Waypoints.allSecretsMap.get(currentRoomKey);
        } else {
            lastRoomName = null;
            currentRoomKey = null;
        }
        return output;
    }

    public static void toggledChat() {
        if (newRoom) {
            EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
            if (autoTextOutput == null || autoTextOutput.isEmpty()) {
                return;
            }
            for (String message : autoTextOutput) {
                entityPlayerSP.addChatMessage(new ChatComponentText(message));
            }
        }
    }

    public static void renderText() {
        if (autoTextOutput == null || autoTextOutput.isEmpty()) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int y = 0;
        for (String message : autoTextOutput) {
            int roomStringWidth = mc.fontRendererObj.getStringWidth(message);
            TextRenderer.drawText(mc, message, ((scaledResolution.getScaledWidth() * scaleX) / 100) - (roomStringWidth / 2), ((scaledResolution.getScaledHeight() * scaleY) / 100) + y, 1.0d, true);
            y += mc.fontRendererObj.FONT_HEIGHT;
        }
    }

    public static void renderCoord() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        BlockPos relativeCoord = Utils.actualToRelative(new BlockPos(player.posX, player.posY, player.posZ));
        if (relativeCoord == null) {
            return;
        }
        List<String> coordDisplay = new ArrayList<>();
        coordDisplay.add("Direction: " + Utils.originCorner);
        coordDisplay.add("Origin: " + Utils.originBlock.getX() + "," + Utils.originBlock.getY() + "," + Utils.originBlock.getZ());
        coordDisplay.add("Relative Pos.: " + relativeCoord.getX() + "," + relativeCoord.getY() + "," + relativeCoord.getZ());
        int yPos = 0;
        for (String message : coordDisplay) {
            int roomStringWidth = mc.fontRendererObj.getStringWidth(message);
            TextRenderer.drawText(mc, message, ((scaledResolution.getScaledWidth() * 95) / 100) - roomStringWidth, ((scaledResolution.getScaledHeight() * 5) / 100) + yPos, 1.0d, true);
            yPos += mc.fontRendererObj.FONT_HEIGHT;
        }
    }
}
