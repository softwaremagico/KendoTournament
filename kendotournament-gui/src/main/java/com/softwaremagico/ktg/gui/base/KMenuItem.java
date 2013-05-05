package com.softwaremagico.ktg.gui.base;

import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import javax.swing.JMenuItem;

public class KMenuItem extends JMenuItem {

    public KMenuItem(String tag) {
        setTranslatedText(tag);
    }

    public final void setTranslatedText(String tag) {
        Translator trans = LanguagePool.getTranslator("gui.xml");
        String label = trans.getTranslatedText(tag);
        if (label != null) {
            setText(label);
        } else {
            setText("** error tag '" + tag + "' **");
        }
    }
}
