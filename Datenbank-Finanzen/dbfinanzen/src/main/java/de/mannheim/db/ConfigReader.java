package de.mannheim.db;

import java.util.Properties;
import java.io.*;

public class ConfigReader {
    File file;
    String url = "db.config";
    private Properties properties;

    public ConfigReader() {
        file = new File(url);
        properties = new Properties();
        if (!file.exists()) {
            createDefaultConfig(file);
        }
        if (readConfig().equals("null")) {
            System.out.println("in der Config Datei steht keine gültige Url");

        }

    }

    private void createDefaultConfig(File file) {
        try (OutputStream output = new FileOutputStream(file)) {
            properties.setProperty("db.url", "null");
            properties.store(output, "Standard-Konfigurationsdatei erstellt");
            System.out.println("Standard-Konfiguration erstellt.");
            System.out.println("Ändern sie die Werte unter der db.config file");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readConfig() {
        Properties properties = new Properties();
        String urlDataOfConfig = "null";
        try (FileInputStream input = new FileInputStream(url)) {
            properties.load(input);

            urlDataOfConfig = properties.getProperty("db.url");
            if (urlDataOfConfig.equals("null")) {
                return "null";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return urlDataOfConfig;

    }

}
