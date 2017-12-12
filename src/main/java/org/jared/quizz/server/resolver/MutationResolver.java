package org.jared.quizz.server.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.jared.quizz.server.Store;
import org.jared.quizz.server.model.Quizz;
import org.jared.quizz.server.model.Team;
import org.springframework.stereotype.Component;

@Component
public class MutationResolver implements GraphQLMutationResolver {

    private final Store store;

    public MutationResolver(Store store) {
        this.store = store;
    }

    public Quizz createQuizz(String name) {
        return store.createQuizz(name);
    }

    public Team createTeam(String teamName) {
        Team team = new Team().setName(teamName);
        store.addTeam(team);
        return team;
    }

    public Team updateTeam(String teamId, String name, Integer points, String quizzId) {
        Team team = store.findTeamByID(teamId);
        if (name != null) {
            team.setName(name);
        }
        if (points != null) {
            team.setPoints(points);
        }
        if (quizzId != null) {
            Quizz quizz = store.findQuizzByID(quizzId);
            team.setQuizz(quizz);
        }
        return team;
    }
}
