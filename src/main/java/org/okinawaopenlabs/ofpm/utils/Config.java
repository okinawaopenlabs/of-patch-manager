package org.okinawaopenlabs.ofpm.utils;

import java.util.List;
import org.apache.commons.configuration.Configuration;

/**
 * Interface to get properties config file.
 *
 * @author kurahashi
 * @version 0.1
 *
 */
public interface Config {
	/**
     * Get value to corresponding to the key.
     *
     * @param key
     * @return
     * 	string value.
     */
    String getString(String key);

    /**
     * Get value to corresponding to the key.
     * Specified value is returned if not contains key to config file.
     *
     * @param key
     * @param defaultValue
     * @return
     * 	String value.
     */
    String getString(String key, String defaultValue);

    /**
     * Get value to corresponding to the key.
     *
     * @param key
     * @return
     * 	int value.
     */
    int getInt(String key);

    /**
     * Get value to corresponding to the key.
     * Specified value is returned if not contains key to config file.
     *
     * @param key
     * @param defaultValue
     * @return
     * 	int value.
     */
    int getInt(String key, int defaultValue);

    /**
     * Return with string in contents of config file.
     *
     * @return
     * 	Contents of config file.
     */
    String getContents();

    /**
     * Return with Configuration object in contents of config file.
     *
     * @return
     * 	Contents of Configuration object.
     */
    Configuration getConfiguration();

    List<Object> getList(String key);
}
