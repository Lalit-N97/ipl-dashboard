package com.lnsteps.ipldashboard.batch.processor;

import java.time.LocalDate;

import org.springframework.batch.item.ItemProcessor;

import com.lnsteps.ipldashboard.batch.bean.MatchInputBean;
import com.lnsteps.ipldashboard.model.MatchEntity;

public class MatchDataProcessor implements ItemProcessor<MatchInputBean, MatchEntity> {

	@Override
	public MatchEntity process(MatchInputBean matchInput) throws Exception {
		MatchEntity match = new MatchEntity();
		match.setId(Long.parseLong(matchInput.getId()));
		match.setCity(matchInput.getCity());

		match.setDate(LocalDate.parse(matchInput.getDate()));

		match.setPlayerOfMatch(matchInput.getPlayer_of_match());
		match.setVenue(matchInput.getVenue());

		// Set Team 1 and Team 2 depending on the innings order
		String firstInningsTeam, secondInningsTeam;

		if ("bat".equals(matchInput.getToss_decision())) {
			firstInningsTeam = matchInput.getToss_winner();
			secondInningsTeam = matchInput.getToss_winner().equals(matchInput.getTeam1()) ? matchInput.getTeam2()
					: matchInput.getTeam1();

		} else {
			secondInningsTeam = matchInput.getToss_winner();
			firstInningsTeam = matchInput.getToss_winner().equals(matchInput.getTeam1()) ? matchInput.getTeam2()
					: matchInput.getTeam1();
		}
		match.setTeam1(firstInningsTeam);
		match.setTeam2(secondInningsTeam);

		match.setTossWinner(matchInput.getToss_winner());
		match.setTossDecision(matchInput.getToss_decision());
		match.setMatchWinner(matchInput.getWinner());
		match.setResult(matchInput.getResult());
		match.setResultMargin(matchInput.getResult_margin());
		match.setUmpire1(matchInput.getUmpire1());
		match.setUmpire2(matchInput.getUmpire2());

		return match;
	}

}
