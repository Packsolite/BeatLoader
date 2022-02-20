package de.sneakometer.beatloader.core.playerdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonElement;

import de.sneakometer.beatloader.core.BeatLoader;

public class PlayerDataWriter {

	public void writePlayerData(JsonElement playerDataJson) {
		File file = new File(BeatLoader.PLAYERDATA_PATH);

		try (FileWriter writer = new FileWriter(file)) {
			BeatLoader.gson.toJson(playerDataJson, writer);
		} catch (IOException e) {
			System.out.println("Could not save playerdata:");
			e.printStackTrace();
			return;
		}
	}
}
