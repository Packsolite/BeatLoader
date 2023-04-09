package de.sneakometer.beatloader.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.AllArgsConstructor;

public class BeatSaverUtil {
	static Map<String, String> keyByHashCache = new HashMap<>();

	/**
	 * We use scoresaber to get a list of ranked songs we want. But to download them,
	 * we need to use beatsaver, which only uses its own song-keys to download maps.
	 * <p>
	 * This requests all the songs keys by their hashes in chunks of 50.
	 */
	public static void fillInKeys(List<Song> songs) throws IOException {
		long distinct = songs.stream().map(Song::getSongHash).distinct().count();
		if(distinct != songs.size()){
			// if it's not, we may end up requesting the same song twice
			throw new IllegalStateException("Hashes must be unique!");
		}
		// fill in already known (cached) keys
		for (Song song : songs) {
			if (keyByHashCache.containsKey(song.songHash)) {
				song.key = keyByHashCache.get(song.songHash);
			}
		}
		// filter remaining songs without a key
		List<Song> remainingSongs = songs.stream().filter(song -> song.key == null).toList();
		if (remainingSongs.isEmpty()) {
			return;
		}
		if (remainingSongs.size() > 50) {
			fillInKeys(songs.subList(0, 50));
			fillInKeys(songs.subList(50, songs.size()));
			return;
		}
		String url = "https://api.beatsaver.com/maps/hash/" + String.join(",", remainingSongs.stream()
				.map(Song::getSongHash)
				.toList());
		System.out.println("GET " + url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "chrome");

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		// the response differs when there are multiple hashes provided
		if (remainingSongs.size() > 1) {
			for (Song song : remainingSongs) {
				String hash = song.songHash;
				if (keyByHashCache.containsKey(hash)) {
					throw new IllegalStateException("already containing key for song hash " + song.getSongHash() + " (song.key=" + song.key + ")");
				}
				JsonObject jsonObject = new JsonParser().parse(response.toString())
						.getAsJsonObject().getAsJsonObject(hash.toLowerCase());
				String key = jsonObject.getAsJsonPrimitive("id")
						.getAsString();

				keyByHashCache.put(hash, key);
				song.key = key;
			}
		} else {
			Song song = remainingSongs.get(0);
			String hash = song.songHash;
			JsonObject jsonObject = new JsonParser().parse(response.toString())
					.getAsJsonObject();
			String key = jsonObject.getAsJsonPrimitive("id")
					.getAsString();

			keyByHashCache.put(hash, key);
			song.key = key;
		}

		in.close();
	}

	public static <T> Stream<List<T>> batches(List<T> source, int length) {
		if (length <= 0)
			throw new IllegalArgumentException("length = " + length);
		int size = source.size();
		if (size <= 0)
			return Stream.empty();
		int fullChunks = (size - 1) / length;
		return IntStream.range(0, fullChunks + 1).mapToObj(
				n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
	}

	public static void downloadSongFiles(Song song, File fileTo) throws IOException {
		DownloadStream is = getStream(song);
		System.out.println("song hash=" + song.getSongHash() + " id=" + song.id + " key=" + song.key);

		if (is == null) {
			System.out.println("Found no stream for " + song + " try again in 5 seconds");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			is = getStream(song);
			if (is == null) {
				System.out.println("Found no stream again. Skipping song...");
				return;
			}
		}

		ZipInputStream zis = new ZipInputStream(is.in);

		if (!fileTo.exists()) {
			fileTo.mkdirs();
		}

		int downloadedBytes = 0;
		long lastPrint = 0;
		byte[] buffer = new byte[1024];

		ZipEntry entry;

		while ((entry = zis.getNextEntry()) != null) {
			String name = entry.getName();
			File file = new File(fileTo.getAbsoluteFile() + File.separator + name);
			// create directories for sub directories in zip
			new File(file.getParent()).mkdirs();

			FileOutputStream fos = new FileOutputStream(file);
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
				downloadedBytes += len;
				if (System.currentTimeMillis() - lastPrint > TimeUnit.SECONDS.toMillis(1)) {
					lastPrint = System.currentTimeMillis();
					printProgress(downloadedBytes, is.size);
				}
			}
			fos.close();
			// close this ZipEntry
			zis.closeEntry();
		}
		printProgress(downloadedBytes, is.size);
		BeatLoader.TOTAL_DOWNLOADED++;
		is.in.close();
	}

	private static void printProgress(int downloadedBytes, int contentLength) {
		int percentage = Math.min(100, (int) Math.round((double) downloadedBytes / contentLength * 100));
		double downloadedmb = Math.round((double) downloadedBytes / 1024 / 1024 * 10) / 10;
		double totalmb = Math.round((double) contentLength / 1024 / 1024 * 10) / 10;
		System.out.println(downloadedmb + "/" + totalmb + "mb (" + percentage + "%)");
	}

	public static DownloadStream getStream(Song song) {
		try {
			String url = "https://beatsaver.com/api/download/key/" + song.key;
			System.out.println("GET " + url);
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			// con.setRequestProperty("User-Agent", "chrome");
			InputStream in = con.getInputStream();
			if (in == null) {
				return null;
			}
			return new DownloadStream(con.getContentLength(), in);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@AllArgsConstructor
	static class DownloadStream {
		int size;
		InputStream in;
	}

	public static void downloadSong(Song song, boolean replaceIfExist) throws IOException, InterruptedException {
		if (song.key == null) {
			throw new IllegalStateException("Cannot download songs without a key.");
		}
		final String fileNameRegex = "[^A-Za-z0-9!+-., ]";
		String dirName = song.key + " (" + song.getSongName()
				.replaceAll(fileNameRegex, "") + " - "
				+ song.getLevelAuthorName()
				.replaceAll(fileNameRegex, "")
				+ ")";
		File path = new File(BeatLoader.SONGS_PATH + File.separator + dirName);

		if (path.exists()) {
			if (replaceIfExist) {
				System.out.println("Path found for " + song.getSongName() + ", delete...");
				deleteDirectory(path);
				path.delete();
				System.out.println("Continue download...");
			} else {
				System.out.println("Song files already downloaded... Skipping");
				return;
			}
		}
		deleteOldFiles(song.key);
		path.mkdirs();
		try {
			downloadSongFiles(song, path);
		} catch (IOException e) {
			System.out.println("Could not download song " + song.getSongName() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void deleteDoubles() {
		HashSet<String> keys = new HashSet<String>();
		File path = new File(BeatLoader.SONGS_PATH);
		for (File file : path.listFiles()) {
			if (file.isDirectory()) {
				String name = file.getName()
						.split(" ")[0];
				if (!keys.add(name)) {
					System.out.println("Deleting song with equal key: " + file.getName());
					FileUtil.deleteDirectory(file);
				}
			}
		}
	}

	private static void deleteOldFiles(String key) {
		File path = new File(BeatLoader.SONGS_PATH);
		for (File file : path.listFiles()) {
			if (file.isDirectory()) {
				String name = file.getName()
						.split(" ")[0];
				if (name.equals(key)) {
					System.out.println("Deleting " + name + " as it has wrong name");
					file.delete();
				}
			}
		}
	}

	private static boolean deleteDirectory(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directoryToBeDeleted.delete();
	}
}
