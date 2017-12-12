package org.jared.quizz.server.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import org.jared.quizz.server.model.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamResolver implements GraphQLResolver<Team> {

    private static final String URL_PREFIX = "https://robohash.org/";

    public String getAvatarUrl(Team team) {
        return  URL_PREFIX + team.getName();
    }

}
