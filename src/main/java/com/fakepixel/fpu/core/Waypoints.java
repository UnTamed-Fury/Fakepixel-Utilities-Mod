package com.fakepixel.fpu.core;

import com.fakepixel.fpu.FakepixelUtilities;
import com.fakepixel.fpu.events.PacketEvent;
import com.fakepixel.fpu.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class Waypoints {
    public static boolean enabled = true;
    public static boolean showEntrance = true;
    public static boolean showSuperboom = true;
    public static boolean showSecrets = true;
    public static boolean showFairySouls = true;
    public static boolean sneakToDisable = true;
    public static boolean disableWhenAllFound = true;
    public static boolean allFound = false;
    public static boolean showWaypointText = true;
    public static boolean showBoundingBox = true;
    public static boolean showBeacon = true;
    public static int secretNum = 0;
    public static int completedSecrets = 0;
    public static Map<String, List<Boolean>> allSecretsMap = new HashMap();
    public static List<Boolean> secretsList = new ArrayList(Arrays.asList(new Boolean[9]));
    static long lastSneakTime = 0;

    /* JADX WARN: Removed duplicated region for block: B:103:0x035d  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x040f A[SYNTHETIC] */
    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onWorldRender(net.minecraftforge.client.event.RenderWorldLastEvent r18) {
        /*
            Method dump skipped, instruction units count: 1046
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fakepixel.fpu.core.Waypoints.onWorldRender(net.minecraftforge.client.event.RenderWorldLastEvent):void");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChat(ClientChatReceivedEvent event) {
        if (Utils.inDungeons && enabled && event.type == 2) {
            String[] actionBarSections = event.message.getUnformattedText().split(" {3,}");
            for (String section : actionBarSections) {
                if (section.contains("Secrets") && section.contains("/")) {
                    String cleanedSection = StringUtils.stripControlCodes(section);
                    String[] splitSecrets = cleanedSection.split("/");
                    completedSecrets = Integer.parseInt(splitSecrets[0].replaceAll("[^0-9]", ""));
                    int totalSecrets = Integer.parseInt(splitSecrets[1].replaceAll("[^0-9]", ""));
                    allFound = totalSecrets == secretNum && completedSecrets == secretNum;
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        Boolean isSecretFound;
        if (Utils.inDungeons && enabled) {
            if ((!disableWhenAllFound || !allFound) && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                Block block = event.world.getBlockState(event.pos).getBlock();
                if ((block == Blocks.chest || block == Blocks.skull) && AutoRoom.lastRoomJson != null && AutoRoom.lastRoomName != null) {
                    secretNum = AutoRoom.lastRoomJson.get("secrets").getAsInt();
                    if (FakepixelUtilities.waypointsJson.get(AutoRoom.lastRoomName) != null) {
                        JsonArray secretsArray = FakepixelUtilities.waypointsJson.get(AutoRoom.lastRoomName).getAsJsonArray();
                        int arraySize = secretsArray.size();
                        for (int i = 0; i < arraySize; i++) {
                            JsonObject secretsObject = secretsArray.get(i).getAsJsonObject();
                            if (secretsObject.get("category").getAsString().equals("chest") || secretsObject.get("category").getAsString().equals("wither")) {
                                BlockPos pos = Utils.relativeToActual(new BlockPos(secretsObject.get("x").getAsInt(), secretsObject.get("y").getAsInt(), secretsObject.get("z").getAsInt()));
                                if (pos == null) {
                                    return;
                                }
                                if (pos.equals(event.pos)) {
                                    for (int j = 1; j <= secretNum && j - 1 < secretsList.size(); j++) {
                                        if (secretsObject.get("secretName").getAsString().contains(String.valueOf(j)) && ((isSecretFound = secretsList.get(j - 1)) == null || isSecretFound.booleanValue())) {
                                            secretsList.set(j - 1, false);
                                            allSecretsMap.replace(AutoRoom.currentRoomKey, secretsList);
                                            FakepixelUtilities.logger.info("[FPU]: Detected " + secretsObject.get("category").getAsString() + " click, turning off waypoint for secret #" + j);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.ReceiveEvent event) {
        Boolean isSecretFound;
        if (Utils.inDungeons && enabled) {
            if (disableWhenAllFound && allFound) {
                return;
            }
            Minecraft mc = Minecraft.getMinecraft();
            if (event.packet instanceof S0DPacketCollectItem) {
                S0DPacketCollectItem packet = (S0DPacketCollectItem) event.packet;
                Entity entityItem = mc.theWorld.getEntityByID(packet.getCollectedItemEntityID());
                if (entityItem instanceof EntityItem) {
                    EntityItem item = (EntityItem) entityItem;
                    Entity entity = mc.theWorld.getEntityByID(packet.getEntityID());
                    if (entity == null) {
                        return;
                    }
                    String name = item.getEntityItem().getDisplayName();
                    if ((name.contains("Decoy") || name.contains("Defuse Kit") || name.contains("Dungeon Chest Key") || name.contains("Healing VIII") || name.contains("Inflatable Jerry") || name.contains("Spirit Leap") || name.contains("Training Weights") || name.contains("Trap") || name.contains("Treasure Talisman")) && entity.getCommandSenderEntity().getName().equals(mc.thePlayer.getName()) && AutoRoom.lastRoomJson != null && AutoRoom.lastRoomName != null) {
                        secretNum = AutoRoom.lastRoomJson.get("secrets").getAsInt();
                        if (FakepixelUtilities.waypointsJson.get(AutoRoom.lastRoomName) != null) {
                            JsonArray secretsArray = FakepixelUtilities.waypointsJson.get(AutoRoom.lastRoomName).getAsJsonArray();
                            int arraySize = secretsArray.size();
                            for (int i = 0; i < arraySize; i++) {
                                JsonObject secretsObject = secretsArray.get(i).getAsJsonObject();
                                if (secretsObject.get("category").getAsString().equals("item") || secretsObject.get("category").getAsString().equals("bat")) {
                                    BlockPos pos = Utils.relativeToActual(new BlockPos(secretsObject.get("x").getAsInt(), secretsObject.get("y").getAsInt(), secretsObject.get("z").getAsInt()));
                                    if (pos == null) {
                                        return;
                                    }
                                    if (entity.getDistanceSq(pos) <= 36.0d) {
                                        for (int j = 1; j <= secretNum && j - 1 < secretsList.size(); j++) {
                                            if (secretsObject.get("secretName").getAsString().contains(String.valueOf(j)) && ((isSecretFound = secretsList.get(j - 1)) == null || isSecretFound.booleanValue())) {
                                                secretsList.set(j - 1, false);
                                                allSecretsMap.replace(AutoRoom.currentRoomKey, secretsList);
                                                FakepixelUtilities.logger.info("[FPU]: " + entity.getCommandSenderEntity().getName() + " picked up " + StringUtils.stripControlCodes(name) + " from a " + secretsObject.get("category").getAsString() + " secret, turning off waypoint for secret #" + j);
                                                return;
                                            }
                                        }
                                    } else {
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        Boolean isSecretFound;
        if (Utils.inDungeons && enabled && sneakToDisable) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            if (FMLClientHandler.instance().getClient().gameSettings.keyBindSneak.isPressed()) {
                if (System.currentTimeMillis() - lastSneakTime < 1000 && AutoRoom.lastRoomJson != null && AutoRoom.lastRoomName != null) {
                    secretNum = AutoRoom.lastRoomJson.get("secrets").getAsInt();
                    if (FakepixelUtilities.waypointsJson.get(AutoRoom.lastRoomName) != null) {
                        JsonArray secretsArray = FakepixelUtilities.waypointsJson.get(AutoRoom.lastRoomName).getAsJsonArray();
                        int arraySize = secretsArray.size();
                        for (int i = 0; i < arraySize; i++) {
                            JsonObject secretsObject = secretsArray.get(i).getAsJsonObject();
                            if (secretsObject.get("category").getAsString().equals("chest") || secretsObject.get("category").getAsString().equals("wither") || secretsObject.get("category").getAsString().equals("item") || secretsObject.get("category").getAsString().equals("bat")) {
                                BlockPos pos = Utils.relativeToActual(new BlockPos(secretsObject.get("x").getAsInt(), secretsObject.get("y").getAsInt(), secretsObject.get("z").getAsInt()));
                                if (pos == null) {
                                    return;
                                }
                                if (player.getDistanceSq(pos) <= 16.0d) {
                                    for (int j = 1; j <= secretNum && j - 1 < secretsList.size(); j++) {
                                        if (secretsObject.get("secretName").getAsString().contains(String.valueOf(j)) && ((isSecretFound = secretsList.get(j - 1)) == null || isSecretFound.booleanValue())) {
                                            secretsList.set(j - 1, false);
                                            allSecretsMap.replace(AutoRoom.currentRoomKey, secretsList);
                                            FakepixelUtilities.logger.info("s: Player sneaked near " + secretsObject.get("category").getAsString() + " secret, turning off waypoint for secret #" + j);
                                            return;
                                        }
                                    }
                                } else {
                                    continue;
                                }
                            }
                        }
                    }
                }
                lastSneakTime = System.currentTimeMillis();
            }
        }
    }
}
