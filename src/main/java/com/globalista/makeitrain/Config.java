package com.globalista.makeitrain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Config {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    public String registryKey = "Use RegistryKey dimension matching instead of checking if the dimension has skylight (default: true)";
    public boolean registryKeyEnable = true;

    public static Config loadConfigFile(File file) {
        Config config = null;

        if (file.exists()) {
            try (BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                config = gson.fromJson(fileReader, Config.class);
            } catch (IOException e) {
                throw new RuntimeException("[Make It Rain] Problem occurred when trying to load config: ", e);
            }
        }
        if (config == null) {
            config = new Config();
        }
        config.saveConfigFile(file);
        return config;
    }

    public void saveConfigFile(File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
