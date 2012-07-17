package com.softwaremagico.ktg.gui.fight;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.files.Path;
import java.io.File;
import java.util.HashMap;

/**
 *
 * @author jhortelano
 */
public class ScorePool {
    private static HashMap<String, File> existingScoreImages = new HashMap<>();
    
    public static File getScoreImage(String scoreImagePath) {
        File score = existingScoreImages.get(scoreImagePath);
        if (score == null) {
            score = getBackground(scoreImagePath);
            existingScoreImages.put(scoreImagePath, score);
        }
        return score;
    }
    
        private static File getBackground(String image) {
        File file = new File(image);
        if (!file.exists()) {
            file = new File(Path.returnImagePath() + image);
            if (!file.exists()) {
            }
        }
        return file;
    }
}
