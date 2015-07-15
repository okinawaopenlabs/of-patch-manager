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

package org.okinawaopenlabs.orientdb.client;

import java.sql.SQLException;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.log4j.Logger;

import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import org.okinawaopenlabs.ofpm.utils.Config;
import org.okinawaopenlabs.ofpm.utils.ConfigImpl;


public class ConnectionManager {

	private static final Logger logger = Logger.getLogger(ConnectionManager.class);

    private static ConnectionManager dbAccessManager = null;

    private static ODatabaseDocumentTx database;

    /**
     * @param config
     */
    private ConnectionManager() {
    }

    /**
     * @param config
     * @throws SQLException
     */
    private static void initialize(Config config) throws SQLException {
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("initialize(config=%s) - start", config));
    	}

        String user = config.getString(CONFIG_KEY_DB_USER);
        String password = config.getString(CONFIG_KEY_DB_PASSWORD);
        String url = config.getString(CONFIG_KEY_DB_URL);

        try {
        	database = new ODatabaseDocumentTx(url).open(user, password);
        	if (database == null) {
        		String message = "failed to load database.";
        		throw new RuntimeException(message);
        	}
        } catch (RuntimeException re) {
        	throw new SQLException(re.getMessage(), re);
        }
    	if (logger.isDebugEnabled()) {
    		logger.debug("initialize() - end");
    	}
    }

    /**
     * @return instance
     * @throws SQLException
     */
    synchronized public static ConnectionManager getInstance() throws SQLException {
    	if (logger.isDebugEnabled()) {
    		logger.debug("getInstance() - start");
    	}
        if (dbAccessManager == null) {
            dbAccessManager = new ConnectionManager();
        }
        if (database == null || database.isClosed()) {
        	initialize(new ConfigImpl());
        }
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("getInstance(dbAccessManager=%s) - end", dbAccessManager));
    	}
        return dbAccessManager;
    }

    /**
     * @param config
     * @return instance
     * @throws SQLException
     */
    synchronized public static ConnectionManager getInstance(Config config) throws SQLException {
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("getInstance(config=%s) - start", config));
    	}
        if (dbAccessManager == null) {
            dbAccessManager = new ConnectionManager();
        }
        if (database == null || database.isClosed()) {
        	initialize(config);
        }
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("getInstance(dbAccessManager=%s) - end", dbAccessManager));
    	}
        return dbAccessManager;
    }

    /**
     * return database
     * @return database object
     */
    synchronized public ODatabaseDocumentTx getDatabase() {
    	if (logger.isDebugEnabled()) {
    		logger.debug("getDatabase() - start");
    		logger.debug(String.format("getDatabase(database=%s) - end", database));
    	}
        return database;
    }

    /**
     * commit
     *
     * @param database
     * @throws SQLException
     */
    synchronized public void commit(ODatabaseDocumentTx database) throws SQLException {
    }

    /**
     * rollback
     *
     * @param database
     * @throws SQLException
     */
    synchronized public void rollback(ODatabaseDocumentTx database) throws SQLException {
    }

    /**
     * close database
     *
     * @param database
     */
    synchronized public void close(ODatabaseDocumentTx database) {
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("close(database=%s) - start", database));
    	}
        if (database != null && !database.isClosed()) {
            database.close();
        }
    	if (logger.isDebugEnabled()) {
    		logger.debug("close() - end");
    	}
    }

}
