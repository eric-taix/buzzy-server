package org.jared.quizz.server.resolver;

import org.jared.quizz.server.Store;
import org.jared.quizz.server.model.Team;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class TeamsPublisher implements Publisher<List<Team>> {

    private List<Subscriber<? super List<Team>>> subscribers = new ArrayList();

    private Store store;

    public TeamsPublisher(Store store) {
        this.store = store;
        this.store.getModelChanges().subscribe(updatedTeam -> {
            for (Subscriber subscriber : subscribers) {
                subscriber.onNext(store.getTeams(null));
            }
        });

    }

    @Override
    public void subscribe(Subscriber<? super List<Team>> subscriber) {
        subscriber.onNext(store.getTeams(null));
        subscribers.add(subscriber);
    }

}
