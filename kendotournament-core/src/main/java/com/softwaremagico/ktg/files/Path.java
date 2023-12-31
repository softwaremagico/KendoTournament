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
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Path {

	private static final String DIRECTORY_STORE_USER_DATA = ".kendoTournament";
	private static final String CONFIG_FOLDER = "configuration";
	private static final String DATABASE_FOLDER = "databases";
	private static final String LOG_FOLDER = "logs";
	private static final String LANGUAGE_FILE = "language.txt";
	private static final String CONFIG_FILE = "config.txt";
	private static final String CONNECTION_FILE = "connection.txt";

	public static final String TRANSLATIONS_FOLDER = "translations";
	private static String rootPath = null;

	private Path() {
	}

	public static String getRootPath() {
		if (rootPath == null) {
			String path = Path.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			File libPath = new File(path);
			File parent = libPath.getParentFile().getParentFile();
			try {
				// Convert '%20' into space.
				rootPath = URLDecoder.decode(parent.getAbsolutePath() + File.separator, "utf-8");
			} catch (UnsupportedEncodingException e) {
				rootPath = parent.getAbsolutePath() + File.separator;
			}
		}
		return rootPath;
	}

	private static String getRelativePath(String folder) {
		File f = new File(getRootPath() + File.separator + folder + File.separator);
		if (f.exists()) {
			// Installed. Core file is in lib folder. Descent to parent project
			// from child project.
			return f.getPath() + File.separator;
		} else {
			// Not installed. Core is in maven repository. Use command line
			// folder
			File file = new File(".." + File.separator + folder + File.separator);
			return file.getAbsolutePath() + File.separator;
		}
	}

	public static String getImagePath() {
		return getRelativePath("images");
	}

	public static String getIconPath() {
		return getImagePath() + "icons" + File.separator;
	}

	public static String getTranslatorPath() {
		return getRelativePath(TRANSLATIONS_FOLDER);
	}

	public static String getDiplomaPath() {
		return getImagePath() + "diploma" + File.separator + "diploma.png";
	}

	public static String returnDatabaseSchemaPath() {
		return getRelativePath("database");
	}

	public static String getBackgroundPath() {
		return getImagePath() + "background" + File.separator + "background.png";
	}

	public static String getBannerPath() {
		return getImagePath() + "banner" + File.separator + "banner.png";
	}

	public static String getLogoPath() {
		return getImagePath() + "logo" + File.separator + "kendoUV.gif";
	}

	public static String getDefaultPhoto() {
		return getDefaultsImagesFolder() + "defaultPhoto.png";
	}

	public static String getDefaultBanner() {
		return getDefaultsImagesFolder() + "defaultBanner.png";
	}

	public static String getMainPhoto() {
		return getDefaultsImagesFolder() + "mainPhoto.png";
	}

	public static String getDefaultsImagesFolder() {
		return getImagePath() + "defaults" + File.separator;
	}

	public static String getWhiteSquare() {
		return getDefaultsImagesFolder() + "clean.png";
	}

	public static String getManualPath() {
		return getRelativePath("manual");
	}

	public static String getScoreFolder() {
		return getImagePath() + "score" + File.separator;
	}

	public static String getLogFile() {
		return getPathLogFolderInHome() + File.separator + "kendoTournament.log";
	}

	public static String returnIconFolder() {
		return getImagePath() + "icons" + File.separator;
	}

	private static String getPathFolderInHome() {
		String homeFolder = System.getProperty("user.home");
		Folder.makeFolderIfNotExist(homeFolder + File.separator + DIRECTORY_STORE_USER_DATA);
		return homeFolder + File.separator + DIRECTORY_STORE_USER_DATA;
	}

	public static String getPathConfigInHome() {
		Folder.makeFolderIfNotExist(getPathFolderInHome() + File.separator + CONFIG_FOLDER);
		return getPathFolderInHome() + File.separator + CONFIG_FOLDER + File.separator + CONFIG_FILE;
	}

	public static String getPathLanguageConfigFile() {
		Folder.makeFolderIfNotExist(getPathFolderInHome() + File.separator + CONFIG_FOLDER);
		return getPathFolderInHome() + File.separator + CONFIG_FOLDER + File.separator + LANGUAGE_FILE;
	}

	public static String getPathConnectionConfigInHome() {
		Folder.makeFolderIfNotExist(getPathFolderInHome() + File.separator + CONFIG_FOLDER);
		return getPathFolderInHome() + File.separator + CONFIG_FOLDER + File.separator + CONNECTION_FILE;
	}

	public static String getPathDatabaseFolderInHome() {
		Folder.makeFolderIfNotExist(getPathFolderInHome() + File.separator + DATABASE_FOLDER);
		return getPathFolderInHome() + File.separator + DATABASE_FOLDER;
	}

	public static String getPathLogFolderInHome() {
		Folder.makeFolderIfNotExist(getPathFolderInHome() + File.separator + LOG_FOLDER);
		return getPathFolderInHome() + File.separator + LOG_FOLDER;
	}
}
