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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.pool.impl.GenericObjectPool;

import static org.apache.commons.lang.StringUtils.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import org.okinawaopenlabs.ofpm.utils.Config;
import org.okinawaopenlabs.ofpm.utils.ConfigImpl;

public class ConnectionManagerJdbc {

    private static ConnectionManagerJdbc dbAccessManager = null;

    private static DataSource dataSource = null;

    private static String driverUrl = null;

    /**
     * @param config
     */
    private ConnectionManagerJdbc(Config config) {
    	initializeDataSource(config);
    }

    /**
     * @param config
     * @throws SQLException
     */
	private void initializeDataSource(Config config) {
        String user = config.getString(CONFIG_KEY_DB_USER);
        String password = config.getString(CONFIG_KEY_DB_PASSWORD);

        Properties params = new Properties();

        if (isNotEmpty(user) && isNotEmpty(password)) {
            params.put("user", user);
            params.put("password", password);
        }

        // ドライバのロード
        String driver = config.getString(CONFIG_KEY_DB_DRIVER);
        boolean loadSuccess = DbUtils.loadDriver(driver);
        if (!loadSuccess) {
            String message = "failed to load driver.";
            throw new RuntimeException(message);
        }

        // コネクションをプールするDataSource を作成する
        @SuppressWarnings("rawtypes")
		GenericObjectPool pool = new GenericObjectPool();
        // コネクションプールの設定を行う
        int  maxActive = config.getInt(CONFIG_KEY_DB_MAX_ACTIVE_CONN, 100);
        long maxWait   = Long.parseLong(config.getString(CONFIG_KEY_DB_WAIT, "-1"));
        pool.setMaxActive(maxActive);
        pool.setMaxIdle(maxActive);
        pool.setMaxWait(maxWait);

        driverUrl = config.getString(CONFIG_KEY_DB_URL);
        ConnectionFactory connFactory = new DriverManagerConnectionFactory(driverUrl, params);
        new PoolableConnectionFactory(connFactory, pool, null,
                null, // validationQuery
                false, // defaultReadOnly
                false); // defaultAutoCommit
        dataSource = new PoolingDataSource(pool);
    }

    /**
     * @return instance
     * @throws SQLException
     */
    synchronized public static ConnectionManagerJdbc getInstance() {
        if (dbAccessManager == null) {
            dbAccessManager = new ConnectionManagerJdbc(new ConfigImpl());
        }
        return dbAccessManager;
    }

    /**
     * @param config
     * @return instance
     * @throws SQLException
     */
    synchronized public static ConnectionManagerJdbc getInstance(Config config) {
        if (dbAccessManager == null) {
            dbAccessManager = new ConnectionManagerJdbc(config);
        }
        return dbAccessManager;
    }

    /**
     * return database
     * @return database object
     */
    synchronized public Connection getConnection() throws SQLException {
        Connection conn = null;
        conn = dataSource.getConnection();
        return conn;
    }

    /**
     * commit
     *
     * @param database
     * @throws SQLException
     */
    synchronized public void commit(Connection conn) throws SQLException {
        DbUtils.commitAndClose(conn);
    }

    /**
     * rollback
     *
     * @param database
     * @throws SQLException
     */
    synchronized public void rollback(Connection conn) throws SQLException {
        DbUtils.rollback(conn);
    }

    /**
     * close database
     *
     * @param database
     */
    synchronized public void close(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            DbUtils.close(conn);
        }
    }

    synchronized public void close(ResultSet rs) throws SQLException {
        if (rs != null && !rs.isClosed()) {
            DbUtils.close(rs);
        }
    }
}
