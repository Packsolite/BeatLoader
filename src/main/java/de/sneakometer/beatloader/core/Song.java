package de.sneakometer.beatloader.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Song {
	private int uid;
	private String id;
	private String name;
	private String songSubName;
	private String songAuthorName;
	private String levelAuthorName;
	private int bpm;
	private String diff;
	private String scores;
	private String scores_day;
	private int ranked;
	private String stars;
	private String image;

	@Override
	public String toString() {
		return name + " " + songSubName + " - " + songAuthorName + " mapped by " + levelAuthorName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + uid;
		return result;
	}

	public boolean equalID(Song other) {
		return id.equals(other.id);
	}
}
