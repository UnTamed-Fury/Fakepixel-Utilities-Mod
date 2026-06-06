package com.fakepixel.fpu.gui;

import com.fakepixel.fpu.FakepixelUtilities;
import com.fakepixel.fpu.core.AutoRoom;
import com.fakepixel.fpu.handlers.OpenLink;
import com.fakepixel.fpu.handlers.TextRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.lib.Opcodes;

public class LinkGUI extends GuiScreen {
    private GuiButton discordClient;
    private GuiButton discordBrowser;
    private GuiButton SBPSecrets;
    private GuiButton close;

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void initGui() {
        super.initGui();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int height = sr.getScaledHeight();
        int width = sr.getScaledWidth();
        this.discordClient = new GuiButton(0, (width / 2) - Opcodes.INVOKEINTERFACE, (height / 6) + 96, Opcodes.ISHL, 20, "DSG Discord Client");
        this.discordBrowser = new GuiButton(1, (width / 2) - 60, (height / 6) + 96, Opcodes.ISHL, 20, "DSG Discord Browser");
        this.SBPSecrets = new GuiButton(2, (width / 2) + 65, (height / 6) + 96, Opcodes.ISHL, 20, "SBP Secrets Mod");
        this.close = new GuiButton(3, (width / 2) - 60, (height / 6) + Opcodes.L2I, Opcodes.ISHL, 20, "Close");
        this.buttonList.add(this.discordClient);
        this.buttonList.add(this.discordBrowser);
        this.buttonList.add(this.SBPSecrets);
        this.buttonList.add(this.close);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String displayText;
        drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();
        if (AutoRoom.lastRoomName == null) {
            displayText = "Where would you like to view secrets for: " + EnumChatFormatting.RED + "null";
        } else {
            displayText = "Where would you like to view secrets for: " + EnumChatFormatting.GREEN + AutoRoom.lastRoomName;
        }
        int displayWidth = mc.fontRendererObj.getStringWidth(displayText);
        TextRenderer.drawText(mc, displayText, (this.width / 2) - (displayWidth / 2), (this.height / 6) + 56, 1.0d, false);
        String noteText = EnumChatFormatting.GRAY + "If you wish to have the hotkey go directly to DSG or SBP instead of this GUI run " + EnumChatFormatting.WHITE + "/room set <gui | dsg | sbp>";
        int noteWidth = mc.fontRendererObj.getStringWidth(noteText);
        TextRenderer.drawText(mc, noteText, (this.width / 2) - (noteWidth / 2), (int) (((double) this.height) * 0.9d), 1.0d, false);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void actionPerformed(GuiButton button) {
        EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
        if (button == this.discordClient) {
            OpenLink.openDiscord("client");
            entityPlayerSP.closeScreen();
            return;
        }
        if (button == this.discordBrowser) {
            OpenLink.openDiscord("browser");
            entityPlayerSP.closeScreen();
            return;
        }
        if (button == this.SBPSecrets) {
            if (FakepixelUtilities.usingSBPSecrets) {
                OpenLink.openSBPSecrets();
            } else {
                ChatComponentText sbp = new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.UNDERLINE + "https://discord.gg/2UjaFqfPwJ");
                sbp.setChatStyle(sbp.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/2UjaFqfPwJ")));
                entityPlayerSP.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU]: You need the Skyblock Personalized (SBP) Mod for this feature, get it from ").appendSibling(sbp));
            }
            entityPlayerSP.closeScreen();
            return;
        }
        if (button == this.close) {
            entityPlayerSP.closeScreen();
        }
    }
}
