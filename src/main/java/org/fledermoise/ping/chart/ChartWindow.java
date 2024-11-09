package org.fledermoise.ping.chart;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import java.util.ArrayList;

public class ChartWindow extends JFrame implements KeyListener {

    private ArrayList<Integer> pingValues;
    private ChartPanel chartPanel;

    public ChartWindow() {
        super("Ping chart");
        pingValues = new ArrayList<>();

        chartPanel = new ChartPanel(this);
        chartPanel.setPreferredSize(new Dimension(460, 225));

        setContentPane(chartPanel);
        addKeyListener(this);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(false);
    }

    public void display() {
        pack();
        setMinimumSize(getSize());
        setVisible(true);
    }

    public void addPingValue(int ping) {
        if (ping == -1) {
            if (pingValues.size() == 0) return;
            if (pingValues.get(pingValues.size() - 1) == -1) return;
        }

        pingValues.add(ping);
        if (pingValues.size() > 100) {
            pingValues.remove(0);
        }
        chartPanel.repaint();
    }

    public ArrayList<Integer> getPingValues() {
        return pingValues;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_SPACE:
                dispose();
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}

