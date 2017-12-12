package org.jared.quizz.server;

import org.jared.quizz.server.model.Quizz;
import org.jared.quizz.server.model.Team;
import org.jared.quizz.server.model.TeamFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Store {

    private List<Quizz> quizzes = new ArrayList<>();
    private List<Team> teams = new ArrayList<>();

    public Quizz createQuizz(String name) {
        Quizz quizz = new Quizz(name);
        quizzes.add(quizz);
        return quizz;
    }

    public List<Quizz> getQuizzes() {
        return quizzes;
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public List<Team> getTeams(TeamFilter filter) {
        if (filter != null) {
            switch (filter.getType()) {
                case NAME: return teams.stream().filter((team) -> team.getName().equals(filter.getDesiratedValue())).collect(Collectors.toList());
                case QUIZZ_ID: return teams.stream().filter((team) -> team.getQuizz() != null ? team.getQuizz().getId().equals(filter.getDesiratedValue()) : false).collect(Collectors.toList());
            }
        }
        return teams;
    }

    public Team findTeamByID(String teamId) {
        return teams.stream().filter(team -> team.getId().equals(teamId)).findFirst().get();
    }

    public Quizz findQuizzByID(String quizzId) {
        return quizzes.stream().filter(team -> team.getId().equals(quizzId)).findFirst().get();
    }
}
