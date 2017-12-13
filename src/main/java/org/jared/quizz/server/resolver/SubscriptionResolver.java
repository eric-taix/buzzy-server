package org.jared.quizz.server.resolver;

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
import graphql.schema.DataFetchingEnvironment;
import org.jared.quizz.server.Store;
import org.jared.quizz.server.model.Team;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionResolver implements GraphQLSubscriptionResolver {

    @Autowired
    private Store store;

    public Publisher<Team> getTeam(String teamId, DataFetchingEnvironment env) {
        return new TeamPublisher(store.getTeam(teamId));
    }

}
