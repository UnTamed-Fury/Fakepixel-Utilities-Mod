package com.fakepixel.fpu;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ChatListener {
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());
        if (message.contains("Welcome to Fakepixel SkyBlock!") && !FakepixelUtilities.inSkyblock) {
            FakepixelUtilities.inSkyblock = true;
            if (Minecraft.getMinecraft().thePlayer != null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[FPU] " + EnumChatFormatting.GREEN + "Joined SkyBlock, The mod will work now!"));
            }
        }
        if (message.contains("spooked into the lobby!") || message.contains("connected to the lobby!") || message.contains("Sending you to lobby") || message.contains("You are now in the lobby") || message.contains("Moving you to lobby")) {
            triggerModStopNotification();
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        triggerModStopNotification();
    }

    private void triggerModStopNotification() {
        if (FakepixelUtilities.inSkyblock) {
            FakepixelUtilities.inSkyblock = false;
            if (Minecraft.getMinecraft().thePlayer != null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[FPU] " + EnumChatFormatting.RED + "Left Skyblock, The Mod will stop working now!"));
            }
        }
    }
}
