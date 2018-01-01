package org.jared.quizz.server.resolver;

import org.jared.quizz.server.model.Team;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class TeamPublisher implements Publisher<Team> {

    private final Team team;
    private List<Subscriber<? super Team>> subscribers = new ArrayList();

    public TeamPublisher(Team team) {
        this.team = team;
        team.getModelChanges().subscribe(updatedTeam -> {
            for (Subscriber subscriber : subscribers) {
                subscriber.onNext(team);
            }
        });

    }

    @Override
    public void subscribe(Subscriber<? super Team> subscriber) {
        subscriber.onNext(this.team);
        subscribers.add(subscriber);
    }

}
