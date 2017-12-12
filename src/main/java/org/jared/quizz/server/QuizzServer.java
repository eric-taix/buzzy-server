package org.jared.quizz.server;

import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.execution.ExecutionStrategy;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.servlet.ExecutionStrategyProvider;
import org.jared.quizz.server.model.Team;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@SpringBootApplication
@EnableWebSocket
public class QuizzServer extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(QuizzServer.class, args);
    }

    @Bean
    public ExecutionStrategyProvider executionStrategyProvider() {
        return new ExecutionStrategyProvider() {
            @Override
            public ExecutionStrategy getQueryExecutionStrategy() {
                return new AsyncExecutionStrategy();
            }

            @Override
            public ExecutionStrategy getMutationExecutionStrategy() {
                return new AsyncSerialExecutionStrategy();
            }

            @Override
            public ExecutionStrategy getSubscriptionExecutionStrategy() {
                return new SubscriptionExecutionStrategy();
            }
        };
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public GraphQLSchema schema() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("schema.graphqls");
        Reader streamReader = new InputStreamReader(stream);
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(streamReader);
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Subscription")
                        .dataFetcher("team", new DataFetcher() {
                            @Override
                            public Object get(DataFetchingEnvironment environment) {
                                return new Publisher() {
                                    @Override
                                    public void subscribe(Subscriber s) {
                                        s.onNext(new Team().setName("SUB" + System.currentTimeMillis()));
                                    }
                                };
                            }
                        })
                )
                .build();

        return new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
    }

}