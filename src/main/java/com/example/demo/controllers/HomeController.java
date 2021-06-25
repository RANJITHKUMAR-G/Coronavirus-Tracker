package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.models.LocationStats;
import com.example.demo.services.CoronaVirusDataServices;

@Controller
public class HomeController {
	
	@Autowired
	CoronaVirusDataServices coronaVirusDataServices;
	
	@GetMapping
	public String home(Model model) {
		List<LocationStats> allStats = coronaVirusDataServices.getAllStats();
		int totalCases = allStats.stream().mapToInt(a-> a.getLatestTotalCases()).sum();
		int totalCasesPrevDay = allStats.stream().mapToInt(a-> a.getDiffFromPrevDay()).sum();
		model.addAttribute("locationStats", coronaVirusDataServices.getAllStats());
		model.addAttribute("totalReportedCases", totalCases);
		model.addAttribute("totalPrevDayCases", totalCasesPrevDay);
		return "home";
	}

}
