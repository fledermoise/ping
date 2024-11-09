package org.fledermoise.ping;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.MenuItem;

public class TrayItem {

    private App app;

    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private Font font;

    private MenuItem changeHost, showChart, pause, close;

    public TrayItem(App app) {
        this.app = app;

        systemTray = SystemTray.getSystemTray();
        font = new Font("Arial", 0, 15);
    }

    public void init(String host) throws AWTException {
        Image image = createTrayIconImage(null, null);
        String tooltip = "Ping";
        PopupMenu popupMenu = createPopupMenu(host);

        trayIcon = new TrayIcon(image, tooltip, popupMenu);
        trayIcon.addActionListener(e -> app.showChart());
        systemTray.add(trayIcon);
    }

    private void show(String content, Color color) {
        Image image = createTrayIconImage(content, color);
        trayIcon.setImage(image);
    }

    public void show(String content) {
        show(content, Color.WHITE);
    }

    public void show(int ping) {
        if (ping < 0) {
            show("/", Color.WHITE);
            return;
        }

        String content = Integer.toString(ping);
        Color color = Color.WHITE;

        if (ping >= 0) color = Color.GREEN;
        if (ping > 35) color = Color.YELLOW;
        if (ping > 70) color = Color.RED;

        if (ping >= 0 && ping < 10) content = "0" + content;
        if (ping > 99) content = "99";

        show(content, color);
    }

    private Image createTrayIconImage(String content, Color color) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        if (content == null) return image;

        Graphics2D graphics = image.createGraphics();
        graphics.setFont(font);
        graphics.setColor(color);

        int stringWidth = graphics.getFontMetrics().stringWidth(content);
        int x = (image.getWidth() - stringWidth) / 2;
        graphics.drawString(content, x, 13);

        graphics.dispose();

        return image;
    }

    private PopupMenu createPopupMenu(String host) {
        PopupMenu popupMenu = new PopupMenu();

        changeHost = new MenuItem(host);
        showChart = new MenuItem("Show chart");
        pause = new MenuItem("Pause");
        close = new MenuItem("Close");

        setHostLabel(host);

        changeHost.addActionListener(e -> app.changeHost());
        showChart.addActionListener(e -> app.showChart());
        pause.addActionListener(e -> app.toggleUpdate());
        close.addActionListener(e -> app.close());

        popupMenu.add(changeHost);
        popupMenu.add(showChart);
        popupMenu.add(pause);
        popupMenu.add(close);

        return popupMenu;
    }

    private void addMenuPaddings(int padding) {
        MenuItem[] items = {changeHost, showChart, pause, close};

        int maxLength = 0;
        for (MenuItem item : items) {
            int length = item.getLabel().trim().length();
            if (length > maxLength) maxLength = length;
        }

        for (MenuItem item : items) {
            int paddings = maxLength + padding - item.getLabel().length();
            String label = item.getLabel();
            for (int i = 0; i < paddings; i++) label += " ";
            item.setLabel(label);
        }
    }

    public void setPauseLabel(boolean running) {
        if (running) pause.setLabel("Pause");
        else pause.setLabel("Resume");
    }

    public void setHostLabel(String host) {
        changeHost.setLabel(host);
        addMenuPaddings(5);
    }
}
