package com.softwaremagico.ktg.gui.tournament;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JLabel;

import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.tournament.ITournamentManager;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;

public class TournamentGroupBox extends Group {
	private static final long serialVersionUID = -2303870847314824614L;
	private TGroup tournamentGroup;
	private ITournamentManager tournamentManager;
	private DesignGroupWindow dgw;
	private boolean selected = false;
	private ITranslator trans = null;
	private boolean color = true;
	private MouseAdapter ma;
	private boolean selectable = false;

	public TournamentGroupBox(TGroup tournamentGroup, boolean selectable) {
		this.tournamentGroup = tournamentGroup;
		this.tournamentManager = TournamentManagerFactory.getManager(tournamentGroup.getTournament());
		this.selectable = selectable;
		setLayout(new GridBagLayout());
		setLanguage();
		updateText();
		updateSize();
		setBackground(new Color(230, 230, 230));
		setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
		removeAll();
		label.setHorizontalTextPosition(JLabel.LEFT);
		label.setForeground(Color.BLACK);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.LINE_START;
		add(label, c);
	}

	public void update() {
		updateText();
		updateSize();
	}

	public TGroup getTournamentGroup() {
		return tournamentGroup;
	}

	void onlyShow() {
		this.setToolTipText("");
	}

	void enhance(boolean yes) {
		if (yes) {
			label.setFont(new Font("Tahoma", Font.BOLD, 12));
		} else {
			label.setFont(new Font("Tahoma", Font.PLAIN, 12));
		}
	}

	private void setLanguage() {
		trans = LanguagePool.getTranslator("gui.xml");
		if (tournamentGroup.getLevel() == 0 && selectable) {
			this.setToolTipText(trans.getTranslatedText("ToolTipEditable"));
		} else {
			this.setToolTipText(trans.getTranslatedText("ToolTipNotEditable"));
		}
	}

	final void updateSize() {
		// xSize = 110 * Math.max(2, teams.size());
		xSize = 200;
		ySize = 50 + 13 * Math.max(2, tournamentGroup.getTeams().size());
		setPreferredSize(new Dimension(xSize, ySize));
		setMaximumSize(new Dimension(xSize, ySize));
		setMinimumSize(new Dimension(0, 0));
	}

	private String getText(boolean withRanking) {
		String text = "<html>";

		List<Team> teamRanking;
		if (withRanking) {
			teamRanking = Ranking.getTeamsRanking(tournamentGroup.getFights());
		} else {
			teamRanking = tournamentGroup.getTeams();
		}
		if (teamRanking.isEmpty()) {
			text += "<b>" + getDefaultLabel() + "</b>";
		} else {
			for (int i = 0; i < teamRanking.size(); i++) {
				Color c = obtainWinnerColor(i, true);
				if (color) {
					text += "<b><font color=\"#" + Integer.toHexString(c.getRed())
							+ Integer.toHexString(c.getGreen()) + Integer.toHexString(c.getBlue()) + "\">";
				}
				text += teamRanking.get(i).getShortName();
				if (color) {
					text += "</b></font>";
				}
				if (i < teamRanking.size() - 1) {
					text += "<br>";
				}
			}
		}
		text += "</html>";
		return text;
	}

	public final void updateText() {
		if (!tournamentGroup.areFightsStarted()) {
			label.setText(getText(false));
		} else {
			label.setText(getText(true));
		}
	}

	public final void updateTextOrderByScore() {
		label.setText(getText(true));
	}

	public String getDefaultLabel() {
		// Select label
		String s;
		if (tournamentGroup.getLevel() < tournamentManager.getNumberOfLevels() - 2) {
			s = trans.getTranslatedText("Round") + " "
					+ (tournamentManager.getNumberOfLevels() - tournamentGroup.getLevel());
		} else if (tournamentGroup.getLevel() == tournamentManager.getNumberOfLevels() - 2) {
			s = trans.getTranslatedText("SemiFinalLabel");
		} else {
			s = trans.getTranslatedText("FinalLabel");
		}
		return s;
	}

	public Color obtainWinnerColor(int winner, boolean check) {
		int red, green, blue;
		try {
			if ((!check)
					|| (winner < tournamentGroup.getMaxNumberOfWinners() && winner >= 0 && tournamentGroup
							.areFightsOver(FightPool.getInstance().get(tournamentGroup.getTournament())))) {
				if (winner == 0) {
					red = 220;
					green = 20;
					blue = 20;
				} else {
					red = 0 + (winner) * 37;
					red = red % 221;
					green = 0 + (winner) * 144;
					green = green % 171;
					blue = 0 + (winner) * 239 - winner * 150;
					blue = blue % 245;
				}
				return new Color(red, green, blue);
			}
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
		return new Color(0, 0, 0);
	}

	public void setSelected() {
		if (tournamentGroup.getLevel() == 0) {
			selected = true;
			setBackground(new Color(200, 200, 200));
			if (selectable) {
				setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
			} else {
				setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
			}
		}
	}

	public void setUnselected() {
		selected = false;
		setBackground(new Color(230, 230, 230));
		if (tournamentGroup.getLevel() == 0 && selectable) {
			setBorder(javax.swing.BorderFactory.createRaisedBevelBorder());
		} else {
			setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void activateColor(boolean value) {
		color = value;
	}

	public void openDesignGroupWindow(TournamentDesigner jf) {
		dgw = new DesignGroupWindow(tournamentGroup);
		addDesignGroupListeners(jf);
		dgw.setVisible(true);
	}

	public void removeTeams() {
		tournamentGroup.removeTeams();
		label.setText(getText(false));
		updateSize();
	}

	public Integer getMaxNumberOfWinners() {
		return tournamentGroup.getMaxNumberOfWinners();
	}

	/**
	 * **********************************************
	 * 
	 * LISTENERS
	 * 
	 *********************************************** 
	 */
	/**
	 * Add Listeners
	 * 
	 * @param al
	 */
	public void addMouseClickListener(MouseAdapter e) {
		ma = e;
		addMouseListener(ma);
	}

	public void removeMouseClickListener() {
		removeMouseListener(ma);
	}

	/**
	 * *******************************************************************
	 * 
	 * DESIGN GROUP WINDOW LISTENERS
	 * 
	 ******************************************************************** 
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addDesignGroupListeners(TournamentDesigner jf) {
		dgw.addWindowCloseListener(new closeWindows(jf));
	}

	class closeWindows extends WindowAdapter {

		private TournamentDesigner leagueDesigner;

		closeWindows(TournamentDesigner jf) {
			leagueDesigner = jf;
		}

		@Override
		public void windowClosed(WindowEvent evt) {
			update();
			leagueDesigner.updateInfo();
			leagueDesigner.repaint();
		}
	}
}
