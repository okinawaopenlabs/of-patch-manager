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
