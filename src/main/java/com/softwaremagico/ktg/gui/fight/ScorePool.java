/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.gui.fight;

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
