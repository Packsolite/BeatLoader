package de.sneakometer.beatloader.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

		// elemninate doubles
		Iterator<Song> itr = songs.iterator();
		while (itr.hasNext()){
			Song song = itr.next();
			boolean isDouble = false;
			for(Song otherSong : songs) {
				if (song == otherSong) continue;
				if(song.songHash.equalsIgnoreCase(otherSong.songHash)) {
					System.out.println("Found double songs in leaderboard: ");
					System.out.println(song.toString());
					System.out.println(otherSong.toString());
					isDouble = true;
				}
			}
			if(isDouble){
				itr.remove();
			}
		}
		return songs;
	}
}
