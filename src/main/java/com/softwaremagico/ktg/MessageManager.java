package com.softwaremagico.ktg;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author LOCAL\jhortelano
 */
public class MessageManager {

    private static final int LINE = 50;
    private static final Translator trans = LanguagePool.getTranslator("messages.xml");
    private static ImageIcon winnerIcon = new ImageIcon(MessageManager.class.getResource("/cup.png"));

    /**
     * Show an error message translated to the language stored
     *
     * @param code
     * @param title
     * @param language
     */
    public static void errorMessage(String code, String title) {
        customMessage(trans.returnTag(code), title, JOptionPane.ERROR_MESSAGE);
        Log.finest(code, title);
    }

    public static void winnerMessage(String code, String title, String finalText, int option) {
        String text = trans.returnTag(code);
        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
        }
        //customMessage(text.trim() + ": " + finalText.trim(), title, option);

        Log.finest(title + ": " + text);

        JFrame frame = null;
        JOptionPane.showMessageDialog(frame,
                text.trim() + ":\n" + finalText.trim(), title,
                JOptionPane.INFORMATION_MESSAGE, winnerIcon);
    }

    public static void translatedMessage(String code, String title, String finalText, int option) {
        String text = trans.returnTag(code);
        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
        }
        customMessage(text.trim() + ": " + finalText.trim(), title, option);
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
    public static void translatedMessage(String code, String title, int option) {
        customMessage(trans.returnTag(code), title, option);
    }

    public static void basicErrorMessage(String text, String title) {
        Log.finest(text);
        showGraphicMessage(text, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void customMessage(String text, String title, int option) {
        showGraphicMessage(text, title, option);
        Log.finest(title + ": " + text);
    }

    public static void showGraphicMessage(String text, String title, int option) {
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

    public static void informationMessage(String code, String title) {
        MessageManager.translatedMessage(code, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void informationMessage(String code, String title, String finalText) {
        MessageManager.translatedMessage(code, title, finalText, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean questionMessage(String code, String title) {
        JFrame frame = null;
        int n = JOptionPane.showConfirmDialog(frame, trans.returnTag(code), title, JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            return true;
        } else if (n == JOptionPane.NO_OPTION) {
            return false;
        } else {
            return false;
        }
    }
}
