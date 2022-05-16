package com.lnsteps.ipldashboard.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lnsteps.ipldashboard.dao.MatchRepository;
import com.lnsteps.ipldashboard.dao.TeamRepository;
import com.lnsteps.ipldashboard.model.MatchEntity;
import com.lnsteps.ipldashboard.model.TeamEntity;

@RestController
@CrossOrigin
public class TeamController {

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private MatchRepository matchRepository;

	@GetMapping("/team")
	public Iterable<TeamEntity> getAllTeam() {
		return this.teamRepository.findAll();
	}

	@GetMapping("/team/{teamName}")
	public TeamEntity getTeam(@PathVariable String teamName) {
		TeamEntity team = this.teamRepository.findByTeamName(teamName);
		team.setMatches(matchRepository.findLatestMatchesbyTeam(teamName, 4));
		return team;
	}

	@GetMapping("/team/{teamName}/matches")
	public List<MatchEntity> getMatchesForTeam(@PathVariable String teamName, @RequestParam("yearofdate") int year) {
		LocalDate startDate = LocalDate.of(year, 1, 1);
		LocalDate endDate = LocalDate.of(year + 1, 1, 1);
		return this.matchRepository.getMatchesByTeamBetweenDates(teamName, startDate, endDate);
	}
}
