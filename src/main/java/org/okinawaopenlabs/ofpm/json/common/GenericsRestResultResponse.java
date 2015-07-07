package org.okinawaopenlabs.ofpm.json.common;

import java.util.ArrayList;
import java.util.List;

public class GenericsRestResultResponse<T> extends RestResponse {
        private List<T> result = new ArrayList<T>();

        public List<T> getResult() {
                return result;
        }

        public void setResult(List<T> result) {
                this.result = result;
        }
}
