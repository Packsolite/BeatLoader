package de.sneakometer.beatloader.core;

import java.util.Collection;

import com.google.gson.JsonArray;

public class ScoreSaberUtil {

	public static Collection<Song> getRankedSongs() {
		return getRankedSongs(-1);
	}

	public static Collection<Song> getRankedSongs(double stars) {
		LeaderboardRequest leaderboardRequest = new LeaderboardRequest();
		LeaderboardParser leaderboardParser = new LeaderboardParser();
		SongParser songParser = new SongParser();
		SongFilter songFilter = new SongFilter();

		String leaderboardJson = leaderboardRequest.getLeaderboardAsJsonString();
		JsonArray songArray = leaderboardParser.parseLeaderboard(leaderboardJson);
		Collection<Song> songs = songParser.parseSongs(songArray);
		songFilter.filterRanked(songs, stars);
		return songs;
	}
}
