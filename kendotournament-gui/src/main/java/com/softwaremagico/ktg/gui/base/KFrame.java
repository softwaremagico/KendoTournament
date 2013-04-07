package com.softwaremagico.ktg.gui.base;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public abstract class KFrame extends JFrame {

    private static final String FRAME_TITLE = "Kendo Tournament Generator";
    protected static final int margin = 5;
    protected Integer textDefaultWidth = 80;
    protected Integer textDefaultHeight = 25;
    protected Integer inputDefaultWidth = 160;
    protected Integer inputColumns = 12;
    protected Integer xPadding = 5;
    protected Integer yPadding = 10;

    public KFrame() {
        setTitle(FRAME_TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(this.getClass().getResource("/kendo.png")).getImage());
    }

    protected void defineWindow(Integer width, Integer height) {
        setSize(width, height);
        setMinimumSize(new Dimension(width, height));
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2
                - (int) (this.getWidth() / 2), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()
                / 2 - (int) (this.getHeight() / 2));
    }
    
    public abstract void update();
}
