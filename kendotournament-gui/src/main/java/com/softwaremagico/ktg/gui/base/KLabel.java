package com.softwaremagico.ktg.gui.base;
/*
 * #%L
 * Kendo Tournament Generator GUI
 * %%
 * Copyright (C) 2008 - 2013 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> Valencia (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.awt.Font;

import javax.swing.JLabel;

import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;

public class KLabel extends JLabel {
	private static final long serialVersionUID = -5622225463794843573L;

	public KLabel() {

	}

	public KLabel(String tag) {
		ITranslator trans = LanguagePool.getTranslator("gui.xml");
		String label = trans.getTranslatedText(tag);
		if (label != null) {
			setText(label);
		} else {
			setText("** error tag '" + tag + "' **");
		}
	}

	public void setBoldFont(boolean bold) {
		Font currentFont = this.getFont();
		setFont(new Font(currentFont.getName(), currentFont.getStyle() | Font.BOLD, currentFont.getSize()));
	}

	public void setFontSize(int fontSize) {
		Font currentFont = this.getFont();
		setFont(new Font(currentFont.getName(), currentFont.getStyle(), fontSize));
	}
}
