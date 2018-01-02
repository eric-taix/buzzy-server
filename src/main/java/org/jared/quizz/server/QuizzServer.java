package org.jared.quizz.server;

import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.execution.ExecutionStrategy;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.servlet.ExecutionStrategyProvider;
import org.jared.apollo.ws.ApolloWsHandler;
import org.jared.quizz.server.model.Team;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.io.File;
import java.util.List;

@SpringBootApplication
@EnableWebSocket
@ComponentScan("org.jared")
public class QuizzServer implements WebSocketConfigurer {

    @Autowired
    private Store store;

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(QuizzServer.class, args);
        final Store store = ctx.getBean(Store.class);

        load(store);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                save(store);
            }
        });
    }

    private static void save(Store store) {
        Serializer serializer = new Persister();
        try {
            serializer.write(store, new File("data.xml"));
            System.out.println("Data persisted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void  load(Store store) throws Exception {
        Serializer serializer = new Persister();
        Store loadedStore = serializer.read(Store.class, new File("data.xml"));
        System.out.println("Data loaded");
        for(Team team : loadedStore.getTeams(null)) {
            store.addTeam(team);
        }
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

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(wsHandler(), "/subscription").setAllowedOrigins("*")
                .setHandshakeHandler(handshakeHandler());
    }

    @Bean
    public HandshakeHandler handshakeHandler() {
        DefaultHandshakeHandler handshakeHandler = new DefaultHandshakeHandler();
        handshakeHandler.setSupportedProtocols("graphql-ws");
        return handshakeHandler;
    }

    @Bean
    public WebSocketHandler wsHandler() {
        return new ApolloWsHandler();
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}