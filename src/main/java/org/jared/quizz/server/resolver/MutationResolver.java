package org.jared.quizz.server.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.jared.quizz.server.Store;
import org.jared.quizz.server.model.Team;
import org.springframework.stereotype.Component;

@Component
public class MutationResolver implements GraphQLMutationResolver {

    private final Store store;

    public MutationResolver(Store store) {
        this.store = store;
    }

    public Team createTeam(String teamName) {
        Team team = new Team().setName(teamName);
        store.addTeam(team);
        return team;
    }

    public Team updateTeam(String teamId, String name, Integer points) {
       return store.updateTeam(teamId, name, points);
    }

    public Team buzz(String teamId) {
        return store.buzz(teamId);
    }

    public Team correct() { return store.correct(); }

    public Team wrong() { return store.wrong(); }
}
