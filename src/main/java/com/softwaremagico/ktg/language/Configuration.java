/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 09-dic-2008.
 */
package com.softwaremagico.ktg.language;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.files.Folder;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jorge
 */
public class Configuration {

    private Folder directory;
    private final String CONFIG_FOLDER = "configuration";
    private final String LANGUAGE_FILE = "language.txt";
    private final String directory_STORE_USER_DATA = "kendoTournament";

    public Configuration() {
        try {
            directory = new Folder(CONFIG_FOLDER);
        } catch (Exception ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /************************************************
     *
     *                    DATABASE
     *
     ************************************************/
    /**
     * Stores into a file the language selected.
     */
    public void storeLanguageConfiguration(String language) {
        getPathConfigInHome();
        directory.SaveTextInFile(language, getPathConfigInHome() + LANGUAGE_FILE);
    }

    /**
     * Restore from a file the language selected.
     */
    public void readLanguageConfiguration() {
        try {
            String text = directory.ReadFileAsText(getPathConfigInHome() + LANGUAGE_FILE, false);
            if (text.length() > 1) {
                KendoTournamentGenerator.getInstance().language = text;
            }
            if (text.startsWith("Error opening the file")) {
                    KendoTournamentGenerator.getInstance().language = "en";
                    File f = new File(getPathConfigInHome() + LANGUAGE_FILE);
                    f.createNewFile();                    
                    storeLanguageConfiguration(KendoTournamentGenerator.getInstance().language);
            }
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void makeFolderIfNotExist(String file) {
        File f = new File(file);
        f.mkdir();
    }

    public String getPathConfigInHome() {
        String config = System.getProperty("user.home");
        String soName = System.getProperty("os.name");
        if (soName.toLowerCase().contains("linux")) {
            makeFolderIfNotExist(config + File.separator + "." + directory_STORE_USER_DATA + File.separator);
            makeFolderIfNotExist(config + File.separator + "." + directory_STORE_USER_DATA + File.separator + CONFIG_FOLDER + File.separator);
            return config + File.separator + "." + directory_STORE_USER_DATA + File.separator + CONFIG_FOLDER + File.separator;
        } else if (soName.toLowerCase().contains("windows") || soName.toLowerCase().contains("vista")) {
            makeFolderIfNotExist(config + File.separator + directory_STORE_USER_DATA + File.separator);
            makeFolderIfNotExist(config + File.separator + directory_STORE_USER_DATA + File.separator + CONFIG_FOLDER + File.separator);
            return config + File.separator + directory_STORE_USER_DATA + File.separator + CONFIG_FOLDER + File.separator;
        }
        return config + File.separator + directory_STORE_USER_DATA + File.separator + CONFIG_FOLDER + File.separator;
    }
}
