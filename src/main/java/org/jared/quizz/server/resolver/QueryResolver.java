package org.jared.quizz.server.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.jared.quizz.server.Store;
import org.jared.quizz.server.model.TeamFilter;
import org.jared.quizz.server.model.Team;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryResolver implements GraphQLQueryResolver {

    private final Store store;

    public QueryResolver(Store store) {
        this.store = store;
    }

    public List<Team> teams(TeamFilter filter) {
        return store.getTeams(filter);
    }

    public Team team(String teamId) {
        return store.getTeam(teamId);
    }

}
