package com.fakepixel.fpu;

import com.fakepixel.fpu.PriceManager;
import com.fakepixel.fpu.core.FPUConfig;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.lib.Opcodes;

public class FpuGuiScreen extends GuiScreen {
    private int currentPage = 0;
    private GuiTextField searchField;

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        int x = (this.width - 260) / 2;
        int y = ((this.height - 280) / 2) - 15;
        this.buttonList.add(new GuiButton(10, x + 10, y + 30, 55, 20, this.currentPage == 0 ? EnumChatFormatting.GOLD + "▶ FPU" : "FPU"));
        this.buttonList.add(new GuiButton(11, x + 70, y + 30, 55, 20, this.currentPage == 1 ? EnumChatFormatting.GOLD + "▶ Price" : "Price"));
        this.buttonList.add(new GuiButton(12, x + Opcodes.IXOR, y + 30, 55, 20, this.currentPage == 2 ? EnumChatFormatting.GOLD + "▶ About" : "About"));
        this.buttonList.add(new GuiButton(13, x + Opcodes.ARRAYLENGTH, y + 30, 55, 20, this.currentPage == 3 ? EnumChatFormatting.GOLD + "▶ Dev" : "Dev"));
        if (this.currentPage == 0) {
            String tooltipStatus = FakepixelUtilities.isShowTooltip ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
            this.buttonList.add(new GuiButton(1, x + 30, y + 65, 200, 20, "Show Tooltips: " + tooltipStatus));
            String minionStatus = FakepixelUtilities.isMinionOverlayEnabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
            this.buttonList.add(new GuiButton(3, x + 30, y + Opcodes.LMUL, 200, 20, "Minion Chest Prices: " + minionStatus));
            String adviceStatus = FakepixelUtilities.isMinionUpgradeAdviceEnabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
            this.buttonList.add(new GuiButton(4, x + 30, y + Opcodes.I2B, 200, 20, "Minion Upgrades: " + adviceStatus));
            String safeModeStatus = FakepixelUtilities.isSafeModeEnabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
            this.buttonList.add(new GuiButton(6, x + 30, y + Opcodes.INVOKEINTERFACE, 200, 20, "Safe Mode: " + safeModeStatus));
            String smoothTpStatus = FakepixelUtilities.isSmoothTPEnabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
            this.buttonList.add(new GuiButton(14, x + 30, y + 225, 200, 20, "Smooth Aote/Aotv: " + smoothTpStatus));
            return;
        }
        if (this.currentPage == 1) {
            this.searchField = new GuiTextField(0, this.fontRendererObj, x + 30, y + 65, 200, 20);
            this.searchField.setMaxStringLength(30);
            this.searchField.setFocused(true);
            this.searchField.setText("");
            return;
        }
        if (this.currentPage != 2 && this.currentPage == 3) {
            String sendInfoStatus = FakepixelUtilities.isSendInfoEnabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
            this.buttonList.add(new GuiButton(5, x + 30, y + 65, 200, 20, "Send Info: " + sendInfoStatus));
            String debugStatus = FakepixelUtilities.isDebugMode ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
            this.buttonList.add(new GuiButton(2, x + 30, y + Opcodes.LMUL, 200, 20, "Debug Logs: " + debugStatus));
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int x = (this.width - 260) / 2;
        int y = ((this.height - 280) / 2) - 15;
        drawRect(x, y, x + 260, y + 280, -183496684);
        drawRect(x - 1, y - 1, x + 260 + 1, y, -11908518);
        drawRect(x - 1, y, x, y + 280, -11908518);
        drawRect(x + 260, y, x + 260 + 1, y + 280, -11908518);
        drawRect(x - 1, y + 280, x + 260 + 1, y + 280 + 1, -11908518);
        drawCenteredString(this.fontRendererObj, EnumChatFormatting.AQUA + "FakepixelUtilities v1.0.4", this.width / 2, y + 12, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.currentPage == 1 && this.searchField != null) {
            this.searchField.drawTextBox();
            String query = this.searchField.getText().trim().toUpperCase().replace(" ", "_");
            int renderY = y + 95;
            if (!query.isEmpty()) {
                PriceManager.ItemData data = PriceManager.getCachedPrice(query);
                if (data != null) {
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GOLD + "[FPU LIVE LOOKUP]", x + 30, renderY, -1);
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + "Bazaar Buy: " + EnumChatFormatting.YELLOW + data.bzBuy, x + 30, renderY + 15, -1);
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + "Bazaar Sell: " + EnumChatFormatting.YELLOW + data.bzSell, x + 30, renderY + 28, -1);
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + "Ah Highest: " + EnumChatFormatting.YELLOW + data.ahHigh, x + 30, renderY + 41, -1);
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + "Ah Lowest: " + EnumChatFormatting.YELLOW + data.ahLow, x + 30, renderY + 54, -1);
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + "Ah Average: " + EnumChatFormatting.YELLOW + data.ahAvg, x + 30, renderY + 67, -1);
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "Last updated: " + data.lastUpdated, x + 30, renderY + 82, -1);
                    return;
                }
                this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "Indexing database metrics...", x + 30, renderY, -1);
                return;
            }
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "Type item name (e.g., WHEAT)", x + 30, renderY, -1);
            return;
        }
        if (this.currentPage == 2) {
            int renderY2 = y + 55;
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GOLD + "CherryTree Team's Members:", x + 25, renderY2, -1);
            drawRect(x + 25, renderY2 + 11, (x + 260) - 25, renderY2 + 12, -11908518);
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.LIGHT_PURPLE + "c1727.c", x + 30, renderY2 + 18, -1);
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + " -> " + EnumChatFormatting.WHITE + "Project Founder", x + 30, renderY2 + 29, -1);
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.AQUA + "_jatin_e", x + 30, renderY2 + 44, -1);
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + " -> " + EnumChatFormatting.WHITE + "Project Manager", x + 30, renderY2 + 55, -1);
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.YELLOW + "notgamer__69", x + 30, renderY2 + 70, -1);
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + " -> " + EnumChatFormatting.WHITE + "Project Admin", x + 30, renderY2 + 81, -1);
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.BLUE + "ansh04766", x + 30, renderY2 + 96, -1);
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + " -> " + EnumChatFormatting.WHITE + "Project Admin", x + 30, renderY2 + Opcodes.DMUL, -1);
            drawRect(x + 25, renderY2 + Opcodes.ISHR, (x + 260) - 25, renderY2 + Opcodes.LSHR, -11908518);
            drawCenteredString(this.fontRendererObj, EnumChatFormatting.GRAY + "Made with " + EnumChatFormatting.RED + "❤" + EnumChatFormatting.GRAY + " by " + EnumChatFormatting.LIGHT_PURPLE + "CherryTree Team", this.width / 2, renderY2 + Opcodes.IINC, -1);
            drawCenteredString(this.fontRendererObj, EnumChatFormatting.GREEN + "Great People Great Community", this.width / 2, renderY2 + Opcodes.I2B, -1);
            return;
        }
        if (this.currentPage == 3) {
            drawCenteredString(this.fontRendererObj, EnumChatFormatting.AQUA + "—— Developer Only ——", this.width / 2, y + Opcodes.IF_ICMPNE, -1);
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id != 10) {
            if (button.id != 11) {
                if (button.id != 12) {
                    if (button.id != 13) {
                        if (this.currentPage == 0) {
                            if (button.id == 1) {
                                FakepixelUtilities.isShowTooltip = !FakepixelUtilities.isShowTooltip;
                            } else if (button.id == 3) {
                                FakepixelUtilities.isMinionOverlayEnabled = !FakepixelUtilities.isMinionOverlayEnabled;
                            } else if (button.id == 4) {
                                FakepixelUtilities.isMinionUpgradeAdviceEnabled = !FakepixelUtilities.isMinionUpgradeAdviceEnabled;
                            } else if (button.id == 6) {
                                FakepixelUtilities.isSafeModeEnabled = !FakepixelUtilities.isSafeModeEnabled;
                            } else if (button.id == 14) {
                                FakepixelUtilities.isSmoothTPEnabled = !FakepixelUtilities.isSmoothTPEnabled;
                            }
                            FPUConfig.saveConfig();
                            initGui();
                            return;
                        }
                        if (this.currentPage == 3) {
                            if (button.id == 5) {
                                if (FakepixelUtilities.isDeveloper()) {
                                    FakepixelUtilities.isSendInfoEnabled = !FakepixelUtilities.isSendInfoEnabled;
                                } else {
                                    showAccessDenied("Send Info");
                                }
                                FPUConfig.saveConfig();
                                initGui();
                                return;
                            }
                            if (button.id == 2) {
                                if (FakepixelUtilities.isDeveloper()) {
                                    FakepixelUtilities.isDebugMode = !FakepixelUtilities.isDebugMode;
                                } else {
                                    showAccessDenied("Debug Logs");
                                }
                                FPUConfig.saveConfig();
                                initGui();
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    this.currentPage = 3;
                    initGui();
                    return;
                }
                this.currentPage = 2;
                initGui();
                return;
            }
            this.currentPage = 1;
            initGui();
            return;
        }
        this.currentPage = 0;
        initGui();
    }

    private void showAccessDenied(String feature) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[FPU] " + EnumChatFormatting.RED + "You can't toggle " + feature + " feature because it's only for our Developers!"));
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.currentPage == 1 && this.searchField != null && this.searchField.isFocused()) {
            if (keyCode != 1) {
                this.searchField.textboxKeyTyped(typedChar, keyCode);
                return;
            } else {
                super.keyTyped(typedChar, keyCode);
                return;
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.currentPage != 1 || this.searchField == null) {
            return;
        }
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void updateScreen() {
        super.updateScreen();
        if (this.currentPage != 1 || this.searchField == null) {
            return;
        }
        this.searchField.updateCursorCounter();
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}
