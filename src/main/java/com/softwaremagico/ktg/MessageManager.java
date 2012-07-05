/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg;

import com.softwaremagico.ktg.language.Translator;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author LOCAL\jhortelano
 */
public class MessageManager {

    private static final int LINE = 50;
    private static final Translator trans = new Translator("messages.xml");

    /**
     * Show an error message translated to the language stored
     *
     * @param code
     * @param title
     * @param language
     */
    public static void errorMessage(String code, String title, String language) {
        customMessage(trans.returnTag(code, language), title, JOptionPane.ERROR_MESSAGE);
        Log.finest(code, title, language);
    }

    public static void translatedMessage(String code, String title, String language, String finalText, int option) {
        String text = trans.returnTag(code, language);
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
    public static void translatedMessage(String code, String title, String language, int option) {
        customMessage(trans.returnTag(code, language), title, option);
    }

    public static void errorMessage(String text, String title) {
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

    public static void informationManager(String code, String title, String language) {
        MessageManager.translatedMessage(code, title, KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void informationManager(String code, String title, String language, String finalText) {
        MessageManager.translatedMessage(code, title, KendoTournamentGenerator.getInstance().language, finalText, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean questionMessage(String code, String title, String language) {
        JFrame frame = null;
        int n = JOptionPane.showConfirmDialog(frame, trans.returnTag(code, language), title, JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            return true;
        } else if (n == JOptionPane.NO_OPTION) {
            return false;
        } else {
            return false;
        }
    }
}
