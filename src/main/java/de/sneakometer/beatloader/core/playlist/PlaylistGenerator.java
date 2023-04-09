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

	public Playlist generatePlayList(String playlistName, String author, String description, Collection<Song> songs, URL imageResource) throws IOException, URISyntaxException {
		byte[] imageData = Files.readAllBytes(Paths.get(imageResource.toURI()));
		return this.generatePlayList(playlistName, author, description, songs, imageData);
	}

	public Playlist generatePlayList(String playlistName, String author, String description, Collection<Song> songs, byte[] image) throws IOException {
		Playlist playlist = new Playlist();
		playlist.playlistTitle = playlistName;
		String encodedImage = new String(Base64.getEncoder()
				.encode(image));
		playlist.image = encodedImage;
		playlist.playlistAuthor = author;
		playlist.playlistDescription = description;

		List<String> hashes = new ArrayList<String>();
		songs.forEach(song -> {
			if (hashes.contains(song.getId()))
				return;

			System.out.println("Adding to playlist: " + song.toString());

			PlaylistSong pls = new PlaylistSong();
			pls.hash = song.getSongHash();
			pls.songName = song.getSongName();
			// pls.key = BeatSaverUtil.getKeyByHash(song.getId());
			pls.key = song.getKey();
			playlist.songs.add(pls);

			hashes.add(song.getId());
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
