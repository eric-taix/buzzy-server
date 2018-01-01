package org.jared.apollo.ws;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OperationType {
    GQL_CONNECTION_INIT("connection_init"),
    GQL_START("start"),
    GQL_STOP("stop"),
    GQL_CONNECTION_TERMINATE("connection_terminate"),

    GQL_CONNECTION_ERROR("connection_error"),
    GQL_CONNECTION_ACK("connection_ack"),
    GQL_DATA("data"),
    GQL_ERROR("error"),
    GQL_COMPLETE("complete"),
    GQL_CONNECTION_KEEP_ALIVE("connection_keep_alive");


    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
