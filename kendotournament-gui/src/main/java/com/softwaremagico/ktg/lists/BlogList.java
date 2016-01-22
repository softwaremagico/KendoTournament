package com.softwaremagico.ktg.lists;

/*
 * #%L
 * Kendo Tournament Manager GUI
 * %%
 * Copyright (C) 2008 - 2015 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> Valencia (Spain).
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