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

import org.apache.commons.dbutils.ResultSetHandler;

public interface ConnectionUtilsJdbc {
	 /**
     * get connection
     * @param autoCommit
     * @return connection
     * @throws SQLException
     */
    public abstract Connection getConnection(boolean autoCommit) throws SQLException;

    /**
     * close connection
     * @param conn
     */
    public abstract void close(Connection conn);

    /**
     * exec query
     * @param conn
     * @param sql
     * @param handler
     * @return result SQL
     * @throws SQLException
     */
    public abstract <T> T query(Connection conn, String sql,
            ResultSetHandler<T> handler, Object... params) throws SQLException;

    public abstract <T> T query(Connection conn, String sql,
            ResultSetHandler<T> handler) throws SQLException;
    /**
     * commit
     * @param conn
     * @throws SQLException
     */
    public abstract void commit(Connection conn) throws SQLException;

    /**
     * rollback
     * @param conn
     */
    public abstract void rollback(Connection conn);

    /**
     * insert
     * @param conn
     * @param sql
     * @param params
     * @return line of insert
     * @throws SQLException
     */
    public abstract int update(Connection conn, String sql, Object[] params)
            throws SQLException;
    /**
     * insert
     * @param conn
     * @param sql
     * @return line of insert
     * @throws SQLException
     */
    public abstract int update(Connection conn, String sql)
            throws SQLException;

    /**
     * exec query
     * @param conn
     * @param sql
     * @throws SQLException
     */
    public abstract void query(Connection conn, String sql) throws SQLException;

}
