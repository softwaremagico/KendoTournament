package com.softwaremagico.ktg.files;
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

import com.softwaremagico.ktg.log.KendoLog;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Folder {

    public String folderName;
    private List<String> files = new ArrayList<>();

    /**
     * Creates a new instance of Folder
     */
    public Folder(String tmp_directory) throws Exception {
        folderName = tmp_directory;
    }

    public String ReturnFolder() {
        return folderName;
    }

    public static List<String> obtainFolders(String src) throws Exception {
        //Creamos el Objeto File con la URL que queremos desplegar
        File dir = new File(src);
        List<String> lista = new ArrayList<>();
        if (dir.isDirectory()) {
            if (!dir.exists()) {
                throw new Exception("Error: El directorio no existe");
            }

            //tomamos los files contenidos en la URL dada
            String[] archivos = dir.list();
            lista.addAll(Arrays.asList(archivos));
        }
        return lista;
    }

    private static List<String> searchfiles(String directory) throws Exception {
        List<String> filesDisponibles = obtainFolders(directory);
        List<String> listaDatos = new ArrayList<>();
        for (int i = 0; i < filesDisponibles.size(); i++) {
            String dato = filesDisponibles.get(i);
            dato = dato.replaceAll(".txt", "");
            if (!dato.contains("svn")) {
                if (!dato.contains("plantilla")) {
                    listaDatos.add(dato);
                }
            }
        }
        return listaDatos;
    }

    public static List<String> ObtainfilesSubdirectory(String subdirectory) throws Exception {
        return searchfiles(subdirectory);
    }

    public List<String> Disponiblesfiles() {
        return files;
    }

    public static List<String> readFileLines(String filename) throws IOException {
        return MyFile.inLines(filename);

    }

    public static String readFileAsText(String filename) throws FileNotFoundException, IOException {
        return MyFile.inString(filename).trim();
    }

    /**
     * Store text in a file. The text must be written in a String list.
     *
     * @param dataList The text to be written
     * @file the path to the file.
     */
    public static boolean saveListInFile(List<String> dataList, String file) {
        File outputFile;
        byte b[];
        //Se guarda en el filename
        outputFile = new File(file);
        try {
            outputFile.delete();
        } catch (Exception e) {
        }
        try {
            FileOutputStream outputChannel = new FileOutputStream(outputFile);
            for (int i = 0; i < dataList.size(); i++) {
                b = (dataList.get(i).toString() + System.getProperty("line.separator")).getBytes();
                try {
                    outputChannel.write(b);
                } catch (IOException ex) {
                }
            }
            try {
                outputChannel.close();
            } catch (IOException ex) {
                return false;
            }
        } catch (FileNotFoundException ex) {
            String text = "Impossible to generate the file:\n\t" + file
                    + "\nCheck the Folder.\n";
            KendoLog.severe(Folder.class.getName(), text);
            return false;
        }
        return true;
    }

    /**
     * Store text in a file. The text must be written in a String list.
     *
     * @param dataList The text to be written
     * @file the path to the file.
     */
    public static void saveTextInFile(String text, String file) {
        File outputFile;
        byte b[];
        //Se guarda en el filename
        outputFile = new File(file);
        try {
            FileOutputStream outputChannel = new FileOutputStream(outputFile);

            b = text.getBytes();
            try {
                outputChannel.write(b);
            } catch (IOException ex) {
            }

            try {
                outputChannel.close();
            } catch (IOException ex) {
            }
        } catch (FileNotFoundException ex) {
            String msg = "Impossible to generate file:\n\t" + file
                    + ". \nIs the working directory created properly?\n"
                    + "Check into \"Configuration -> Configurate the Computer\"";
            KendoLog.severe(Folder.class.getName(), msg);
        }
    }

    public static void appendTextToFile(String text, String file) {
        FileWriter fw = null;
        try {
            boolean append = true;
            fw = new FileWriter(file, append);
            fw.write(text); //appends the string to the file
        } catch (IOException ex) {
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Get all files in a subfolder with the defined extension.
     *
     * @param folder
     * @param extension if extension is null, will return all files in the
     * folder.
     * @return
     */
    public static List<String> obtainFilesInFolder(String folder, final String extension) {
        File dir = new File(folder);
        List<String> fileNames = new ArrayList<>();

        File[] files;

        if (extension != null) {
            files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(extension);
                }
            });
        } else {
            files = dir.listFiles();
        }

        for (File f : files) {
            fileNames.add(f.getName());
        }

        return fileNames;
    }

    public static void makeFolderIfNotExist(String file) {
        File f = new File(file);
        f.mkdir();
    }
}
