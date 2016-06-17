/*
 *   Copyright 2015 Okinawa Open Laboratory, General Incorporated Association
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

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
