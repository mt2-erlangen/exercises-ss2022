/*
 * MainClass.java
 * Copyright (C) 2020 Stephan Seitz <stephan.seitz@fau.de>
 *
 * Distributed under terms of the GPLv3 license.
 */
package exercises;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class Exercise02 {

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {

		var connection = new URL(url).openConnection();
		connection.setRequestProperty("Accept", "application/json");
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
		String jsonText = readAll(rd);
		JSONObject json = new JSONObject(jsonText);
		return json;
	}


	// <your name> <your idm>
	// <your partner's name> <your partner's idm> (if you submit with a group partner)
	public static void main(String[] args) throws MalformedURLException, IOException {
		var response = readJsonFromUrl("https://api.corona-zahlen.org/germany/history/cases");
		var data = response.getJSONArray("data");

		var cases = new float[data.length()];
		for (int i = 0; i < data.length(); i++) {
			cases[i] = data.getJSONObject(i).getFloat("cases");
		}

		System.out.println(
				"Data provided by Robert-Koch-Institut via " + response.getJSONObject("meta").getString("info"));
		System.out.println("Data from " + data.getJSONObject(0).getString("date") + " to "
				+ data.getJSONObject(data.length() - 1).getString("date"));
		var signal = new mt.Signal(cases, "Cases");
		signal.show();

		var filterMean = new mt.LinearFilter(
				new float[] { 1.f / 7.f, 1.f / 7.f, 1.f / 7.f, 1.f / 7.f, 1.f / 7.f, 1.f / 7.f, 1.f / 7.f },
				"Mean Week");
		var filterDifference = new mt.LinearFilter(new float[] { 1.f, 0.f, 0.f, 0.f, 0.f, 0.f, -1.f },
				"Difference Week");

		var meanData = filterMean.apply(signal);
		var diffData = filterDifference.apply(signal);
		meanData.show();
		diffData.show();

		var combination1 = filterMean.apply(diffData);
		var combination2 = filterDifference.apply(meanData);

		combination1.show();
		combination2.show();
	}
}
