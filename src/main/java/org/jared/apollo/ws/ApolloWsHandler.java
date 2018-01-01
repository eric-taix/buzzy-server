package org.jared.apollo.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class ApolloWsHandler extends TextWebSocketHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApolloWsHandler.class);


    @Autowired
    private OperationListener operationListener;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        OperationMessage operationMessage = OperationMessage.fromMessage(message.getPayload());

        try {
            switch (operationMessage.getType()) {
                case GQL_CONNECTION_INIT:
                    operationListener.onConnectionInit(session, operationMessage);
                    break;
                case GQL_START:
                    operationListener.onStart(session, operationMessage);
                    break;
            }
        }
        catch (Exception ex) {
            LOG.error("Error while receiving websocket message", ex);
            operationListener.onError(session, operationMessage, ex);
        }
        return;
    }


}
