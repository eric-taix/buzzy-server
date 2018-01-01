package org.jared.quizz.server;

import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.execution.ExecutionStrategy;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.servlet.ExecutionStrategyProvider;
import org.jared.apollo.ws.ApolloWsHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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

@SpringBootApplication
@EnableWebSocket
@ComponentScan("org.jared")
public class QuizzServer implements WebSocketConfigurer {

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