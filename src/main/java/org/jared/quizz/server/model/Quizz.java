package org.jared.quizz.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Quizz {

    private String id;
    private String name;
    private List<Team> teams = new ArrayList<>();

    public Quizz(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Quizz setName(String name) {
        this.name = name;
        return this;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public Quizz addTeam(Team newTeam) {
        this.teams.add(newTeam);
        return this;
    }
}
