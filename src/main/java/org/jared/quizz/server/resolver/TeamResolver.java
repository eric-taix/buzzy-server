package org.jared.quizz.server.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import org.jared.quizz.server.model.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamResolver implements GraphQLResolver<Team> {

    private static final String URL_PREFIX = "/avatar/";
    private static final String URL_SUFFIX = "?bgset=any&sets=set1,set2,set3,set4";

    public String getAvatarUrl(Team team) {
        return  URL_PREFIX + team.getName() + URL_SUFFIX;
    }

}
