package org.fledermoise.ping;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Update implements Runnable {

    private App app;

    private boolean running = true;
    private String host = null;

    public Update(App app) {
        this.app = app;
    }

    public void setHost(String host) {
        this.host = host;
    }

    private void start() throws IOException {
        running = true;
        Process process = Runtime.getRuntime().exec("ping -t " + host);
        Pattern pattern = Pattern.compile("=([0-9]+)ms");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            if (!running) break;
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                String pingText = matcher.group(0);
                pingText = pingText.substring(1, pingText.length() - 2);

                app.updatePing(Integer.parseInt(pingText));
            } else {
                app.updatePing(-1);
            }
        }

        reader.close();
        process.destroy();
    }

    public void stop() {
        app.updatePing(-1);
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        if (host == null) throw new AssertionError("Host is not set.");
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
