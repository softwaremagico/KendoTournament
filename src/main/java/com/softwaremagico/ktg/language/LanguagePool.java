/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.language;

import java.util.HashMap;

/**
 *
 * @author jhortelano
 */
public class LanguagePool {

    private static HashMap<String, Translator> existingTranslators = new HashMap<>();

    private LanguagePool() {
    }

    public static Translator getTranslator(String xmlFile) {
        System.out.println(existingTranslators.size());
        Translator translator = existingTranslators.get(xmlFile);
        if (translator == null) {
            translator = new Translator(xmlFile);
            existingTranslators.put(xmlFile, translator);
        }
        return translator;
    }
}
