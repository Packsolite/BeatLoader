package de.sneakometer.beatloader.core.playerdata;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import de.sneakometer.beatloader.core.BeatLoader;

public class PlayerDataReader {

	public JsonElement getPlayerDataReader() {

		File file = new File(BeatLoader.PLAYERDATA_PATH);
		JsonParser parser = new JsonParser();
		JsonElement element = null;

		try (FileReader reader = new FileReader(file)) {
			element = parser.parse(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return element;
	}
}
