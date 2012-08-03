package com.softwaremagico.ktg.gui;
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

import com.softwaremagico.ktg.KendoTournamentGenerator;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author jorge
 */
public abstract class KendoFrame extends javax.swing.JFrame {

    JFileChooser fc;

    /**
     * Generate a window to search in the file system.
     *
     * @param mode The kind of window.
     * @param filter the file filter (sql, pdf, etc.)
     * @see setFileSelectionMode
     */
    public String exploreWindows(String title, int mode, String file, javax.swing.filechooser.FileFilter filter) {
        JFrame frame = null;
        try {
            fc = new JFileChooser(new File(KendoTournamentGenerator.getInstance().getDefaultDirectory() + File.separator));
            fc.setFileFilter(filter);
            fc.setFileSelectionMode(mode);
            if (file.length() == 0 && !title.equals("Load")) {
                fc.setSelectedFile(new File(defaultFileName()));
            } else {
                fc.setSelectedFile(new File(file));
            }
            int fcReturn = fc.showDialog(frame, title);
            if (fcReturn == JFileChooser.APPROVE_OPTION) {
                KendoTournamentGenerator.getInstance().changeDefaultExplorationFolder(fc.getSelectedFile().toString());
                if (fc.getSelectedFile().isDirectory()) {
                    return fc.getSelectedFile().toString()
                            + File.pathSeparator + defaultFileName();
                }
                return fc.getSelectedFile().toString();
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        return "";
    }

    /**
     * Generate a window to search in the file system.
     *
     * @param mode The kind of window.
     * @see setFileSelectionMode
     */
    public String exploreWindowsForPdf(String title, int mode, String file) {
        return exploreWindows(title, mode, file, new PdfFilter());
    }

    /**
     * Generate a window to search in the file system.
     *
     * @param mode The kind of window.
     * @see setFileSelectionMode
     */
    public String exploreWindowsForPdfAndTxt(String title, int mode, String file) {
        return exploreWindows(title, mode, file, new PdfAndTxtFilter());
    }

    /**
     * Generate a window to search in the file system.
     *
     * @param mode The kind of window.
     * @see setFileSelectionMode
     */
    public String exploreWindowsForSql(String title, int mode, String file) {
        return exploreWindows(title, mode, file, new SqlFilter());
    }

    /**
     * Generate a window to search in the file system.
     *
     * @param mode The kind of window.
     * @see setFileSelectionMode
     */
    public String exploreWindowsForPng(String title, int mode, String file) {
        return exploreWindows(title, mode, file, new PngFilter());
    }

    /**
     * Generate a window to search in the file system.
     *
     * @param mode The kind of window.
     * @see setFileSelectionMode
     */
    public String exploreWindowsForKtg(String title, int mode, String file) {
        return exploreWindows(title, mode, file, new KtgFilter());
    }

    /**
     * Generate a window to search in the file system.
     *
     * @param mode The kind of window.
     * @see setFileSelectionMode
     */
    public String exploreWindowsForCsv(String title, int mode, String file) {
        return exploreWindows(title, mode, file, new CsvFilter());
    }

    protected String exploreWindow(String title, int mode) {
        JFrame frame = null;

        //fc = new JFileChooser(new File(KendoTournamentGenerator.getInstance().GetDefaultDirectory() + File.separator));
        fc = new FileChooserPreview(KendoTournamentGenerator.getInstance().getDefaultDirectory());
        fc.setFileSelectionMode(mode);
        int fcReturn = fc.showDialog(frame, title);
        if (fcReturn == JFileChooser.APPROVE_OPTION) {
            KendoTournamentGenerator.getInstance().changeDefaultExplorationFolder(fc.getSelectedFile().toString());
            return fc.getSelectedFile().toString();
        }
        return "";
    }

    public abstract String defaultFileName();

    private class PdfFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            return file.isDirectory() || filename.endsWith(".pdf");
        }

        @Override
        public String getDescription() {
            return "Portable Document Format";
        }
    }

    private class PdfAndTxtFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            return file.isDirectory() || filename.endsWith(".pdf") || filename.endsWith(".txt");
        }

        @Override
        public String getDescription() {
            return "Portable Document Format and text files";
        }
    }

    private class PngFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            return file.isDirectory() || filename.endsWith(".png");
        }

        @Override
        public String getDescription() {
            return "Portable Network Graphics";
        }
    }

    private class SqlFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            return file.isDirectory() || filename.endsWith(".sql");
        }

        @Override
        public String getDescription() {
            return "SQL File";
        }
    }

    private class KtgFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            return file.isDirectory() || filename.endsWith(".ktg");
        }

        @Override
        public String getDescription() {
            return "Specific file of this program";
        }
    }
    
        private class CsvFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            return file.isDirectory() || filename.endsWith(".csv");
        }

        @Override
        public String getDescription() {
            return "Comma-Separated Values";
        }
    }
}
