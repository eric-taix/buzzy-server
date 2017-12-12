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
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.singletonList;

@Component
@ServerEndpoint(value = "/graphql", configurator = SpringConfigurator.class)
public class WsServer {

    private static final Logger log = LoggerFactory.getLogger(WsServer.class);
    private final AtomicReference<Subscription> subscriptionRef = new AtomicReference<>();

    @Autowired
    private GraphQLSchema graphQLSchema;

    @OnMessage
    public String onMessage(Session session, String message){
        QueryParameters parameters = QueryParameters.from(message);

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
            public void onSubscribe(Subscription s) {
                subscriptionRef.set(s);
                request(1);
            }

            @Override
            public void onNext(ExecutionResult er) {
                log.debug("Sending stick price update");
                try {
                    Object stockPriceUpdate = er.getData();
                    session.getBasicRemote().sendText(JsonKit.toJsonString(stockPriceUpdate));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                request(1);
            }

            @Override
            public void onError(Throwable t) {
                log.error("Subscription threw an exception", t);
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
        return null;
    }

    private void request(int n) {
        Subscription subscription = subscriptionRef.get();
        if (subscription != null) {
            subscription.request(n);
        }
    }
}
