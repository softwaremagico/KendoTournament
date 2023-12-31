package com.softwaremagico.ktg.lists;

/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
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
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.tournament.ITournamentManager;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FightListPDF extends ParentList {

	private final static Integer BORDER_WIDTH = 0;
	private Tournament tournament;
	private ITournamentManager tournamentManager;

	public FightListPDF(Tournament tournament) {
		this.tournament = tournament;
		this.tournamentManager = TournamentManagerFactory.getManager(tournament);
		trans = LanguagePool.getTranslator("gui.xml");
	}

	public PdfPTable fightTable(Fight f, String font, int fontSize) {
		float[] widths = getTableWidths();
		PdfPTable fightTable = new PdfPTable(widths);
		fightTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

		fightTable.addCell(getHeader3(f.getTeam1().getName() + " Vs " + f.getTeam2().getName(), 0));

		for (int i = 0; i < f.getTournament().getTeamSize(); i++) {
			RegisteredPerson competitor = f.getTeam1().getMember(i, f.getIndex());
			String name = "";
			if (competitor != null) {
				name = competitor.getSurnameNameIni();
			}
			fightTable.addCell(getCell(name, 1, Element.ALIGN_LEFT));
			fightTable.addCell(getEmptyCell());
			competitor = f.getTeam2().getMember(i, f.getIndex());
			name = "";
			if (competitor != null) {
				name = competitor.getSurnameNameIni();
			}
			fightTable.addCell(getCell(name, 1, Element.ALIGN_RIGHT));
		}

		return fightTable;
	}

	private PdfPTable simpleTable(PdfPTable mainTable) {
		PdfPCell cell;
		mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

		for (int i = 0; i < tournament.getFightingAreas(); i++) {
			List<Fight> fights = new ArrayList<>();
			try {
				fights = FightPool.getInstance().get(tournament, i);
			} catch (SQLException ex) {
				AlertManager.showSqlErrorMessage(ex);
			}
			mainTable.addCell(getEmptyRow());
			mainTable.addCell(getEmptyRow());
			mainTable.addCell(getHeader2(
					trans.getTranslatedText("FightAreaNoDots") + " " + Tournament.getFightAreaName(i), 0));

                    for (Fight fight : fights) {
                        cell = new PdfPCell(fightTable(fight, font, fontSize));
                        cell.setBorderWidth(BORDER_WIDTH);
                        cell.setColspan(3);
                        // if (fights.get(j).isOver()) {
                        // cell.setBackgroundColor(new com.itextpdf.text.BaseColor(200,
                        // 200, 200));
                        // } else {
                        cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 255, 255));
                        // }
                        // cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				mainTable.addCell(cell);
                    }
		}

		return mainTable;
	}

	private PdfPTable championshipTable(PdfPTable mainTable) {
		PdfPCell cell;

		for (int l = 0; l < tournamentManager.getNumberOfLevels(); l++) {
			if (tournamentManager.getLevel(l).hasFightsAssigned()) {
				/*
				 * Header of the phase
				 */
				mainTable.addCell(getEmptyRow());
				mainTable.addCell(getEmptyRow());

				if (l < TournamentManagerFactory.getManager(tournament).getNumberOfLevels() - 2) {
					mainTable.addCell(getHeader1(trans.getTranslatedText("Round") + " " + (l + 1), 0,
							Element.ALIGN_LEFT));
				} else if (l == TournamentManagerFactory.getManager(tournament).getNumberOfLevels() - 2) {
					mainTable.addCell(getHeader1(trans.getTranslatedText("SemiFinalLabel"), 0, Element.ALIGN_LEFT));
				} else {
					mainTable.addCell(getHeader1(trans.getTranslatedText("FinalLabel"), 0, Element.ALIGN_LEFT));
				}

				// mainTable.addCell(getHeader1(trans.getTranslatedText("Round") + " " + (l + 1) + ":", 0,
				// Element.ALIGN_LEFT));

				List<TGroup> groups = tournamentManager.getGroups(l);

				List<Fight> fights = new ArrayList<>();
				try {
					fights = FightPool.getInstance().get(tournament);
				} catch (SQLException ex) {
					AlertManager.showSqlErrorMessage(ex);
				}
				for (int i = 0; i < groups.size(); i++) {
					mainTable.addCell(getEmptyRow());
					mainTable.addCell(getHeader2(
							trans.getTranslatedText("GroupString") + " " + (i + 1) + " ("
									+ trans.getTranslatedText("FightArea") + " "
									+ Tournament.getFightAreaName(groups.get(i).getFightArea()) + ")", 0));

                                    for (Fight fight : fights) {
                                        if (groups.get(i).isFightOfGroup(fight)) {
                                            cell = new PdfPCell(fightTable(fight, font, fontSize));
                                            cell.setBorderWidth(BORDER_WIDTH);
                                            cell.setColspan(3);
                                            // if (FightPool.getManager(tournament).get(j).isOver())
                                            // {
                                            // cell.setBackgroundColor(new
                                            // com.itextpdf.text.BaseColor(200, 200, 200));
                                            // } else {
                                            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 255, 255));
                                            // }
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                            mainTable.addCell(cell);
                                        }
                                    }
				}
			}
		}
		return mainTable;
	}

	@Override
	public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
			String font, int fontSize) {
		if (tournament.getType().equals(TournamentType.LEAGUE)) {
			simpleTable(mainTable);
		} else {
			championshipTable(mainTable);
		}
	}

	@Override
	public float[] getTableWidths() {
		float[] widths = { 0.40f, 0.10f, 0.40f };
		return widths;
	}

	@Override
	public void setTablePropierties(PdfPTable mainTable) {
		mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	}

	@Override
	public void createHeaderRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
			String font, int fontSize) {
		PdfPCell cell;
		Paragraph p;
		p = new Paragraph(tournament.getName(), FontFactory.getFont(font, fontSize + 16, Font.BOLD));
		cell = new PdfPCell(p);
		cell.setBorderWidth(headerBorder);
		cell.setColspan(getTableWidths().length);
		cell.setMinimumHeight(50);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTable.addCell(cell);
	}

	@Override
	public void createFooterRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
			String font, int fontSize) {
		mainTable.addCell(getEmptyRow());
	}

	@Override
	protected Rectangle getPageSize() {
		return PageSize.A4;
	}

	@Override
	protected String fileCreatedOkTag() {
		return "fightsListOK";
	}

	@Override
	protected String fileCreatedBadTag() {
		return "fightsListBad";
	}
}
