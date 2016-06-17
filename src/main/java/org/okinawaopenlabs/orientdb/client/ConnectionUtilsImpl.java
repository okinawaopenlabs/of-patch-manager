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
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import org.okinawaopenlabs.ofpm.utils.Config;
import org.okinawaopenlabs.ofpm.utils.ConfigImpl;


public class ConnectionUtilsImpl implements ConnectionUtils {

    /**
     * 設定ファイル
     */
    private Config config;

    /**
     * デフォルトのコンストラクタ.
     */
    public ConnectionUtilsImpl() {
        this.config = new ConfigImpl();
    }

    /**
     * パラメータ付きコンストラクタ.
     *
     * @param config
     */
    public ConnectionUtilsImpl(Config config) {
        this.config = config;
    }

	@Override
	public ODatabaseDocumentTx getDatabase() throws SQLException {
		return ConnectionManager.getInstance(config).getDatabase();
	}

	@Override
	public void close(ODatabaseDocumentTx database) throws SQLException {
		ConnectionManager.getInstance(config).close(database);
	}

	@Override
	synchronized public List<ODocument> query(ODatabaseDocumentTx database, String query) {
		return database.query(new OSQLSynchQuery<ODocument>(query));
	}

	@Override
	public void commit(ODatabaseDocumentTx database) {
		// not implemented
	}

	@Override
	public void rollback(ODatabaseDocumentTx database) {
		// not implemented
	}

	@Override
	public int update(ODatabaseDocumentTx database, String sql, Object[] params) {
		return 0;
	}

	@Override
	public int update(ODatabaseDocumentTx database, String sql) {
		return 0;
	}

}
