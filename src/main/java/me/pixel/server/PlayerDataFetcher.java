package me.pixel.server;

import me.pixel.common.PlayerData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PlayerDataFetcher {
    private String url;
    private Map<String, PlayerData> playerDataMap = new HashMap<>();

    public PlayerDataFetcher(String url) {
        this.url = url;
    }

    public void fetchPlayerData() {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("GET");

            Gson gson = new Gson();
            JsonObject response = gson.fromJson(new InputStreamReader(conn.getInputStream()), JsonObject.class);
            JsonObject players = response.getAsJsonObject("players");

            for (String playerName : players.keySet()) {
                JsonObject playerInfo = players.getAsJsonObject(playerName);
                PlayerData playerData = new PlayerData(
                        playerInfo.get("uuid").getAsString(),
                        playerInfo.get("cape").getAsBoolean(),
                        playerInfo.get("ears").getAsBoolean(),
                        playerInfo.get("flipped").getAsBoolean(),
                        playerInfo.get("enchanted_cape").getAsBoolean(),
                        playerInfo.get("cape_texture").getAsString(),
                        playerInfo.has("ears_texture") ? playerInfo.get("ears_texture").getAsString() : null
                );
                playerDataMap.put(playerData.getUuid(), playerData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PlayerData getPlayerData(String uuid) {
        return playerDataMap.get(uuid);
    }
}
