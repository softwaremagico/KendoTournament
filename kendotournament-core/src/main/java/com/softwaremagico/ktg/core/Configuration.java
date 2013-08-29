package com.softwaremagico.ktg.core;

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

import com.softwaremagico.ktg.files.Folder;
import com.softwaremagico.ktg.files.Path;
import java.io.File;
import java.io.IOException;

/**
 * Stores the user configuration in a file.
 */
public class Configuration {

	/**
	 * **********************************************
	 * 
	 * DATABASE
	 * 
	 *********************************************** 
	 */
	/**
	 * Stores into a file the language selected.
	 */
	public static void storeLanguageConfiguration(String language) {
		Path.getPathConfigInHome();
		Folder.saveTextInFile(language, Path.getPathLanguageConfigFile());
	}

	/**
	 * Restore from a file the language selected.
	 */
	public static void readLanguageConfiguration() {
		try {
			String text = Folder.readFileAsText(Path.getPathLanguageConfigFile());
			if (text.length() > 1) {
				KendoTournamentGenerator.getInstance().setLanguage(text);
			}
			if (text.startsWith("Error opening the file")) {
				KendoTournamentGenerator.getInstance().setLanguage("en");
				File f = new File(Path.getPathLanguageConfigFile());
				f.createNewFile();
				storeLanguageConfiguration(KendoTournamentGenerator.getInstance().getLanguage());
			}
		} catch (IOException ex) {
			KendoLog.info(Configuration.class.getName(), "Language file not found");
		}

	}
}
