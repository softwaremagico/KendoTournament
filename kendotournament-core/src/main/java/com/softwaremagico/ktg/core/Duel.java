package com.softwaremagico.ktg.core;

/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A duel is a combat between one member of one team with other member of other
 * team. A fight will have a number of duels depending of the member number.
 */
public class Duel implements Comparable<Duel> {

	private Fight fight;
	private Integer order;
	private final int POINTS_TO_WIN = 2;
	private List<Score> hitsFromCompetitorA = new ArrayList<>(); // M, K, T, D,
																	// H, I
	private List<Score> hitsFromCompetitorB = new ArrayList<>(); // M, K, T, D,
																	// H, I
	private Boolean faultsCompetitorA = false;
	private Boolean faultsCompetitorB = false;

	public Duel(Fight fight, Integer order) {
		this.fight = fight;
		this.order = order;
		addRounds();
	}

	public Fight getFight() {
		return fight;
	}

	public Integer getOrder() {
		return order;
	}

	/**
	 * Set a hit point to a player
	 * 
	 * @param playerA
	 *            if true, the hit is set to the player of the first team, if
	 *            false to the player of the other team.
	 * @param hitNumber
	 * @param score
	 */
	public void setHit(boolean playerA, Integer hitNumber, Score score) {
		if (playerA) {
			hitsFromCompetitorA.set(hitNumber, score);
		} else {
			hitsFromCompetitorB.set(hitNumber, score);
		}
	}

	/**
	 * Obtains the score of the player.
	 * 
	 * @param playerA
	 *            if true, gets scored from player of the first team, if false
	 *            from player of the other team.
	 * @return
	 */
	public List<Score> getHits(boolean playerA) {
		if (playerA) {
			return hitsFromCompetitorA;
		} else {
			return hitsFromCompetitorB;
		}
	}

	/**
	 * Generate the structure of the rounds.
	 */
	private void addRounds() {
		for (int i = 0; i < POINTS_TO_WIN; i++) {
			hitsFromCompetitorA.add(Score.EMPTY);
			hitsFromCompetitorB.add(Score.EMPTY);
		}
	}

	/**
	 * Count the rounds and the score of each player to know if the duels is
	 * over or not.
	 * 
	 * @return true if the round is over.
	 */
	public boolean isOver() {
		int pointA = 0;
		int pointB = 0;
		for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
			if (Score.isValidPoint(hitsFromCompetitorA.get(i))) {
				pointA++;
			}
		}
		for (int i = 0; i < hitsFromCompetitorB.size(); i++) {
			if (Score.isValidPoint(hitsFromCompetitorB.get(i))) {
				pointB++;
			}
		}
		if (pointA >= POINTS_TO_WIN || pointB >= POINTS_TO_WIN) {
			return true;
		}
		return false;
	}

	/**
	 * Defines if a duel has been started or not. A duel is started if there is
	 * any score, fault or is marked as over.
	 * 
	 * @return
	 */
	public boolean isStarted() {
		if ((getScore(true)) > 0 || (getScore(false)) > 0 || getFaults(true) || getFaults(false) || isOver()) {
			return true;
		}
		return false;
	}

	/**
	 * Add a hit to a player to store.
	 * 
	 * @param round
	 *            Number of round of the result is obtained.
	 * @param result
	 *            Hit caused.
	 * @param firstTeamPlayer
	 *            True if is the firstTeamPlayer who caused the hit, false if is the
	 *            player2.
	 * @return the round updated.
	 */
	public Integer setResultInRound(int round, Score result, boolean firstTeamPlayer) {
		int roundUpdated = round;
		try {
			if (Score.isValidPoint(result)) {
				if (firstTeamPlayer) {
					// If the first round has no point and we put the second
					// one, it is a mistake!
					if (round > 0 && hitsFromCompetitorA.get(round - 1).equals(Score.EMPTY)
							&& !result.equals(Score.EMPTY)) {
						roundUpdated = setResultInRound(round - 1, result, firstTeamPlayer);
					} else {
						hitsFromCompetitorA.set(round, result);
						// It is impossible that both players has the second
						// point.
						if ((round == hitsFromCompetitorA.size() - 1) && (!result.equals(Score.EMPTY))) {
							setResultInRound(round, Score.EMPTY, !firstTeamPlayer);
						}
					}
				} else {
					// If the first round has no point and we put the second
					// one, is a mistake!
					if (round > 0 && hitsFromCompetitorB.get(round - 1).equals(Score.EMPTY)
							&& !result.equals(Score.EMPTY)) {
						roundUpdated = setResultInRound(round - 1, result, firstTeamPlayer);
					} else {
						hitsFromCompetitorB.set(round, result);
						// It is impossible that both players has the second
						// point.
						if ((round == hitsFromCompetitorB.size() - 1) && (!result.equals(Score.EMPTY))) {
							setResultInRound(round, Score.EMPTY, !firstTeamPlayer);
						}
					}
				}
			}
		} catch (IndexOutOfBoundsException iob) {
		}
		return roundUpdated;
	}

	/**
	 * Removes the score of a player in a round.
	 * 
	 * @param round
	 * @param firstTeamPlayer
	 *            true will choose the player of the first team, false will
	 *            choose the player of the second team.
	 */
	public void clearResultInRound(int round, boolean firstTeamPlayer) {
		try {
			if (firstTeamPlayer) {
				for (int i = round; i < POINTS_TO_WIN; i++) {
					hitsFromCompetitorA.set(i, Score.EMPTY);
				}
			} else {
				for (int i = round; i < POINTS_TO_WIN; i++) {
					hitsFromCompetitorB.set(i, Score.EMPTY);
				}
			}
		} catch (IndexOutOfBoundsException iob) {
		}
	}

	/**
	 * Add a fault to a player.
	 * 
	 * @param firstTeamPlayer
	 */
	public void setFaults(boolean firstTeamPlayer) {
		int faultRound = getScore((!firstTeamPlayer));
		if (firstTeamPlayer) {
			if (faultsCompetitorA == true) {
				setResultInRound(faultRound, Score.HANSOKU, !firstTeamPlayer);
				faultsCompetitorA = false;
			} else {
				faultsCompetitorA = true;
			}
		} else {
			if (faultsCompetitorB == true) {
				setResultInRound(faultRound, Score.HANSOKU, !firstTeamPlayer);
				faultsCompetitorB = false;
			} else {
				faultsCompetitorB = true;
			}
		}
	}

	/**
	 * Gets the faults of a player
	 * 
	 * @param firstTeamPlayer
	 *            true will choose the player of the first team, false will
	 *            choose the player of the second team.
	 * @return
	 */
	public Boolean getFaults(boolean firstTeamPlayer) {
		if (firstTeamPlayer) {
			return faultsCompetitorA;
		} else {
			return faultsCompetitorB;
		}
	}

	/**
	 * Remove all faults of a player. Note: it will not remove any hansoku point
	 * obtained by the accumulation of faults.
	 * 
	 * @param firstTeamPlayer
	 *            true will choose the player of the first team, false will
	 *            choose the player of the second team.
	 */
	public void resetFaults(boolean firstTeamPlayer) {
		if (firstTeamPlayer) {
			faultsCompetitorA = false;
		} else {
			faultsCompetitorB = false;
		}
	}

	/**
	 * Get the number of hits done by one player.
	 * 
	 * @param firstTeamPlayer
	 *            true will choose the player of the first team, false will
	 *            choose the player of the second team.
	 * @return
	 */
	public Integer getScore(boolean firstTeamPlayer) {
		int round = 0;
		if (firstTeamPlayer) {
			for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
				if (!hitsFromCompetitorA.get(i).equals(Score.EMPTY)) {
					round++;
				} else {
					return round;
				}
			}
		} else {
			for (int i = 0; i < hitsFromCompetitorB.size(); i++) {
				if (!hitsFromCompetitorB.get(i).equals(Score.EMPTY)) {
					round++;
				} else {
					return round;
				}
			}
		}
		return round;
	}

	/**
	 * Gets the winner of the duel.
	 * 
	 * @return -1 if player of first team, 0 if draw, 1 if player of second
	 *         tiem.
	 */
	public int winner() {
		int pointA = 0;
		int pointB = 0;
		for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
			if (Score.isValidPoint(hitsFromCompetitorA.get(i))) {
				pointA++;
			}
		}
		for (int i = 0; i < hitsFromCompetitorB.size(); i++) {
			if (Score.isValidPoint(hitsFromCompetitorB.get(i))) {
				pointB++;
			}
		}

		if (pointA > pointB) {
			return -1;
		}
		if (pointA < pointB) {
			return 1;
		}
		return 0;
	}

	private int getHits(Score sc) {
		int count = 0;
		for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
			if (hitsFromCompetitorA.get(i).equals(sc)) {
				count++;
			}
		}
		for (int i = 0; i < hitsFromCompetitorB.size(); i++) {
			if (hitsFromCompetitorB.get(i).equals(sc)) {
				count++;
			}
		}
		return count;
	}

	public int getMems() {
		return getHits(Score.MEN);
	}

	public int getKotes() {
		return getHits(Score.KOTE);
	}

	public int getDoes() {
		return getHits(Score.DO);
	}

	public int getTsukis() {
		return getHits(Score.TSUKI);
	}

	public int getHansokus() {
		return getHits(Score.HANSOKU);
	}

	public int getIppones() {
		return getHits(Score.IPPON);
	}

	private int getHits(Score sc, boolean firstTeamPlayer) {
		int count = 0;
		if (firstTeamPlayer) {
			for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
				if (hitsFromCompetitorA.get(i).equals(sc)) {
					count++;
				}
			}
		} else {
			for (int i = 0; i < hitsFromCompetitorB.size(); i++) {
				if (hitsFromCompetitorB.get(i).equals(sc)) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Gets the number of this hits type.
	 * 
	 * @param firstTeamPlayer
	 *            true will choose the player of the first team, false will
	 *            choose the player of the second team.
	 * @return
	 */
	public int getMems(boolean firstTeamPlayer) {
		return getHits(Score.MEN, firstTeamPlayer);
	}

	/**
	 * Gets the number of this hits type.
	 * 
	 * @param firstTeamPlayer
	 *            true will choose the player of the first team, false will
	 *            choose the player of the second team.
	 * @return
	 */
	public int getKotes(boolean firstTeamPlayer) {
		return getHits(Score.KOTE, firstTeamPlayer);
	}

	/**
	 * Gets the number of this hits type.
	 * 
	 * @param firstTeamPlayer
	 *            true will choose the player of the first team, false will
	 *            choose the player of the second team.
	 * @return
	 */
	public int getDoes(boolean firstTeamPlayer) {
		return getHits(Score.DO, firstTeamPlayer);
	}

	/**
	 * Gets the number of this hits type.
	 * 
	 * @param firstTeamPlayer
	 *            true will choose the player of the first team, false will
	 *            choose the player of the second team.
	 * @return
	 */
	public int getTsukis(boolean firstTeamPlayer) {
		return getHits(Score.TSUKI, firstTeamPlayer);
	}

	/**
	 * Gets the number of this hits type.
	 * 
	 * @param firstTeamPlayer
	 *            true will choose the player of the first team, false will
	 *            choose the player of the second team.
	 * @return
	 */
	public int getHansokus(boolean firstTeamPlayer) {
		return getHits(Score.HANSOKU, firstTeamPlayer);
	}

	/**
	 * Gets the number of this hits type.
	 * 
	 * @param firstTeamPlayer
	 *            true will choose the player of the first team, false will
	 *            choose the player of the second team.
	 * @return
	 */
	public int getIppones(boolean firstTeamPlayer) {
		return getHits(Score.IPPON, firstTeamPlayer);
	}

	/**
	 * Gets the number of this hits type.
	 * 
	 * @param firstTeamPlayer
	 *            true will choose the player of the first team, false will
	 *            choose the player of the second team.
	 * @return
	 */
	public void completeIppons(boolean firstTeamPlayer) {
		setResultInRound(0, Score.IPPON, firstTeamPlayer);
		setResultInRound(1, Score.IPPON, firstTeamPlayer);
	}

	@Override
	public String toString() {
		RegisteredPerson memberA = getFight().getTeam1().getMember(order, getFight().getIndex());
		RegisteredPerson memberB = getFight().getTeam2().getMember(order, getFight().getIndex());
		String text = memberA.getShortSurnameName(10) + " (";
		for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
			text += (hitsFromCompetitorA.get(i).getAbbreviature());
		}
		text += (") " + memberB.getShortSurnameName(10) + " (");
		for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
			text += (hitsFromCompetitorB.get(i).getAbbreviature());
		}
		text += ")\n";

		return text;
	}

	@Override
	public int compareTo(Duel o) {
		Integer fightCompare = getFight().compareTo(o.getFight());
		if (fightCompare != 0) {
			return fightCompare;
		}
		return order.compareTo(o.order);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + Objects.hashCode(this.fight);
		hash = 41 * hash + Objects.hashCode(this.order);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Duel other = (Duel) obj;
		if (!Objects.equals(this.fight, other.fight)) {
			return false;
		}
		if (!Objects.equals(this.order, other.order)) {
			return false;
		}
		return true;
	}
}
