
package com.softwaremagico.ktg.lists;


public class SelectTournamentForCompetitorPointList extends ListFromTournamentCreatePDF {
    
    public SelectTournamentForCompetitorPointList() {
        super(true);
        this.setTitle(trans.getTranslatedText("PointListMenuItem"));
    }

    @Override
    protected ParentList getPdfGenerator() {
      return new CompetitorsScoreList(listTournaments.get(TournamentComboBox.getSelectedIndex()));
    }

    @Override
    public String defaultFileName() {
        try {
            return TournamentComboBox.getSelectedItem().toString() + "_CompetitorsPointsList";
        } catch (NullPointerException npe) {
            return null;
        }
    }
    
}
