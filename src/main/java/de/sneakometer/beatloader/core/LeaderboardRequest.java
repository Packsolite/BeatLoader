package de.sneakometer.beatloader.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LeaderboardRequest {

	public String getLeaderboardAsJsonString() {

		try {
			System.out.println("Send request to scoresaber...");
			String url = "http://scoresaber.com/api.php?function=get-leaderboards&limit=2147483647&page=1";
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
