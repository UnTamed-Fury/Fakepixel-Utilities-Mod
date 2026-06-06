package com.fakepixel.fpu;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SmoothTPListener {
    private double startOffsetX;
    private double startOffsetY;
    private double startOffsetZ;
    private static final float TP_DURATION = 300.0f;
    private double realX;
    private double realY;
    private double realZ;
    private double realPrevX;
    private double realPrevY;
    private double realPrevZ;
    private double realLastX;
    private double realLastY;
    private double realLastZ;
    private double prevX;
    private double prevY;
    private double prevZ;
    private long tpStartTime = 0;
    private boolean isInterpolating = false;
    private boolean prevInitialized = false;
    private float cachedProgress = 0.0f;
    private long cachedProgressTime = -1;

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (FakepixelUtilities.isSmoothTPEnabled) {
            Minecraft mc = Minecraft.getMinecraft();
            Entity viewEntity = mc.getRenderViewEntity();
            if (viewEntity == null) {
                this.prevInitialized = false;
                return;
            }
            if (event.phase == TickEvent.Phase.START) {
                double currX = viewEntity.posX;
                double currY = viewEntity.posY;
                double currZ = viewEntity.posZ;
                if (this.prevInitialized) {
                    double dx = currX - this.prevX;
                    double dy = currY - this.prevY;
                    double dz = currZ - this.prevZ;
                    double distSq = (dx * dx) + (dy * dy) + (dz * dz);
                    if (distSq > 16.0d && distSq < 10000.0d && isAoteHeld(mc)) {
                        double remainingX = 0.0d;
                        double remainingY = 0.0d;
                        double remainingZ = 0.0d;
                        if (this.isInterpolating) {
                            float prog = getSmoothedProgress(System.currentTimeMillis());
                            if (prog < 1.0f) {
                                double rem = 1.0d - ((double) prog);
                                remainingX = this.startOffsetX * rem;
                                remainingY = this.startOffsetY * rem;
                                remainingZ = this.startOffsetZ * rem;
                            }
                        }
                        this.startOffsetX = (this.prevX - currX) + remainingX;
                        this.startOffsetY = (this.prevY - currY) + remainingY;
                        this.startOffsetZ = (this.prevZ - currZ) + remainingZ;
                        this.tpStartTime = System.currentTimeMillis();
                        this.cachedProgressTime = -1L;
                        this.isInterpolating = true;
                    }
                }
                this.prevX = currX;
                this.prevY = currY;
                this.prevZ = currZ;
                this.prevInitialized = true;
                if (this.isInterpolating) {
                    long now = System.currentTimeMillis();
                    float progress = getSmoothedProgress(now);
                    if (progress >= 1.0f) {
                        this.isInterpolating = false;
                        return;
                    }
                    double rem2 = 1.0d - ((double) progress);
                    double offsetX = this.startOffsetX * rem2;
                    double offsetY = this.startOffsetY * rem2;
                    double offsetZ = this.startOffsetZ * rem2;
                    this.realX = viewEntity.posX;
                    this.realY = viewEntity.posY;
                    this.realZ = viewEntity.posZ;
                    this.realPrevX = viewEntity.prevPosX;
                    this.realPrevY = viewEntity.prevPosY;
                    this.realPrevZ = viewEntity.prevPosZ;
                    this.realLastX = viewEntity.lastTickPosX;
                    this.realLastY = viewEntity.lastTickPosY;
                    this.realLastZ = viewEntity.lastTickPosZ;
                    viewEntity.posX += offsetX;
                    viewEntity.posY += offsetY;
                    viewEntity.posZ += offsetZ;
                    viewEntity.prevPosX += offsetX;
                    viewEntity.prevPosY += offsetY;
                    viewEntity.prevPosZ += offsetZ;
                    viewEntity.lastTickPosX += offsetX;
                    viewEntity.lastTickPosY += offsetY;
                    viewEntity.lastTickPosZ += offsetZ;
                    return;
                }
                return;
            }
            if (event.phase == TickEvent.Phase.END && this.isInterpolating) {
                viewEntity.posX = this.realX;
                viewEntity.posY = this.realY;
                viewEntity.posZ = this.realZ;
                viewEntity.prevPosX = this.realPrevX;
                viewEntity.prevPosY = this.realPrevY;
                viewEntity.prevPosZ = this.realPrevZ;
                viewEntity.lastTickPosX = this.realLastX;
                viewEntity.lastTickPosY = this.realLastY;
                viewEntity.lastTickPosZ = this.realLastZ;
            }
        }
    }

    private float getSmoothedProgress(long now) {
        if (now == this.cachedProgressTime) {
            return this.cachedProgress;
        }
        long elapsed = now - this.tpStartTime;
        if (elapsed >= 300) {
            this.cachedProgress = 1.0f;
            this.cachedProgressTime = now;
            return 1.0f;
        }
        float t = elapsed / TP_DURATION;
        float smoothed = t * t * (3.0f - (2.0f * t));
        this.cachedProgress = smoothed;
        this.cachedProgressTime = now;
        return smoothed;
    }

    private boolean isAoteHeld(Minecraft mc) {
        if (mc.thePlayer == null || mc.thePlayer.getHeldItem() == null) {
            return false;
        }
        String name = mc.thePlayer.getHeldItem().getDisplayName().toLowerCase();
        return name.contains("aspect of the end") || name.contains("aspect of the void") || name.contains("aote") || name.contains("aotv");
    }
}
