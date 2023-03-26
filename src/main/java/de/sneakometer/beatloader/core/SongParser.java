package de.sneakometer.beatloader.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class SongParser {

	public Collection<Song> parseSongs(JsonArray jsonArray) {
		Collection<Song> songs = new ArrayList<>();

		long s = System.currentTimeMillis();

		for (JsonElement element : jsonArray) {
			Song song = BeatLoader.gson.fromJson(element, Song.class);
			songs.add(song);
		}

		System.out.println("Parsed " + songs.size() + " songs in " + (System.currentTimeMillis() - s) + "ms");
		return songs;
	}
}
