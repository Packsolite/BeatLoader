package de.sneakometer.beatloader.core;

import java.util.*;

import com.google.gson.JsonArray;

public class ScoreSaberUtil {

	public static List<Song> getRankedSongs(double minStars, double maxStars) {
		LeaderboardRequest leaderboardRequest = new LeaderboardRequest();
		LeaderboardParser leaderboardParser = new LeaderboardParser();
		SongParser songParser = new SongParser();

		List<Song> songs = new ArrayList<>();

		int pageNum = 1;
		while (true) {
			BeatLoader.updateStatus("Downloading leaderboard page " + pageNum);
			String leaderboardJson = leaderboardRequest.getLeaderboardAsJsonString(minStars, maxStars, pageNum);
			JsonArray songArray = leaderboardParser.parseLeaderboard(leaderboardJson);
			Collection<Song> leaderBordPage = songParser.parseSongs(songArray);
			if(leaderBordPage.isEmpty()) break;
			songs.addAll(leaderBordPage);
			pageNum++;
		}

		// sort by stars
		songs.sort(Comparator.comparingDouble(Song::getStars));

		Set<String> hashes = new HashSet<>();

		// elemninate duplicate hashes
		Iterator<Song> itr = songs.iterator();
		while (itr.hasNext()){
			Song song = itr.next();
			if(!hashes.add(song.songHash)){
				System.out.println("Found duplicate hash in leaderboard: " + song.songHash);
				itr.remove();
			}
		}

		return songs;
	}
}
