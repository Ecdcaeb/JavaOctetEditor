package cn.enaium.joe.util;

import org.tinylog.configuration.ConfigurationLoader;

import java.util.Properties;

public class LoggerConfigurationLoader implements ConfigurationLoader {
    @Override
    public Properties load() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("writer", "file");
        properties.setProperty("writer.file", "lastest.log");
        properties.setProperty("exception", "strip: jdk.internal");
        return properties;
    }
}
