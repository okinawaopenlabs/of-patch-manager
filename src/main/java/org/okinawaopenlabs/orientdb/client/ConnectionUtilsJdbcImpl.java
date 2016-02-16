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
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;

import org.okinawaopenlabs.ofpm.utils.Config;
import org.okinawaopenlabs.ofpm.utils.ConfigImpl;

public class ConnectionUtilsJdbcImpl implements ConnectionUtilsJdbc {

	private static final Logger logger = Logger.getLogger(ConnectionUtilsJdbcImpl.class);

    /**
     * setting file
     */
    private Config config;

    /**
     * default constructor
     */
    public ConnectionUtilsJdbcImpl() {
        this.config = new ConfigImpl();
    }

    /**
     * constructor with parameter
     * @param config
     */
    public ConnectionUtilsJdbcImpl(Config config) {
        this.config = config;
    }

    @Override
    public Connection getConnection(boolean autoCommit) throws SQLException {
        Connection conn = ConnectionManagerJdbc.getInstance(config).getConnection();
        conn.setAutoCommit(autoCommit);
        return conn;
    }

    @Override
    public void close(Connection conn) {
        try {
        	ConnectionManagerJdbc.getInstance(config).close(conn);
        } catch (SQLException se) {
            String message = "failed to close connection.";
            logger.warn(message);
        }
    }

    @Override
    public void commit(Connection conn) throws SQLException {
    	ConnectionManagerJdbc.getInstance(config).commit(conn);
    }

    @Override
    public void rollback(Connection conn) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("rollback(conn=%s) - start ", conn));
        }
        try {
        	ConnectionManagerJdbc.getInstance(config).rollback(conn);
        } catch (SQLException se) {
            String message = "failed to rollback connection.";
            logger.warn(message);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("rollback() - end ");
        }
    }

    @Override
    public int update(Connection conn, String sql, Object[] params)
            throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("update(conn=%s, sql=%s, params=%s) - start ", conn, sql, params));
        }
        QueryRunner qRunner = new QueryRunner();
        int rows = qRunner.update(conn, sql, params);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("update(rows=%s) - end ", rows));
        }
        return rows;
    }

    @Override
    public int update(Connection conn, String sql) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("update(conn=%s, sql=%s) - start ", conn, sql));
        }
        QueryRunner qRunner = new QueryRunner();
        int rows = qRunner.update(conn, sql);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("update(rows=%s) - end ", rows));
        }
        return rows;
    }

    @Override
    public void query(Connection conn, String sql) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("query(conn=%s, sql=%s) - start ", conn, sql));
        }
        QueryRunner qRunner = new QueryRunner();
        ResultSetHandler<Object> rsh = null;
        qRunner.query(conn, sql, rsh);
        if (logger.isDebugEnabled()) {
            logger.debug("query() - end ");
        }
    }

    @Override
    public <T> T query(Connection conn, String sql,
            ResultSetHandler<T> handler, Object... params) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.trace(String.format("query(conn=%s, sql=%s, handler=%s, params=%s) - start ", conn, sql, handler, params.toString()));
        }
        QueryRunner qRunner = new QueryRunner();
        T records = qRunner.query(conn, sql, handler, params);
        if (logger.isDebugEnabled()) {
            logger.trace(String.format("query(records=%s) - end ", records));
        }
        return records;
    }

    @Override
    public <T> T query(Connection conn, String sql,
            ResultSetHandler<T> handler) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.trace(String.format("query(conn=%s, sql=%s, handler=%s) - start ", conn, sql, handler));
        }
        QueryRunner qRunner = new QueryRunner();
        T records = qRunner.query(conn, sql, handler);
        if (logger.isDebugEnabled()) {
            logger.trace(String.format("query(records=%s) - end ", records));
        }
        return records;
    }
}
