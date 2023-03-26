package de.sneakometer.beatloader.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LeaderboardRequest {

	public String getLeaderboardAsJsonString(double minStars, double maxStars, int page) {

		try {
			System.out.println("Send request to scoresaber...");
			String url = "https://scoresaber.com/api/leaderboards?ranked=true&minStar="+minStars+"&maxStar="+maxStars+"&sort=1&unique=true&withMetadata=false&page=" + page;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "chrome");
			int responseCode = con.getResponseCode();
			System.out.println("Got response code: " + responseCode);
			System.out.println("Reading response...");
			
			System.out.println("len: " + con.getHeaderFields());

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			System.out.println("Got leaderboard json!");
			return response.toString();
		} catch (Exception ex) {
		}
		return null;
	}
}
