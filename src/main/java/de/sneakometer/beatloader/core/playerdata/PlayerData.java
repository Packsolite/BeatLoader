package de.sneakometer.beatloader.core.playerdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.sneakometer.beatloader.core.BeatLoader;
import de.sneakometer.beatloader.core.Song;
import lombok.Getter;

public class PlayerData {

	@Getter
	private JsonObject playerdataJson;
	private JsonArray localPlayers;
	private JsonObject player;
	private JsonArray favList;
	private JsonArray levelsStatData;

	public PlayerData(JsonElement playerData) {
		playerdataJson = playerData.getAsJsonObject();
		localPlayers = playerdataJson.getAsJsonArray("localPlayers");
		player = localPlayers.get(0)
				.getAsJsonObject();
		favList = player.getAsJsonArray("favoritesLevelIds");
		levelsStatData = player.getAsJsonArray("levelsStatsData");
	}

	public boolean hasDownloadedSong(Song song) {

		String id = BeatLoader.CUSTOM_LEVEL_ID_PREFIX + song.getId();

		for (JsonElement element : this.levelsStatData) {
			JsonObject levelData = element.getAsJsonObject();
			String levelId = levelData.getAsJsonPrimitive("levelId")
					.getAsString();
			if (id.equalsIgnoreCase(levelId))
				return true;
		}

		return false;
	}

	public void clearFavorites() {
		// clear favlist
		while (favList.size() != 0)
			favList.remove(0);
	}

	public void addFavorite(Song song) {
		favList.add(new JsonPrimitive(BeatLoader.CUSTOM_LEVEL_ID_PREFIX + song.getId()));
	}

	public void save() {
		new PlayerDataWriter().writePlayerData(this.playerdataJson);
	}
}
