package com.fakepixel.fpu.utils;

import com.fakepixel.fpu.handlers.ScoreboardHandler;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.BlockPos;

public class Utils {
    public static boolean inSkyblock = false;
    public static boolean inDungeons = false;
    public static boolean dungeonOverride = false;
    public static BlockPos originBlock = null;
    public static String originCorner = null;

    public static void checkForSkyblock() {
        ScoreObjective scoreboardObj;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.theWorld != null && !mc.isSingleplayer() && (scoreboardObj = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1)) != null) {
            String scObjName = ScoreboardHandler.cleanSB(scoreboardObj.getDisplayName());
            if (scObjName.contains("SKYBLOCK")) {
                inSkyblock = true;
                return;
            }
        }
        inSkyblock = false;
    }

    public static void checkForDungeons() {
        if (dungeonOverride) {
            inDungeons = true;
            return;
        }
        if (inSkyblock) {
            List<String> scoreboard = ScoreboardHandler.getSidebarLines();
            for (String s : scoreboard) {
                String sCleaned = ScoreboardHandler.cleanSB(s);
                if (sCleaned.contains("The Catacombs")) {
                    inDungeons = true;
                    return;
                }
            }
        }
        inDungeons = false;
    }

    public static int dungeonTop(double x, double y, double z) {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient worldClient = mc.theWorld;
        for (int i = 255; i >= 78; i--) {
            Block top = worldClient.getBlockState(new BlockPos(x, i, z)).getBlock();
            if (top != Blocks.air && checkPlatform(x, i, z)) {
                return i;
            }
        }
        return -1;
    }

    public static int dungeonBottom(double x, double y, double z) {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient worldClient = mc.theWorld;
        for (int i = 0; i <= 68; i++) {
            Block bottom = worldClient.getBlockState(new BlockPos(x, i, z)).getBlock();
            if (bottom == Blocks.bedrock || bottom == Blocks.stone) {
                return i;
            }
        }
        return -1;
    }

    public static int dungeonHeight(double x, double z) {
        return dungeonTop(x, 68.0d, z) - dungeonBottom(x, 68.0d, z);
    }

    public static boolean checkPlatform(double x, double y, double z) {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient worldClient = mc.theWorld;
        int checkedNorth = 0;
        int checkedSouth = 0;
        int checkedEast = 0;
        int checkedWest = 0;
        for (int j = 0; j < 10; j++) {
            Block checkNorth = worldClient.getBlockState(new BlockPos(x, y, z - ((double) j))).getBlock();
            if (checkNorth != Blocks.air) {
                checkedNorth++;
            }
            Block checkSouth = worldClient.getBlockState(new BlockPos(x, y, z + ((double) j))).getBlock();
            if (checkSouth != Blocks.air) {
                checkedSouth++;
            }
            Block checkEast = worldClient.getBlockState(new BlockPos(x + ((double) j), y, z)).getBlock();
            if (checkEast != Blocks.air) {
                checkedEast++;
            }
            Block checkWest = worldClient.getBlockState(new BlockPos(x - ((double) j), y, z)).getBlock();
            if (checkWest != Blocks.air) {
                checkedWest++;
            }
        }
        return checkedNorth == 10 || checkedSouth == 10 || checkedEast == 10 || checkedWest == 10;
    }

    public static int endOfRoom(int x, int y, int z, String direction) {
        WorldClient worldClient;
        Minecraft mc = Minecraft.getMinecraft();
        worldClient = mc.theWorld;
        switch (direction) {
            case "n":
                for (int i = 1; i <= 200; i++) {
                    Block northEnd = worldClient.getBlockState(new BlockPos(x, y, z - i)).getBlock();
                    if (northEnd == Blocks.air || checkPlatform(x, y + 1, z - i) || Math.abs(dungeonHeight(x, z - i) - dungeonHeight(x, (z - i) + 1)) > 3) {
                        return (z - i) + 1;
                    }
                }
                return -1;
            case "s":
                for (int i2 = 1; i2 <= 200; i2++) {
                    Block southEnd = worldClient.getBlockState(new BlockPos(x, y, z + i2)).getBlock();
                    if (southEnd == Blocks.air || checkPlatform(x, y + 1, z + i2) || Math.abs(dungeonHeight(x, z + i2) - dungeonHeight(x, (z + i2) - 1)) > 3) {
                        return (z + i2) - 1;
                    }
                }
                return -1;
            case "e":
                for (int i3 = 1; i3 <= 200; i3++) {
                    Block eastEnd = worldClient.getBlockState(new BlockPos(x + i3, y, z)).getBlock();
                    if (eastEnd == Blocks.air || checkPlatform(x + i3, y + 1, z) || Math.abs(dungeonHeight(x + i3, z) - dungeonHeight((x + i3) - 1, z)) > 3) {
                        return (x + i3) - 1;
                    }
                }
                return -1;
            case "w":
                for (int i4 = 1; i4 <= 200; i4++) {
                    Block westEnd = worldClient.getBlockState(new BlockPos(x - i4, y, z)).getBlock();
                    if (westEnd == Blocks.air || checkPlatform(x - i4, y + 1, z) || Math.abs(dungeonHeight(x - i4, z) - dungeonHeight((x - i4) + 1, z)) > 3) {
                        return (x - i4) + 1;
                    }
                }
                return -1;
            default:
                return -1;
        }
    }

    public static int northWidth(int x, int y, int z) {
        int northZ = endOfRoom(x, y, z, "n");
        return endOfRoom(x, y, northZ, "e") - endOfRoom(x, y, northZ, "w");
    }

    public static int southWidth(int x, int y, int z) {
        int southZ = endOfRoom(x, y, z, "s");
        return endOfRoom(x, y, southZ, "e") - endOfRoom(x, y, southZ, "w");
    }

    public static int eastWidth(int x, int y, int z) {
        int eastX = endOfRoom(x, y, z, "e");
        return endOfRoom(eastX, y, z, "s") - endOfRoom(eastX, y, z, "n");
    }

    public static int westWidth(int x, int y, int z) {
        int westX = endOfRoom(x, y, z, "w");
        return endOfRoom(westX, y, z, "s") - endOfRoom(westX, y, z, "n");
    }

    public static String getDimensions(int x, int y, int z) {
        return "n:" + northWidth(x, y, z) + " s:" + southWidth(x, y, z) + " e:" + eastWidth(x, y, z) + " w:" + westWidth(x, y, z);
    }

    public static String getSize(int x, int y, int z) {
        int n = northWidth(x, y, z);
        int s = southWidth(x, y, z);
        int e = eastWidth(x, y, z);
        int w = westWidth(x, y, z);
        if (n == s && s == e && e == w) {
            return n == 30 ? "1x1" : n == 62 ? "2x2" : "error";
        }
        if (n == s && e == w) {
            return (n == 62 && e == 30) ? "1x2" : (n == 30 && e == 62) ? "1x2" : (n == 94 && e == 30) ? "1x3" : (n == 30 && e == 94) ? "1x3" : (n == 126 && e == 30) ? "1x4" : (n == 30 && e == 126) ? "1x4" : "error";
        }
        if (n != s || e != w) {
            int length62 = (n == 62 ? 1 : 0) + (s == 62 ? 1 : 0) + (e == 62 ? 1 : 0) + (w == 62 ? 1 : 0);
            int length30 = (n == 30 ? 1 : 0) + (s == 30 ? 1 : 0) + (e == 30 ? 1 : 0) + (w == 30 ? 1 : 0);
            return (length62 < 2 || length30 != 4 - length62) ? "error" : "L-shape";
        }
        return "error";
    }

    public static String blockFrequency(int x, int y, int z, boolean isPlayerPos) {
        if (y == -1) {
            return null;
        }
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient worldClient = mc.theWorld;
        List<String> blockList = new ArrayList<>();
        List<String> frequencies = new ArrayList<>();
        if (northWidth(x, y, z) == southWidth(x, y, z)) {
            if (eastWidth(x, y, z) == westWidth(x, y, z)) {
                int northEndZ = endOfRoom(x, y, z, "n");
                int northWestEndX = endOfRoom(x, y, northEndZ, "w");
                int southEndZ = endOfRoom(x, y, z, "s");
                int southEastEndX = endOfRoom(x, y, southEndZ, "e");
                BlockPos northWestCorner = new BlockPos(northWestEndX, y, northEndZ);
                BlockPos southEastCorner = new BlockPos(southEastEndX, y, southEndZ);
                Iterable<BlockPos> blocks = BlockPos.getAllInBox(northWestCorner, southEastCorner);
                for (BlockPos blockPos : blocks) {
                    if (isPlayerPos) {
                        checkCorner(blockPos);
                    }
                    blockList.add(worldClient.getBlockState(blockPos).toString());
                }
            } else if (getSize(x, y, z).equals("L-shape")) {
                if (eastWidth(x, y, z) > westWidth(x, y, z)) {
                    int eastEndX = endOfRoom(x, y, z, "e");
                    int northEndZ2 = endOfRoom(eastEndX, y, z, "n");
                    for (int i = 0; i < 200; i++) {
                        Block nextColumn = worldClient.getBlockState(new BlockPos(eastEndX, y, northEndZ2 + i)).getBlock();
                        if (nextColumn == Blocks.air || checkPlatform(eastEndX, y + 1, northEndZ2 + i) || (i > 0 && Math.abs(dungeonHeight(eastEndX, northEndZ2 + i) - dungeonHeight(eastEndX, (northEndZ2 + i) - 1)) > 3)) {
                            break;
                        }
                        for (int j = 0; j < 200; j++) {
                            BlockPos nextBlockPos = new BlockPos(eastEndX - j, y, northEndZ2 + i);
                            Block nextBlock = worldClient.getBlockState(nextBlockPos).getBlock();
                            if (nextBlock == Blocks.air || checkPlatform(eastEndX - j, y + 1, northEndZ2 + i) || (j > 0 && Math.abs(dungeonHeight(eastEndX - j, northEndZ2 + i) - dungeonHeight((eastEndX - j) + 1, northEndZ2 + i)) > 3)) {
                                break;
                            }
                            if (isPlayerPos) {
                                checkCorner(nextBlockPos);
                            }
                            blockList.add(nextBlock.toString());
                        }
                    }
                } else if (westWidth(x, y, z) > eastWidth(x, y, z)) {
                    int westEndX = endOfRoom(x, y, z, "w");
                    int northEndZ3 = endOfRoom(westEndX, y, z, "n");
                    for (int i2 = 0; i2 < 200; i2++) {
                        Block nextColumn2 = worldClient.getBlockState(new BlockPos(westEndX, y, northEndZ3 + i2)).getBlock();
                        if (nextColumn2 == Blocks.air || checkPlatform(westEndX, y + 1, northEndZ3 + i2) || (i2 > 0 && Math.abs(dungeonHeight(westEndX, northEndZ3 + i2) - dungeonHeight(westEndX, (northEndZ3 + i2) - 1)) > 3)) {
                            break;
                        }
                        for (int j2 = 0; j2 < 200; j2++) {
                            BlockPos nextBlockPos2 = new BlockPos(westEndX + j2, y, northEndZ3 + i2);
                            Block nextBlock2 = worldClient.getBlockState(nextBlockPos2).getBlock();
                            if (nextBlock2 == Blocks.air || checkPlatform(westEndX + j2, y + 1, northEndZ3 + i2) || (j2 > 0 && Math.abs(dungeonHeight(westEndX + j2, northEndZ3 + i2) - dungeonHeight((westEndX + j2) - 1, northEndZ3 + i2)) > 3)) {
                                break;
                            }
                            if (isPlayerPos) {
                                checkCorner(nextBlockPos2);
                            }
                            blockList.add(nextBlock2.toString());
                        }
                    }
                }
            }
        } else if (getSize(x, y, z).equals("L-shape")) {
            if (northWidth(x, y, z) > southWidth(x, y, z)) {
                int northEndZ4 = endOfRoom(x, y, z, "n");
                int westEndX2 = endOfRoom(x, y, northEndZ4, "w");
                for (int i3 = 0; i3 < 200; i3++) {
                    Block nextColumn3 = worldClient.getBlockState(new BlockPos(westEndX2 + i3, y, northEndZ4)).getBlock();
                    if (nextColumn3 == Blocks.air || checkPlatform(westEndX2 + i3, y + 1, northEndZ4) || (i3 > 0 && Math.abs(dungeonHeight(westEndX2 + i3, northEndZ4) - dungeonHeight((westEndX2 + i3) - 1, northEndZ4)) > 3)) {
                        break;
                    }
                    for (int j3 = 0; j3 < 200; j3++) {
                        BlockPos nextBlockPos3 = new BlockPos(westEndX2 + i3, y, northEndZ4 + j3);
                        Block nextBlock3 = worldClient.getBlockState(nextBlockPos3).getBlock();
                        if (nextBlock3 == Blocks.air || checkPlatform(westEndX2 + i3, y + 1, northEndZ4 + j3) || (j3 > 0 && Math.abs(dungeonHeight(westEndX2 + i3, northEndZ4 + j3) - dungeonHeight(westEndX2 + i3, (northEndZ4 + j3) - 1)) > 3)) {
                            break;
                        }
                        if (isPlayerPos) {
                            checkCorner(nextBlockPos3);
                        }
                        blockList.add(nextBlock3.toString());
                    }
                }
            } else if (southWidth(x, y, z) > northWidth(x, y, z)) {
                int southEndZ2 = endOfRoom(x, y, z, "s");
                int westEndX3 = endOfRoom(x, y, southEndZ2, "w");
                for (int i4 = 0; i4 < 200; i4++) {
                    Block nextColumn4 = worldClient.getBlockState(new BlockPos(westEndX3 + i4, y, southEndZ2)).getBlock();
                    if (nextColumn4 == Blocks.air || checkPlatform(westEndX3 + i4, y + 1, southEndZ2) || (i4 > 0 && Math.abs(dungeonHeight(westEndX3 + i4, southEndZ2) - dungeonHeight((westEndX3 + i4) - 1, southEndZ2)) > 3)) {
                        break;
                    }
                    for (int j4 = 0; j4 < 200; j4++) {
                        BlockPos nextBlockPos4 = new BlockPos(westEndX3 + i4, y, southEndZ2 - j4);
                        Block nextBlock4 = worldClient.getBlockState(nextBlockPos4).getBlock();
                        if (nextBlock4 == Blocks.air || checkPlatform(westEndX3 + i4, y + 1, southEndZ2 - j4) || (j4 > 0 && Math.abs(dungeonHeight(westEndX3 + i4, southEndZ2 - j4) - dungeonHeight(westEndX3 + i4, (southEndZ2 - j4) + 1)) > 3)) {
                            break;
                        }
                        if (isPlayerPos) {
                            checkCorner(nextBlockPos4);
                        }
                        blockList.add(nextBlock4.toString());
                    }
                }
            }
        }
        if (blockList.isEmpty()) {
            return null;
        }
        Set<String> distinct = new HashSet<>(blockList);
        for (String s : distinct) {
            frequencies.add(s + ":" + Collections.frequency(blockList, s));
        }
        Collections.sort(frequencies);
        return String.join(",", frequencies);
    }

    public static void checkCorner(BlockPos blockPos) {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient worldClient = mc.theWorld;
        if (worldClient.getBlockState(blockPos).getBlock() == Blocks.stained_hardened_clay) {
            Block northBlock = worldClient.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() - 1)).getBlock();
            Block southBlock = worldClient.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1)).getBlock();
            Block eastBlock = worldClient.getBlockState(new BlockPos(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ())).getBlock();
            Block westBlock = worldClient.getBlockState(new BlockPos(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ())).getBlock();
            if (northBlock == Blocks.air && southBlock != Blocks.air && eastBlock != Blocks.air && westBlock == Blocks.air) {
                originCorner = "northwest";
                originBlock = blockPos;
                return;
            }
            if (northBlock == Blocks.air && southBlock != Blocks.air && eastBlock == Blocks.air && westBlock != Blocks.air) {
                originCorner = "northeast";
                originBlock = blockPos;
                return;
            }
            if (northBlock != Blocks.air && southBlock == Blocks.air && eastBlock == Blocks.air && westBlock != Blocks.air) {
                originCorner = "southeast";
                originBlock = blockPos;
            } else if (northBlock != Blocks.air && southBlock == Blocks.air && eastBlock != Blocks.air && westBlock == Blocks.air) {
                originCorner = "southwest";
                originBlock = blockPos;
            }
        }
    }

    public static String floorFrequency(int x, int y, int z) {
        if (y == -1) {
            return null;
        }
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient worldClient = mc.theWorld;
        List<String> blockList = new ArrayList<>();
        List<String> frequencies = new ArrayList<>();
        if (northWidth(x, y, z) == southWidth(x, y, z) && eastWidth(x, y, z) == westWidth(x, y, z)) {
            int northEndZ = endOfRoom(x, y, z, "n");
            int northWestEndX = endOfRoom(x, y, northEndZ, "w");
            int southEndZ = endOfRoom(x, y, z, "s");
            int southEastEndX = endOfRoom(x, y, southEndZ, "e");
            BlockPos northWestCorner = new BlockPos(northWestEndX + 10, 68, northEndZ + 10);
            BlockPos southEastCorner = new BlockPos(southEastEndX - 10, 68, southEndZ - 10);
            Iterable<BlockPos> blocks = BlockPos.getAllInBox(northWestCorner, southEastCorner);
            for (BlockPos blockPos : blocks) {
                blockList.add(worldClient.getBlockState(blockPos).getBlock().toString());
            }
        }
        if (getSize(x, y, z).equals("L-shape")) {
            blockList.add(String.valueOf(dungeonTop(x, 68.0d, z)));
        }
        if (blockList.isEmpty()) {
            return null;
        }
        Set<String> distinct = new HashSet<>(blockList);
        for (String s : distinct) {
            frequencies.add(s + ":" + Collections.frequency(blockList, s));
        }
        Collections.sort(frequencies);
        return String.join(",", frequencies);
    }

    public static String getMD5(String input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static BlockPos actualToRelative(BlockPos actual) {
        double x;
        double z;
        if (originBlock == null || originCorner == null) {
            return null;
        }
        x = 0.0d;
        z = 0.0d;
        switch (originCorner) {
            case "northwest":
                x = actual.getX() - originBlock.getX();
                z = actual.getZ() - originBlock.getZ();
                break;
            case "northeast":
                x = actual.getZ() - originBlock.getZ();
                z = -(actual.getX() - originBlock.getX());
                break;
            case "southeast":
                x = -(actual.getX() - originBlock.getX());
                z = -(actual.getZ() - originBlock.getZ());
                break;
            case "southwest":
                x = -(actual.getZ() - originBlock.getZ());
                z = actual.getX() - originBlock.getX();
                break;
        }
        return new BlockPos(x, actual.getY(), z);
    }

    public static BlockPos relativeToActual(BlockPos relative) {
        double x;
        double z;
        if (originBlock == null || originCorner == null) {
            return null;
        }
        x = 0.0d;
        z = 0.0d;
        switch (originCorner) {
            case "northwest":
                x = relative.getX() + originBlock.getX();
                z = relative.getZ() + originBlock.getZ();
                break;
            case "northeast":
                x = -(relative.getZ() - originBlock.getX());
                z = relative.getX() + originBlock.getZ();
                break;
            case "southeast":
                x = -(relative.getX() - originBlock.getX());
                z = -(relative.getZ() - originBlock.getZ());
                break;
            case "southwest":
                x = relative.getZ() + originBlock.getX();
                z = -(relative.getX() - originBlock.getZ());
                break;
        }
        return new BlockPos(x, relative.getY(), z);
    }
}
