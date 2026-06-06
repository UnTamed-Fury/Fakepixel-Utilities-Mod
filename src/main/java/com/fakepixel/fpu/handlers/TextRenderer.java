package com.fakepixel.fpu.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;

public class TextRenderer extends Gui {
    public static void drawText(Minecraft mc, String text, int x, int y, double scale, boolean outline) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        int y2 = y - mc.fontRendererObj.FONT_HEIGHT;
        for (String line : text.split("\n")) {
            y2 = (int) (((double) y2) + (((double) mc.fontRendererObj.FONT_HEIGHT) * scale));
            if (outline) {
                String noColourLine = StringUtils.stripControlCodes(line);
                mc.fontRendererObj.drawString(noColourLine, ((int) Math.round(((double) x) / scale)) - 1, (int) Math.round(((double) y2) / scale), 0, false);
                mc.fontRendererObj.drawString(noColourLine, ((int) Math.round(((double) x) / scale)) + 1, (int) Math.round(((double) y2) / scale), 0, false);
                mc.fontRendererObj.drawString(noColourLine, (int) Math.round(((double) x) / scale), ((int) Math.round(((double) y2) / scale)) - 1, 0, false);
                mc.fontRendererObj.drawString(noColourLine, (int) Math.round(((double) x) / scale), ((int) Math.round(((double) y2) / scale)) + 1, 0, false);
                mc.fontRendererObj.drawString(line, (int) Math.round(((double) x) / scale), (int) Math.round(((double) y2) / scale), 16777215, false);
            } else {
                mc.fontRendererObj.drawString(line, (int) Math.round(((double) x) / scale), (int) Math.round(((double) y2) / scale), 16777215, true);
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
