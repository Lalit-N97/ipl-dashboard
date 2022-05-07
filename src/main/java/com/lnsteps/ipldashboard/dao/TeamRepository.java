package com.lnsteps.ipldashboard.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lnsteps.ipldashboard.model.TeamEntity;

public interface TeamRepository extends JpaRepository<TeamEntity, Long>  {

	TeamEntity findByTeamName(String teamName);
    
}