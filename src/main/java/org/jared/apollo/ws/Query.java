package org.jared.apollo.ws;

import java.util.Map;

public class Query {

    private String query;
    private Map<String, Object> variables;
    private String operationName;
    private Map<String, Object> extensions;

    public String getQuery() {
        return query;
    }

    public Query setQuery(String query) {
        this.query = query;
        return this;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public Query setVariables(Map<String, Object> variables) {
        this.variables = variables;
        return this;
    }

    public String getOperationName() {
        return operationName;
    }

    public Query setOperationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public Query setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
        return this;
    }
}
