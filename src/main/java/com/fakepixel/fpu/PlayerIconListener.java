package com.fakepixel.fpu;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class PlayerIconListener {
    private static final ResourceLocation MOD_ICON = new ResourceLocation(FakepixelUtilities.MODID, "textures/gui/icon.png");
    private final Set<String> activeFpuUsers = Collections.synchronizedSet(new HashSet());

    public PlayerIconListener() {
        startHeartbeatAndFetchThread();
    }

    private void startHeartbeatAndFetchThread() {
        Thread syncThread = new Thread(() -> {
            Throwable th = null;
            Throwable th2 = null;
            while (true) {
                try {
                    String myName = Minecraft.getMinecraft().getSession().getUsername();
                    String apiUrl = FakepixelUtilities.getApiUrl();
                    if (myName != null && !myName.isEmpty()) {
                        URL heartbeatUrl = new URL(apiUrl + "/api/heartbeat");
                        HttpURLConnection conn = (HttpURLConnection) heartbeatUrl.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setDoOutput(true);
                        conn.setConnectTimeout(3000);
                        JsonObject json = new JsonObject();
                        json.addProperty("username", myName);
                        OutputStream os = conn.getOutputStream();
                        Throwable th3 = null;
                        try {
                            try {
                                os.write(json.toString().getBytes("UTF-8"));
                                if (os != null) {
                                    if (0 != 0) {
                                        try {
                                            os.close();
                                        } catch (Throwable th4) {
                                            th3.addSuppressed(th4);
                                        }
                                    } else {
                                        os.close();
                                    }
                                }
                                conn.getResponseCode();
                                conn.disconnect();
                            } catch (Throwable th5) {
                                th3 = th5;
                                throw th5;
                            }
                        } finally {
                            if (os == null) {
                                break;
                            } else if (th2 == null) {
                                break;
                            } else {
                                try {
                                    break;
                                } catch (Throwable th6) {
                                }
                            }
                        }
                    }
                    URL fetchUrl = new URL(apiUrl + "/api/users");
                    HttpURLConnection fetchConn = (HttpURLConnection) fetchUrl.openConnection();
                    fetchConn.setRequestMethod("GET");
                    fetchConn.setConnectTimeout(3000);
                    if (fetchConn.getResponseCode() == 200) {
                        InputStreamReader reader = new InputStreamReader(fetchConn.getInputStream(), "UTF-8");
                        Throwable th7 = null;
                        try {
                            try {
                                JsonObject response = new JsonParser().parse(reader).getAsJsonObject();
                                JsonArray usersArray = response.getAsJsonArray("users");
                                Set<String> tempUsers = new HashSet<>();
                                for (int i = 0; i < usersArray.size(); i++) {
                                    tempUsers.add(usersArray.get(i).getAsString().toLowerCase());
                                }
                                this.activeFpuUsers.clear();
                                this.activeFpuUsers.addAll(tempUsers);
                                if (reader != null) {
                                    if (0 != 0) {
                                        try {
                                            reader.close();
                                        } catch (Throwable th8) {
                                            th7.addSuppressed(th8);
                                        }
                                    } else {
                                        reader.close();
                                    }
                                }
                            } finally {
                                if (reader == null) {
                                    break;
                                } else if (th == null) {
                                    break;
                                } else {
                                    try {
                                        break;
                                    } catch (Throwable th9) {
                                    }
                                }
                            }
                        } catch (Throwable th10) {
                            th7 = th10;
                            throw th10;
                        }
                    }
                    fetchConn.disconnect();
                } catch (Exception e) {
                }
                try {
                    Thread.sleep(30000L);
                } catch (InterruptedException e2) {
                    return;
                }
            }
        });
        syncThread.setDaemon(true);
        syncThread.start();
    }

    @SubscribeEvent
    public void onRenderPlayerName(RenderLivingEvent.Specials.Pre event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (this.activeFpuUsers.contains(player.getName().toLowerCase())) {
                event.setCanceled(true);
                String nameText = player.getDisplayName().getFormattedText();
                Minecraft mc = Minecraft.getMinecraft();
                double x = event.x;
                double y = ((event.y + ((double) player.height)) + 0.5d) - (player.isSneaking() ? 0.25d : 0.0d);
                double z = event.z;
                float viewerYaw = mc.getRenderManager().playerViewY;
                float viewerPitch = mc.getRenderManager().playerViewX;
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                GlStateManager.rotate(-viewerYaw, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(viewerPitch, 1.0f, 0.0f, 0.0f);
                GlStateManager.scale(-0.025f, -0.025f, 0.025f);
                GlStateManager.disableLighting();
                GlStateManager.depthMask(false);
                GlStateManager.disableDepth();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                int strWidth = mc.fontRendererObj.getStringWidth(nameText);
                GlStateManager.disableTexture2D();
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                int bX = ((-strWidth) / 2) - 1;
                int bW = strWidth + 2;
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos(bX, (-1) + 8, 0.0d).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
                worldrenderer.pos(bX + bW, (-1) + 8, 0.0d).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
                worldrenderer.pos(bX + bW, -1, 0.0d).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
                worldrenderer.pos(bX, -1, 0.0d).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
                tessellator.draw();
                GlStateManager.enableTexture2D();
                mc.fontRendererObj.drawString(nameText, (-strWidth) / 2, 0, 553648127);
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                mc.fontRendererObj.drawString(nameText, (-strWidth) / 2, 0, -1);
                GlStateManager.disableDepth();
                GlStateManager.depthMask(false);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                mc.getTextureManager().bindTexture(MOD_ICON);
                Gui.drawModalRectWithCustomSizedTexture((strWidth / 2) + 2, -1, 0.0f, 0.0f, 10, 10, 10.0f, 10.0f);
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                GL11.glDepthRange(0.0d, 0.0d);
                Gui.drawModalRectWithCustomSizedTexture((strWidth / 2) + 2, -1, 0.0f, 0.0f, 10, 10, 10.0f, 10.0f);
                GL11.glDepthRange(0.0d, 1.0d);
                GlStateManager.enableLighting();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }
}
