package com.softwaremagico.ktg.gui.tournament.king;

import java.awt.Toolkit;

import javax.swing.JFrame;

import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;

public class KingOfTheMountainTournament extends JFrame {
	private static final long serialVersionUID = -9216293186060151629L;
	private ITranslator trans = null;

	public KingOfTheMountainTournament() {
		initComponents();
		setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2), (int) Toolkit.getDefaultToolkit()
				.getScreenSize().getHeight()
				/ 2 - (int) (this.getHeight() / 2));
		setLanguage();
	}

	private void initComponents() {

	}

	private void setLanguage() {
		trans = LanguagePool.getTranslator("gui.xml");
		this.setTitle(trans.getTranslatedText("kingOfTheMountainTournament"));
	}

}
