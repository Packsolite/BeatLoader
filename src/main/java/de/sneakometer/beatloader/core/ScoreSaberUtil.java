package de.sneakometer.beatloader.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonArray;

public class ScoreSaberUtil {

	public static Collection<Song> getRankedSongs(double minStars, double maxStars) {
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

		return songs;
	}
}
