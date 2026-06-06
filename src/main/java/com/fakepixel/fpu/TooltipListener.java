package com.fakepixel.fpu;

import com.fakepixel.fpu.PriceManager;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TooltipListener {
    private static final ConcurrentHashMap<String, Boolean> fetchingItems = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack item;
        String internalName;
        if (!FakepixelUtilities.inSkyblock || !FakepixelUtilities.isShowTooltip || (item = event.itemStack) == null || item.getItem() == null || (internalName = FakepixelUtilities.getSkyblockId(item)) == null || internalName.isEmpty() || isUIItem(internalName)) {
            return;
        }
        List<String> tooltip = event.toolTip;
        tooltip.add("");
        tooltip.add(EnumChatFormatting.GOLD + "[FPU PRICES]");
        tooltip.add(EnumChatFormatting.RED + "ID: " + EnumChatFormatting.GREEN + internalName);
        PriceManager.ItemData data = PriceManager.getCachedPrice(internalName);
        if (data != null) {
            tooltip.add(EnumChatFormatting.GRAY + "Bazaar Buy: " + EnumChatFormatting.YELLOW + data.bzBuy);
            tooltip.add(EnumChatFormatting.GRAY + "Bazaar Sell: " + EnumChatFormatting.YELLOW + data.bzSell);
            tooltip.add(EnumChatFormatting.GRAY + "Ah Highest: " + EnumChatFormatting.YELLOW + data.ahHigh);
            tooltip.add(EnumChatFormatting.GRAY + "Ah Lowest: " + EnumChatFormatting.YELLOW + data.ahLow);
            tooltip.add(EnumChatFormatting.GRAY + "Ah Average: " + EnumChatFormatting.YELLOW + data.ahAvg);
            tooltip.add(EnumChatFormatting.GRAY + "Last updated: " + EnumChatFormatting.RED + data.lastUpdated);
        } else {
            triggerAsyncFetchIfNeeded(internalName);
            tooltip.add(EnumChatFormatting.DARK_GRAY + "Loading price metrics...");
        }
        tooltip.add(EnumChatFormatting.GOLD + "--------------------");
    }

    private void triggerAsyncFetchIfNeeded(String itemName) {
        if (fetchingItems.putIfAbsent(itemName, true) == null) {
            PriceManager.fetchPriceAsync(itemName, () -> {
                fetchingItems.remove(itemName);
            });
        }
    }

    private boolean isUIItem(String id) {
        String lowerId = id.toLowerCase();
        if (lowerId.equals("air") || lowerId.contains("page") || lowerId.contains("back") || lowerId.contains("close") || lowerId.contains("filler") || lowerId.contains("glass_pane") || lowerId.contains("barrier") || lowerId.equals("search") || lowerId.contains("%")) {
            return true;
        }
        String[] uiIds = {"bin_filter", "item_tier", "sort", "tools_&_misc", "blocks", "consumables", "accessories", "armor", "weapons", "fast_travel", "recipe_book", "skyblock_leveling", "quest_log", "calendar_and_events", "storage", "your_bags", "pets", "crafting_table", "wardrobe", "personal_bank", "potion_bag", "quiver", "accessory_bag", "fishing_bag", "sack_of_sacks", "time_pocket", "recipe_required", "skyblock_hub", "dungeon_hub", "crystal_hollows", "crystall_hollows", "island_browser", "the_end", "dwarven_mines", "the_barn", "the_park", "gold_mine", "deep_caverns", "spiders_den", "spider's_den", "crimson_isle", "co-op_bank_account", "co_op_bank_account", "personal_vault", "bank_upgrades", "personal_bank_account", "withdraw_coins", "deposit_coins", "recent_transactions", "information", "skyblock_menu", "your_skyblock_profile", "reset_settings", "manage_orders", "sell_inventory_now", "sell_sacks_now", "oddities", "woods_&_fishes", "combat", "mining", "farming", "paper_icons", "go_back", "sell_item", "skyblock_hub_#1", "skyblock_hub_#2", "skyblock_hub_#3", "skyblock_hub_#4", "skyblock_hub_#5", "skyblock_hub_#6", "skyblock_hub_#7", "skyblock_hub_#8", "vip_hub", "city_projects", "account_and_profile_upgrades", "bits_shop", "rank_badges", "community_shop", "paul", "diana", "diaz", "foxy", "aatrox", "display_item", "bookshelf_power", "enchant_item", "reforge_item", "combine_items", "runic_pedestal", "apply_a_rune_or_fuse_two_runes", "iron_armor", "rosetta's_armor", "celeste_set", "squire_armor", "mercenary_armor", "starlight_set", "no_treasures!", "no_treasures", "salvage_items", "essence_crafting", "basic_reforging", "reforge_anvil", "essence_shop", "master_mode", "find_a_party", "dungeon_classes", "toggle_auto_ready_up", "tank", "berserk", "archer", "mage", "healer", "wisdom_stats", "misc_stats", "combat_stats", "gathering_stats", "trades", "crafted_minions", "your_skills", "crystal_core", "giant_mushroom", "precursor_ruins", "whole_purse", "half_purse", "deposit", "withdraw", "your_whole_purse", "half_your_purse", "specific_amount", "profile_management", "settings", "hub_portal", "skyblock_guide", "leveling_rewards", "prefix_emblems", "find_all_fairy_souls", "skyblock_basics", "introduce_yourself", "auction_house", "fisherman_gerald", "chicken_race", "lone_adventurer", "end_race", "park_race", "dungeon_hub_races", "meet_2pb", "talk_to_the_carpenter", "traveling_zoo", "chocolate_factory", "calendar", "clear_quiver", "convert_pet_to_an_item", "hide_pets", "pet_score_rewards", "quick_crafting_slot", "everything_in_the_account", "half_the_account", "starter_account", "coins_transaction", "trading!", "new_deal", "gunpowder_mines", "lapis_quarry", "pigmen's_den", "slimehill", "obsidian_sanctuary", "diamond_reserve", "heart_of_the_mountain", "commissions", "the_forge", "general_information", "ongoing_event", "join_the_crystal_hollows", "filter", "claim_all", "auction_browser", "auctions_browser", "view_bids", "create_auction", "create_bin_auction", "click_an_item_in_your_inventory", "click_an_item_in_your_inventory!", "switch_to_auction", "switch_to_bin", "auction_for_item:", "island_category", "private_island", "personal_settings", "double_tap_to_drop", "profile_viewer", "hide_players", "hide_map", "start_a_new_queue", "refresh", "search_settings", "salvage_item", "essence_guide", "convert_to_dungeon_items", "upgrade_items", "anvil", "sauls_recommendation", "saul's_recommendation", "dungeoneering", "drill_anvil", "drill", "drill_fuel", "drill_part", "drill_fuel/part", "remove_fuel_tank", "remove_drill_engine", "remove_upgrade_module", "chocolate_facotory", "collect_all", "ideal_layout", "next_tier", "auction_stats", "buyer_stats", "seller_stats", "change_destination", "change_teleport_direction", "change_icon", "rename_pad", "pickup_teleport_pad", "select_type", "pickup_training_dummy", "confirm", "cancel", "other_player_confirmed!", "other_player_confirmed", "deal_timer!", "warning!", "warning", "accessory_bag_shortcut", "accessories_breakdown", "learn_power_from_stones", "stats_tuning", "held_item", "trade_request", "open_reward_chest", "selling_whole_inventory", "items_sold!", "inventory", "skills", "slayers", "effects", "bags", "dungeons", "buy_item_right_now", "bid_history", "submit_bid", "accessory_bag_upgrades", "sell_reforges_on_accessories", "item_to_sacrifice", "item_to_upgrade", "deal!", "deal", "slayer_quest_complete", "slayer_quest_complete!", "offer_a_pet", "hire_kat", "pet_sitter", "not_released_yet!", "not_released_yet", "boss_drops", "profile_upgrades", "bazaar_flipper", "preparing", "superpairs", "ultrasequencer", "chronomatron", "experience_bottles", "create_buy_order", "create_sell_offer", "sell_instantly", "buy_instantly", "instasell_ignore", "create_buy_offer", "create_sell_order", "dungeon_map", "inventory_sold", "inventory_sold!", "fuel", "automated_shipping"};
        for (String uiId : uiIds) {
            if (lowerId.equals(uiId)) {
                return true;
            }
        }
        return lowerId.contains("backpack") || lowerId.contains("ender_chest") || lowerId.contains("minion") || lowerId.contains("file_sales") || lowerId.contains("skyblock_gems") || lowerId.contains("mayor") || lowerId.contains("rng") || lowerId.contains("catacombs") || lowerId.contains("recipes") || lowerId.contains("level") || lowerId.contains("slot") || lowerId.contains("account") || lowerId.contains("election") || lowerId.contains("spooky_festival") || lowerId.contains("jerry_workshop") || lowerId.contains("hoppity's_hunt") || lowerId.contains("trading") || lowerId.contains("fire_sales") || lowerId.contains("chocolate_factory") || lowerId.contains("mithril_and") || lowerId.contains("commission") || lowerId.contains("item_price") || lowerId.contains("item_price:") || lowerId.contains("duration") || lowerId.contains("duration:") || lowerId.contains("starting_bid") || lowerId.contains("starting_bid:") || lowerId.contains("switch_to") || lowerId.contains("auction_for_item") || lowerId.contains("auction_for_item:") || lowerId.contains("hub") || lowerId.contains("portal") || lowerId.contains("portal's") || lowerId.contains("island") || lowerId.contains("setting") || lowerId.contains("settings") || lowerId.contains("visit") || lowerId.contains("visits") || lowerId.contains("visits:") || lowerId.contains("party") || lowerId.contains("'s_party") || lowerId.contains("tank_") || lowerId.contains("berserk_") || lowerId.contains("archer_") || lowerId.contains("mage_") || lowerId.contains("healer_") || lowerId.contains("foraging_") || lowerId.contains("carpentry_") || lowerId.contains("runecrafting_") || lowerId.contains("social_") || lowerId.contains("taming_") || lowerId.contains("enchanting_") || lowerId.contains("alchemy_") || lowerId.contains("fishing_") || lowerId.contains("combat_") || lowerId.contains("mining_") || lowerId.contains("farming_") || lowerId.contains("your_stuff") || lowerId.contains("deal_timer") || lowerId.contains("deal_timer!") || lowerId.contains("deal_timer!_") || lowerId.contains("[vip]") || lowerId.contains("[vip") || lowerId.contains("[mvp]") || lowerId.contains("[mvp") || lowerId.contains("[sponsor]") || lowerId.contains("[special]") || lowerId.contains("[bt]") || lowerId.contains("[owner]") || lowerId.contains("[admin]") || lowerId.contains("[admin") || lowerId.contains("admin]") || lowerId.contains("[helper]") || lowerId.contains("[mod]") || lowerId.contains("[jr") || lowerId.contains("[youtube]") || lowerId.contains("[gm]") || lowerId.contains("[support]") || lowerId.contains("[dev]") || lowerId.contains("[s+]") || lowerId.contains("[s") || lowerId.contains("bid_amount:_") || lowerId.contains("bid_amount:") || lowerId.contains("revenant_horror") || lowerId.contains("tarantula_broodfather") || lowerId.contains("sven_packmaster") || lowerId.contains("voidgloom_seraph") || lowerId.contains("inferno_demonlord") || lowerId.contains("coins_allowance_") || lowerId.contains("coins_allowance") || lowerId.contains("magic_find_") || lowerId.contains("magic_find") || lowerId.contains("guests_limit_") || lowerId.contains("guests_limit") || lowerId.contains("accessory_bag") || lowerId.contains("accessory_bag_") || lowerId.contains("categories") || lowerId.contains("pending_experiment") || lowerId.contains("pending_experiment_") || lowerId.contains("_profile_info") || lowerId.contains("collection");
    }
}
