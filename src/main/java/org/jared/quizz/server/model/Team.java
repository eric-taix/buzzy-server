package org.jared.quizz.server.model;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import java.util.UUID;

import static org.jared.quizz.server.model.State.THINKING;

public class Team {

    private PublishSubject<Team> changeObservable = PublishSubject.create();

    private String id;
    private String name;
    private String avatarUrl;
    private int points;
    private State state = THINKING;

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
        changeObservable.onNext(this);
        return this;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Team setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        changeObservable.onNext(this);
        return this;
    }

    public int getPoints() {
        return points;
    }

    public Team setPoints(Integer points) {
        this.points = points;
        changeObservable.onNext(this);
        return this;
    }

    public State getState() {
        return state;
    }

    public Team setState(State state) {
        this.state = state;
        changeObservable.onNext(this);
        return this;
    }

    public Observable<Team> getModelChanges() {
        return changeObservable;
    }
}
