package com.softwaremagico.ktg.tools;

import java.text.Normalizer;
import java.text.Normalizer.Form;

public class Tools {

    private Tools() {
    }

    public static boolean isSimilar(String string1, String string2) {
        String newString1 = Normalizer.normalize(string1.toLowerCase().trim(), Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String newString2 = Normalizer.normalize(string2.toLowerCase().trim(), Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return newString1.contains(newString2);
    }

    
}
