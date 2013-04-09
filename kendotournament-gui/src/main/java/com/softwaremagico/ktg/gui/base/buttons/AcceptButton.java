package com.softwaremagico.ktg.gui.base.buttons;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;

public abstract class AcceptButton extends KButton {

    public AcceptButton() {
        setTranslatedText("AcceptButton");
        this.setPreferredSize(new Dimension(80, 40));
        setIcon(new ImageIcon("accept.png"));
        addActionListener(new AcceptListener());
    }

    public abstract void acceptAction();

    class AcceptListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            acceptAction();
        }
    }
}
