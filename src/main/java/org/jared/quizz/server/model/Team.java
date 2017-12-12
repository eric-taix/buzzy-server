package org.jared.quizz.server.model;

import java.util.UUID;

public class Team {

    private String id;
    private String name;
    private String avatarUrl;
    private Quizz quizz;
    private int points;

    public Team() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Team setName(String name) {
        this.name = name;
        return this;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Team setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public Quizz getQuizz() {
        return quizz;
    }

    public Team setQuizz(Quizz quizz) {
        if (this.quizz != null) {
            this.quizz.getTeams().remove(this);
        }
        this.quizz = quizz;
        quizz.addTeam(this);
        return this;
    }

    public int getPoints() {
        return points;
    }

    public Team setPoints(Integer points) {
        this.points = points;
        return this;
    }
}
