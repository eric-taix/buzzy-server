package org.jared.quizz.server;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.schema.GraphQLSchema;
import org.jared.quizz.server.util.JsonKit;
import org.jared.quizz.server.util.QueryParameters;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.singletonList;


public class WsHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(WsHandler.class);
    private final AtomicReference<Subscription> subscriptionRef = new AtomicReference<>();

    @Autowired
    private GraphQLSchema graphQLSchema;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        QueryParameters parameters = QueryParameters.from(message.getPayload());

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(parameters.getQuery())
                .variables(parameters.getVariables())
                .operationName(parameters.getOperationName())
                .build();

        Instrumentation instrumentation = new ChainedInstrumentation(
                singletonList(new TracingInstrumentation())
        );

        GraphQL graphQL = GraphQL
                .newGraphQL(graphQLSchema)
                .instrumentation(instrumentation)
                .build();

        ExecutionResult executionResult = graphQL.execute(executionInput);

        Publisher<ExecutionResult> stockPriceStream = executionResult.getData();
        stockPriceStream.subscribe(new Subscriber<ExecutionResult>() {

            @Override
            public void onSubscribe(Subscription subscription) {
                subscriptionRef.set(subscription);
                request(1);
            }

            @Override
            public void onNext(ExecutionResult executionResult) {
                try {
                    session.sendMessage(new TextMessage(JsonKit.toJsonString(executionResult.getData())));
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
                log.info("Subscription complete");
                try {
                    session.close();
                } catch (IOException e) {
                }
            }
        });
    }

    private void request(int n) {
        Subscription subscription = subscriptionRef.get();
        if (subscription != null) {
            subscription.request(n);
        }
    }
}
