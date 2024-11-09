package org.fledermoise.ping;

import java.awt.AWTException;
import java.io.IOException;

import org.fledermoise.ping.chart.ChartWindow;

public class App {

    private Config config;
    private ChartWindow chart;
    private TrayItem trayItem;
    private HostInput hostInput;

    private Update update;
    private Thread updateThread;

    public App() throws IOException {
        config = new Config();
        update = new Update(this);
        chart = new ChartWindow();
        trayItem = new TrayItem(this);
        hostInput = new HostInput(this);
    }

    public void run() throws IOException, AWTException {
        trayItem.init(config.get("host"));
        update.setHost(config.get("host"));

        updateThread = new Thread(update);
        updateThread.start();
    }

    public void updatePing(int ping) {
        trayItem.show(ping);
        chart.addPingValue(ping);
    }

    public void changeHost() {
        hostInput.display(config.get("host"));
    }

    public void changeHost(String host) {

        config.set("host", host);
        update.setHost(host);
        trayItem.setHostLabel(host);

        try {
            update.stop();
            updateThread.join();
            updateThread = new Thread(update);
            updateThread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showChart() {
        chart.display();
    }

    public void toggleUpdate() {
        if (update.isRunning()) {
            update.stop();
            trayItem.setPauseLabel(false);
            trayItem.show("--");
        } else {
            updateThread = new Thread(update);
            updateThread.start();
            trayItem.setPauseLabel(true);
        }
    }

    public void close() {
        try {
            update.stop();
            updateThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            App app = new App();
            app.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}