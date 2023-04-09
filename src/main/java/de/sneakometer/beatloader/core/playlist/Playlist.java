package de.sneakometer.beatloader.core.playlist;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
	public String playlistTitle;
	public String playlistAuthor;

	public String playlistDescription;
	public String image;
	public int playlistSongCount;
	public List<PlaylistSong> songs = new ArrayList<>();
	public String customDetailUrl = null;
	public String customArchiveUrl = null;
}
