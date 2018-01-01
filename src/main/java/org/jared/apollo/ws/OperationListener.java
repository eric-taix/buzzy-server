package org.jared.apollo.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.schema.GraphQLSchema;
import org.jared.quizz.server.util.JsonKit;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.singletonList;

@Component
public class OperationListener {

    private final AtomicReference<Subscription> subscriptionRef = new AtomicReference<>();


    @Autowired
    private GraphQLSchema graphQLSchema;

    public static final String ID = "id";
    private static ObjectMapper mapper = new ObjectMapper();

    public void onConnectionInit(WebSocketSession session, OperationMessage operationMessage) throws IOException {
        session.sendMessage(prepare(OperationMessage.ack()));
    }

    public void onStart(WebSocketSession session, OperationMessage operationMessage) throws IOException {
        session.getAttributes().put(ID, operationMessage.getId());
        Query query = mapper.convertValue(operationMessage.getPayload(), Query.class);
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query.getQuery())
                .variables(query.getVariables())
                .operationName(query.getOperationName())
                .build();

        Instrumentation instrumentation = new ChainedInstrumentation(singletonList(new TracingInstrumentation()));
        GraphQL graphQL = GraphQL
                .newGraphQL(graphQLSchema)
                .instrumentation(instrumentation)
                .build();

        ExecutionResult executionResult = graphQL.execute(executionInput);

        String fieldName = getFieldNameFromOperation(operationMessage);
        Publisher<ExecutionResult> executionStream = executionResult.getData();
        executionStream.subscribe(new Subscriber<ExecutionResult>() {

            @Override
            public void onSubscribe(Subscription subscription) {
                subscriptionRef.set(subscription);
                request(1);
            }

            @Override
            public void onNext(ExecutionResult executionResult) {
                try {
                    Map<String, Object> result = new HashMap<>();
                    result.put(fieldName, executionResult.getData());
                    session.sendMessage(prepare(OperationMessage.data((String) session.getAttributes().get(ID), result)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                request(1);
            }

            @Override
            public void onError(Throwable t) {
                try {
                    session.close();
                } catch (IOException e) {
                }
            }

            @Override
            public void onComplete() {
                try {
                    session.close();
                } catch (IOException e) {
                }
            }
        });
    }

    public void onError(WebSocketSession session, OperationMessage operationMessage, Exception ex) throws IOException {
        session.sendMessage(prepare(OperationMessage.error((String) session.getAttributes().get(ID), ex)));
    }

    private TextMessage prepare(OperationMessage message) throws JsonProcessingException {
        return new TextMessage(mapper.writeValueAsString(message));
    }

    private void request(int n) {
        Subscription subscription = subscriptionRef.get();
        if (subscription != null) {
            subscription.request(n);
        }
    }

    protected String getFieldNameFromOperation(OperationMessage operationMessage) {
        String operationName = (String)((Map)operationMessage.getPayload()).get("operationName");
        return "onTeamChanged".equals(operationName) ? "team" : "teams";
    }
}
