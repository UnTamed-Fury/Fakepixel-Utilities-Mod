package com.fakepixel.fpu.handlers;

import com.fakepixel.fpu.FakepixelUtilities;
import com.fakepixel.fpu.core.AutoRoom;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.FMLClientHandler;

public class OpenLink {
    public static void checkForLink(String type) {
        List<String> autoText;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (!AutoRoom.chatToggled && !AutoRoom.guiToggled && (autoText = AutoRoom.autoText()) != null) {
            AutoRoom.autoTextOutput = autoText;
        }
        if (AutoRoom.lastRoomHash == null) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: You do not appear to be in a detected Dungeon room right now."));
        }
        if (AutoRoom.lastRoomJson == null) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: This command does not work when the current room is detected as one of multiple."));
            return;
        }
        if (AutoRoom.lastRoomJson.get("dsg").getAsString().equals("null") && AutoRoom.lastRoomJson.get("sbp") == null) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: There are no channels/images for this room."));
            return;
        }
        switch (type) {
            case "gui":
                FakepixelUtilities.guiToOpen = "link";
                break;
            case "dsg":
                openDiscord("client");
                break;
            case "sbp":
                if (FakepixelUtilities.usingSBPSecrets) {
                    openSBPSecrets();
                    break;
                } else {
                    ChatComponentText sbp = new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.UNDERLINE + "https://discord.gg/2UjaFqfPwJ");
                    sbp.setChatStyle(sbp.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/2UjaFqfPwJ")));
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: You need the Skyblock Personalized (SBP) Mod for this feature, get it from ").appendSibling(sbp));
                    break;
                }
        }
    }

    public static void openDiscord(String type) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (AutoRoom.lastRoomJson.get("dsg").getAsString().equals("null")) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: There is no DSG channel for this room."));
            return;
        }
        try {
            if (type.equals("client")) {
                player.addChatMessage(new ChatComponentText("[FPU]: Opening DSG Discord in Client..."));
                Desktop.getDesktop().browse(new URI("discord://" + AutoRoom.lastRoomJson.get("dsg").getAsString()));
            } else {
                player.addChatMessage(new ChatComponentText("[FPU]: Opening DSG Discord in Browser..."));
                Desktop.getDesktop().browse(new URI("https://discord.com" + AutoRoom.lastRoomJson.get("dsg").getAsString()));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void openSBPSecrets() {
        String category;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (AutoRoom.lastRoomJson.get("sbp") == null) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: There are no SBP images for this room."));
            return;
        }
        String name = AutoRoom.lastRoomJson.get("sbp").getAsString();
        category = AutoRoom.lastRoomJson.get("category").getAsString();
        switch (category) {
            case "Puzzle":
                category = "puzzles";
                break;
            case "Trap":
                category = "puzzles";
                break;
            case "L-shape":
                category = "L";
                break;
        }
        ClientCommandHandler.instance.executeCommand(FMLClientHandler.instance().getClientPlayerEntity(), "/secretoverride " + category + " " + name);
    }
}
