package com.lnsteps.ipldashboard.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lnsteps.ipldashboard.model.MatchEntity;


public interface MatchRepository extends JpaRepository<MatchEntity, Long>  {

    List<MatchEntity> getByTeam1OrTeam2OrderByDateDesc(String teamName1, String teamName2, Pageable pageable);

    @Query("select m from MatchEntity m where (m.team1 = :teamName or m.team2 = :teamName) and m.date between :dateStart and :dateEnd order by date desc")
    List<MatchEntity> getMatchesByTeamBetweenDates(
        @Param("teamName") String teamName, 
        @Param("dateStart") LocalDate dateStart, 
        @Param("dateEnd") LocalDate dateEnd
    );
    
    // List<MatchEntity> getByTeam1AndDateBetweenOrTeam2AndDateBetweenOrderByDateDesc(
    //     String teamName1, LocalDate date1, LocalDate date2,
    //     String teamName2, LocalDate date3, LocalDate date4
    //     );

    
    default List<MatchEntity> findLatestMatchesbyTeam(String teamName, int count) {
        return getByTeam1OrTeam2OrderByDateDesc(teamName, teamName, PageRequest.of(0, count));
    }

}