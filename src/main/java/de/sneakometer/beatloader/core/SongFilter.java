package de.sneakometer.beatloader.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SongFilter {

	public void filterRanked(Collection<Song> songs) {
		this.filterRanked(songs, -1);
	}

	public void filterRanked(Collection<Song> songs, double minStars) {
		Iterator<Song> iter = songs.iterator();
		while (iter.hasNext()) {
			Song song = iter.next();
			if (song.getRanked() == 0 || getStars(song) < minStars) {
				iter.remove();
			}
		}
	}

	private double getStars(Song song) {
		try {
			return Double.parseDouble(song.getStars());
		} catch (RuntimeException ex) {
			return 0;
		}
	}

	// filters out double entries
	public void filterDoubleEntries(Collection<Song> songs) {
		List<Song> addedSongs = new ArrayList<>();

		Iterator<Song> iter = songs.iterator();
		while (iter.hasNext()) {
			Song song = iter.next();

			boolean isDouble = false;

			for (Song added : addedSongs) {
				if (added.getId()
						.equalsIgnoreCase(song.getId())) {
					isDouble = true;
					break;
				}
			}

			if (isDouble) {
				iter.remove();
			} else {
				addedSongs.add(song);
			}
		}
	}
}
