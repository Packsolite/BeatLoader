package de.sneakometer.beatloader.core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.google.gson.Gson;

import de.sneakometer.beatloader.core.playerdata.PlayerData;
import de.sneakometer.beatloader.core.playerdata.PlayerDataReader;
import de.sneakometer.beatloader.core.playlist.Playlist;
import de.sneakometer.beatloader.core.playlist.PlaylistGenerator;
import de.sneakometer.beatloader.main.Main;

public class BeatLoader {
	public static double minStars = 5;
	public static String PLAYERDATA_PATH;
	public static String SONGS_PATH;
	public static String PLAYLISTS_PATH;
	public static String CUSTOM_LEVEL_ID_PREFIX = "custom_level_";
	public static int TOTAL_DOWNLOADED = 0;

	public static Gson gson = new Gson();
	public static ExecutorService executor = Executors.newCachedThreadPool();

	private static final List<Consumer<String>> statusSubscriber = new ArrayList<>();

	public static void subscribeStatus(Consumer<String> consumer) {
		statusSubscriber.add(consumer);
	}

	public static void updateStatus(String status) {
		System.out.println("Status: " + status);
		statusSubscriber.forEach(c -> c.accept(status));
	}

	public static CompletableFuture<Void> start(double minStars, double maxStars, boolean createPlaylist, boolean favorite, boolean redownloadAll) {
		updateStatus("Preparing...");
		return CompletableFuture.supplyAsync(() -> {
			TOTAL_DOWNLOADED = 0;
			specifyGameDir();
			Collection<Song> songs = ScoreSaberUtil.getRankedSongs(minStars);

			updateStatus("Found " + songs.size() + " songs");
			BeatSaverUtil.deleteDoubles();

			PlayerData playerData = new PlayerData(new PlayerDataReader().getPlayerDataReader());

			downloadSongs(songs, playerData, redownloadAll);

			if (favorite) {
				addToFavorites(songs, playerData);
			}

			if (createPlaylist) {
				try {
					createPlaylist(songs);
				} catch (IOException | URISyntaxException e) {
					updateStatus("Failed generating playlist");
					throw new RuntimeException(e);
				}
			}
			updateStatus("Done! (" + TOTAL_DOWNLOADED + " songs downloaded)");
			return null;
		});
	}

	static void downloadSongs(Collection<Song> songs, PlayerData playerData, boolean redownloadAll) {
		for (Song song : songs) {
			if (!playerData.hasDownloadedSong(song)) {
				updateStatus("Downloading " + song.getName() + " - " + song.getSongAuthorName() + "...");
				try {
					BeatSaverUtil.downloadSong(song, redownloadAll);
				} catch (Exception ex) {
					System.out.println("Could not download song: " + ex.getMessage());
				}
			}
		}
	}

	static void addToFavorites(Collection<Song> songs, PlayerData playerData) {
		updateStatus("Adding songs as favorite...");
		playerData.clearFavorites();
		for (Song song : songs) {
			playerData.addFavorite(song);
		}
		playerData.save();
	}

	static void createPlaylist(Collection<Song> songs) throws IOException, URISyntaxException {
		updateStatus("Generating Playlist...");
		PlaylistGenerator playlistgen = new PlaylistGenerator();
		Playlist playlist = playlistgen.generatePlayList("Ranked Songs", "Sneakometer", songs, Main.class.getResource("/image/rankedImage.png"));
		playlistgen.savePlaylist(playlist, new File(PLAYLISTS_PATH + "\\RankedSongs.json"));
	}

	static void specifyGameDir() {
		findBeatSaberHome();

		// user home path
		String userHome = System.getProperty("user.home");
		PLAYERDATA_PATH = userHome + "/AppData/LocalLow/Hyperbolic Magnetism/Beat Saber/PlayerData.dat";

		if (!new File(PLAYERDATA_PATH).exists()) {
			System.out.println("Error: Could not find your PlayerData.dat");
			updateStatus("PlayerData.dat not found");
			throw new RuntimeException("could not find PlayerData.dat");
		}
	}

	private static void findBeatSaberHome() {
		List<String> common = Arrays.asList("C:/Program Files (x86)/Steam/steamapps/common/Beat Saber", "C:/SteamLibrary/steamapps/common/Beat Saber", "D:/SteamLibrary/steamapps/common/Beat Saber",
				"E:/SteamLibrary/steamapps/common/Beat Saber", "F:/SteamLibrary/steamapps/common/Beat Saber");
		for (String path : common) {
			File file = new File(path);
			if (file.exists()) {
				System.out.println("Found Beat Saber dir at " + path);
				SONGS_PATH = path + "/Beat Saber_Data/CustomLevels";
				PLAYLISTS_PATH = path + "/Playlists";
				return;
			}
		}
		updateStatus("Gamedir not found.");
		throw new RuntimeException("Gamedir not found");
	}
}
