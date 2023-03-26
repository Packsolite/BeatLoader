package de.sneakometer.beatloader.core.playlist;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.sneakometer.beatloader.core.BeatSaverUtil;
import de.sneakometer.beatloader.core.Song;

public class PlaylistGenerator {

	public Playlist generatePlayList(String playlistName, String author, Collection<Song> songs, URL imageResource) throws IOException, URISyntaxException {
		byte[] imageData = Files.readAllBytes(Paths.get(imageResource.toURI()));
		return this.generatePlayList(playlistName, author, songs, imageData);
	}

	public Playlist generatePlayList(String playlistName, String author, Collection<Song> songs, byte[] image) throws IOException {
		Playlist playlist = new Playlist();
		playlist.playlistTitle = playlistName;
		String encodedImage = new String(Base64.getEncoder()
				.encode(image));
		playlist.image = encodedImage;
		playlist.playlistAuthor = author;

		List<String> hashes = new ArrayList<String>();
		songs.forEach(song -> {
			if (hashes.contains(song.getId()))
				return;

			try {
				PlaylistSong pls = new PlaylistSong();
				pls.hash = song.getId();
				pls.songName = song.getSongName();
				pls.key = BeatSaverUtil.getKeyByHash(song.getId());
				playlist.songs.add(pls);

				hashes.add(song.getId());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});

		playlist.playlistSongCount = playlist.songs.size();
		return playlist;
	}

	public void savePlaylist(Playlist playlist, File file) {
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting()
					.create();

			// write playlist json
			FileWriter fw = new FileWriter(file);
			gson.toJson(playlist, fw);
			fw.flush();
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
