package org.jared.quizz.server;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import org.jared.quizz.server.model.State;
import org.jared.quizz.server.model.Team;
import org.jared.quizz.server.model.TeamFilter;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Root
public class Store {

    private static final Predicate<Team> TEAM_HAS_BUZZED = team -> State.BUZZED.equals(team.getState());

    private transient PublishSubject<List<Team>> changeObservable = PublishSubject.create();
    @ElementList
    private List<Team> teams = new ArrayList<>();

    public synchronized void addTeam(Team team) {
        teams.add(team);
        changeObservable.onNext(teams);
    }

    public synchronized List<Team> getTeams(TeamFilter filter) {
        Stream<Team> result = null;
        if (filter != null) {
            switch (filter.getType()) {
                case NAME:
                    result = teams.stream().filter((team) -> team.getName().equals(filter.getDesiratedValue()));
            }
        } else {
            result = teams.stream();
        }
        return result
                .sorted(Comparator.comparingInt(Team::getPoints).reversed())
                .collect(Collectors.toList());
    }

    public synchronized Team buzz(String teamId) {
        Team curentTeam = teams.stream().filter(filterById(teamId)).findFirst().get();
        boolean teamHasBuzzzed = teams.stream().anyMatch(TEAM_HAS_BUZZED);
        if (!teamHasBuzzzed) {
            teams.forEach(team -> {
                if (team.getId().equals(teamId)) {
                    team.setState(State.BUZZED);
                } else if (!State.WRONG.equals(team.getState())) {
                    team.setState(State.PAUSED);
                }
            });
        }
        changeObservable.onNext(teams);
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
        changeObservable.onNext(teams);
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
        changeObservable.onNext(teams);
        return buzzedTeam;
    }

    public Observable<List<Team>> getModelChanges() {
        return changeObservable;
    }

    public Team wrong() {
        Team buzzedTeam = teams.stream().filter(TEAM_HAS_BUZZED).findFirst().get();
        teams.forEach(team -> {
            if (!State.BUZZED.equals(team.getState()) && !State.WRONG.equals(team.getState())) {
                team.setState(State.THINKING);
            }
        });
        buzzedTeam.setState(State.WRONG);
        changeObservable.onNext(teams);
        return buzzedTeam;
    }
}
