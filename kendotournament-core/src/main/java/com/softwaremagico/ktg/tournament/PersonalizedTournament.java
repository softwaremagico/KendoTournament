package com.softwaremagico.ktg.tournament;

import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;


public class PersonalizedTournament extends SimpleTournament {

    protected PersonalizedTournament(Tournament tournament) {
        super(tournament);
    }
    
    @Override
    public List<Fight> createRandomFights(Integer level) throws PersonalizedFightsException {
      throw new PersonalizedFightsException("");
    }
    
    @Override
    public List<Fight> createSortedFights(Integer level) throws PersonalizedFightsException {
    	throw new PersonalizedFightsException("");
    }

   
}
