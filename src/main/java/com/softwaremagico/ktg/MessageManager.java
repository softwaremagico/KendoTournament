/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.softwaremagico.ktg.language.Translator;

/**
 *
 * @author LOCAL\jhortelano
 */
public class MessageManager {

    private static final int LINE = 50;
    private static final Translator trans = new Translator("messages.xml");
    private static boolean logActivated;

    /**
     * Show an error message translated to the language stored
     *
     * @param code
     * @param title
     * @param language
     */
    public static void errorMessage(String code, String title, String language, boolean log) {
        customMessage(trans.returnTag(code, language), title, JOptionPane.ERROR_MESSAGE, log);
    }

    public static void customMessage(String code, String title, String language, String finalText, int option, boolean log) {
        String text = trans.returnTag(code, language);
        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
        }
        customMessage(text.trim() + ": " + finalText.trim(), title, option, log);
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
    public static void customMessage(String code, String title, String language, int option, boolean log) {
        customMessage(trans.returnTag(code, language), title, option, log);
    }

    public static void errorMessage(String text, String title, boolean log) {
        logActivated = log;
        ShowTextMessage(text, title);
        ShowErrorMessage(text, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void customMessage(String text, String title, int option, boolean log) {
        logActivated = log;
        ShowTextMessage(text, title);
        ShowErrorMessage(text, title, option);
    }

    public static void ShowErrorMessage(String text, String title, int option) {
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

    public static void ShowTextMessage(String text, String title) {
        java.util.Date date = new java.util.Date();
        long lnMilisegundos = date.getTime();
        java.sql.Time sqlTime = new java.sql.Time(lnMilisegundos);

        System.out.println(title + ":");
        System.out.println("[" + sqlTime + "] " + text);

        if (logActivated) {
            Log.storeLog(title + ": " + text);
        }
    }

    public static boolean question(String code, String title, String language) {
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
