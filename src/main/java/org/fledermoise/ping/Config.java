package org.fledermoise.ping;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Config {

    public static final String CONFIG_DIR = System.getenv("AppData") + "\\Ping";
    public static final String CONFIG_FILE = "config.cfg";
    public static final String DEFAULT_HOST = "store.steampowered.com";

    private File configFile;
    private HashMap<String, String> config;

    public Config() throws IOException {
        File config_dir = new File(CONFIG_DIR);
        configFile = new File(CONFIG_DIR + "\\" + CONFIG_FILE);

        if (!config_dir.exists()) config_dir.mkdirs();
        if (!configFile.exists()) configFile.createNewFile();

        config = new HashMap<String, String>();
        config.put("host", DEFAULT_HOST);
    }

    public void load() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(configFile));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("=");
            if (parts.length != 2) continue;
            config.put(parts[0].trim(), parts[1].trim());
        }

        reader.close();
    }

    public void save() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));

        for (String key : config.keySet()) {
            writer.write(key + "=" + config.get(key));
            writer.newLine();
        }

        writer.flush();
        writer.close();
    }

    public void clear() throws IOException {
        config.clear();
        save();
    }

    public String get(String key) {
        return config.get(key);
    }

    public void set(String key, String value) {
        config.put(key, value);

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
