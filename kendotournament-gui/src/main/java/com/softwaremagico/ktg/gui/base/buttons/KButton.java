package com.softwaremagico.ktg.gui.base.buttons;
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

import javax.swing.Icon;
import javax.swing.JButton;

import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;

public class KButton extends JButton {
	private static final long serialVersionUID = -7971142311588646995L;

	public void setTranslatedText(String tag) {
		ITranslator trans = LanguagePool.getTranslator("gui.xml");
		setText(trans.getTranslatedText(tag));
	}

	// Disable button icons.
	@Override
	public void setIcon(Icon defaultIcon) {
		if (this instanceof DownButton || this instanceof UpButton) {
			super.setIcon(defaultIcon);
		}
	}
}
