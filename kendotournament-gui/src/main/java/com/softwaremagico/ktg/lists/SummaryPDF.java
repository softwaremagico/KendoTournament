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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.tournament.ITournamentManager;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;

public class SummaryPDF extends ParentList {

	private Tournament tournament;
	private final int border = 0;
	private int useOnlyShiaijo = -1;
	protected boolean showNotFinishedFights = true; // If true, only show not finished fights, if false only show
	// finished fights.
	protected boolean showAll = true; // If true, show finished and not finished fights;
	protected boolean divideByShiaijo = false; // If true, each shiaijo in different page.
	private ITournamentManager tournamentManager;

	public SummaryPDF(Tournament championship, int shiaijo) {
		tournament = championship;
		useOnlyShiaijo = shiaijo;
		this.tournamentManager = TournamentManagerFactory.getManager(championship);
	}

	protected String getDrawFight(Fight f, int duel) {
		// Draw Fights
		String draw;
		if (f.getDuels().get(duel).winner() == 0 && f.isOver()) {
			draw = "" + Score.DRAW.getAbbreviation();
		} else {
			draw = "" + Score.EMPTY.getAbbreviation();
		}
		return draw;
	}

	protected String getFaults(Fight f, int duel, boolean leftTeam) {
		String faultSimbol;
		boolean faults;
		if (leftTeam) {
			faults = f.getDuels().get(duel).getFaults(true);
		} else {
			faults = f.getDuels().get(duel).getFaults(false);
		}
		if (faults) {
			faultSimbol = "" + Score.FAULT.getAbbreviation();
		} else {
			faultSimbol = "" + Score.EMPTY.getAbbreviation();
		}
		return faultSimbol;
	}

	protected String getScore(Fight f, int duel, int score, boolean leftTeam) {
		if (leftTeam) {
			return f.getDuels().get(duel).getHits(true).get(score).getAbbreviation() + "";
		} else {
			return f.getDuels().get(duel).getHits(false).get(score).getAbbreviation() + "";
		}
	}

	private PdfPTable fightTable(Fight f, boolean first) throws DocumentException {
		PdfPTable Table;
		Table = new PdfPTable(getTableWidths());

		if (!first) {
			Table.addCell(getEmptyRow());
		}

		// Team1
		Table.addCell(getHeader3(f.getTeam1().getName(), 0, 4));

		// Separation Draw Fights
		Table.addCell(getEmptyCell());

		// Team2
		Table.addCell(getHeader3(f.getTeam2().getName(), 0, 4));

		for (int i = 0; i < f.getTeam1().getNumberOfMembers(f.getIndex()); i++) {
			// Team 1
			RegisteredPerson competitor = f.getTeam1().getMember(i, f.getIndex());
			String name = "";
			if (competitor != null) {
				name = competitor.getSurnameNameIni();
			}
			Table.addCell(getCell(name, 1, 1, Element.ALIGN_LEFT));

			// Faults
			Table.addCell(getCell(getFaults(f, i, true), 1, 1, Element.ALIGN_CENTER));

			// Points
			Table.addCell(getCell(getScore(f, i, 1, true), 1, 1, Element.ALIGN_CENTER));
			Table.addCell(getCell(getScore(f, i, 0, true), 1, 1, Element.ALIGN_CENTER));

			Table.addCell(getCell(getDrawFight(f, i), 1, Element.ALIGN_CENTER));

			// Points Team 2
			Table.addCell(getCell(getScore(f, i, 0, false), 1, 1, Element.ALIGN_CENTER));
			Table.addCell(getCell(getScore(f, i, 1, false), 1, 1, Element.ALIGN_CENTER));

			// Faults
			Table.addCell(getCell(getFaults(f, i, false), 1, 1, Element.ALIGN_CENTER));

			// Team 2
			competitor = f.getTeam2().getMember(i, f.getIndex());
			name = "";
			if (competitor != null) {
				name = competitor.getSurnameNameIni();
			}
			Table.addCell(getCell(name, 1, 1, Element.ALIGN_RIGHT));
		}
		Table.addCell(getEmptyRow());

		return Table;
	}

	@Override
	public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
			String font, int fontSize) throws EmptyPdfBodyException {
		PdfPCell cell;
		boolean first = true;
		// boolean added = false;
		// int lastLevel = -1;
		//
		// List<Fight> fights = new ArrayList<>();
		// try {
		// if (useOnlyShiaijo < 0) {
		// fights = FightPool.getInstance().get(tournament);
		// } else {
		// fights = FightPool.getInstance().get(tournament, useOnlyShiaijo);
		// }
		// } catch (SQLException ex) {
		// AlertManager.showSqlErrorMessage(ex);
		// }
		//
		// for (int i = 0; i < fights.size(); i++) {
		// if ((showAll) || (fights.get(i).isOver() == !showNotFinishedFights)) {
		// /*
		// * Header of the phase
		// */
		// if (lastLevel != fights.get(i).getLevel() && !tournament.getType().equals(TournamentType.LEAGUE)) {
		// mainTable.addCell(getEmptyRow());
		//
		// if (fights.get(i).getLevel() < TournamentManagerFactory.getManager(tournament).getNumberOfLevels() - 2) {
		// mainTable.addCell(getHeader1(trans.getTranslatedText("Round") + " " + (fights.get(i).getLevel() + 1), 0,
		// Element.ALIGN_LEFT));
		// } else if (fights.get(i).getLevel() == TournamentManagerFactory.getManager(tournament).getNumberOfLevels() -
		// 2) {
		// mainTable.addCell(getHeader1(trans.getTranslatedText("SemiFinalLabel"), 0, Element.ALIGN_LEFT));
		// } else {
		// mainTable.addCell(getHeader1(trans.getTranslatedText("FinalLabel"), 0, Element.ALIGN_LEFT));
		// }
		//
		// // mainTable.addCell(getHeader1(trans.getTranslatedText("Round") + " " + (fights.get(i).getLevel() +
		// // 1) + ":", 0, Element.ALIGN_LEFT));
		//
		// lastLevel = fights.get(i).getLevel();
		// }
		//
		// try {
		// cell = new PdfPCell(fightTable(fights.get(i), first));
		// } catch (DocumentException ex) {
		// cell = new PdfPCell();
		// AlertManager.showErrorInformation(this.getClass().getName(), ex);
		// }
		// cell.setBorderWidth(border);
		// cell.setColspan(getTableWidths().length);
		// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		// mainTable.addCell(cell);
		//
		// first = false;
		// added = true;
		// }
		// }
		// if (!added) {
		// throw new EmptyPdfBodyException("No fights selected");
		// }

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
									+ trans.getTranslatedText("FightAreaNoDots") + " "
									+ Tournament.getFightAreaName(groups.get(i).getFightArea()) + ")", 0));

					for (int j = 0; j < fights.size(); j++) {
						if (groups.get(i).isFightOfGroup(fights.get(j))) {

							try {
								cell = new PdfPCell(fightTable(fights.get(i), first));
							} catch (DocumentException ex) {
								cell = new PdfPCell();
								AlertManager.showErrorInformation(this.getClass().getName(), ex);
							}
							cell.setBorderWidth(border);
							cell.setColspan(getTableWidths().length);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							mainTable.addCell(cell);
						}
					}
				}
			}
		}
	}

	@Override
	public float[] getTableWidths() {
		float[] widths = { 0.29f, 0.03f, 0.08f, 0.08f, 0.04f, 0.08f, 0.08f, 0.03f, 0.29f };
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

		String header = tournament.getName();
		if (useOnlyShiaijo >= 0) {
			header += " (" + trans.getTranslatedText("FightAreaNoDots") + " "
					+ Tournament.getFightAreaName(useOnlyShiaijo) + ")";
		}
		p = new Paragraph(header, FontFactory.getFont(font, fontSize + 15, Font.BOLD));
		cell = new PdfPCell(p);
		cell.setColspan(getTableWidths().length);
		cell.setBorderWidth(headerBorder);
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
		return "SummaryListOK";
	}

	@Override
	protected String fileCreatedBadTag() {
		return "SummaryListBad";
	}
}
