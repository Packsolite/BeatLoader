package de.sneakometer.beatloader.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LeaderboardParser {
	public JsonArray parseLeaderboard(String json) {
		JsonParser parser = new JsonParser();
		JsonObject leaderboard = parser.parse(json).getAsJsonObject();
		return leaderboard.getAsJsonArray("leaderboards");
	}
}