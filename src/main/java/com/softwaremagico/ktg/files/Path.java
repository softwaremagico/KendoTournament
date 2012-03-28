/*
 *
 This software is designed by Jorge Hortelano Otero.
 softwaremagico@gmail.com
 Copyright (C) 2012 Jorge Hortelano Otero.
 C/Quart 89, 3. Valencia CP:46008 (Spain).
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 Created on agost of 2008.
 */
package com.softwaremagico.ktg.files;

import java.io.File;

/**
 *
 * @author jorge
 */
public class Path {

    private Path() {
    }

    public static String returnRootPath() {
        String soName = System.getProperty("os.name");
        if (soName.contains("Linux") || soName.contains("linux")) {
            File f = new File("/usr/share/kendotournament/");
            if (f.exists()) {
                return f.getPath() + File.separator;
            } else {
                return "";
            }
        } else if (soName.contains("Windows") || soName.contains("windows") || soName.contains("vista") || soName.contains("Vista")) {
            return "";
        }
        return "";
    }

    public static String returnImagePath() {
        return returnRootPath() + "images" + File.separator;
    }

    public static String returnTranslatorPath() {
        return returnRootPath() + "translations" + File.separator;
    }

    public static String returnDiplomaPath() {
        return returnImagePath() + "diploma" + File.separator + "diploma.png";
    }

    public static String returnDatabasePath() {
        return returnRootPath() + "database" + File.separator;
    }

    public static String returnBackgroundPath() {
        return returnImagePath() + "background" + File.separator + "background.png";
    }

    public static String returnBannerPath() {
        return returnImagePath() + "banner" + File.separator + "banner.png";
    }

    public static String returnLogoPath() {
        return returnImagePath() + "logo" + File.separator + "kendoUV.gif";
    }

    public static String returnDefaultPhoto() {
        return returnDefault() + "defaultPhoto.png";
    }

    public static String returnDefaultBanner() {
        return returnDefault() + "defaultBanner.png";
    }

    public static String returnMainPhoto() {
        return returnDefault() + "mainPhoto.png";
    }

    public static String returnDefault() {
        return returnImagePath() + "defaults" + File.separator;
    }

    public static String returnWhiteSquare() {
        return returnDefault() + "clean.png";
    }

    public static String returnManualPath() {
        return returnRootPath() + "manual" + File.separator;
    }

    public static String returnScoreFolder() {
        return returnImagePath() + "score" + File.separator;
    }
    
    public static String returnLogFile(){
        return "kendoTournament.log";
    }
}
