package com.softwaremagico.ktg.lists;

/*
 * #%L
 * Kendo Tournament Manager GUI
 * %%
 * Copyright (C) 2008 - 2013 Softwaremagico
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

import java.sql.SQLException;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.tournament.ScoreOfCompetitor;

public class CompetitorsScoreList extends ParentList {
	private List<ScoreOfCompetitor> competitorTopTen;
	private Tournament tournament;

	public CompetitorsScoreList(Tournament tournament) {
		this.tournament = tournament;
		try {
			if (tournament == null) { // null == all.
				competitorTopTen = Ranking.getCompetitorsScoreRanking(FightPool.getInstance().getAll());
			} else {
				competitorTopTen = Ranking.getCompetitorsScoreRanking(FightPool.getInstance().get(tournament));
			}
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
		trans = LanguagePool.getTranslator("gui.xml");
	}

	@Override
	public void createHeaderRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
			String font, int fontSize) {
		PdfPCell cell;

		// Tournament name.
		if (tournament != null) {
			Paragraph p1 = new Paragraph(tournament.getName(), FontFactory.getFont(font, fontSize + 15, Font.BOLD));
			cell = new PdfPCell(p1);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		} else {
			// List name.
			Paragraph p2 = new Paragraph(trans.getTranslatedText("GeneralClassification"), FontFactory.getFont(font,
					fontSize + 15, Font.BOLD));
			cell = new PdfPCell(p2);
		}

		cell.setColspan(getTableWidths().length);
		cell.setBorderWidth(headerBorder);
		// cell.setBackgroundColor(new Color(255, 255, 255));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTable.addCell(cell);

	}

	@Override
	public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
			String font, int fontSize) throws EmptyPdfBodyException {

		mainTable.addCell(getCell(trans.getTranslatedText("CompetitorMenu"), 0, Element.ALIGN_CENTER));
		// mainTable.addCell(getCell(trans.getTranslatedText("fightsWon"), 0, Element.ALIGN_CENTER));
		mainTable.addCell(getCell(trans.getTranslatedText("duelsWon"), 0, Element.ALIGN_CENTER));
		mainTable.addCell(getCell(trans.getTranslatedText("histsWon"), 0, Element.ALIGN_CENTER));
		mainTable.addCell(getCell(trans.getTranslatedText("Fights"), 0, Element.ALIGN_CENTER));

		for (int i = 0; i < competitorTopTen.size(); i++) {
			mainTable.addCell(getCell(competitorTopTen.get(i).getCompetitor().getSurnameName(), 1));
			/*
			 * mainTable.addCell(getCell(competitorTopTen.get(i).getWonFights() + "/" +
			 * competitorTopTen.get(i).getDrawFights(), 1, Element.ALIGN_CENTER));
			 */
			mainTable.addCell(getCell(competitorTopTen.get(i).getDuelsWon() + "/"
					+ competitorTopTen.get(i).getDuelsDraw(), 1, Element.ALIGN_CENTER));
			mainTable.addCell(getCell("" + competitorTopTen.get(i).getHits(), 1, Element.ALIGN_CENTER));

			mainTable.addCell(getCell("" + competitorTopTen.get(i).getDuelsDone(), 1, Element.ALIGN_CENTER));
		}
	}

	@Override
	public void createFooterRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
			String font, int fontSize) {
		mainTable.addCell(getEmptyRow());
	}

	@Override
	public float[] getTableWidths() {
		float[] widths = { 0.50f, 0.20f, 0.20f, 0.20f };
		return widths;
	}

	@Override
	public void setTablePropierties(PdfPTable mainTable) {
		mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	}

	@Override
	protected Rectangle getPageSize() {
		return PageSize.A4;
	}

	@Override
	protected String fileCreatedOkTag() {
		return "scoreListOK";
	}

	@Override
	protected String fileCreatedBadTag() {
		return "scoreListBad";
	}
}
