package org.cybergarage.upnp.xml;

import org.cybergarage.upnp.control.QueryListener;
import org.cybergarage.upnp.control.QueryResponse;

/* JADX INFO: loaded from: classes.dex */
public class StateVariableData extends NodeData {
    private String value = "";
    private QueryListener queryListener = null;
    private QueryResponse queryRes = null;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public QueryListener getQueryListener() {
        return this.queryListener;
    }

    public void setQueryListener(QueryListener queryListener) {
        this.queryListener = queryListener;
    }

    public QueryResponse getQueryResponse() {
        return this.queryRes;
    }

    public void setQueryResponse(QueryResponse res) {
        this.queryRes = res;
    }
}
