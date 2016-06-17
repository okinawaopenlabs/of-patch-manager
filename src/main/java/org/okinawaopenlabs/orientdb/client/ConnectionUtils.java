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
import java.util.List;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

public interface ConnectionUtils {
	 /**
     * データベースを取得する。
     *
     * @return データベース
     */
    public abstract ODatabaseDocumentTx getDatabase() throws SQLException;


    /**
     * データベースをクローズする。
     *
     * @param データベース
     */
    public abstract void close(ODatabaseDocumentTx database) throws SQLException;

    /**
     * 問い合わせをおこなう
     *
     * @param database
     *            データベース
     * @param sql
     *            SQL文
     * @param handler
     *            結果セットのハンドラ
     * @return SQLの実行結果
     */
    public abstract List<ODocument> query(ODatabaseDocumentTx database, String sql);
    /**
     * コミットする。
     *
     * @param database
     *            データベース
     */
    public abstract void commit(ODatabaseDocumentTx database);

    /**
     * ロールバックする。
     *
     * @param database
     *            データベース
     */
    public abstract void rollback(ODatabaseDocumentTx database);
    /**
     * INSERT処理を実行する
     *
     * @param database
     *            データベース
     * @param sql
     *            SQL文
     * @param params
     *            バインドパラメータ
     * @return INSERTした行数
     */
    public abstract int update(ODatabaseDocumentTx database, String sql, Object[] params);
    /**
     * INSERT処理を実行する
     *
     * @param database
     *            データベース
     * @param sql
     *            SQL文
     * @return INSERTした行数
     */
    public abstract int update(ODatabaseDocumentTx database, String sql);
}
