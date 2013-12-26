package com.softwaremagico.ktg.tournament;

public class TournamentScore {

    private ScoreType scoreType;
    private int pointsVictoy = 1;
    private int pointsDraw = 0;

    public TournamentScore(ScoreType scoreType) {
        this.scoreType = scoreType;
    }

    public TournamentScore(ScoreType scoreType, int pointsVictoy, int pointsDraw) {
        this.scoreType = scoreType;
        this.pointsDraw = pointsDraw;
        this.pointsVictoy = pointsVictoy;
    }

    public ScoreType getScoreType() {
        return scoreType;
    }

    public void setScoreType(ScoreType type) {
        this.scoreType = type;
    }

    public float getPointsVictory() {
        return pointsVictoy;
    }

    public void setPointsVictory(int pointsVictoy) {
        this.pointsVictoy = pointsVictoy;
    }

    public float getPointsDraw() {
        return pointsDraw;
    }

    public void setPointsDraw(int pointsDraw) {
        this.pointsDraw = pointsDraw;
    }
}
