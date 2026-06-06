package com.fakepixel.fpu.commands;

import com.fakepixel.fpu.FakepixelUtilities;
import com.fakepixel.fpu.core.AutoRoom;
import com.fakepixel.fpu.core.Waypoints;
import com.fakepixel.fpu.handlers.ConfigHandler;
import com.fakepixel.fpu.handlers.OpenLink;
import com.fakepixel.fpu.utils.Utils;
import com.google.gson.JsonElement;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;

public class DungeonRoomCommand extends CommandBase {
    public String getCommandName() {
        return "room";
    }

    public String getCommandUsage(ICommandSender arg0) {
        return "/" + getCommandName();
    }

    public List<String> getCommandAliases() {
        return Collections.singletonList("dungeonroom");
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, new String[]{"help", "waypoints", "move", "toggle", "set", "discord"});
        }
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("toggle")) {
                return getListOfStringsMatchingLastWord(args, new String[]{"help", "gui", "chat", "waypointtext", "waypointboundingbox", "waypointbeacon"});
            }
            if (args[0].equalsIgnoreCase("set")) {
                return getListOfStringsMatchingLastWord(args, new String[]{"gui", "dsg", "sbp"});
            }
            return null;
        }
        return null;
    }

    public void processCommand(ICommandSender arg0, String[] arg1) {
        new Thread(() -> {
            int top;
            String size;
            String MD5;
            String floorHash;
            Minecraft mc;
            WorldClient worldClient;
            EntityPlayer player = (EntityPlayer) arg0;
            int x = (int) Math.floor(player.posX);
            int y = (int) Math.floor(player.posY);
            int z = (int) Math.floor(player.posZ);
            if (arg1.length < 1) {
                if (!Utils.inDungeons) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: Use this command in dungeons or run \"/room help\" for additional options"));
                }
                String blockFrequencies = Utils.blockFrequency(x, Utils.dungeonTop(x, y, z), z, true);
                if (blockFrequencies == null) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: Make sure you aren't in a hallway between rooms and that your render distance is high enough."));
                    return;
                }
                List<String> autoText = AutoRoom.autoText();
                if (autoText != null) {
                    AutoRoom.autoTextOutput = autoText;
                }
                if (AutoRoom.autoTextOutput == null || AutoRoom.autoTextOutput.isEmpty()) {
                    return;
                }
                for (String message : AutoRoom.autoTextOutput) {
                    player.addChatMessage(new ChatComponentText(message));
                }
                return;
            }
            top = Utils.dungeonTop(x, y, z);
            String blockFrequencies2 = Utils.blockFrequency(x, top, z, true);
            size = Utils.getSize(x, top, z);
            MD5 = Utils.getMD5(blockFrequencies2);
            String floorFrequencies = Utils.floorFrequency(x, top, z);
            floorHash = Utils.getMD5(floorFrequencies);
            switch (arg1[0].toLowerCase()) {
                case "help":
                    player.addChatMessage(new ChatComponentText("\n" + EnumChatFormatting.GOLD + "FakepixelUtilities Mod Version " + FakepixelUtilities.VERSION + "\n" + EnumChatFormatting.DARK_PURPLE + "Hotkeys: (Configurable in Controls Menu)\n" + EnumChatFormatting.AQUA + " " + GameSettings.getKeyDisplayString(FakepixelUtilities.keyBindings[1].getKeyCode()) + EnumChatFormatting.WHITE + " - Opens Secret Waypoints configuration GUI\n" + EnumChatFormatting.AQUA + " " + GameSettings.getKeyDisplayString(FakepixelUtilities.keyBindings[0].getKeyCode()) + EnumChatFormatting.WHITE + " - (old) Opens images of secret locations\n" + EnumChatFormatting.DARK_PURPLE + "Commands:\n" + EnumChatFormatting.AQUA + " /room" + EnumChatFormatting.WHITE + " - Tells you in chat what room you are standing in.\n" + EnumChatFormatting.AQUA + " /room help" + EnumChatFormatting.WHITE + " - Displays this message.\n" + EnumChatFormatting.AQUA + " /room waypoints" + EnumChatFormatting.WHITE + " - Opens Secret Waypoints config GUI, alternatively can be opened with hotkey\n" + EnumChatFormatting.AQUA + " /room move <x> <y>" + EnumChatFormatting.WHITE + " - Moves the GUI room name text to a coordinate. <x> and <y> are numbers between 0 and 100. Default is 50 for <x> and 5 for <y>.\n" + EnumChatFormatting.AQUA + " /room toggle [argument]" + EnumChatFormatting.WHITE + " - Run \"/room toggle help\" for full list of toggles.\n" + EnumChatFormatting.AQUA + " /room set <gui | dsg | sbp>" + EnumChatFormatting.WHITE + " - Configure whether the hotkey opens the selector GUI or directly goes to DSG/SBP.\n" + EnumChatFormatting.AQUA + " /room discord" + EnumChatFormatting.WHITE + " - Opens the Discord invite for this mod in your browser.\n"));
                    break;
                case "gui":
                case "open":
                    if (!Utils.inDungeons) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: Use this command in dungeons"));
                        break;
                    } else {
                        OpenLink.checkForLink("gui");
                        break;
                        }
                        case
 "dsg":
                    if (!Utils.inDungeons) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: Use this command in dungeons"));
                        break;
                    } else {
                        OpenLink.checkForLink("dsg");
                        break;
                        }
                        case
 "sbp":
                    if (!Utils.inDungeons) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: Use this command in dungeons"));
                        break;
                    } else {
                        OpenLink.checkForLink("sbp");
                        break;
                        }
                        case
 "set":
                    switch (arg1[1].toLowerCase()) {
                        case "gui":
                            FakepixelUtilities.hotkeyOpen = "gui";
                            player.addChatMessage(new ChatComponentText("Hotkey has been set to open: GUI"));
                            ConfigHandler.writeStringConfig("gui", "hotkeyOpen", "gui");
                            break;
                        case "dsg":
                            FakepixelUtilities.hotkeyOpen = "dsg";
                            player.addChatMessage(new ChatComponentText("Hotkey has been set to open: DSG"));
                            ConfigHandler.writeStringConfig("gui", "hotkeyOpen", "dsg");
                            break;
                        case "sbp":
                            FakepixelUtilities.hotkeyOpen = "sbp";
                            player.addChatMessage(new ChatComponentText("Hotkey has been set to open: SBP"));
                            ConfigHandler.writeStringConfig("gui", "hotkeyOpen", "sbp");
                            break;
                        default:
                            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: Valid options are <gui | dsg | sbp>"));
                            break;
                            }
                            case
 "wp":
                case "waypoint":
                case "waypoints":
                    FakepixelUtilities.guiToOpen = "waypoints";
                    break;
                case "move":
                    AutoRoom.scaleX = Integer.parseInt(arg1[1]);
                    AutoRoom.scaleY = Integer.parseInt(arg1[2]);
                    ConfigHandler.writeIntConfig("gui", "scaleX", AutoRoom.scaleX);
                    ConfigHandler.writeIntConfig("gui", "scaleY", AutoRoom.scaleY);
                    player.addChatMessage(new ChatComponentText("Room GUI has been moved to " + arg1[1] + ", " + arg1[2]));
                    break;
                case "toggle":
                    String toggleHelp = "\n" + EnumChatFormatting.GOLD + " FakepixelUtilities Mod Toggle Commands:\n" + EnumChatFormatting.AQUA + " /room toggle gui" + EnumChatFormatting.WHITE + " - Toggles displaying current room in gui.\n" + EnumChatFormatting.AQUA + " /room toggle chat" + EnumChatFormatting.WHITE + " - Toggles writing current room name in chat.\n" + EnumChatFormatting.AQUA + " /room toggle waypointtext" + EnumChatFormatting.WHITE + " - Toggles displaying waypoint names above waypoints.\n" + EnumChatFormatting.AQUA + " /room toggle waypointboundingbox" + EnumChatFormatting.WHITE + " - Toggles displaying the bounding box on waypoints.\n" + EnumChatFormatting.AQUA + " /room toggle waypointbeacon" + EnumChatFormatting.WHITE + " - Toggles displaying the beacon above waypoints.\n";
                    if (arg1.length == 1) {
                        player.addChatMessage(new ChatComponentText(toggleHelp));
                        break;
                    } else {
                        switch (arg1[1].toLowerCase()) {
                            case "help":
                                player.addChatMessage(new ChatComponentText(toggleHelp));
                                break;
                            case "gui":
                                AutoRoom.guiToggled = !AutoRoom.guiToggled;
                                ConfigHandler.writeBooleanConfig("toggles", "guiToggled", AutoRoom.guiToggled);
                                player.addChatMessage(new ChatComponentText("Display room names in GUI has been set to: " + AutoRoom.guiToggled));
                                break;
                            case "chat":
                                AutoRoom.chatToggled = !AutoRoom.chatToggled;
                                ConfigHandler.writeBooleanConfig("toggles", "chatToggled", AutoRoom.chatToggled);
                                player.addChatMessage(new ChatComponentText("Display room names in Chat has been set to: " + AutoRoom.chatToggled));
                                break;
                            case "text":
                            case "waypointtext":
                                Waypoints.showWaypointText = !Waypoints.showWaypointText;
                                ConfigHandler.writeBooleanConfig("waypoint", "showWaypointText", Waypoints.showWaypointText);
                                player.addChatMessage(new ChatComponentText("Show Waypoint Text has been set to: " + Waypoints.showWaypointText));
                                break;
                            case "boundingbox":
                            case "waypointboundingbox":
                                Waypoints.showBoundingBox = !Waypoints.showBoundingBox;
                                ConfigHandler.writeBooleanConfig("waypoint", "showBoundingBox", Waypoints.showBoundingBox);
                                player.addChatMessage(new ChatComponentText("Show Waypoint Bounding Box has been set to: " + Waypoints.showBoundingBox));
                                break;
                            case "beacon":
                            case "waypointbeacon":
                                Waypoints.showBeacon = !Waypoints.showBeacon;
                                ConfigHandler.writeBooleanConfig("waypoint", "showBeacon", Waypoints.showBeacon);
                                player.addChatMessage(new ChatComponentText("Show Waypoint Beacon has been set to: " + Waypoints.showBeacon));
                                break;
                            case "dev":
                            case "coord":
                                AutoRoom.coordToggled = !AutoRoom.coordToggled;
                                ConfigHandler.writeBooleanConfig("toggles", "coordToggled", AutoRoom.coordToggled);
                                player.addChatMessage(new ChatComponentText("Display dev coords has been set to: " + AutoRoom.coordToggled));
                                break;
                            case "override":
                                Utils.dungeonOverride = !Utils.dungeonOverride;
                                player.addChatMessage(new ChatComponentText("Force inDungeons has been set to: " + Utils.dungeonOverride));
                                break;
                            default:
                                player.addChatMessage(new ChatComponentText(toggleHelp));
                                break;
                        }
                    }
                    break;
                case "Authors":
                    ConfigHandler.reloadConfig();
                    player.addChatMessage(new ChatComponentText("BrokenHearts,Hamza"));
                    break;
                case "reload":
                    ConfigHandler.reloadConfig();
                    player.addChatMessage(new ChatComponentText("Reloaded config file"));
                    break;
                case "discord":
                    try {
                        player.addChatMessage(new ChatComponentText("[FPU]: Opening Fakepixel Discord invite in browser..."));
                        Desktop.getDesktop().browse(new URI("https://discord.gg/Fakepixel"));
                        break;
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                        return;
                    }
                case "json":
                    if (!Utils.inDungeons) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: Use this command in dungeons"));
                        break;
                    } else {
                        if (FakepixelUtilities.roomsJson.get(MD5) != null) {
                            if (MD5.equals("16370f79b2cad049096f881d5294aee6") && !floorHash.equals("94fb12c91c4b46bd0c254edadaa49a3d")) {
                                floorHash = "e617eff1d7b77faf0f8dd53ec93a220f";
                            }
                            int arraySize = FakepixelUtilities.roomsJson.get(MD5).getAsJsonArray().size();
                            if (arraySize >= 2) {
                                boolean floorHashFound = false;
                                List<String> chatMessages = new ArrayList<>();
                                for (int i = 0; i < arraySize; i++) {
                                    JsonElement jsonFloorHash = FakepixelUtilities.roomsJson.get(MD5).getAsJsonArray().get(i).getAsJsonObject().get("floorhash");
                                    if (floorHash != null && jsonFloorHash != null) {
                                        if (floorHash.equals(jsonFloorHash.getAsString())) {
                                            String json = FakepixelUtilities.roomsJson.get(MD5).getAsJsonArray().get(i).getAsJsonObject().toString();
                                            player.addChatMessage(new ChatComponentText(json));
                                            floorHashFound = true;
                                        }
                                    } else {
                                        String json2 = FakepixelUtilities.roomsJson.get(MD5).getAsJsonArray().get(i).getAsJsonObject().toString();
                                        chatMessages.add(json2);
                                    }
                                }
                                if (!floorHashFound) {
                                    for (String json3 : chatMessages) {
                                        player.addChatMessage(new ChatComponentText(json3));
                                    }
                                }
                            } else {
                                String json4 = FakepixelUtilities.roomsJson.get(MD5).getAsJsonArray().get(0).getAsJsonObject().toString();
                                player.addChatMessage(new ChatComponentText(json4));
                            }
                        }
                        break;
                        }
                        case
 "copy":
                    if (Utils.inDungeons) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + Utils.getDimensions(x, top, z)));
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + size + " " + MD5));
                        break;
                        }
                        case
 "copyfloor":
                    if (Utils.inDungeons) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "floorhash " + floorHash));
                        break;
                        }
                        case
 "dev":
                    player.addChatMessage(new ChatComponentText("dev: size = " + size));
                    player.addChatMessage(new ChatComponentText("dev: MD5 = " + MD5));
                    player.addChatMessage(new ChatComponentText("dev: floorhash = " + floorHash));
                    break;
                case "coord":
                    if (Utils.originBlock == null) {
                        FakepixelUtilities.logger.warn("[FPU]: originBlock is null");
                        break;
                    } else {
                        BlockPos relativeCoord = Utils.actualToRelative(new BlockPos(player.posX, player.posY, player.posZ));
                        if (relativeCoord != null) {
                            player.addChatMessage(new ChatComponentText("Origin: " + Utils.originBlock.getX() + "," + Utils.originBlock.getY() + "," + Utils.originBlock.getZ()));
                            player.addChatMessage(new ChatComponentText("Relative Pos.: " + relativeCoord.getX() + "," + relativeCoord.getY() + "," + relativeCoord.getZ()));
                            break;
                        }
                    }
                    break;
                case "add":
                    mc = Minecraft.getMinecraft();
                    worldClient = mc.theWorld;
                    switch (arg1[1].toLowerCase()) {
                        case "chest":
                            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.objectMouseOver.getBlockPos() != null) {
                                BlockPos viewingPos = Utils.actualToRelative(mc.objectMouseOver.getBlockPos());
                                if (viewingPos != null) {
                                    if (worldClient.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() == Blocks.chest) {
                                        player.addChatMessage(new ChatComponentText("{\n  \"secretName\":\"# - Chest\",\n  \"category\":\"chest\",\n  \"x\":" + viewingPos.getX() + ",\n  \"y\":" + viewingPos.getY() + ",\n  \"z\":" + viewingPos.getZ() + "\n}"));
                                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("{\n  \"secretName\":\"# - Chest\",\n  \"category\":\"chest\",\n  \"x\":" + viewingPos.getX() + ",\n  \"y\":" + viewingPos.getY() + ",\n  \"z\":" + viewingPos.getZ() + "\n}"), (ClipboardOwner) null);
                                    } else {
                                        player.addChatMessage(new ChatComponentText("You are not looking at a Chest Secret"));
                                    }
                                    break;
                                }
                            } else {
                                player.addChatMessage(new ChatComponentText("You are not looking at anything"));
                                break;
                                }
                                case
 "wither":
                            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.objectMouseOver.getBlockPos() != null) {
                                BlockPos viewingPos2 = Utils.actualToRelative(mc.objectMouseOver.getBlockPos());
                                if (viewingPos2 != null) {
                                    if (worldClient.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() == Blocks.skull) {
                                        player.addChatMessage(new ChatComponentText("{\n  \"secretName\":\"# - Wither Essence\",\n  \"category\":\"wither\",\n  \"x\":" + viewingPos2.getX() + ",\n  \"y\":" + viewingPos2.getY() + ",\n  \"z\":" + viewingPos2.getZ() + "\n}"));
                                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("{\n  \"secretName\":\"# - Wither Essence\",\n  \"category\":\"wither\",\n  \"x\":" + viewingPos2.getX() + ",\n  \"y\":" + viewingPos2.getY() + ",\n  \"z\":" + viewingPos2.getZ() + "\n}"), (ClipboardOwner) null);
                                    } else {
                                        player.addChatMessage(new ChatComponentText("You are not looking at a Wither Essence Secret"));
                                    }
                                    break;
                                }
                            } else {
                                player.addChatMessage(new ChatComponentText("You are not looking at anything"));
                                break;
                                }
                                case
 "superboom":
                            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.objectMouseOver.getBlockPos() != null) {
                                BlockPos viewingPos3 = Utils.actualToRelative(mc.objectMouseOver.getBlockPos());
                                if (viewingPos3 != null) {
                                    if (worldClient.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() == Blocks.stonebrick) {
                                        player.addChatMessage(new ChatComponentText("{\n  \"secretName\":\"# - Superboom\",\n  \"category\":\"superboom\",\n  \"x\":" + viewingPos3.getX() + ",\n  \"y\":" + viewingPos3.getY() + ",\n  \"z\":" + viewingPos3.getZ() + "\n}"));
                                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("{\n  \"secretName\":\"# - Superboom\",\n  \"category\":\"superboom\",\n  \"x\":" + viewingPos3.getX() + ",\n  \"y\":" + viewingPos3.getY() + ",\n  \"z\":" + viewingPos3.getZ() + "\n}"), (ClipboardOwner) null);
                                    } else {
                                        player.addChatMessage(new ChatComponentText("You are not looking at a Superboom entrance"));
                                    }
                                    break;
                                }
                            } else {
                                player.addChatMessage(new ChatComponentText("You are not looking at anything"));
                                break;
                                }
                                case
 "lever":
                            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.objectMouseOver.getBlockPos() != null) {
                                BlockPos viewingPos4 = Utils.actualToRelative(mc.objectMouseOver.getBlockPos());
                                if (viewingPos4 != null) {
                                    if (worldClient.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() == Blocks.lever) {
                                        player.addChatMessage(new ChatComponentText("{\n  \"secretName\":\"# - Lever\",\n  \"category\":\"lever\",\n  \"x\":" + viewingPos4.getX() + ",\n  \"y\":" + viewingPos4.getY() + ",\n  \"z\":" + viewingPos4.getZ() + "\n}"));
                                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("{\n  \"secretName\":\"# - Lever\",\n  \"category\":\"lever\",\n  \"x\":" + viewingPos4.getX() + ",\n  \"y\":" + viewingPos4.getY() + ",\n  \"z\":" + viewingPos4.getZ() + "\n}"), (ClipboardOwner) null);
                                    } else {
                                        player.addChatMessage(new ChatComponentText("You are not looking at a Lever"));
                                    }
                                    break;
                                }
                            } else {
                                player.addChatMessage(new ChatComponentText("You are not looking at anything"));
                                break;
                                }
                                case
 "fairysoul":
                            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.objectMouseOver.getBlockPos() != null) {
                                BlockPos viewingPos5 = Utils.actualToRelative(mc.objectMouseOver.getBlockPos().up(1));
                                if (viewingPos5 != null) {
                                    if (worldClient.getBlockState(mc.objectMouseOver.getBlockPos().up(1)).getBlock() == Blocks.air) {
                                        player.addChatMessage(new ChatComponentText("{\n  \"secretName\":\"Fairy Soul\",\n  \"category\":\"fairysoul\",\n  \"x\":" + viewingPos5.getX() + ",\n  \"y\":" + viewingPos5.getY() + ",\n  \"z\":" + viewingPos5.getZ() + "\n}"));
                                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("{\n  \"secretName\":\"Fairy Soul\",\n  \"category\":\"fairysoul\",\n  \"x\":" + viewingPos5.getX() + ",\n  \"y\":" + viewingPos5.getY() + ",\n  \"z\":" + viewingPos5.getZ() + "\n}"), (ClipboardOwner) null);
                                    } else {
                                        player.addChatMessage(new ChatComponentText("You are not looking at the block below a Fairy Soul"));
                                    }
                                    break;
                                }
                            } else {
                                player.addChatMessage(new ChatComponentText("You are not looking at anything"));
                                break;
                                }
                                case
 "item":
                            BlockPos playerPos = Utils.actualToRelative(new BlockPos(player.posX, player.posY, player.posZ));
                            if (playerPos != null) {
                                player.addChatMessage(new ChatComponentText("{\n  \"secretName\":\"# - Item\",\n  \"category\":\"item\",\n  \"x\":" + playerPos.getX() + ",\n  \"y\":" + playerPos.getY() + ",\n  \"z\":" + playerPos.getZ() + "\n}"));
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("{\n  \"secretName\":\"# - Item\",\n  \"category\":\"item\",\n  \"x\":" + playerPos.getX() + ",\n  \"y\":" + playerPos.getY() + ",\n  \"z\":" + playerPos.getZ() + "\n}"), (ClipboardOwner) null);
                                break;
                                }
                                case
 "entrance":
                            BlockPos entrancePos = Utils.actualToRelative(new BlockPos(player.posX, player.posY + 1.0d, player.posZ));
                            if (entrancePos != null) {
                                player.addChatMessage(new ChatComponentText("{\n  \"secretName\":\"# - Entrance\",\n  \"category\":\"entrance\",\n  \"x\":" + entrancePos.getX() + ",\n  \"y\":" + entrancePos.getY() + ",\n  \"z\":" + entrancePos.getZ() + "\n}"));
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("{\n  \"secretName\":\"# - Entrance\",\n  \"category\":\"entrance\",\n  \"x\":" + entrancePos.getX() + ",\n  \"y\":" + entrancePos.getY() + ",\n  \"z\":" + entrancePos.getZ() + "\n}"), (ClipboardOwner) null);
                                break;
                                }
                                case
 "bat":
                            BlockPos batPos = Utils.actualToRelative(new BlockPos(player.posX, player.posY + 1.0d, player.posZ));
                            if (batPos != null) {
                                player.addChatMessage(new ChatComponentText("{\n  \"secretName\":\"# - Bat\",\n  \"category\":\"bat\",\n  \"x\":" + batPos.getX() + ",\n  \"y\":" + batPos.getY() + ",\n  \"z\":" + batPos.getZ() + "\n}"));
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("{\n  \"secretName\":\"# - Bat\",\n  \"category\":\"bat\",\n  \"x\":" + batPos.getX() + ",\n  \"y\":" + batPos.getY() + ",\n  \"z\":" + batPos.getZ() + "\n}"), (ClipboardOwner) null);
                                break;
                            }
                            break;
                        default:
                            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: Valid options are <chest | wither | superboom | lever | fairysoul | item | entrance | bat>"));
                            break;
                    }
                    break;
                default:
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: Run \"/room\" by itself to see the room name or run \"/room help\" for additional options"));
                    break;
            }
        }).start();
    }
}
