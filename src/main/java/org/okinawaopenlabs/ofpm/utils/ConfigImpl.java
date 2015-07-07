package org.okinawaopenlabs.ofpm.utils;

import java.io.StringWriter;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import static org.okinawaopenlabs.constants.OfpmDefinition.*;

public class ConfigImpl implements Config {

    private static final Logger logger = Logger.getLogger(ConfigImpl.class);

    private Configuration config = null;

    /**
     * Load config file.
     *
     * @throws RuntimeException
     * Failed to load config file.
     */
    public ConfigImpl() {
        this(DEFAULT_PROPERTIY_FILE);
    }

    /**
     * Specify config file and then create instance.
     *
     * @param config
     * 	File path.
     */
    public ConfigImpl(String config) {
        try {
            this.config = new PropertiesConfiguration(config);
        } catch (ConfigurationException e) {
            String message = "failed to read config file.";
            logger.error(message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Specify config object and then create instance.
     *
     * @param config
     * Config object.
     */
    public ConfigImpl(Configuration config) {
        this.config = config;
    }

    @Override
    public String getString(String key) {
        String value = getConfiguration().getString(key);
        return value;
    }

    @Override
    public String getString(String key, String defaultValue) {
        try {
            String value = getConfiguration().getString(key, defaultValue);
            return value;
        } catch (ConversionException e) {
            return defaultValue;
        }
    }

    @Override
    public int getInt(String key) {
        return getConfiguration().getInt(key);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        try {
            int value = getConfiguration().getInt(key, defaultValue);
            return value;
        } catch (ConversionException e) {
            return defaultValue;
        }
    }

    @Override
    public String getContents() {
        PropertiesConfiguration prop = new PropertiesConfiguration();
        prop.append(this.config);
        StringWriter sw = new StringWriter();
        try {
            prop.save(sw);
        } catch (ConfigurationException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    /**
     * Configuration object is returned, it to provide reference to config file.
     *
     * @return
     * Configuration object to provide reference to config file.
     */
    @Override
    public Configuration getConfiguration() {
        return this.config;
    }

    @Override
    public List<Object> getList(String key) {
           List<Object> values = getConfiguration().getList(key);
           return values;
    }
}
