package com.softwaremagico.ktg.gui;
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

import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.persistence.DatabaseConnection;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class AlertManager {

    private static final int LINE = 50;
    private static final Translator trans = LanguagePool.getTranslator("messages.xml");
    private static ImageIcon winnerIcon = null;

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

    public static void winnerMessage(String className, String code, String title, String winnerTeam) {
        String text = trans.getTranslatedText(code);
        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
        }
        if (winnerIcon == null) {
            winnerIcon = new ImageIcon(AlertManager.class.getResource("/cup.png"));
        }
        KendoLog.finest(className, text);
        JFrame frame = null;
        JOptionPane.showMessageDialog(frame,
                text.trim() + ":\n" + winnerTeam.trim(), title,
                JOptionPane.INFORMATION_MESSAGE, winnerIcon);
    }

    public static void translatedMessage(String className, String code, String title, String finalText, int option) {
        String text = trans.getTranslatedText(code);
        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
        }
        customMessage(className, text.trim() + ": " + finalText.trim(), title, option);
    }

    /**
     * Show a message (type defined by option) translated to the language
     * stored.
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
