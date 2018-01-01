package org.jared.apollo.ws;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OperationMessage {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Object payload;
    private String id;
    private OperationType type;

    public static OperationMessage fromMessage(String message) throws IOException {
        return objectMapper.readValue(message, OperationMessage.class);
    }

    private OperationMessage() {}

    public Object getPayload() {
        return payload;
    }

    public OperationMessage setPayload(Object payload) {
        this.payload = payload;
        return this;
    }

    public String getId() {
        return id;
    }

    public OperationMessage setId(String id) {
        this.id = id;
        return this;
    }

    public OperationType getType() {
        return type;
    }

    public OperationMessage setType(OperationType type) {
        this.type = type;
        return this;
    }

    public static OperationMessage error(String id, Exception ex) {
        return new OperationMessage().setId(id).setType(OperationType.GQL_ERROR).setPayload(ex.getMessage());
    }

    public static OperationMessage ack() {
        return new OperationMessage().setType(OperationType.GQL_CONNECTION_ACK);
    }

    public static OperationMessage data(String id, Object data) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("data", data);
        return new OperationMessage().setId(id).setType(OperationType.GQL_DATA).setPayload(payload);
    }
}
