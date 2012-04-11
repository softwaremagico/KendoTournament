/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.championship;

import java.awt.Font;
import javax.swing.JLabel;

/**
 *
 * @author Jorge
 */
public abstract class Group extends javax.swing.JPanel {

    int xSize;
    int ySize;
    JLabel label = new JLabel();

    Group() {
    }

    public final void updateText(String text) {
        label.setText(formatText(text));
    }

    void updateFont(String font, int size) {
        label.setFont(new Font(font, Font.BOLD, size));
    }

    String formatText(String text) {
        String tag = "<html><b><font size=\"+1\" color=\"#000000\">";
        text = tag + text;
        text += "</font></b></html>";
        return text;
    }
}
