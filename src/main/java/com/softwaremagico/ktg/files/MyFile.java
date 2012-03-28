/*
 * MyFile.java
 *
 *
 * This software is designed by Jorge Hortelano Otero.
 * SoftwareMagico@gmail.com
 * Copyright (C) 2012 Jorge Hortelano Otero.
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
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 *
 * Created on 22 de noviembre de 2007, 15:18
 *
 *
 */
package com.softwaremagico.ktg.files;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import com.softwaremagico.ktg.MessageManager;

/**
 *
 * @author Jorge Hortelano
 */
public class MyFile {

    String nombreFichero;
    private boolean read = false;

    /**
     * Creates a new instance of LecturaFicheros
     */
    public MyFile(String tmp_file) {
        nombreFichero = tmp_file;
    }

    /**
     * Devuelve las lineas de un fichero leido anteriormente.
     */
    public List<String> InLines(boolean verbose) throws IOException {
        String OS = System.getProperty("os.name");
        if (OS.contains("Windows Vista")) {
            return ReadTextFileInLines("ISO8859_1", verbose);
        } else if (OS.contains("Windows")) {
            return ReadTextFileInLines("Cp1252", verbose);
        }
        return ReadTextFileInLines("UTF8", verbose);
    }

    /**
     * Devuelve las lineas de un fichero leido anteriormente.
     */
    public List<String> InLines(boolean verbose, String mode) throws IOException {
        return ReadTextFileInLines(mode, verbose);
    }

    public String InString(boolean verbose) throws IOException {
        String OS = System.getProperty("os.name");
        return ReadTextFile("ISO8859_1", verbose);
        /*
         * if (OS.contains("Windows Vista") || (OS.contains("Windows 7"))) {
         * return ReadTextFile("ISO8859_1", verbose); } else if
         * (OS.contains("Windows")) { return ReadTextFile("Cp1252", verbose); }
         * return ReadTextFile("UTF8", verbose);
         */
    }

    /**
     * Devuelve el fichero leido como una lista de lineas.
     */
    private List<String> ReadTextFileInLines(String mode, boolean verbose) {
        List<String> contents = new ArrayList<String>();

        BufferedReader input = null;
        try {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(new File(nombreFichero)), mode));
            String line = null;
            while ((line = input.readLine()) != null) {
                contents.add(line);
            }
            read = true;
        } catch (FileNotFoundException ex) {
            if (verbose) {
                MessageManager.customMessage("Impossible to read the file: " + nombreFichero, "Error", JOptionPane.ERROR_MESSAGE, false);
            }
            read = false;
        } catch (IOException ex) {
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return contents;
    }

    /**
     * Devuelve el fichero leido como un unico string.
     */
    private String ReadTextFile(boolean verbose) {
        File licence = new File(nombreFichero);
        String text = "Error opening the file:" + nombreFichero;
        try {
            FileInputStream inputData = new FileInputStream(licence);

            byte bt[] = new byte[(int) licence.length()];
            int numBytes = inputData.read(bt);    /*
             * If not this line, the file is no readed propetly.
             */
            text = new String(bt);
            inputData.close();
        } catch (IOException ex) {
            if (verbose) {
                MessageManager.errorMessage(text, "File Error", false);
            }
        }
        return text;
    }

    /**
     * Devuelve el fichero leido como un unico string.
     */
    private String ReadTextFile(String mode, boolean verbose) {
        String text = "";
        List<String> doc = ReadTextFileInLines(mode, verbose);

        for (int i = 0; i < doc.size(); i++) {
            if (!doc.get(i).startsWith("[") && !doc.get(i).startsWith("]") && !doc.get(i).startsWith("<")) {
                text += doc.get(i) + "\n";
            }
        }

        return text;
    }

    public void deleteFile() {
        File f = new File(nombreFichero);
        if (f.exists() && f.canWrite()) {
            f.delete();
        }
    }

    public boolean successInReadingFile() {
        return read;
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the Reader.read(char[]
         * buffer) method. We iterate until the Reader return -1 which means
         * there's no more data to read. We use the StringWriter class to
         * produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    public static boolean fileExist(String path) {
        File f = new File(path);
        if (f.exists()) {
            return true;
        }
        return false;
    }
}
