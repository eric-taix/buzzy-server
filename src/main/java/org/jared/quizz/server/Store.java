package org.jared.quizz.server;

import org.jared.quizz.server.model.State;
import org.jared.quizz.server.model.Team;
import org.jared.quizz.server.model.TeamFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class Store {

    private static final Predicate<Team> TEAM_HAS_BUZZED = team -> State.BUZZED.equals(team.getState());

    private List<Team> teams = new ArrayList<>();

    public synchronized void addTeam(Team team) {
        teams.add(team);
    }

    public synchronized List<Team> getTeams(TeamFilter filter) {
        if (filter != null) {
            switch (filter.getType()) {
                case NAME:
                    return teams.stream().filter((team) -> team.getName().equals(filter.getDesiratedValue())).collect(Collectors.toList());
            }
        }
        return teams;
    }

    public synchronized Team buzz(String teamId) {
        Team curentTeam = teams.stream().filter(filterById(teamId)).findFirst().get();
        boolean teamHasBuzzzed = teams.stream().anyMatch(TEAM_HAS_BUZZED);
        if (!teamHasBuzzzed) {
            teams.forEach(team -> team.setState(team.getId().equals(teamId) ? State.BUZZED : State.PAUSED));
        }
        return curentTeam;
    }

    public synchronized Team updateTeam(String teamId, String name, Integer points) {
        Team team = teams.stream().filter(filterById(teamId)).findFirst().get();
        if (name != null) {
            team.setName(name);
        }
        if (points != null) {
            team.setPoints(points);
        }
        return team;
    }

    private Predicate<Team> filterById(String teamId) {
        return team -> team.getId().equals(teamId);
    }


    public Team getTeam(String teamId) {
        return teams.stream().filter(filterById(teamId)).findFirst().get();
    }

    public Team correct() {
        Team buzzedTeam = teams.stream().filter(TEAM_HAS_BUZZED).findFirst().get();
        teams.forEach(team -> {
            if (State.BUZZED.equals(team.getState())) {
                team.setPoints(team.getPoints() + 1);
            }
            team.setState(State.THINKING);
        });
        return buzzedTeam;
    }
}
