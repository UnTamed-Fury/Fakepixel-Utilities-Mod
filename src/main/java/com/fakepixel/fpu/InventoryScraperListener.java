package com.fakepixel.fpu;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InventoryScraperListener {
    private long lastScanTime = 0;

    @SubscribeEvent
    public void onGuiDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        String itemId;
        if (FakepixelUtilities.inSkyblock && (event.gui instanceof GuiChest) && FakepixelUtilities.isSendInfoEnabled && System.currentTimeMillis() - this.lastScanTime >= 2000) {
            GuiChest chest = (GuiChest) event.gui;
            IInventory inv = ((ContainerChest) chest.inventorySlots).getLowerChestInventory();
            JsonArray batchItems = new JsonArray();
            int itemCount = 0;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack != null) {
                    String buyPrice = extractLorePrice(stack, "Buy price:");
                    String sellPrice = extractLorePrice(stack, "Sell price:");
                    String ahPrice = extractLorePrice(stack, "Starting bid:");
                    if (ahPrice == null) {
                        ahPrice = extractLorePrice(stack, "Top bid:");
                    }
                    if (ahPrice == null) {
                        ahPrice = extractLorePrice(stack, "Buy it now:");
                    }
                    if ((buyPrice != null || sellPrice != null || ahPrice != null) && (itemId = FakepixelUtilities.getSkyblockId(stack)) != null && !itemId.equals("AIR") && !itemId.contains("%")) {
                        JsonObject itemData = new JsonObject();
                        itemData.addProperty("itemName", itemId);
                        if (buyPrice != null) {
                            itemData.addProperty("bzBuy", buyPrice);
                        }
                        if (sellPrice != null) {
                            itemData.addProperty("bzSell", sellPrice);
                        }
                        if (ahPrice != null) {
                            itemData.addProperty("ahPrice", ahPrice);
                        }
                        batchItems.add(itemData);
                        itemCount++;
                    }
                }
            }
            if (itemCount > 0) {
                PriceManager.sendBatchData(batchItems);
                this.lastScanTime = System.currentTimeMillis();
                if (FakepixelUtilities.isDebugMode && Minecraft.getMinecraft().thePlayer != null) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "[FPU-Debug] Sent information to database of " + itemCount + " items present in the page."));
                }
            }
        }
    }

    private String extractLorePrice(ItemStack stack, String prefix) {
        try {
            if (!stack.hasTagCompound()) {
                return null;
            }
            NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
            if (display.hasKey("Lore")) {
                NBTTagList lore = display.getTagList("Lore", 8);
                for (int j = 0; j < lore.tagCount(); j++) {
                    String line = EnumChatFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(j)).trim();
                    if (line.toLowerCase().startsWith(prefix.toLowerCase())) {
                        return line.substring(prefix.length()).replaceAll("[^0-9.,]", "");
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
