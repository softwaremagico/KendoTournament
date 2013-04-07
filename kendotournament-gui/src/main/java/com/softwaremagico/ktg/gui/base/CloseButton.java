package com.softwaremagico.ktg.gui.base;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public class CloseButton extends KButton {

    private static final long serialVersionUID = -3656938562430153336L;
    protected JFrame window;

    public CloseButton(JFrame window) {
        setTranslatedText("CloseButton");
        this.window = window;
        this.setPreferredSize(new Dimension(80, 40));
        addActionListener(new CloseListener());
    }

    protected void closeAction() {
        window.dispose();
    }

    class CloseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            closeAction();
        }
    }
}
