package de.sneakometer.beatloader.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class SongParser {

	public Collection<Song> parseSongs(JsonArray jsonArray) {
		BeatLoader.updateStatus("Parsing songs...");
		Collection<Song> songs = new ArrayList<>();

		long s = System.currentTimeMillis();

		for (JsonElement element : jsonArray) {
			Song song = BeatLoader.gson.fromJson(element, Song.class);

			// search for old song
			if (upgradeSong(song, songs)) {
				continue;
			}

			songs.add(song);
		}

		System.out.println("Parsed " + songs.size() + " total songs in " + (System.currentTimeMillis() - s) + "ms");
		return songs;
	}

	private boolean upgradeSong(Song song, Collection<Song> songs) {
		Optional<Song> songOpt = songs.parallelStream()
				.filter(song::equalID)
				.findAny();
		if (songOpt.isPresent()) {
			Song old = songOpt.get();
			boolean higherStars = getStars(song) > getStars(old);
			boolean oldRanked = old.getRanked() != 0;
			boolean newRanked = song.getRanked() != 0;

			if ((higherStars && (!oldRanked || newRanked)) || (newRanked && !oldRanked)) {
				old.setStars(song.getStars());
				old.setRanked(song.getRanked());
			}
			return true;
		} else {
			return false;
		}
	}

	private double getStars(Song song) {
		try {
			return Double.parseDouble(song.getStars());
		} catch (RuntimeException ex) {
			return 0;
		}
	}
}
