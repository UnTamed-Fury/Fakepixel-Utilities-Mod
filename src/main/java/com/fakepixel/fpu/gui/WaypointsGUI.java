package com.fakepixel.fpu.gui;

import com.fakepixel.fpu.core.AutoRoom;
import com.fakepixel.fpu.core.Waypoints;
import com.fakepixel.fpu.handlers.ConfigHandler;
import com.fakepixel.fpu.handlers.TextRenderer;
import com.fakepixel.fpu.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.lib.Opcodes;

public class WaypointsGUI extends GuiScreen {
    private GuiButton waypointsEnabled;
    private GuiButton showEntrance;
    private GuiButton showSuperboom;
    private GuiButton showSecrets;
    private GuiButton showFairySouls;
    private GuiButton disableWhenAllFound;
    private GuiButton sneakToDisable;
    private GuiButton close;
    public static List<GuiButton> secretButtonList = new ArrayList(Arrays.asList(new GuiButton[9]));
    private static boolean waypointGuiOpened = false;

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void initGui() {
        super.initGui();
        waypointGuiOpened = true;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int height = sr.getScaledHeight();
        int width = sr.getScaledWidth();
        this.waypointsEnabled = new GuiButton(0, (width / 2) - 100, height / 6, 200, 20, waypointBtnText());
        this.showEntrance = new GuiButton(1, ((width / 2) - 100) - Opcodes.FDIV, (height / 6) + 30, 200, 20, "Show Entrance Waypoints: " + getOnOff(Waypoints.showEntrance));
        this.showSuperboom = new GuiButton(2, ((width / 2) - 100) + Opcodes.FDIV, (height / 6) + 30, 200, 20, "Show Superboom Waypoints: " + getOnOff(Waypoints.showSuperboom));
        this.showSecrets = new GuiButton(3, ((width / 2) - 100) - Opcodes.FDIV, (height / 6) + 60, 200, 20, "Show Secret Waypoints: " + getOnOff(Waypoints.showSecrets));
        this.showFairySouls = new GuiButton(4, ((width / 2) - 100) + Opcodes.FDIV, (height / 6) + 60, 200, 20, "Show Fairy Soul Waypoints: " + getOnOff(Waypoints.showFairySouls));
        this.sneakToDisable = new GuiButton(5, ((width / 2) - 100) - Opcodes.FDIV, (height / 6) + 90, 200, 20, "Double-Tap Sneak to Hide Nearby: " + getOnOff(Waypoints.sneakToDisable));
        this.disableWhenAllFound = new GuiButton(6, ((width / 2) - 100) + Opcodes.FDIV, (height / 6) + 90, 200, 20, "Disable when all secrets found: " + getOnOff(Waypoints.disableWhenAllFound));
        this.close = new GuiButton(7, (width / 2) - 100, (height / 6) * 5, 200, 20, "Close");
        this.buttonList.add(this.waypointsEnabled);
        this.buttonList.add(this.showEntrance);
        this.buttonList.add(this.showSuperboom);
        this.buttonList.add(this.showSecrets);
        this.buttonList.add(this.showFairySouls);
        this.buttonList.add(this.sneakToDisable);
        this.buttonList.add(this.disableWhenAllFound);
        this.buttonList.add(this.close);
        if (Utils.inDungeons && Waypoints.secretNum > 0) {
            if (Waypoints.secretNum <= 5) {
                for (int i = 1; i <= Waypoints.secretNum; i++) {
                    int adjustPos = (((-40) * Waypoints.secretNum) - 70) + (80 * i);
                    secretButtonList.set(i - 1, new GuiButton(10 + i, (width / 2) + adjustPos, (height / 6) + Opcodes.TABLESWITCH, 60, 20, i + ": " + getOnOff(Waypoints.secretsList.get(i - 1).booleanValue())));
                    this.buttonList.add(secretButtonList.get(i - 1));
                }
                return;
            }
            for (int i2 = 1; i2 <= ((int) Math.ceil(((double) Waypoints.secretNum) / 2.0d)); i2++) {
                int adjustPos2 = (((-40) * ((int) Math.ceil(((double) Waypoints.secretNum) / 2.0d))) - 70) + (80 * i2);
                secretButtonList.set(i2 - 1, new GuiButton(10 + i2, (width / 2) + adjustPos2, (height / 6) + Opcodes.TABLESWITCH, 60, 20, i2 + ": " + getOnOff(Waypoints.secretsList.get(i2 - 1).booleanValue())));
                this.buttonList.add(secretButtonList.get(i2 - 1));
            }
            for (int i3 = ((int) Math.ceil(((double) Waypoints.secretNum) / 2.0d)) + 1; i3 <= Waypoints.secretNum; i3++) {
                int adjustPos3 = (((-40) * (Waypoints.secretNum - ((int) Math.ceil(((double) Waypoints.secretNum) / 2.0d)))) - 70) + (80 * (i3 - ((int) Math.ceil(((double) Waypoints.secretNum) / 2.0d))));
                secretButtonList.set(i3 - 1, new GuiButton(10 + i3, (width / 2) + adjustPos3, (height / 6) + 200, 60, 20, i3 + ": " + getOnOff(Waypoints.secretsList.get(i3 - 1).booleanValue())));
                this.buttonList.add(secretButtonList.get(i3 - 1));
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();
        int displayWidth = mc.fontRendererObj.getStringWidth("FakepixelUtilities Waypoints:");
        TextRenderer.drawText(mc, "FakepixelUtilities Waypoints:", (this.width / 2) - (displayWidth / 2), (this.height / 6) - 20, 1.0d, false);
        int subtext1Width = mc.fontRendererObj.getStringWidth("Toggle Room Specific Waypoints:");
        TextRenderer.drawText(mc, "Toggle Room Specific Waypoints:", (this.width / 2) - (subtext1Width / 2), (this.height / 6) + Opcodes.F2L, 1.0d, false);
        int subtext2Width = mc.fontRendererObj.getStringWidth("(You can also press the # key matching the secret instead)");
        TextRenderer.drawText(mc, EnumChatFormatting.GRAY + "(You can also press the # key matching the secret instead)", (this.width / 2) - (subtext2Width / 2), (this.height / 6) + Opcodes.FCMPG, 1.0d, false);
        if (!Utils.inDungeons) {
            int subtext3Width = mc.fontRendererObj.getStringWidth("Not in dungeons");
            TextRenderer.drawText(mc, EnumChatFormatting.RED + "Not in dungeons", (this.width / 2) - (subtext3Width / 2), (this.height / 6) + Opcodes.TABLESWITCH, 1.0d, false);
        } else if (Waypoints.secretNum == 0) {
            int subtext3Width2 = mc.fontRendererObj.getStringWidth("No secrets in this room");
            TextRenderer.drawText(mc, EnumChatFormatting.RED + "No secrets in this room", (this.width / 2) - (subtext3Width2 / 2), (this.height / 6) + Opcodes.TABLESWITCH, 1.0d, false);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void actionPerformed(GuiButton button) {
        EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
        if (button == this.waypointsEnabled) {
            Waypoints.enabled = !Waypoints.enabled;
            ConfigHandler.writeBooleanConfig("toggles", "waypointsToggled", Waypoints.enabled);
            this.waypointsEnabled.displayString = waypointBtnText();
        } else if (button == this.showEntrance) {
            Waypoints.showEntrance = !Waypoints.showEntrance;
            ConfigHandler.writeBooleanConfig("waypoint", "showEntrance", Waypoints.showEntrance);
            this.showEntrance.displayString = "Show Entrance Waypoints: " + getOnOff(Waypoints.showEntrance);
        } else if (button == this.showSuperboom) {
            Waypoints.showSuperboom = !Waypoints.showSuperboom;
            ConfigHandler.writeBooleanConfig("waypoint", "showSuperboom", Waypoints.showSuperboom);
            this.showSuperboom.displayString = "Show Superboom Waypoints: " + getOnOff(Waypoints.showSuperboom);
        } else if (button == this.showSecrets) {
            Waypoints.showSecrets = !Waypoints.showSecrets;
            ConfigHandler.writeBooleanConfig("waypoint", "showSecrets", Waypoints.showSecrets);
            this.showSecrets.displayString = "Show Secret Waypoints: " + getOnOff(Waypoints.showSecrets);
        } else if (button == this.showFairySouls) {
            Waypoints.showFairySouls = !Waypoints.showFairySouls;
            ConfigHandler.writeBooleanConfig("waypoint", "showFairySouls", Waypoints.showFairySouls);
            this.showFairySouls.displayString = "Show Fairy Soul Waypoints: " + getOnOff(Waypoints.showFairySouls);
        } else if (button == this.sneakToDisable) {
            Waypoints.sneakToDisable = !Waypoints.sneakToDisable;
            ConfigHandler.writeBooleanConfig("waypoint", "sneakToDisable", Waypoints.sneakToDisable);
            this.sneakToDisable.displayString = "Double-Tap Sneak to Hide Nearby: " + getOnOff(Waypoints.sneakToDisable);
        } else if (button == this.disableWhenAllFound) {
            Waypoints.disableWhenAllFound = !Waypoints.disableWhenAllFound;
            ConfigHandler.writeBooleanConfig("waypoint", "disableWhenAllFound", Waypoints.disableWhenAllFound);
            this.disableWhenAllFound.displayString = "Disable when all secrets found: " + getOnOff(Waypoints.disableWhenAllFound);
        } else if (button == this.close) {
            entityPlayerSP.closeScreen();
        }
        if (Utils.inDungeons && Waypoints.secretNum > 0) {
            for (int i = 1; i <= Waypoints.secretNum; i++) {
                if (button == secretButtonList.get(i - 1)) {
                    Waypoints.secretsList.set(i - 1, Boolean.valueOf(!Waypoints.secretsList.get(i - 1).booleanValue()));
                    if (AutoRoom.lastRoomName != null) {
                        Waypoints.allSecretsMap.replace(AutoRoom.lastRoomName, Waypoints.secretsList);
                    }
                    secretButtonList.get(i - 1).displayString = i + ": " + getOnOff(Waypoints.secretsList.get(i - 1).booleanValue());
                    return;
                }
            }
        }
    }

    public void onGuiClosed() {
        waypointGuiOpened = false;
    }

    protected void keyTyped(char c, int keyCode) throws IOException {
        super.keyTyped(c, keyCode);
        if (waypointGuiOpened && Utils.inDungeons && Waypoints.secretNum > 0) {
            for (int i = 1; i <= Waypoints.secretNum; i++) {
                if (((char) keyCode) == i + 1) {
                    Waypoints.secretsList.set(i - 1, Boolean.valueOf(!Waypoints.secretsList.get(i - 1).booleanValue()));
                    if (AutoRoom.lastRoomName != null) {
                        Waypoints.allSecretsMap.replace(AutoRoom.lastRoomName, Waypoints.secretsList);
                    }
                    secretButtonList.get(i - 1).displayString = i + ": " + getOnOff(Waypoints.secretsList.get(i - 1).booleanValue());
                    return;
                }
            }
        }
    }

    private static String waypointBtnText() {
        if (Waypoints.enabled) {
            return EnumChatFormatting.GREEN + "Waypoints Enabled";
        }
        return EnumChatFormatting.RED + "Waypoints Disabled";
    }

    private static String getOnOff(boolean bool) {
        if (bool) {
            return EnumChatFormatting.GREEN + "On";
        }
        return EnumChatFormatting.RED + "Off";
    }
}
