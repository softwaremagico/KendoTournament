package com.softwaremagico.ktg.gui.base;

import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import javax.swing.JButton;

public class KButton extends JButton {

    public void setTranslatedText(String tag) {
        Translator trans = LanguagePool.getTranslator("gui.xml");
        setText(trans.returnTag(tag));
    }
}
