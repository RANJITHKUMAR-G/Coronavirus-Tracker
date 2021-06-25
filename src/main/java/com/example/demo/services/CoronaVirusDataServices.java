package com.example.demo.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.models.LocationStats;

/* There is a method which is sending inside the services execute the when the application starts */
//Makes as a spring service
@Service
public class CoronaVirusDataServices {

	/* https://github.com/CSSEGISandData/COVID-19 */
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

	private List<LocationStats> allStats = new ArrayList<>();
	/* Hey spring execute the method, when application starts */
	/*
	 * It is basically telling spring when you construct the instance of service
	 * after its done just execute the method
	 */
	@PostConstruct
	/*
	 * schedules the run of a method on regular basis Corn expression specified a
	 * string , schedule to run this method in every second
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	//@Scheduled(cron = "${db_cron}")
	public void fetchVirusData() throws IOException, InterruptedException {
		List<LocationStats> newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// System.out.println(response.body());
		//https://commons.apache.org/proper/commons-csv/user-guide.html
		StringReader csvBodyReader = new StringReader(response.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			LocationStats locationStats = new LocationStats();
			locationStats.setState(record.get("Province/State"));
			locationStats.setCountry(record.get("Country/Region"));
			int latestCases = Integer.parseInt(record.get(record.size()-1));
			int previousDayCases = Integer.parseInt(record.get(record.size()-2));
			locationStats.setLatestTotalCases(latestCases);
			locationStats.setDiffFromPrevDay(latestCases-previousDayCases);
			//System.out.println(locationStats);
			newStats.add(locationStats);
		}
		this.allStats = newStats;
	}
	public List<LocationStats> getAllStats() {
		return allStats;
	}
	public void setAllStats(List<LocationStats> allStats) {
		this.allStats = allStats;
	}
}
