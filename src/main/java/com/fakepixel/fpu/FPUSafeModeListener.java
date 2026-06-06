package com.fakepixel.fpu;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent.Post;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class FPUSafeModeListener {
   private static final ResourceLocation LOCK_ICON = new ResourceLocation("fpu", "textures/gui/lock.png");
   private long lastAlertTime = 0L;
   private long pendingTradeCloseTime = 0L;
   private final List<String> allowedInventories = Arrays.asList("inventory", "chest", "large chest", "ender chest", "backpack", "vault", "Inventory", "Minion");

   private boolean shouldBlockDrop() {
      return FakepixelUtilities.inSkyblock && FakepixelUtilities.isSafeModeEnabled;
   }

   private boolean isChatOpen() {
      return Minecraft.getMinecraft().currentScreen instanceof GuiChat;
   }

   private void printDropWarning() {
      long now = System.currentTimeMillis();
      if (now - this.lastAlertTime > 1000L && Minecraft.getMinecraft().thePlayer != null) {
         Minecraft.getMinecraft()
            .thePlayer
            .addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU] Safe mode is on! You can't drop any item!"));
         this.lastAlertTime = now;
      }
   }

   @SubscribeEvent
   public void onKeyInput(KeyInputEvent event) {
      if (this.shouldBlockDrop()) {
         if (!this.isChatOpen()) {
            int dropKey = Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode();
            if (Keyboard.getEventKey() == dropKey && Keyboard.getEventKeyState()) {
               KeyBinding.setKeyBindState(dropKey, false);

               while (Minecraft.getMinecraft().gameSettings.keyBindDrop.isPressed()) {
               }

               this.printDropWarning();
            }
         }
      }
   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.START) {
         if (!this.shouldBlockDrop()) {
            this.pendingTradeCloseTime = 0L;
         } else {
            if (!this.isChatOpen()) {
               while (Minecraft.getMinecraft().gameSettings.keyBindDrop.isPressed()) {
                  this.printDropWarning();
               }
            }

            if (this.pendingTradeCloseTime > 0L && System.currentTimeMillis() >= this.pendingTradeCloseTime) {
               this.pendingTradeCloseTime = 0L;
               if (Minecraft.getMinecraft().thePlayer != null) {
                  Minecraft.getMinecraft().thePlayer.closeScreen();
                  Minecraft.getMinecraft()
                     .thePlayer
                     .addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU] Safe mode is on! You can't trade!"));
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onMouseInput(Pre event) {
      if (this.shouldBlockDrop() && event.gui instanceof GuiContainer) {
         if (Mouse.getEventButton() != -1) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null && mc.thePlayer.inventory.getItemStack() != null) {
               GuiContainer gui = (GuiContainer)event.gui;
               int mouseX = Mouse.getEventX() * gui.width / mc.displayWidth;
               int mouseY = gui.height - Mouse.getEventY() * gui.height / mc.displayHeight - 1;
               int guiLeft = 0;
               int guiTop = 0;
               int xSize = 176;
               int ySize = 166;

               try {
                  Field fLeft = GuiContainer.class.getDeclaredField("guiLeft");
                  fLeft.setAccessible(true);
                  guiLeft = fLeft.getInt(gui);
                  Field fTop = GuiContainer.class.getDeclaredField("guiTop");
                  fTop.setAccessible(true);
                  guiTop = fTop.getInt(gui);
                  Field fXSize = GuiContainer.class.getDeclaredField("xSize");
                  fXSize.setAccessible(true);
                  xSize = fXSize.getInt(gui);
                  Field fYSize = GuiContainer.class.getDeclaredField("ySize");
                  fYSize.setAccessible(true);
                  ySize = fYSize.getInt(gui);
               } catch (Exception var16) {
               }

               if (mouseX < guiLeft || mouseX >= guiLeft + xSize || mouseY < guiTop || mouseY >= guiTop + ySize) {
                  event.setCanceled(true);
                  if (Mouse.getEventButtonState()) {
                     this.printDropWarning();
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onGuiKey(net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre event) {
      if (this.shouldBlockDrop()) {
         int dropKey = Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode();
         if (Keyboard.getEventKey() == dropKey) {
            if (event.gui instanceof GuiChat) {
               return;
            }

            event.setCanceled(true);
            if (Keyboard.getEventKeyState()) {
               this.printDropWarning();
            }
         }
      }
   }

   @SubscribeEvent
   public void onDrawGui(Post event) {
      if (this.shouldBlockDrop() && event.gui instanceof GuiContainer) {
         GuiContainer gui = (GuiContainer)event.gui;
         if (gui instanceof GuiChest) {
            GuiChest chest = (GuiChest)gui;

            try {
               IInventory lowerChest = null;

               try {
                  Field fieldLower = GuiChest.class.getDeclaredField("draggedStack");
                  fieldLower.setAccessible(true);
                  lowerChest = (IInventory)fieldLower.get(chest);
               } catch (Exception var7) {
                  if (chest.inventorySlots instanceof ContainerChest) {
                     lowerChest = ((ContainerChest)chest.inventorySlots).getLowerChestInventory();
                  }
               }

               if (lowerChest != null && lowerChest.getSizeInventory() == 54) {
                  ItemStack middleSlot = lowerChest.getStackInSlot(4);
                  if (middleSlot != null && middleSlot.getItem() != null) {
                     String name = EnumChatFormatting.getTextWithoutFormattingCodes(middleSlot.getDisplayName()).toLowerCase();
                     if ((name.contains("your stuff") || name.contains("their stuff") || name.contains("trading")) && this.pendingTradeCloseTime == 0L) {
                        this.pendingTradeCloseTime = System.currentTimeMillis() + 1000L;
                     }
                  }
               }
            } catch (Exception var8) {
            }
         }

         this.renderLocks(gui);
         this.redrawTooltip(gui, event.mouseX, event.mouseY);
      }
   }

   private void renderLocks(GuiContainer gui) {
      int guiLeft = 0;
      int guiTop = 0;

      try {
         Field fLeft = GuiContainer.class.getDeclaredField("guiLeft");
         fLeft.setAccessible(true);
         guiLeft = fLeft.getInt(gui);
         Field fTop = GuiContainer.class.getDeclaredField("guiTop");
         fTop.setAccessible(true);
         guiTop = fTop.getInt(gui);
      } catch (Exception var15) {
      }

      for (Slot slot : gui.inventorySlots.inventorySlots) {
         boolean isPlayerInv = slot.inventory == Minecraft.getMinecraft().thePlayer.inventory;
         boolean shouldShowLock = isPlayerInv;
         if (!isPlayerInv && slot.inventory != null) {
            String invName = "";

            try {
               invName = EnumChatFormatting.getTextWithoutFormattingCodes(slot.inventory.getDisplayName().getUnformattedText()).toLowerCase();
            } catch (Exception var12) {
               invName = slot.inventory.getName().toLowerCase();
            }

            if (invName.contains("chest") || invName.contains("backpack") || invName.contains("vault")) {
               shouldShowLock = true;
               if (invName.contains("secret chest")
                  || invName.contains("wood chest")
                  || invName.contains("gold chest")
                  || invName.contains("diamond chest")
                  || invName.contains("emerald chest")
                  || invName.contains("obsidian chest")
                  || invName.contains("bedrock chest")) {
                  shouldShowLock = false;
               }
            }
         }

         if (shouldShowLock && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            if (isPlayerInv) {
               String itemName = "";

               try {
                  itemName = EnumChatFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()).toLowerCase().trim();
               } catch (Exception var14) {
               }

               if (Item.getIdFromItem(stack.getItem()) == 399 && itemName.contains("skyblock menu")
                  || itemName.contains("dungeon map")
                  || itemName.contains("magical map")) {
                  continue;
               }
            }

            if (!isPlayerInv) {
               int itemId = Item.getIdFromItem(stack.getItem());
               String itemName = "";

               try {
                  itemName = EnumChatFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()).toLowerCase().trim();
               } catch (Exception var13) {
               }

               if (itemId == 160
                  || itemId == 166
                  || itemId == 262
                  || itemName.isEmpty()
                  || itemName.contains("close")
                  || itemName.contains("go back")
                  || itemName.contains("next page")
                  || itemName.contains("previous page")
                  || itemName.contains("first page")
                  || itemName.contains("last page")
                  || itemName.contains("skyblock menu")
                  || itemName.contains("page")) {
                  continue;
               }
            }

            int x = guiLeft + slot.xDisplayPosition;
            int y = guiTop + slot.yDisplayPosition;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.translate(0.0F, 0.0F, 500.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(LOCK_ICON);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }
   }

   private void redrawTooltip(GuiContainer gui, int mouseX, int mouseY) {
      Slot hoveredSlot = null;
      int guiLeft = 0;
      int guiTop = 0;

      try {
         Field fLeft = GuiContainer.class.getDeclaredField("guiLeft");
         fLeft.setAccessible(true);
         guiLeft = fLeft.getInt(gui);
         Field fTop = GuiContainer.class.getDeclaredField("guiTop");
         fTop.setAccessible(true);
         guiTop = fTop.getInt(gui);
      } catch (Exception var12) {
      }

      for (Slot slot : gui.inventorySlots.inventorySlots) {
         if (mouseX >= guiLeft + slot.xDisplayPosition
            && mouseX < guiLeft + slot.xDisplayPosition + 16
            && mouseY >= guiTop + slot.yDisplayPosition
            && mouseY < guiTop + slot.yDisplayPosition + 16) {
            hoveredSlot = slot;
            break;
         }
      }

      if (hoveredSlot != null && hoveredSlot.getHasStack() && Minecraft.getMinecraft().thePlayer.inventory.getItemStack() == null) {
         try {
            Method m = GuiScreen.class.getDeclaredMethod("renderToolTip", ItemStack.class, int.class, int.class);
            m.setAccessible(true);
            m.invoke(gui, hoveredSlot.getStack(), mouseX, mouseY);
         } catch (Exception var11) {
         }
      }
   }

   @SubscribeEvent
   public void onGuiOpen(GuiOpenEvent event) {
      if (this.shouldBlockDrop() && event.gui != null) {
         if (event.gui instanceof GuiChest) {
            GuiChest chest = (GuiChest)event.gui;
            String title = "";

            try {
               Field fieldLower = GuiChest.class.getDeclaredField("draggedStack");
               fieldLower.setAccessible(true);
               IInventory lowerChest = (IInventory)fieldLower.get(chest);
               title = lowerChest.getDisplayName().getUnformattedText().toLowerCase();
            } catch (Exception var6) {
               if (chest.inventorySlots instanceof ContainerChest) {
                  title = ((ContainerChest)chest.inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText().toLowerCase();
               }
            }

            if ((title.contains("trade") || title.contains("trading") || title.contains("deal") || title.startsWith("you "))
               && this.pendingTradeCloseTime == 0L) {
               this.pendingTradeCloseTime = System.currentTimeMillis() + 1000L;
            }
         }
      }
   }

   @SubscribeEvent
   public void onCommandSend(CommandEvent event) {
      if (this.shouldBlockDrop() && event.command != null) {
         if (event.command.getCommandName().toLowerCase().equals("trade")) {
            event.setCanceled(true);
            if (Minecraft.getMinecraft().thePlayer != null) {
               Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[FPU] Safe mode is on! You can't trade!"));
            }
         }
      }
   }

   @SubscribeEvent
   public void onRenderPlayerNamePre(net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre event) {
      if (event.entity instanceof EntityPlayer || event.entity instanceof EntityArmorStand) {
         GlStateManager.disableDepth();
      }
   }

   @SubscribeEvent
   public void onRenderPlayerNamePost(net.minecraftforge.client.event.RenderLivingEvent.Specials.Post event) {
      if (event.entity instanceof EntityPlayer || event.entity instanceof EntityArmorStand) {
         GlStateManager.enableDepth();
      }
   }
}
