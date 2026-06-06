package com.fakepixel.fpu;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PriceManager {
    private static final long FETCH_COOLDOWN_MS = 4000;
    private static final HashMap<String, ItemData> priceCache = new HashMap<>();
    private static final HashMap<String, Long> lastFetchMap = new HashMap<>();
    public static boolean isServerOnline = true;

    public static class ItemData {
        public String bzBuy;
        public String bzSell;
        public String ahHigh;
        public String ahLow;
        public String ahAvg;
        public String lastUpdated;

        public ItemData(String bzB, String bzS, String ahH, String ahL, String ahA, String lu) {
            this.bzBuy = bzB;
            this.bzSell = bzS;
            this.ahHigh = ahH;
            this.ahLow = ahL;
            this.ahAvg = ahA;
            this.lastUpdated = lu;
        }
    }

    public static void clearCache(String itemName) {
        priceCache.remove(itemName);
        lastFetchMap.remove(itemName);
    }

    public static ItemData getCachedPrice(String itemName) {
        long now = System.currentTimeMillis();
        if (!lastFetchMap.containsKey(itemName) || now - lastFetchMap.get(itemName).longValue() > FETCH_COOLDOWN_MS) {
            lastFetchMap.put(itemName, Long.valueOf(now));
            triggerBackgroundFetch(itemName);
        }
        return priceCache.get(itemName);
    }

    public static ItemData getPrice(String itemName) {
        if (priceCache.containsKey(itemName)) {
            triggerBackgroundFetch(itemName);
            return priceCache.get(itemName);
        }
        try {
            return (ItemData) CompletableFuture.supplyAsync(() -> {
                try {
                    URL url = new URL(FakepixelUtilities.getApiUrl() + "/api/price/" + itemName.replace(" ", "%20"));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    if (conn.getResponseCode() == 200) {
                        isServerOnline = true;
                        JsonObject json = new JsonParser().parse(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
                        ItemData data = new ItemData(json.get("bzBuy").getAsString(), json.get("bzSell").getAsString(), json.get("ahHigh").getAsString(), json.get("ahLow").getAsString(), json.get("ahAvg").getAsString(), json.get("lastUpdated").getAsString());
                        priceCache.put(itemName, data);
                        return data;
                    }
                    return null;
                } catch (Exception e) {
                    isServerOnline = false;
                    return null;
                }
            }).get();
        } catch (Exception e) {
            return null;
        }
    }

    public static void fetchPriceAsync(String itemName, Runnable onComplete) {
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(FakepixelUtilities.getApiUrl() + "/api/price/" + itemName.replace(" ", "%20"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(2000);
                if (conn.getResponseCode() == 200) {
                    isServerOnline = true;
                    JsonObject json = new JsonParser().parse(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
                    priceCache.put(itemName, new ItemData(json.get("bzBuy").getAsString(), json.get("bzSell").getAsString(), json.get("ahHigh").getAsString(), json.get("ahLow").getAsString(), json.get("ahAvg").getAsString(), json.get("lastUpdated").getAsString()));
                }
            } catch (Exception e) {
                isServerOnline = false;
            }
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    private static void triggerBackgroundFetch(String itemName) {
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(FakepixelUtilities.getApiUrl() + "/api/price/" + itemName.replace(" ", "%20"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(2000);
                if (conn.getResponseCode() == 200) {
                    isServerOnline = true;
                    JsonObject json = new JsonParser().parse(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
                    priceCache.put(itemName, new ItemData(json.get("bzBuy").getAsString(), json.get("bzSell").getAsString(), json.get("ahHigh").getAsString(), json.get("ahLow").getAsString(), json.get("ahAvg").getAsString(), json.get("lastUpdated").getAsString()));
                }
            } catch (Exception e) {
                isServerOnline = false;
            }
        });
    }

    public static void sendBatchData(JsonArray itemsArray) {
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(FakepixelUtilities.getApiUrl() + "/api/update-batch");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-API-Key", FakepixelUtilities.getApiKey());
                conn.setDoOutput(true);
                conn.setConnectTimeout(3000);
                OutputStream os = conn.getOutputStream();
                os.write(itemsArray.toString().getBytes("UTF-8"));
                os.flush();
                os.close();
                if (conn.getResponseCode() == 200) {
                    isServerOnline = true;
                }
            } catch (Exception e) {
                isServerOnline = false;
            }
        });
    }
}
