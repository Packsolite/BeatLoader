package de.sneakometer.beatloader.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Song {

	String key, id, songHash, songName, songSubName, levelAuthorName;
	double stars;

	public boolean equalsID(Song other) {
		return id.equals(other.id);
	}
}
