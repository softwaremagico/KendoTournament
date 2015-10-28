package com.softwaremagico.ktg.lists;

import com.softwaremagico.ktg.core.Tournament;

public class BlogList extends ListFromTournamentCreateTxt {

    public BlogList() {
        super(false);
        this.setTitle(trans.getTranslatedText("titleBlogStatistics"));
    }

    @Override
    public String defaultFileName() {
        try {
            return TournamentComboBox.getSelectedItem().toString() + "_blog.txt";
        } catch (NullPointerException npe) {
            return null;
        }
    }

    @Override
    protected String getTxtGenerator() {
        Tournament tournament = listTournaments.get(TournamentComboBox.getSelectedIndex());
        return BlogExporter.getWordpressFormat(tournament);
    }

}
