package com.softwaremagico.ktg.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

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

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.DatabaseConnection;
import com.softwaremagico.ktg.persistence.FightPool;

public class AlertManager {

	private static final int LINE = 50;
	private static final ITranslator trans = LanguagePool.getTranslator("messages.xml");
	private static ImageIcon winnerIcon = null;
	private static ImageIcon clockIcon = null;
	private static JDialog waitingNetworkDialog;

	/**
	 * Show an error message translated to the language stored
	 * 
	 * @param code
	 * @param title
	 * @param language
	 */
	public static void errorMessage(String className, String code, String title) {
		customMessage(className, trans.getTranslatedText(code), title, JOptionPane.ERROR_MESSAGE);
	}

	public static void errorMessage(String className, String code, String title, String finalText) {
		String text = trans.getTranslatedText(code);
		if (text.endsWith(".")) {
			text = text.substring(0, text.length() - 1);
		}
		customMessage(className, text + ": " + finalText, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void customIconMessage(String className, ImageIcon icon, String text, String title) {
		KendoLog.finest(className, text);
		JOptionPane.showMessageDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE, icon);
	}

	public static JDialog winnerMessage(String className, String code, String title, String winnerTeam) {
		String text = trans.getTranslatedText(code);
		if (text.endsWith(".")) {
			text = text.substring(0, text.length() - 1);
		}
		if (winnerIcon == null) {
			winnerIcon = new ImageIcon(AlertManager.class.getResource("/cup.png"));
		}
		return createWinnerMessage(className, text.trim() + ":\n" + winnerTeam.trim());
		// customIconMessage(className, winnerIcon, text.trim() + ":\n" + winnerTeam.trim(), title);
	}

	private static JDialog createWinnerMessage(String className, String text) {
		KendoLog.finest(className, text);
		if (winnerIcon == null) {
			winnerIcon = new ImageIcon(AlertManager.class.getResource("/cup.png"));
		}

		final JOptionPane winnerMessage = new JOptionPane(text, JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.DEFAULT_OPTION, winnerIcon);

		final JDialog winnerDialog = new JDialog(new JFrame(), "!!!!", false);

		winnerMessage.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();

				if (winnerDialog.isVisible() && (e.getSource() == winnerMessage)
						&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {
					winnerDialog.dispose();
				}
			}
		});

		int width = 580;
		int height = 155;
		winnerDialog.setSize(width, height);
		winnerDialog.setMinimumSize(new Dimension(width, height));
		winnerDialog.setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (width / 2),
				(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (height / 2));

		winnerDialog.setAlwaysOnTop(true);
		winnerDialog.requestFocus();
		winnerDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		winnerDialog.setContentPane(winnerMessage);

		return winnerDialog;
	}

	public static JDialog createWaitingArenasMessage(Tournament tournament, int fightArea) throws SQLException {
		// wait for other arena fights. Show message.
		String arenas = "";
		for (int i = 0; i < tournament.getFightingAreas(); i++) {
			// Obtain not fimished arenas.
			if (fightArea != i && !FightPool.getInstance().areAllOver(tournament, i)) {
				arenas += Tournament.getFightAreaName(i) + " ";
			}
		}
		// Prepare message
		arenas = arenas.trim().replace(" ", ", ");

		final JOptionPane optionPane = new JOptionPane(LanguagePool.getTranslator("messages.xml").getTranslatedText(
				"waitingArena")
				+ ": " + arenas, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);

		final JDialog waitingArenaDialog = new JDialog(new JFrame(), "Waiting", false);

		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();

				if (waitingArenaDialog.isVisible() && (e.getSource() == optionPane)
						&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {
					waitingArenaDialog.setVisible(false);
				}
			}
		});

		waitingArenaDialog.setAlwaysOnTop(true);
		waitingArenaDialog.requestFocus();
		waitingArenaDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		int width = 450;
		int height = 135;
		waitingArenaDialog.setSize(width, height);
		waitingArenaDialog.setMinimumSize(new Dimension(width, height));
		waitingArenaDialog.setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2
				- (int) (width / 2), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2
				- (int) (height / 2));

		waitingArenaDialog.setContentPane(optionPane);

		return waitingArenaDialog;
	}

	public static JDialog createWaitingNetworkMessage() {
		if (waitingNetworkDialog == null) {
			final JOptionPane optionPane = createWaitingDatabaseMessage();
			optionPane.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					String prop = e.getPropertyName();

					if (waitingNetworkDialog.isVisible() && (e.getSource() == optionPane)
							&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {
						waitingNetworkDialog.setVisible(false);
					}
				}
			});

			waitingNetworkDialog = new JDialog(new JFrame(), "tic tac", false);
			waitingNetworkDialog.setAlwaysOnTop(true);
			waitingNetworkDialog.requestFocus();
			waitingNetworkDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

			int width = 600;
			int height = 155;
			waitingNetworkDialog.setSize(width, height);
			waitingNetworkDialog.setMinimumSize(new Dimension(width, height));
			waitingNetworkDialog.setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2
					- (int) (width / 2), 0);

			waitingNetworkDialog.setContentPane(optionPane);
		}
		return waitingNetworkDialog;
	}

	private static JOptionPane createWaitingDatabaseMessage() {
		if (clockIcon == null) {
			clockIcon = new ImageIcon(AlertManager.class.getResource("/waiting.png"));
		}

		JOptionPane waitingMessage = new JOptionPane(trans.getTranslatedText("waitingConnection"),
				JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, clockIcon);

		return waitingMessage;
	}

	public static void translatedMessage(String className, String code, String title, String finalText, int option) {
		String text = trans.getTranslatedText(code);
		if (text.endsWith(".")) {
			text = text.substring(0, text.length() - 1);
		}
		customMessage(className, text.trim() + ": " + finalText.trim(), title, option);
	}

	/**
	 * Show a message (type defined by option) translated to the language stored.
	 * 
	 * @param code
	 * @param title
	 * @param language
	 * @param option
	 */
	public static void translatedMessage(String className, String code, String title, int option) {
		customMessage(className, trans.getTranslatedText(code), title, option);
	}

	public static void basicErrorMessage(String className, String text, String title) {
		KendoLog.severe(className, text);
		showGraphicMessage(text, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void errorMessage(String className, Throwable throwable) {
		String error = getStackTrace(throwable);
		basicErrorMessage(className, error, "Error");
	}

	public static void customMessage(String className, String text, String title, int option) {
		showGraphicMessage(text, title, option);
		KendoLog.finest(className, text);
	}

	private static void showGraphicMessage(String text, String title, int option) {
		int i = 0, caracteres = 0;
		try {
			String texto[] = text.split(" ");
			text = "";
			while (i < texto.length) {
				text += texto[i] + " ";
				caracteres += texto[i].length();
				if (caracteres > LINE) {
					text = text.trim() + "\n";
					caracteres = 0;
				}
				i++;
			}

			JFrame frame = null;

			text = text.replaceAll("\t", "  ");

			JOptionPane.showMessageDialog(frame, text, title, option);
		} catch (NullPointerException npe) {
		}
	}

	public static void informationMessage(String className, String code, String title) {
		AlertManager.translatedMessage(className, code, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void informationMessage(String className, String code, String title, String finalText) {
		AlertManager.translatedMessage(className, code, title, finalText, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void warningMessage(String className, String code, String title) {
		AlertManager.translatedMessage(className, code, title, JOptionPane.WARNING_MESSAGE);
	}

	public static void warningMessage(String className, String code, String title, String finalText) {
		AlertManager.translatedMessage(className, code, title, finalText, JOptionPane.WARNING_MESSAGE);
	}

	public static boolean questionMessage(String code, String title) {
		JFrame frame = null;
		int n = JOptionPane.showConfirmDialog(frame, trans.getTranslatedText(code), title, JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			return true;
		} else if (n == JOptionPane.NO_OPTION) {
			return false;
		} else {
			return false;
		}
	}

	private static String getStackTrace(Throwable throwable) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		throwable.printStackTrace(printWriter);
		return writer.toString();
	}

	/**
	 * If debug is activated, show information about the error.
	 */
	public static void showErrorInformation(String className, Exception ex) {
		if (KendoTournamentGenerator.isDebugOptionSelected()) {
			errorMessage(className, ex);
		}
	}

	public static void showSqlErrorMessage(SQLException exception) {
		String errorText = DatabaseConnection.getConnection().getDatabase().getSqlErrorMessage(exception);
		showGraphicMessage(errorText, "Database error", JOptionPane.ERROR_MESSAGE);
	}
}
